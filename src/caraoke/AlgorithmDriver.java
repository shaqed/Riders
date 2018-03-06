package caraoke;

import christofides.Christofides;
import inputs.AlgorithmInput;
import inputs.GlobalFunctions;
import org.json.simple.JSONObject;
import polyline_decoder.Point;
import utils.GoogleClient;
import utils.HTTPer;
import utils.JSONmatrix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmDriver {

    public static void main(String[] args) {
        AlgorithmInput input = AlgorithmInput.getInstance("algo-data/kml/Sderot-Route-2.kml", 0.003);

        double radiuses[] = {0.001, 0.002, 0.003};
        for (double radius : radiuses) {
            input.setRadius(radius);
            System.out.println("Starting algorithm with radius: " + input.getRadius());
            long startTime = System.currentTimeMillis();
            List<AlgorithmInput.Passenger> passengersToInclude = go(input);
            int answer = passengersToInclude.size();

            tsp(input, passengersToInclude);

            long endtime = System.currentTimeMillis();

            // TODO: From the passengers selected, There needs to be a TSP solution which will tell who to pick up first
			/* Need to:
			*		1. [V]	Extract the passengers from the function
			*		2. [ ]	Create an adjacency matrix between all of the points
			*			2.1 [V]	Use the Google API and the HTTPer class for creating an API query
			*			2.2 [ ]	Extract the matrix from the HTTP response
			*		3. [ ]	Plug the matrix as well as the source and destination to Christofides to calculate a path
			*	    4. [V]  Form a final path from source to destination using the Google Maps API with stops
			*
			* */

            System.out.println("Total number of passengers on route: " + answer +
                    ". Algorithm took: " + (endtime - startTime) + " ms\n\n");
        }

		System.out.println("----");
    }


    private static void tsp(AlgorithmInput input, List<AlgorithmInput.Passenger> passengersToInclude) {


		GoogleClient googleClient = new GoogleClient();
        JSONObject jsonObject = googleClient.adjacencyMatrixRequest(input.getSource(), passengersToInclude, input.getDestination());

		double [][] g = getMatrixFromJSON(jsonObject);

		Christofides c = null;
		try {
			c = new Christofides(g,false, 0, g.length-1);

			System.out.println(readResult(c, passengersToInclude));
			System.out.println("Check this out @ Google Maps: " + input.getResultOnGoogleMaps(passengersToInclude, c.getCircuit()));


		} catch (Exception e) {
			e.printStackTrace();
		}

	}


    /**
     * Main function of the algorithm
     * @param input Create an instance of AlgorithmInput using the GenerateInput class
     * */
    public static List<AlgorithmInput.Passenger> go(AlgorithmInput input) {
        List<AlgorithmInput.Passenger> passengers = new ArrayList<>();

        // For each passenger, check if its S and its T as circle points intersects with the
        // Driver's path
        List<Point> driverPath = input.getPathToDestination();
        double radius = input.getRadius();

        for (AlgorithmInput.Passenger passenger : input.getPassengers()) {
            Point si = passenger.s;
            Point ti = passenger.t;

            // Check if Si and Ti, with a given radius, intersects with the path
            boolean siIntersects = circleIntersectionWithPath(driverPath, si, radius);
//            boolean tiIntersects = circleIntersectionWithPath(driverPath, ti, radius);
            boolean tiIntersects = true;

            // TODO Temporary debug, for now just prints the passengers you need to include
            if (siIntersects && tiIntersects) {
                System.out.println("You should include: " + passenger );
                passengers.add(passenger);
            }

        }
        return passengers;

    }

	private static HTTPer getAdjacencyMatrixRequest(String source, List<AlgorithmInput.Passenger> passengers, String dest) {
		List<Point> pointList = new ArrayList<>();

		for(AlgorithmInput.Passenger p : passengers) {
			pointList.add(p.s);
		}


		try {
			HTTPer.Builder builder = new HTTPer.Builder();
			builder.setRootURL("https://maps.googleapis.com/maps/api/distancematrix/json");
			builder.setMethod("GET");


			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(source);
			stringBuilder.append("|");
			for(Point p : pointList) {
				stringBuilder.append(p.getLng());
				stringBuilder.append(",");
				stringBuilder.append(p.getLat());
				stringBuilder.append("|");
			}
//            stringBuilder.deleteCharAt(stringBuilder.length()-1);
			stringBuilder.append(dest);

			builder.addURLParameter("origins", stringBuilder.toString());
			builder.addURLParameter("destinations", stringBuilder.toString());
			builder.addURLParameter("key", "AIzaSyD56LHjhdpL7ztyU33rsph0zYYEY136nOo");

			HTTPer http = builder.build();

			return http;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

    private static double[][] getMatrixFromJSON(JSONObject jsonObject) {
        double g [][] = {
                {0, 0.4, 0.4, 1.5, 0.7, 1.4},
                {0.5, 0, 0.5, 1.1, 0.3, 1.0},
                {0.4, 0.4, 0, 1.4, 0.7, 1.3},
                {1.3, 1.1, 1.2, 0, 1.4, 0.3},
                {0.8, 0.4, 0.7, 0.8, 0, 0.8},
                {1.3, 1.0, 1.2, 0.3, 1.3, 0}
        };

		double matrix [][] = JSONmatrix.getMatrix(jsonObject);
		if (matrix != null) {
			return matrix;
		} else {
			System.out.println("BAD MATRIX");
			System.exit(1);
			return null;
		}

	}

    private static String readResult(Christofides christofides, List<AlgorithmInput.Passenger> passengers){
		StringBuilder stringBuilder = new StringBuilder();

    	List<Integer> circuit = christofides.getCircuit();

    	for (int i : circuit) {
			String nameOfNode = null;
			if (i == 0) {
//				nameOfNode = input.getSource();
				nameOfNode = "SOURCE";
			} else if (i == passengers.size()+1) {
//				nameOfNode = input.getDestination();
				nameOfNode = "DESTINATION";
			} else {
				nameOfNode = passengers.get(i-1).name;
			}
			stringBuilder.append(nameOfNode);
    		stringBuilder.append("->");
		}

    	return stringBuilder.toString();
	}

    private static boolean circleIntersectionWithPath(List<Point> path, Point point, double radius) {
        for (int i = 0; i < path.size() - 1; i++) {
            // Intersection must be between 2 points
            Point x = path.get(i);
            Point y = path.get(i+1);
            boolean intersection = LineCircleIntersection.intersect(x,y, point, radius);

            String circleDesmosString = "(x - " + point.getLat() + ")^2 + (y - " + point.getLng()
                    + ")^2 = (" + radius + ")^2";


            if (intersection) {
                debug("Intersection detected between circle: " + circleDesmosString + " and points: " +
                        x.toString() + " and " + y.toString());
                return true;
            } else {
                debug("No intersection between circle: " + circleDesmosString + " and points: " +
                        x.toString() + " and " + y.toString());
            }
        }
        return false;
    }

    private static void debug(String msg) {
        if (false) {
            System.out.println(msg);
        }
    }


}

package caraoke;

import christofides.Christofides;
import inputs.AlgorithmInput;
import org.json.simple.JSONObject;
import polyline_decoder.Point;
import utils.GoogleClient;
import utils.JSONmatrix;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmDriver {
    // TODO: Create a JSON which represent the results of the algorithm

    public static void main(String[] args) {
        AlgorithmInput input = AlgorithmInput.getInstance("algo-data/kml/Ashkelon-Route-1.kml", 0.003);

        double radiuses[] = {0.001, 0.002, 0.01};
        for (double radius : radiuses) {
            input.setRadius(radius);
            System.out.println("Starting algorithm with radius: " + input.getRadius());
            long startTime = System.currentTimeMillis();
            List<AlgorithmInput.Passenger> passengersToInclude = filterPassengers(input);
            int answer = passengersToInclude.size();

            tsp(input, passengersToInclude);

            long endtime = System.currentTimeMillis();


            System.out.println("Total number of passengers on route: " + answer +
                    ". Algorithm took: " + (endtime - startTime) + " ms\n\n");
        }

		System.out.println("----");
    }


    public static List<Point> tsp(AlgorithmInput input, List<AlgorithmInput.Passenger> passengersToInclude) {


		GoogleClient googleClient = new GoogleClient();

		// Currently fails because of inverted lat/lngs of the passengers
        JSONObject jsonObject = googleClient.adjacencyMatrixRequest(input.getSource(), passengersToInclude, input.getDestination());

		double [][] g = getMatrixFromJSON(jsonObject);

		Christofides c = null;
		try {
			c = new Christofides(g,false, 0, g.length-1);

			return c.getCircuitPoints(passengersToInclude, input);


//			System.out.println(readResult(c, passengersToInclude));
//			System.out.println("Check this out @ Google Maps: " + input.getResultOnGoogleMaps(passengersToInclude, c.getCircuit()));
//			System.out.println("Route: " + c.getCircuitString());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


    /**
     * Main function of the algorithm
     * @param input Create an instance of AlgorithmInput using the GenerateInput class
     * */
    public static List<AlgorithmInput.Passenger> filterPassengers(AlgorithmInput input) {
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

    	// TODO THIS FUNCTION NEEDS A RE-WRITE!!
		// FIGURE OUT HOW TO DISCOVER Si and Ti FROM THE CIRCUIT!
		// DONT CONTINUE TILL YOU DO
		// BACKWARDS COMPATABILITY ! WHAT IF TI IS NULL


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
				if (i-1 < passengers.size()) {
					nameOfNode = passengers.get(i-1).name;
				} else {
					System.out.println("figuring out what is: " + i + "in the route");
					nameOfNode = passengers.get((i-1)%passengers.size()).name;
				}
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

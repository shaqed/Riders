package caraoke;

import christofides.Christofides;
import inputs.AlgorithmInput;
import org.json.simple.JSONObject;
import utils.math.LineCircleIntersection;
import utils.math.LinePointIntersection;
import utils.polyline_decoder.Point;
import utils.net.GoogleClient;
import utils.parser.JSONmatrix;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmDriver {
    public static void main(String[] args) {
        AlgorithmInput input = AlgorithmInput.getInstance("algo-data/kml/Ashkelon-Route-1.kml", 0.003);

        double radiuses[] = {0.001, 0.002, 0.01};
        for (double radius : radiuses) {
            input.setRadius(radius);
            System.out.println("Starting algorithm with radius: " + input.getRadius());
            long startTime = System.currentTimeMillis();
            List<AlgorithmInput.Passenger> passengersToInclude = filterPassengers(input);
            int answer = passengersToInclude.size();

			try {
				tsp(input, passengersToInclude);
			} catch (Exception e) {
				e.printStackTrace();
			}

			long endtime = System.currentTimeMillis();


            System.out.println("Total number of passengers on route: " + answer +
                    ". Algorithm took: " + (endtime - startTime) + " ms\n\n");
        }

		System.out.println("----");
    }


    public static List<Point> tsp(AlgorithmInput input, List<AlgorithmInput.Passenger> passengersToInclude) throws Exception {
		GoogleClient googleClient = new GoogleClient();
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
     * Part of the main function of the algorithm.
	 * The part of filtering passengers
     * @param input Create an instance of AlgorithmInput using the GenerateInput class
	 * @return List of Passengers that you should include based on the given radius
     * */
    public static List<AlgorithmInput.Passenger> filterPassengers(AlgorithmInput input) {
        List<AlgorithmInput.Passenger> passengers = new ArrayList<>();

        for (AlgorithmInput.Passenger passenger : input.getPassengers()) {
            if (includePassenger(passenger, input)) {
                System.out.println("You should include: " + passenger);
                passengers.add(passenger);
            }
        }
        return passengers;

    }


    /**
     * Checks if a given passenger is within the route of the input
     * @return -1 if the answer is false, or the index of which the passenger should be included
     * */
    public static boolean includePassenger(AlgorithmInput.Passenger passenger, AlgorithmInput input) {
        double radius = input.getRadius();
        List<Point> driverPath = input.getPathToDestination();
        Point si = passenger.s;
        Point ti = passenger.t;

        // Check if Si and Ti, with a given radius, intersects with the path
        int siIntersectionIndex = circleIntersectionWithPath(driverPath, si, radius);
        boolean siIntersects = siIntersectionIndex != -1;

        int tiIntersectionIndex = circleIntersectionWithPath(driverPath, ti, radius);
        boolean tiIntersects = tiIntersectionIndex != -1;


        boolean siBeforeTi = siIntersectionIndex < tiIntersectionIndex;

        if (siIntersectionIndex == tiIntersectionIndex) {
            // Both Si and Ti intersect the same line
            // Check aerial distance to A

            double distanceFromAToSi = distanceBetweenTwoPoints(input.getpSource(), si);
            double distanceFromAToTi = distanceBetweenTwoPoints(input.getpSource(), ti);

//				System.out.println("A-Si: " + distanceFromAToSi + " A-Ti: " + distanceFromAToTi);
            siBeforeTi = distanceFromAToSi < distanceFromAToTi; // Change siBeforeTi for linear line

        }

        // Final check
        // siBeforeTi = si closer to A than Ti AND si was found before Ti
        return (siIntersects && tiIntersects && siBeforeTi);
    }

    private static double[][] getMatrixFromJSON(JSONObject jsonObject) throws Exception {
        double g [][] = {
                {0, 0.4, 0.4, 1.5, 0.7, 1.4},
                {0.5, 0, 0.5, 1.1, 0.3, 1.0},
                {0.4, 0.4, 0, 1.4, 0.7, 1.3},
                {1.3, 1.1, 1.2, 0, 1.4, 0.3},
                {0.8, 0.4, 0.7, 0.8, 0, 0.8},
                {1.3, 1.0, 1.2, 0.3, 1.3, 0}
        };

		double matrix [][] = JSONmatrix.getMatrix(jsonObject);
		if (matrix != null && matrix.length > 0) {
			return matrix;
		} else {
			throw new Exception("Couldn't create a Distances Matrix from Google Maps API");
		}

	}

	// TODO: Move these two functions to utils.math somewhere
	private static double distanceBetweenTwoPoints(Point point1, Point point2) {
    	double x1 = point1.getLat();
    	double y1 = point1.getLng();

    	double x2 = point2.getLat();
    	double y2 = point2.getLng();

    	return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

    private static int circleIntersectionWithPath(List<Point> path, Point point, double radius) {
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
                return i;
            } else {
                debug("No intersection between circle: " + circleDesmosString + " and points: " +
                        x.toString() + " and " + y.toString());
            }
        }
        return -1;
    }

    private static void debug(String msg) {
        if (true) {
            System.out.println(msg);
        }
    }


}

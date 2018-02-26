package caraoke;

import christofides.Christofides;
import inputs.AlgorithmInput;
import polyline_decoder.Point;
import utils.HTTPer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmDriver {

    public static void main(String[] args) {
        AlgorithmInput input = AlgorithmInput.getInstance("algo-data/kml/Sderot-Route-1.kml", 0.003);

        double radiuses[] = {0.001, 0.002, 0.003};
        for (double radius : radiuses) {
            input.setRadius(radius);
            System.out.println("Starting algorithm with radius: " + input.getRadius());
            long startTime = System.currentTimeMillis();
            int answer = go(input);
            long endtime = System.currentTimeMillis();

            System.out.println("Total number of passengers on route: " + answer +
                    ". Algorithm took: " + (endtime - startTime) + " ms\n\n");
        }

		System.out.println("----");
        tsp(input);
    }


    private static void tsp(AlgorithmInput input) {
    	List<Point> pointList = new ArrayList<>();

		pointList.add(input.getPathToDestination().get(0));

    	for(AlgorithmInput.Passenger p : input.getPassengers()) {
    		pointList.add(p.s);
		}

    	pointList.add(input.getPathToDestination().get(input.getPathToDestination().size()-1));

		// TEST
		try {
			HTTPer.Builder builder = new HTTPer.Builder();
			builder.setRootURL("https://maps.googleapis.com/maps/api/distancematrix/json");
			builder.setMethod("GET");


			List<Point> myPoints = new ArrayList<>();
			myPoints.add(new Point(40.924044,-74.698135));
			myPoints.add(new Point(40.905392,-74.707945));

			StringBuilder stringBuilder = new StringBuilder();
			for(Point p : pointList) {
				stringBuilder.append(p.getLng());
				stringBuilder.append(",");
				stringBuilder.append(p.getLat());
				stringBuilder.append("|");
			}
			stringBuilder.deleteCharAt(stringBuilder.length()-1);


			builder.addURLParameter("origins", stringBuilder.toString());
			builder.addURLParameter("destinations", stringBuilder.toString());
			builder.addURLParameter("key", "AIzaSyD56LHjhdpL7ztyU33rsph0zYYEY136nOo");

			HTTPer http = builder.build();

			System.out.println("Debugging: " + http.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}


		double g [][] = {
				{0, 0.4, 0.4, 1.5, 0.7, 1.4},
				{0.5, 0, 0.5, 1.1, 0.3, 1.0},
				{0.4, 0.4, 0, 1.4, 0.7, 1.3},
				{1.3, 1.1, 1.2, 0, 1.4, 0.3},
				{0.8, 0.4, 0.7, 0.8, 0, 0.8},
				{1.3, 1.0, 1.2, 0.3, 1.3, 0}
		};

		Christofides c = new Christofides(g);
		System.out.println(readResult(c, input));

		// END TEST

		Christofides christofides = new Christofides(pointList, false);
		System.out.println("TSP: " + readResult(christofides, input) + ": " + christofides.getCircuitCost());

	}


    /**
     * Main function of the algorithm
     * @param input Create an instance of AlgorithmInput using the GenerateInput class
     * */
    public static int go(AlgorithmInput input) {
        int numOfPassengersToCollect = 0;

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
                numOfPassengersToCollect++;
            }

        }
        return numOfPassengersToCollect;

    }


    private static String readResult(Christofides christofides, AlgorithmInput input){
		StringBuilder stringBuilder = new StringBuilder();

    	List<Integer> circuit = christofides.getCircuit();
    	List<AlgorithmInput.Passenger> passengers = input.getPassengers();

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

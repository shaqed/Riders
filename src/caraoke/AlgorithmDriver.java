package caraoke;

import christofides.Christofides;
import inputs.AlgorithmInput;
import polyline_decoder.Point;

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

    	for(AlgorithmInput.Passenger p : input.getPassengers()) {
    		pointList.add(p.s);
		}

		Christofides christofides = new Christofides(pointList, false);
		System.out.println("TSP: " + christofides.getCircuitString() + ": " + christofides.getCircuitCost());

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

package caraoke;

import inputs.AlgorithmInput;
import inputs.GenerateInput;
import inputs.GetPointsFromJSON;
import polyline_decoder.Point;

import java.util.List;

public class AlgorithmDriver {

    public static void main(String[] args) {
        AlgorithmInput input = GenerateInput.getInput();

        double radiuses[] = {0.003, 0.006, 0.012};
        for (double radius : radiuses) {
            input.setRadius(radius);
            System.out.println("Starting algorithm with radius: " + input.getRadius());
            long startTime = System.currentTimeMillis();
            int answer = go(input);
            long endtime = System.currentTimeMillis();

            System.out.println("Total number of passengers on route: " + answer +
                    ". Algorithm took: " + (endtime - startTime) + " ms\n\n");
        }
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

            // TODO Temporary output, for now just prints the passengers you need to include
            if (siIntersects && tiIntersects) {
                output("You should include: " + passenger + "\n");
                numOfPassengersToCollect++;
            }

        }
        return numOfPassengersToCollect;

    }

    private static void output(String msg) {
        System.out.println(msg);
    }

    private static boolean circleIntersectionWithPath(List<Point> path, Point point, double radius) {
        for (int i = 0; i < path.size() - 1; i++) {
            // Intersection must be between 2 points
            Point x = path.get(i);
            Point y = path.get(i+1);
            boolean intersection = LineCircleIntersection.intersect(x,y, point, radius);
            if (intersection) {

                // Debug
                if (true){
//                    System.out.println("Intersection found between: " + x + " and " + y);
                }

                return true;
            }
        }
        return false;
    }

}

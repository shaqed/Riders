package caraoke;

import inputs.AlgorithmInput;
import inputs.GenerateInput;
import polyline_decoder.Point;

import java.util.List;

public class AlgorithmDriver {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        go(GenerateInput.getInput());
        long endtime = System.currentTimeMillis();

        System.out.println("Took: " + (endtime - startTime) + " ms");
    }



    /**
     * Main function of the algorithm
     * @param input Create an instance of AlgorithmInput using the GenerateInput class
     * */
    public static void go(AlgorithmInput input) {

        // For each passenger, check if its S and its T as circle points intersects with the
        // Driver's path
        List<Point> driverPath = GetPointsFromJSON.getPoints(input.getPathToDestination());
        double radius = input.getRadius();

        for (AlgorithmInput.Passenger passenger : input.getPassengers()) {
            Point si = passenger.s;
            Point ti = passenger.t;

            // Check if Si and Ti, with a given radius, intersects with the path
            boolean siIntersects = circleIntersectionWithPath(driverPath, si, radius);
            boolean tiIntersects = circleIntersectionWithPath(driverPath, ti, radius);


            // TODO Temporary output, for now just prints the passengers you need to include
            if (siIntersects && tiIntersects) {
                output("You should include: " + passenger);
            }

        }


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
                return true;
            }
        }
        return false;
    }

}

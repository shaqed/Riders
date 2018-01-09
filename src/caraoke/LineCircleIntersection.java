package caraoke;

import polyline_decoder.Point;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LineCircleIntersection {


    // Test of the functions
    public static void main(String[] args) {
        Point point1 = new Point(0,0);
        Point point2 = new Point(20, 20);
        Point pointCircle = new Point(2, 2);
        double radiusCircle = 16;

        System.out.println(intersect(point1, point2, pointCircle, radiusCircle));
    }







    public static boolean intersect(polyline_decoder.Point point1, polyline_decoder.Point point2, polyline_decoder.Point pointCircle, double radius) {

        return intersect(new Point(point1.getLat(), point1.getLng()),
                new Point(point2.getLat(), point2.getLng()),
                new Point(pointCircle.getLat(), pointCircle.getLng()),
                radius);

    }

    /**
     * Check if a circle with a given radius intersects a given line between 2 points
     *
     * */
    public static boolean intersect(Point point1, Point point2, Point pointCircle, double radiusCircle) {
        // Calculate result
        List<Point> answer = getCircleLineIntersectionPoint(point1, point2, pointCircle, radiusCircle);
        if (answer != null) {
            for (Point p : answer) {
                // TODO WHAT HAPPENS IF LINE IS ENTIRELY INSIDE THE CIRCLE
                if (Math.min(point1.x, point2.x) <= p.x && p.x <= Math.max(point1.x ,point2.x)){ // is on the line
                    if (point1.x != point2.x) {

                        System.out.println("DEBUG: Found intersection " + point1.toString() + " " + point2.toString() + " and circle: " + pointCircle.toString());

                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }

    }




    private static List<Point> getCircleLineIntersectionPoint(Point pointA, Point pointB, Point center, double radius) {
        double baX = pointB.x - pointA.x;
        double baY = pointB.y - pointA.y;
        double caX = center.x - pointA.x;
        double caY = center.y - pointA.y;

        double a = baX * baX + baY * baY;
        double bBy2 = baX * caX + baY * caY;
        double c = caX * caX + caY * caY - radius * radius;

        double pBy2 = bBy2 / a;
        double q = c / a;

        double disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return null;
        }
        // if disc == 0 ... dealt with later
        double tmpSqrt = Math.sqrt(disc);
        double abScalingFactor1 = -pBy2 + tmpSqrt;
        double abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }

    static class Point {
        double x, y;

        public Point(double x, double y) { this.x = x; this.y = y; }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

}

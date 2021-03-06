package utils.math;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LineCircleIntersection {


	// Test of the functions
	public static void main(String[] args) {
		Point point1 = new Point(0, 0);
		Point point2 = new Point(4, 4);
		Point pointCircle = new Point(2, 2);
		double radiusCircle = 4;

		System.out.println(intersect(point1, point2, pointCircle, radiusCircle));
	}

	public static boolean verbose = false;


	public static boolean intersect(utils.polyline_decoder.Point point1, utils.polyline_decoder.Point point2, utils.polyline_decoder.Point pointCircle, double radius) {

		return intersect(new Point(point1.getLat(), point1.getLng()),
				new Point(point2.getLat(), point2.getLng()),
				new Point(pointCircle.getLat(), pointCircle.getLng()),
				radius);

	}

	/**
	 * Check if a circle with a given radius intersects a given line between 2 points
	 */
	public static boolean intersect(Point point1, Point point2, Point pointCircle, double radiusCircle) {
		// Calculate result
		List<Point> answer = getCircleLineIntersectionPoint(point1, point2, pointCircle, radiusCircle);
		if (answer != null) {
			if (answer.size() == 2) {

				Point intersectionPoint1 = answer.get(0);
				Point intersectionPoint2 = answer.get(1);

				// Since this is a line, number of intersection points cannot be greater than 2

				boolean lineCrossesPerimeter =
						// The X of the first intersection point is between the points given
						(Math.min(point1.x, point2.x) <= intersectionPoint1.x && intersectionPoint1.x <= Math.max(point1.x, point2.x))
						|| // or the X of the second intersection point
						(Math.min(point1.x, point2.x) <= intersectionPoint2.x && intersectionPoint2.x <= Math.max(point1.x, point2.x));



				boolean lineInsideCircle =
						// Leftmost X is still greater than intersection point
						Math.min(intersectionPoint1.x, intersectionPoint2.x) <= Math.min(point1.x, point2.x)
						&& // And, Rightmost X is still smaller than intersection point
								Math.max(point1.x, point2.x) <= Math.max(intersectionPoint1.x, intersectionPoint2.x);

				if (lineCrossesPerimeter || lineInsideCircle) { // crosses the perimeter or entirely inside the circle
					if (point1.x != point2.x) {
						return true;
					}

				}
			} else if (answer.size() == 1) {
				// Tangent line ?
			} else {
				// Error, unexpected number of intersection points
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


	public static boolean circleIntersectionWithPath(List<utils.polyline_decoder.Point> path, utils.polyline_decoder.Point point, double radius) {
		for (int i = 0; i < path.size() - 1; i++) {
			// Intersection must be between 2 points
			utils.polyline_decoder.Point x = path.get(i);
			utils.polyline_decoder.Point y = path.get(i+1);
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
		if (verbose) {
			System.out.println(msg);
		}
	}

	static class Point {
		double x, y;

		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}

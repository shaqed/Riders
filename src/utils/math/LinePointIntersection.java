package utils.math;

import utils.polyline_decoder.Point;

public class LinePointIntersection {


	/**
	 *
	 * @param p1 Start of line
	 * @param p2 End of line
	 * @param point The point
	 * */
	public static double distanceFromLineToPoint(Point p1, Point p2, Point point) {
		double x1 = p1.getLat();
		double y1 = p1.getLng();

		double x2 = p2.getLat();
		double y2 = p2.getLng();

		double x0 = point.getLat();
		double y0 = point.getLng();

		double a = Math.abs((y2-y1)*x0 - y0*(x2-x1) + x2*y1 - y2*x1);
		double b = Math.sqrt(Math.pow(y2-y1, 2) + Math.pow(x2-x1,2));
		return a/b;
	}




	public static void main(String[] args) {
		Point p1 = new Point(1,1);
		Point p2 = new Point(5, 5);
		Point target = new Point(1,5);

		System.out.println(distanceFromLineToPoint(p1, p2, target));
	}

}

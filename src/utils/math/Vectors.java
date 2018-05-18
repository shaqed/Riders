package utils.math;

import utils.polyline_decoder.Point;

public class Vectors {
	public static double[] computeVector(Point p1, Point p2) {
		double vector[] = new double[2];
		vector[0] = p2.getLat() - p1.getLat();
		vector[1] = p2.getLng() - p2.getLng();
		return vector;
	}
}

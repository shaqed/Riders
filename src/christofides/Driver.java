package christofides;

import utils.polyline_decoder.Point;

import java.util.ArrayList;
import java.util.List;

public class Driver {


	public static void main(String[] args) {
		test4();
	}

	private static void test() {
		System.out.println(System.getProperty("user.dir"));
	}

	private static void testPoints2() {
		List<Point> points = new ArrayList<>();
		points.add(new Point(1,1)); // a
		points.add(new Point(1,2)); // b
		points.add(new Point(2,1)); // c
		points.add(new Point(2,2)); // d

		Christofides christofides = new Christofides(points, false);
		String circuit = christofides.getCircuitString();
		double cost = christofides.getCircuitCost();

		System.out.println("Circuit: " + circuit + " cost: " + cost);

	}

	private static void testPoints() {
		List<Point> points = new ArrayList<>();


		points.add(new Point(1,1));
		points.add(new Point(1,5));
		points.add(new Point(2,3));
		points.add(new Point(4,2));
		points.add(new Point(5,5));

		double G [][] = Christofides.convertPointsToGraph(points);
		Christofides christofides = new Christofides(G);

		String answer = christofides.getCircuitString() + ": " + christofides.getCircuitCost();
		System.out.println(answer);
	}


	private static void test4() {
		double graph[][] = {
				{0 ,0.5, 1.1, 0.3},
				{0.4, 0, 1.4, 0.7},
				{1.1, 1.2, 0, 1.4},
				{0.4, 0.7, 0.8, 0}

		};

		Christofides christofides = new Christofides(Christofides.floydWarshall(graph));
		System.out.println(christofides.getCircuitString() + ": " + christofides.getCircuitCost());

	}

	private static void test3(){
		double[][] graph = new double[][]{
				{0, 10, 15, 20},
				{10, 0, 35, 25},
				{15, 35, 0, 30},
				{20, 25, 30, 0}
		};

		double distances[][] = Christofides.floydWarshall(graph);
		Christofides christofides = new Christofides(distances);

		List<Integer> circuit = christofides.go();
		double cost = christofides.calculatePathCost(circuit);

		System.out.println("Circuit: " + circuit.toString() + " Cost: " + cost);
	}

	private static void test2() throws Exception {
		double [][] graph = new double[][]{

//						A		B		C		D		E		F		G
				/*A*/	{0,		1,		0,		1,		0,		0,		0},
				/*B*/	{1,		0,		1,		1,		0,		1,		0},
				/*C*/	{0,		1,		0,		1,		1,		1,		0},
				/*D*/	{1,		1,		1,		0,		1,		0,		0},
				/*E*/	{0,		0,		1,		1,		0,		1,		1},
				/*F*/	{0,		1,		1,		0,		1,		0,		1},
				/*G*/	{0,		0,		0,		0,		1,		1,		0},
		};

		double distances [][] = Christofides.floydWarshall(graph);
		Christofides christofides = new Christofides(distances);

		christofides.go();

	}

	private static void test1() {
		int X = Integer.MAX_VALUE/4;

		int someMST[][] = {
				{0, 0, 1, 0, 0},
				{0, 0, 1, 0, 0},
				{1, 1, 0, 1, 1},
				{0, 0, 1, 0, 0},
				{0, 0, 1, 0, 0}

		};
	}
}

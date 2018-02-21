package christofides;

import polyline_decoder.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Driver {


	public static void main(String[] args) {

		testPoints();

	}




	private static void testPoints() {
		List<Point> points = new ArrayList<>();


		points.add(new Point(1,1));
		points.add(new Point(1,5));
		points.add(new Point(2,3));
		points.add(new Point(4,2));
		points.add(new Point(5,5));

		int G [][] = Christofides.convertPointsToGraph(points);
		Christofides christofides = new Christofides(G);

		String answer = christofides.getCircuitString() + ": " + christofides.getCircuitCost();
		System.out.println(answer);
	}


	private static void test3(){
		int[][] graph = new int[][]{
				{0, 10, 15, 20},
				{10, 0, 35, 25},
				{15, 35, 0, 30},
				{20, 25, 30, 0}
		};

		int distances[][] = Christofides.floydWarshall(graph);
		Christofides christofides = new Christofides(distances);

		List<Integer> circuit = christofides.go();
		int cost = christofides.calculatePathCost(circuit);

		System.out.println("Circuit: " + circuit.toString() + " Cost: " + cost);
	}

	private static void test2() throws Exception {
		int [][] graph = new int[][]{

//						A		B		C		D		E		F		G
				/*A*/	{0,		1,		0,		1,		0,		0,		0},
				/*B*/	{1,		0,		1,		1,		0,		1,		0},
				/*C*/	{0,		1,		0,		1,		1,		1,		0},
				/*D*/	{1,		1,		1,		0,		1,		0,		0},
				/*E*/	{0,		0,		1,		1,		0,		1,		1},
				/*F*/	{0,		1,		1,		0,		1,		0,		1},
				/*G*/	{0,		0,		0,		0,		1,		1,		0},
		};

		int distances [][] = Christofides.floydWarshall(graph);
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

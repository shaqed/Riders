package christofides;

import polyline_decoder.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of the Christofides algorithm for solving the Traveling Salesman Problem.
 * The implementation can create a Hamiltonian Path as well as a cycle.
 * How to work with this class:
 * 		1. Create an instance using one of the constructors (note that using the constructor without
 * 	The source/dest parameters will create a cycle not a path.
 * 	Also note that the computation is being done in the constructor
 * 		2. Call one of the Getter methods to get your results. Note that the circuit is also the path if
 * 	That's what you selected in the first place
 *
 * */
public class Christofides {

	private double graph[][];
	private boolean verbose = false;

	private List<Integer> circuit;

	/* CONSTRUCTORS THAT BUILD A HAMILTONIAN CYCLE */

	public Christofides(double[][] graph) {
		this.graph = graph;
		this.circuit = go();
	}

	/**
	 * @param graph A fully connected graph only as an adjacency matrix.
	 *              You can use the Floyd Warshall static method to compute that
	 *              from a non-completely connected graph
	 * @param verbose Messages to the output stream
	 * */
	public Christofides(double[][] graph, boolean verbose) {
		this.graph = graph;
		this.verbose = verbose;
		this.circuit = go();
	}

	public Christofides(List<Point> points, boolean verbose) {
		this.verbose = verbose;
		this.graph = convertPointsToGraph(points);
		this.circuit = go();
	}

	/* CONSTRUCTORS THAT BUILD A HAMILTONIAN PATH */

	public Christofides(List<Point> points, boolean verbose, int source, int dest) throws Exception {
		this.verbose = verbose;
		this.graph = convertPointsToGraph(points);
		this.circuit = go(source, dest);
	}

	public Christofides(double graph[][], boolean verbose, int source, int dest) throws Exception {
		this.verbose = verbose;
		this.graph = graph;
		this.circuit = go(source, dest);
	}

	public List<Integer> getCircuit() {
		return circuit;
	}

	public double getCircuitCost() {
		return calculatePathCost(this.circuit);
	}

	public String getCircuitString() {
		StringBuilder stringBuilder = new StringBuilder();


		for (int node : this.circuit) {
			stringBuilder.append(((char) (node + 65)));
		}


		return stringBuilder.toString();
	}


	/**
	 * Creates an Hamiltonian cycle from the graph loaded to the class
	 * */
	public List<Integer> go() {
		// Create MST from the graph
		double [][] mst = new Prim(this.graph).go(1);
		debug("MST:");
//		printGraph(mst);

		// Find out all the vertices with odd degrees and store them in a list
		List<Integer> oddVertices = findOddVerticesInGraph(mst);
		debug("Odd vertices is mst are: " + oddVertices.toString());

		// Form a new graph only with those vertices
		// (edges will change to keep the triangle inequality)
		double onlyOddsGraph [][] = createSubGraphIncluding(oddVertices);
		debug("Sub graph with these vertices");
//		printGraph(onlyOddsGraph);

		// Get perfect matching
		List<List<Double>> tuples = new PerfectMatch().go(onlyOddsGraph, oddVertices);
		debug("Got the following tuples: ");
//		printGraph(tuples);


		// Add it to the MST: AND CONSTRUCT A MULTIGRAPH!
		List<List<Integer>> multiGraph = addEdgesToGraph(mst, tuples);
		debug("Adding them to the MST is the following multigraph");
//		printGraph(multiGraph);

		// Find Euler circuit on that graph
		List<Integer> eulerCircuit = new EulerCircuit().go(multiGraph);

		// Remove duplicated nodes
		List<Integer> hamiltonianCircuit = new ArrayList<>();
		for (int node: eulerCircuit) {
			if (!hamiltonianCircuit.contains(node)) {
				hamiltonianCircuit.add(node);
			}
		}
		hamiltonianCircuit.add(hamiltonianCircuit.get(0));


		double routeSum = 0;
		for (int i = 0; i < hamiltonianCircuit.size()-1; i++) {
			routeSum += this.graph[hamiltonianCircuit.get(i)][hamiltonianCircuit.get(i+1)];
		}

		// There's your Traveling Salesman solution
		System.out.println("Fin: " + hamiltonianCircuit.toString() + ". sum: " + routeSum);
		return hamiltonianCircuit;
	}

	/**
	 * Creates an Hamiltonian path from the graph loaded to the class
	 * @param source Index of the source vertex
	 * @param dest Index of the destination vertex
	 * */
	public List<Integer> go(int source, int dest) throws Exception {

		double mst[][] = new Prim(this.graph).go(0);

		debug("MST:");
		printGraph(mst);


		List<Integer> oddVertices = findOddVerticesInGraph(mst);

		debug("Odd vertices before: " + oddVertices.toString());

		oddVertices.remove(new Integer(source));
		oddVertices.remove(new Integer(dest));

		debug("Odd vertices after: " + oddVertices.toString());

		double oddsOnlyGraph[][] = createSubGraphIncluding(oddVertices);

		List<List<Integer>> multiGraph = new PerfectMatch().goOdd(mst, oddVertices, source, dest);

		debug("Multigraph:");
		if (verbose) {
			printGraph(multiGraph);
		}


		List<Integer> eulerPath = new EulerPath(multiGraph).getPath();

		List<Integer> hamiltonianPath = new ArrayList<>();
		for (int u : eulerPath) {
			if (!hamiltonianPath.contains(u)) {
				hamiltonianPath.add(u);
			}
		}

		return hamiltonianPath;
	}


	/**
	 * TODO function
	 * 		1. Start regularly up until the computation of perfect matching
	 * 		2. Add 2 vertices, s and t connect them with each other and with source and dest
	 * 		3. Do euler path on that graph
	 * 		4. Remove the s and t
	 * */
	public List<Integer> getHamiltonianPath(int source, int dest) throws Exception {

		// Get MST of the graph
		double mst [][] = new Prim(this.graph).go(0);

		// Perfect matching
		// Get edges to add
		List<List<Double>> tuples = new PerfectMatch().go(mst, findOddVerticesInGraph(mst));
		// Add them to the graph
		List<List<Integer>> multiGraph = addEdgesToGraph(mst, tuples);


		int lastVertex = this.graph.length - 1;

		// Add vertex A and attach it to the source
		// Connect A and B with an edge
		List<Integer> vertexA = new ArrayList<>();
		vertexA.add(0); // Add vertex source
		vertexA.add(lastVertex + 2); // Add vertex B

		// Add vertex B and attach it to the destination
		List<Integer> vertexB = new ArrayList<>();
		vertexB.add(lastVertex); // Add vertex dest
		vertexB.add(lastVertex + 1); // Add vertex A

		multiGraph.add(vertexA);
		multiGraph.add(vertexB);

		// Get Euler Path from that graph

		List<Integer> eulerPath = new EulerPath(multiGraph).getPath();

		// Hamiltonian path
		List<Integer> hamiltonian = computeHamiltonian(eulerPath);

		return hamiltonian;
	}

	// PUBLIC HELPER FUNCTIONS

	/**
	 * @param result The answer from the go() function (the hamiltonian circuit)
	 * @return The cost of traveling to each node based on the graph
	 * */
	public double calculatePathCost(List<Integer> result) {
		double routeSum = 0;
		for (int i = 0; i < result.size()-1; i++) {
			routeSum += this.graph[result.get(i)][result.get(i+1)];
		}
		return routeSum;
	}



	// GLOBAL UTILITY FUNCTIONS

	/**
	 * Floyd Warshall Algorithm for multiple-source-shortest-path problem
	 *
	 * @param graph A graph to compute the algorithm on. NOTE: THE GRAPH IS NOT CHANGED IN THE PROCESS!
	 * @return A matrix representing the shortest paths from index i to index j
	 * */
	public static double[][] floydWarshall(double[][] graph) {

		// Since the original graph will not be changed, we're creating a new one
		int V = graph.length;
		double shortDistancesMatrix[][] = new double[V][V];
		int INF = Integer.MAX_VALUE/4;
		for (int i = 0; i < V; i++) {
			for (int j = 0; j < V; j++) {
				if (i == j) {
					shortDistancesMatrix[i][j] = 0;

				} else if (graph[i][j] == 0) {
					// i and j are NOT equal, yet the graph say 0
					// So there's no direct edge between the two
					// For the purpose of the algorithm, apply INF
					shortDistancesMatrix[i][j] = INF;
				} else {
					// i and j are NOT equal
					// And there's a direct edge between them
					// Take the value from the graph
					shortDistancesMatrix[i][j] = graph[i][j];
				}

			}
		}

		// Apply Floyd Warshall algorithm on the slightly-changed graph
		for (int k = 0; k < V; k++) {
			for (int i = 0; i < V; i++) {
				for (int j = 0; j < V; j++) {
					if (shortDistancesMatrix[i][k] + shortDistancesMatrix[k][j] < shortDistancesMatrix[i][j]) {
						shortDistancesMatrix[i][j] = shortDistancesMatrix[i][k] + shortDistancesMatrix[k][j];
					}
				}
			}
		}

		return shortDistancesMatrix;
	}


	public static double[][] convertPointsToGraph(List<Point> points) {
		int V = points.size();
		double [][] graph = new double[V][V];

		for (int u = 0; u < points.size(); u++) {
			for (int v = 0; v < points.size(); v++) {

				Point pointU = points.get(u);
				Point pointV = points.get(v);

				graph[u][v] = (distanceBetweenTwoPoints(pointU, pointV));

			}
		}

		return graph;
	}



	// PRIVATE HELPER FUNCTIONS

	private static List<Integer> computeHamiltonian(List<Integer> eulerPath) {
		List<Integer> hamiltonian = new ArrayList<>();
		for (int u : eulerPath) {
			if (!hamiltonian.contains(u)) {
				hamiltonian.add(u);
			}
		}

		return hamiltonian;
	}

	private static double distanceBetweenTwoPoints(Point p1, Point p2) {
		double x1 = p1.getLat();
		double y1 = p1.getLng();

		double x2 = p2.getLat();
		double y2 = p2.getLng();

		double a1 = Math.pow((x1-x2), 2);
		double a2 = Math.pow((y1-y2), 2);

		return Math.sqrt(a1+a2);
	}

	/**
	 * Creates a sub graph of the original graph from a set of vertices
	 * @param vertices The indexes of the vertices to include from the original graph
	 * @return An adjacency matrix with the SAME SIZE OF THE INPUT.
	 * 			Except that vertices which were removed will have all of their edges marked as INF
	 * */
	private double[][] createSubGraphIncluding(List<Integer> vertices) {
		int V = this.graph.length;
		final int INF = Integer.MAX_VALUE;
		double distances[][] = floydWarshall(this.graph);

		// Create a subgraph, first all edges do not exist
		double subgraph[][] = new double[V][V];
		for (int i = 0; i < V; i++) {
			for (int j = 0; j < V; j++) {
				subgraph[i][j] = INF;
			}
		}

		// After we've removed all vertices from the graph

		for(int u : vertices) { // For each vertex we want to keep
			for (int i : vertices) { // For each other vertex in the graph
				subgraph[u][i] = distances[u][i]; // Shortest path to it without that vertex i
			}
		}

		return subgraph;
	}

	/**
	 * @param graph Adjacency matrix of a graph (in practice, this is the MST)
	 * @param tuples The result from the PerfectMatch algorithm
	 * @return A new multi graph represented with an adjacency list
	 * */
	private List<List<Integer>> addEdgesToGraph(double graph[][], List<List<Double>> tuples) {
		List<List<Integer>> adjacencyList = new ArrayList<>();
		for (int i = 0; i < graph.length; i++) {
			adjacencyList.add(new ArrayList<>());
		}


		// Convert the matrix to the list
		for (int u = 0; u < graph.length; u++) {
			for (int v = 0; v < graph.length; v++) {
				if (graph[u][v] != 0) {
					adjacencyList.get(u).add(v); // add 'v' as a neighbor of 'u'
				}
			}
		}

		// Add the extras
		for(List<Double> tuple : tuples) {
			int u = tuple.get(0).intValue();
			int v = tuple.get(1).intValue();
			// Add the edge on both ends
			adjacencyList.get(u).add(v);
			adjacencyList.get(v).add(u);
		}


		return adjacencyList;
	}

	private List<Integer> findOddVerticesInGraph(double graph[][]) {
		List<Integer> oddVertices = new ArrayList<>();

		for (int i = 0; i < graph.length; i++) {
			// for each vertex i, check if its degree is odd or not

			int degree = 0;
			for (int j = 0; j < graph.length; j++) {
				if (graph[i][j] != 0) {
					degree++;
				}
			}

			if (degree % 2 != 0) {
				oddVertices.add(i);
			}
		}

		return oddVertices;
	}

	private void debug(String msg) {
		if (verbose) {
			System.out.println(msg);
		}
	}

	private void printGraph(double g[][]) {
		if (verbose) {
			for (double[] a : g) {
				System.out.println(Arrays.toString(a));
			}
		}
	}

	private static void printGraph(List<List<Integer>> graph) {
		for (List<Integer> node : graph) {
			System.out.println(node.toString());
		}
	}


	public static void main(String[] args) {
		// Graph from the wikipedia page
		double g[][] = {
//				A	B	C	D	E
				{0,	1,	1,	1,	2},
				{1,	0,	1,	2,	1},
				{1,	1,	0,	1,	1},
				{1,	2,	1,	0,	1},
				{2,	1,	1,	1,	0}
		};

		try {
//			Christofides christofides = new Christofides(g, true, 1, 4);
			Christofides christofides = new Christofides(g, true);

			System.out.println(christofides.go(1, 4).toString());

//			System.out.println(christofides.getCircuitString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int linorFunction() {
		// TODO: a bunch of stuff
		return 0;
	}

}

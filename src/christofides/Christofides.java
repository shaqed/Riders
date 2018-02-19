package christofides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Implementation of the Christofides algorithm for solving the Traveling Salesman Problem
 * How to work with this class:
 * 		1. Create an instance of the class with an adjacency matrix of a fully connected graph.
 * 			If you do not have that, you may use the FloydWarshall() static method of that class to compute a complete graph
 * 		2. Run the go() function to receive the desired circuit
 * 		3. You can also call the calculatePathCost() method to get the cost of your circuit
 *
 * */
public class Christofides {

	private int graph[][];
	private boolean verbose = false;

	public Christofides(int[][] graph) {
		this.graph = graph;
	}

	/**
	 * @param graph A fully connected graph only as an adjacency matrix.
	 *              You can use the Floyd Warshall static method to compute that
	 *              from a non-completely connected graph
	 * @param verbose Messages to the output stream
	 * */
	public Christofides(int[][] graph, boolean verbose) {
		this.graph = graph;
		this.verbose = verbose;
	}

	public List<Integer> go() {
		// Create MST from the graph
		int [][] mst = new Prim(this.graph).go(1);
		debug("MST:");
//		printGraph(mst);

		// Find out all the vertices with odd degrees and store them in a list
		List<Integer> oddVertices = findOddVerticesInGraph(mst);
		debug("Odd vertices is mst are: " + oddVertices.toString());

		// Form a new graph only with those vertices
		// (edges will change to keep the triangle inequality)
		int onlyOddsGraph [][] = createSubGraphIncluding(oddVertices);
		debug("Sub graph with these vertices");
//		printGraph(onlyOddsGraph);

		// Get perfect matching
		List<List<Integer>> tuples = new PerfectMatch().go(onlyOddsGraph, oddVertices);
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


		int routeSum = 0;
		for (int i = 0; i < hamiltonianCircuit.size()-1; i++) {
			routeSum += this.graph[hamiltonianCircuit.get(i)][hamiltonianCircuit.get(i+1)];
		}

		// There's your Traveling Salesman solution
		System.out.println("Fin: " + hamiltonianCircuit.toString() + ". sum: " + routeSum);
		return hamiltonianCircuit;
	}


	public int calculatePathCost(List<Integer> result) {
		int routeSum = 0;
		for (int i = 0; i < result.size()-1; i++) {
			routeSum += this.graph[result.get(i)][result.get(i+1)];
		}
		return routeSum;
	}


	// PRIVATE HELPER FUNCTIONS

	/**
	 * Creates a sub graph of the original graph from a set of vertices
	 * @param vertices The indexes of the vertices to include from the original graph
	 * @return An adjacency matrix with the SAME SIZE OF THE INPUT.
	 * 			Except that vertices which were removed will have all of their edges marked as INF
	 * */
	private int[][] createSubGraphIncluding(List<Integer> vertices) {
		int V = this.graph.length;
		final int INF = Integer.MAX_VALUE;
		int distances[][] = floydWarshall(this.graph);

		// Create a subgraph, first all edges do not exist
		int subgraph[][] = new int[V][V];
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
	private List<List<Integer>> addEdgesToGraph(int graph[][], List<List<Integer>> tuples) {
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
		for(List<Integer> tuple : tuples) {
			int u = tuple.get(0);
			int v = tuple.get(1);
			// Add the edge on both ends
			adjacencyList.get(u).add(v);
			adjacencyList.get(v).add(u);
		}


		return adjacencyList;
	}

	private List<Integer> findOddVerticesInGraph(int graph[][]) {
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

	private static void printGraph(int g[][]) {
		for (int[] a : g) {
			System.out.println(Arrays.toString(a));
		}
	}

	private static void printGraph(List<List<Integer>> graph) {
		for (List<Integer> node : graph) {
			System.out.println(node.toString());
		}
	}

	/**
	 * Floyd Warshall Algorithm for multiple-source-shortest-path problem
	 *
	 * @param graph A graph to compute the algorithm on. NOTE: THE GRAPH IS NOT CHANGED IN THE PROCESS!
	 * @return A matrix representing the shortest paths from index i to index j
	 * */
	public static int[][] floydWarshall(int[][] graph) {

		// Since the original graph will not be changed, we're creating a new one
		int V = graph.length;
		int shortDistancesMatrix[][] = new int[V][V];
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


}

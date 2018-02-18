package christofides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Christofides {

	private int graph[][];

	public Christofides() {
	}

	public Christofides(int[][] graph) {
		this.graph = graph;
	}

	public void go() throws Exception {
		// Create MST from the graph
		int [][] mst = new Prim(this.graph).go(4);
		System.out.println("MST:");
		printGraph(mst);

		// Find out all the vertices with odd degrees and store them in a list
		List<Integer> oddVertices = findOddVerticesInGraph(mst);
		System.out.println("Odd vertices is mst are: " + oddVertices.toString());

		// Form a new graph only with those vertices
		// (edges will change to keep the triangle inequality)
		int onlyOddsGraph [][] = createSubGraphIncluding(oddVertices);
		System.out.println("Sub graph with these vertices");
		printGraph(onlyOddsGraph);

		// Get perfect matching
		List<List<Integer>> tuples = new PerfectMatch().go(onlyOddsGraph, oddVertices, mst);

		// Add it to the MST
		for (List<Integer> tuple : tuples) {
			System.out.println("You should match: " + tuple.get(0) + " with: " + tuple.get(1));
			mst[tuple.get(0)][tuple.get(1)] = tuple.get(2);
			mst[tuple.get(1)][tuple.get(0)] = tuple.get(2);
		}

		System.out.println("MST after adding extra edges:");
		printGraph(mst);

		// Find Euler circuit on that graph
		List<Integer> eulerCircuit = new EulerCircuit(mst).go();

		// Remove duplicated nodes
		List<Integer> hamiltonianCircuit = new ArrayList<>();
		for (int node: eulerCircuit) {
			if (!hamiltonianCircuit.contains(node)) {
				hamiltonianCircuit.add(node);
			}
		}
		hamiltonianCircuit.add(hamiltonianCircuit.get(0));

		// There's your Traveling Salesman solution
		System.out.println("Fin: " + hamiltonianCircuit.toString());
	}


	/**
	 * Creates a sub graph of the original graph from a set of vertices
	 * @param vertices The indexes of the vertices to include from the original graph
	 * @return An adjacency matrix with the SAME SIZE OF THE INPUT.
	 * 			Except that vertices which were removed will have all of their edges marked as INF
	 * */
	public int[][] createSubGraphIncluding(List<Integer> vertices) {
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
	 * Floyd Warshall Algorithm for multiple-source-shortest-path problem
	 *
	 * @param graph A graph to compute the algorithm on. NOTE: THE GRAPH IS NOT CHANGED IN THE PROCESS!
	 * @return A matrix representing the shortest paths from index i to index j
	 * */
	private int[][] floydWarshall(int[][] graph) {

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


	public static void main(String[] args) {
		try {
			int graph[][] = {
					{0, 1, 1, 1, 2},
					{1, 0, 1, 2, 1},
					{1, 1, 0, 1, 1},
					{1, 2, 1, 0, 1},
					{2, 1, 1, 1, 0}
			};
			new Christofides(graph).go();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void printGraph(int g[][]) {
		for (int[] a : g) {
			System.out.println(Arrays.toString(a));
		}
	}

	private static void test1() {
		int X = Integer.MAX_VALUE/4;

		int someMST[][] = {
				{0, 0, 1, 0, 0},
				{0,	0, 1, 0, 0},
				{1, 1, 0, 1, 1},
				{0, 0, 1, 0, 0},
				{0, 0, 1, 0, 0}
		};
		Christofides christ = new Christofides(someMST);
		List<Integer> oddVertices = christ.findOddVerticesInGraph(someMST);
		int ans[][] = christ.createSubGraphIncluding(oddVertices);

		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans.length; j++) {
				System.out.print(ans[i][j] + ", ");
			}
			System.out.println();
		}

//		List<List<Integer>> pairs = new PerfectMatch().go(ans, oddVertices);
//		for(List<Integer> pair : pairs) {
//			System.out.println("Pair: " + pair.get(0) + " with: " + pair.get(1));
//		}
	}
}

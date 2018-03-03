package christofides;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds an euler path in a given graph represented by an adjacency list
 * This class can support multi graphs (graphs with repeated edges) because of the adjacency list
 * How to use:
 * 		1. Call the constructor with a given Adjacency list
 * 		2. Call the getPath() method to receive the euler path
 *
 * 	Notes:
 * 		1. The computation is being done in the constructor
 * 		2. For example, check the driver method of that class
 * */
public class EulerPath {

	/* Driver function for the class, contains the sample "house-like" euler graph */
	public static void main(String[] args) {
		List<Integer> a = new ArrayList<>();
		a.add(1);
		a.add(3);
		a.add(4);

		List<Integer> b = new ArrayList<>();
		b.add(2);
		b.add(0);
		b.add(3);
		b.add(4);

		List<Integer> c = new ArrayList<>();
		c.add(1);
		c.add(3);

		List<Integer> d = new ArrayList<>();
		d.add(1);
		d.add(2);
		d.add(0);
		d.add(4);

		List<Integer> e = new ArrayList<>();
		e.add(0);
		e.add(1);
		e.add(3);


		List<List<Integer>> graph = new ArrayList<>();
		graph.add(a);
		graph.add(b);
		graph.add(c);
		graph.add(d);
		graph.add(e);

		try {
			EulerPath p = new EulerPath(graph);
			p.go();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private List<List<Integer>> adjacencyList;
	private List<Integer> path;
	private boolean verbose = false; // Change this to output

	/**
	 * @param adjacencyList The graph
	 * @throws Exception if graph is invalid (does not contain exactly 2 nodes with odd degrees).
	 * Or any other unexpected behaviour of the class
	 * */
	public EulerPath(List<List<Integer>> adjacencyList) throws Exception {
		if (!isValidGraph(adjacencyList)) {
			for(List<Integer> list : adjacencyList) {
				System.err.println(list.toString());
			}

			throw new Exception("Graph is not eulerian");
		}
		this.adjacencyList = duplicateGraph(adjacencyList);
		this.path = new ArrayList<>();
		go();
	}


	/**
	 * List of integers, each integer represents what vertex to advance to.
	 * The indexes are based on the given graph of course
	 *
	 * @return Euler path
	 * */
	public List<Integer> getPath() {
		return path;
	}


	/* HELPER FUNCTIONS */

	private void go() {
		int startNode = oddVertex();

		debug("Start from: " + startNode);
		this.path.add(startNode);

		int currentNode = startNode;
		while (true) {
			List<Integer> edges = this.adjacencyList.get(currentNode);

			if (edges.size() == 0) {
				break;
			}

			boolean foundNonBridge = false;
			for (int i = 0; i < edges.size(); i++) {

				int v = edges.get(i);
				// Edge is currentNode-v
				boolean isBridge = isThatEdgeABridge(currentNode,v);
				if (!isBridge) { // Not a bridge, follow that edge!

					// Remove the edge from the graph
					this.adjacencyList.get(currentNode).remove(new Integer(v));
					this.adjacencyList.get(v).remove(new Integer(currentNode));
					// Remove the edge from the graph

					debug("DEBUG: advance to: " + toAscii(v));

					this.path.add(v);


					currentNode = v; // We've advanced towards v
					foundNonBridge = true;
					break; // Break the for loop
				}
			}

			if (!foundNonBridge) {
				// No non-bridges edges found, all of them are bridges!
				// Follow the first one

				int nextNode = edges.get(0);

				// Remove the edge from the graph
				this.adjacencyList.get(currentNode).remove(new Integer(nextNode));
				this.adjacencyList.get(nextNode).remove(new Integer(currentNode));
				// Remove the edge from the graph

				debug("DEBUG: advance to: " + toAscii(nextNode));
				this.path.add(nextNode);

				currentNode = nextNode;
			}
		}
	}

	private int oddVertex() {
		for (int i = 0; i < this.adjacencyList.size(); i++) {
			if (this.adjacencyList.get(i).size() % 2 == 1) {
				return i;
			}
		}
		return -1;
	}

	private boolean isThatEdgeABridge(int u, int v) {
		// remove u-v from the graph
		this.adjacencyList.get(u).remove(new Integer(v));
		this.adjacencyList.get(v).remove(new Integer(u));


		// Can we get from u to v without that edge?
		boolean stillReachable = dfsSearch(u, v);



		// return u-v to the graph
		this.adjacencyList.get(u).add(v);
		this.adjacencyList.get(v).add(u);

		return !stillReachable; // If it is not still reachable, then that edge is a bridge between 2 parts of the graph
	}

	private boolean dfsSearch(int u, int v) {
		boolean visited[] = new boolean[this.adjacencyList.size()];
		return dfsSearch(visited, u, v);
	}
	/**
	 * Search to see if you can get to v from u
	 * */
	private boolean dfsSearch(boolean visited[], int u, int v) {
		List<Integer> neighborsOfU = this.adjacencyList.get(u);

		for (int i = 0; i < neighborsOfU.size(); i++) {
			int node = neighborsOfU.get(i);
			if (node == v) {
				return true;
			} else {
				if (!visited[node]) {
					visited[node] = true;
					boolean ans = dfsSearch(visited, node, v);
					if (ans) {
						return true;
					}
				}
			}
		}
		return false;

	}

	/**
	 * Only if exactly 2 nodes have odd degree
	 * */
	private boolean isValidGraph(List<List<Integer>> adjacencyList) {
		// init array of falses... we need 2 that will be true at the end
		boolean isOdd [] = new boolean[adjacencyList.size()];
		for (int i = 0; i < isOdd.length; i++) {
			isOdd[i] = false;
		}

		int numOfOddDegreesVertices = 0;
		for (int i = 0; i < adjacencyList.size(); i++) {
			int degree = adjacencyList.get(i).size();
			if (degree % 2 == 1) {
				isOdd[i] = true;
				numOfOddDegreesVertices++;
			}
		}

		return numOfOddDegreesVertices == 2; // true iff num of odd degrees vertices is 2

	}

	private List<List<Integer>> duplicateGraph(List<List<Integer>> g) {
		List<List<Integer>> newGraph = new ArrayList<>();

		for (int i = 0; i < g.size(); i++) {
			List<Integer> list = g.get(i);
			List<Integer> newList = new ArrayList<>();
			for (int u : list) {
				newList.add(u);
			}
			newGraph.add(newList);
		}

		return newGraph;
	}

	private char toAscii(int n) {
		return (char) (n + 65);
	}

	private void debug(String msg) {
		if (verbose) {
			System.out.println(msg);
		}
	}
}

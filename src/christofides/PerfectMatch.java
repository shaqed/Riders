package christofides;

import inputs.GlobalFunctions;

import java.util.ArrayList;
import java.util.List;

public class PerfectMatch {


	public static void main(String[] args) {
		int graph[][] = {
				{0, 2147483647, 1, 1, 2},
				{2147483647, 2147483647, 2147483647, 2147483647, 2147483647},
				{1, 2147483647, 0, 1, 1},
				{1, 2147483647, 1, 0, 1},
				{2, 2147483647, 1, 1, 0}
		};
		List<Integer> arrayList = new ArrayList<>();
		arrayList.add(0);
		arrayList.add(2);
		arrayList.add(3);
		arrayList.add(4);

//		new PerfectMatch().go(graph, arrayList);
	}


	private boolean verbose = false;

	/**
	 * Given a set of odd vertices, return a perfect matching. The greedy way.
	 * Pick a vertex - and pair it with the closest other vertex you can find
	 *
	 * @param graph            A graph represented with an adjacency matrix
	 * @param oddVerticesInMST List of vertices you wish to pair with one another (is that necessary?)
	 * @return Returns a List of tuples. For every item in the list - that's a list which contains 3 integers.
	 * The first two integers represent the indexes you should pair with one another.
	 * The third one is the weight of the edge
	 */
	public List<List<Double>> go(double graph[][], List<Integer> oddVerticesInMST) {
		List<Integer> odds = duplicateList(oddVerticesInMST);

		List<List<Double>> answer = new ArrayList<>();

		while (!odds.isEmpty()) {
			int sourceVertex = odds.get(0);

			double length = Integer.MAX_VALUE;
			int pairedVertex = -1;



			for (int i = 1; i < odds.size(); i++) {
				int candidateVertex = odds.get(i);

				double distanceFromSourceToCandidate = graph[sourceVertex][candidateVertex];
				if (distanceFromSourceToCandidate < length) {
					length = distanceFromSourceToCandidate;
					pairedVertex = candidateVertex;
				}
			}


			odds.remove(new Integer(sourceVertex));
			odds.remove(new Integer(pairedVertex));
			debug("You should pair: " + sourceVertex + " with: " + pairedVertex + ". length is: " + length);
			List<Double> pair = new ArrayList<>();
			pair.add((double) sourceVertex);
			pair.add((double) pairedVertex);
			pair.add(length);
			answer.add(pair);

		}

		return answer;
	}


	/**
	 * Constructs a minimum weight perfect matching the greedy way on a given graph
	 * This method takes into account that 2 vertices must remain odd at the end of the algorithm
	 * This to enable an euler path to be found in the graph later
	 * @param graph The graph represented as an adjacency matrix (Result of the MST)
	 * @param oddVerticesInMST The odd vertices you wish to pair with one another
	 * @param odd1 The index of the vertex you wish to keep/make odd
	 * @param odd2 The index of the vertex you wish to keep/make odd
	 * @return A multi graph based on the given graph, after additional edges have been added to it
	 *
	 * */
	public List<List<Integer>> goOdd(double graph[][], List<Integer> oddVerticesInMST, int odd1, int odd2) {

		int degree1 = 0; // degree of vertex odd 1
		int degree2 = 0; // degree of vertex odd 2
		for (int i = 0; i < graph.length; i++) {
			if (graph[odd1][i] > 0.0 && graph[odd1][i] != Integer.MAX_VALUE) {
				degree1++;
			}
			if (graph[odd2][i] > 0.0 && graph[odd2][i] != Integer.MAX_VALUE) {
				degree2++;
			}
		}
		debug("Degree of vertex: " + odd1 + " is: " + degree1 + " and vertex: " + odd2 + " is: " + degree2);

		// Consider the following cases
		if (degree1 % 2 == 1 && degree2 % 2 == 1) {
			// Both vertices are already odd, that's ok

			List<List<Double>> tuples = go(graph, oddVerticesInMST);
			List<List<Integer>> multiGraph = addEdgesToGraph(graph, tuples);
			return multiGraph;

		} else if (degree1 % 2 == 0 && degree2 % 2 == 0) {
			// Both vertices are even, you can add an edge between the two and you're done
			List<List<Double>> tuples = go(graph, oddVerticesInMST);

			// Add an edge between the two even vertices
			List<Double> tuple = new ArrayList<>();
			tuple.add((double) odd1);
			tuple.add((double) odd2);
			tuple.add(graph[odd1][odd2]);
			tuples.add(tuple);

			List<List<Integer>> multiGraph = addEdgesToGraph(graph, tuples);
			return multiGraph;

		} else {
			// Exactly one of the vertices is odd, the other is even
			// That means we need to do perfect-match, and the remaining vertex
			// will be paired with the even vertex

			List<Integer> odds = duplicateList(oddVerticesInMST);
			List<List<Double>> ans = new ArrayList<>();

			while (odds.size() > 1) {
				int sourceVertex = odds.get(0);

				// Among the other odd vertices, find a perfect match
				double minLength = 2 << 32;
				int pairedVertex = -1;

				for (int i = 0; i < odds.size(); i++) {
					if (i != sourceVertex) {
						int candidateVertex = odds.get(i);

						if (graph[sourceVertex][candidateVertex] < minLength) {
							minLength = graph[sourceVertex][candidateVertex];
							pairedVertex = candidateVertex;
						}
					}
				}

				// Remove the vertices from the queue
				odds.remove(new Integer(sourceVertex));
				odds.remove(new Integer(pairedVertex));


				// Add the vertices to the list of edges needed to be added
				List<Double> tuple = new ArrayList<>();
				tuple.add((double) sourceVertex);
				tuple.add((double) pairedVertex);
				tuple.add(graph[sourceVertex][pairedVertex]);

				debug("Adding: " + tuple.toString());
				ans.add(tuple);

			} // End of while loop

			if (odds.size() == 1) {
				// Expected... since we got an odd number of odd-degree vertices in the first place
				// Pairing them will result in 1 lonely vertex remaining
				// Pair that vertex with the vertex that is even from the wanted Odds
				List<Double> tuple = new ArrayList<>();
				int sourceVertex = odds.get(0);
				if (degree1 % 2 == 0) {
					tuple.add((double) sourceVertex);
					tuple.add((double) odd1);
					tuple.add(graph[sourceVertex][odd1]);
				} else {
					tuple.add((double) sourceVertex);
					tuple.add((double) odd2);
					tuple.add(graph[sourceVertex][odd2]);
				}
				debug("Adding: " + tuple.toString());
				ans.add(tuple);
			} else {
				System.out.println("WARNING: Unexpected behavior in PerfectMatching. Number of odd-degree vertices was" +
						" odd and yet all of the vertices have been paired");
			}

			return addEdgesToGraph(graph, ans); // Add the edges to a multigraph, and return the answer
		}


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




	private void debug(String msg) {
		if (verbose) {
			System.out.println(msg);
		}
	}

	private List<Integer> duplicateList(List<Integer> src) {
		List<Integer> list = new ArrayList<>();
		for (int i : src) {
			list.add(i);
		}
		return list;
	}
}

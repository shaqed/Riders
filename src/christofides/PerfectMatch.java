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

	// TODO: Issue: this seem to sometimes pair vertices together which are already paired

	/**
	 * Given a set of odd vertices, return a perfect matching. The greedy way.
	 * Pick a vertex - and pair it with the closest other vertex you can find
	 *
	 * @param graph            A graph represented with an adjacency matrix
	 * @param oddVerticesInMST List of vertices you wish to pair with one another (is that necessary?)
	 * @return Returns a List of tuples. For every item in the list - that's a list which contains 3 integers.
	 * The first two integers represent the indexes you should pair with one another.
	 * The third one is how much should that edge take
	 */
	public List<List<Integer>> go(int graph[][], List<Integer> oddVerticesInMST) {
		List<Integer> odds = duplicateList(oddVerticesInMST);

		List<List<Integer>> answer = new ArrayList<>();

		while (!odds.isEmpty()) {
			int sourceVertex = odds.get(0);

			int length = Integer.MAX_VALUE;
			int pairedVertex = -1;



			for (int i = 1; i < odds.size(); i++) {
				int candidateVertex = odds.get(i);

				int distanceFromSourceToCandidate = graph[sourceVertex][candidateVertex];
				if (distanceFromSourceToCandidate < length) {
					length = distanceFromSourceToCandidate;
					pairedVertex = candidateVertex;
				}
			}


			odds.remove(new Integer(sourceVertex));
			odds.remove(new Integer(pairedVertex));
			debug("You should pair: " + sourceVertex + " with: " + pairedVertex + ". length is: " + length);
			List<Integer> pair = new ArrayList<>();
			pair.add(sourceVertex);
			pair.add(pairedVertex);
			pair.add(length);
			answer.add(pair);

		}

		return answer;
	}


	private void debug(String msg) {
//		System.out.println(msg);
	}

	private List<Integer> duplicateList(List<Integer> src) {
		List<Integer> list = new ArrayList<>();
		for (int i : src) {
			list.add(i);
		}
		return list;
	}
}

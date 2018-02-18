package christofides;

import inputs.GlobalFunctions;

import java.util.ArrayList;
import java.util.List;

public class PerfectMatch {




	public static void main(String[] args) {
		int graph[][] = GlobalFunctions.getMatrix();
		List<Integer> arrayList = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			arrayList.add(i);
		}
		new PerfectMatch().go(graph, arrayList);
	}

	/** Given a set of odd vertices, return a perfect matching. The greedy way.
	 * Pick a vertex - and pair it with the closest other vertex you can find
	 *
	 * @param graph A graph represented with an adjacency matrix
	 * @param oddVertices List of vertices you wish to pair with one another (is that necessary?)
	 *
	 * @return Returns a List of pairs. For every item in the list - that's a list which contains 2 integers.
	 * 				These integers represent the indexes you should pair with one another.
	*/
	public List<List<Integer>> go(int graph[][], List<Integer> oddVertices) {
		List<Integer> odds = duplicateList(oddVertices);

		List<List<Integer>> answer = new ArrayList<>();

		while (!odds.isEmpty()) {
			int sourceVertex = odds.get(0);

			double length = Double.MAX_VALUE;
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
//			System.out.println("You should pair: " + sourceVertex + " with: " + pairedVertex + ". length is: " + length);
			List<Integer> pair = new ArrayList<>();
			pair.add(sourceVertex);
			pair.add(pairedVertex);
			answer.add(pair);
		}

		return answer;
	}


	private List<Integer> duplicateList(List<Integer> src) {
		List<Integer> list = new ArrayList<>();
		for (int i : src) {
			list.add(i);
		}
		return list;
	}
}

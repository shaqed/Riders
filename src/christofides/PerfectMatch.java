package christofides;

import inputs.GlobalFunctions;

import java.util.ArrayList;
import java.util.List;

public class PerfectMatch {



//	private double graph[][] = {
//			{0,		1,		2,		1},
//			{1,		0,		1,		2},
//			{2,		1,		0,		1},
//			{1,		2,		1,		0}
//	};

	private int graph[][] = GlobalFunctions.getMatrix();

	public static void main(String[] args) {
		List<Integer> arrayList = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			arrayList.add(i);
		}
		new PerfectMatch().go(arrayList);
	}

	// Given a set of odd vertices, return a perfect matching
	// The greedy way.
	// Pick a vertex - and pair it with the closest other vertex you can find

	// TODO: Return the answer as what ?
	public void go(List<Integer> oddVertices) {
		List<Integer> odds = duplicateList(oddVertices);

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
			System.out.println("You should pair: " + sourceVertex + " with: " + pairedVertex + ". length is: " + length);
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

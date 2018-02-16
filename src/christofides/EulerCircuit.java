package christofides;

import java.util.ArrayList;
import java.util.List;

public class EulerCircuit {

	public static void main(String[] args) {
		new EulerCircuit().go();
	}

	private int graph[][];
	private int E;

	public EulerCircuit() {
		this.graph = new int[][]{
				// TODO: THIS GRAPH IS NOT VALID!
//				A		B		C		D		E
				{0,		1,		0,		1,		0},
				{1,		0,		1,		1,		1},
				{0,		1,		0,		0,		1},
				{1,		1,		0,		0,		1},
				{0,		1,		1,		1,		0}
		};
		this.E = calculateE();

	}

	public EulerCircuit(int[][] graph) {
		this.graph = graph;
		this.E = calculateE();
	}

	public void go() {
		List<Integer> circuit = new ArrayList<>();

		// Start with a node with an odd degree (Assuming graph is indirect)
		int startNode = findIndexOfOddDegreeNode();

		int visitedNodes[] = new int[this.graph.length];
		for (int i = 0; i < visitedNodes.length; i++) {
			visitedNodes[i] = -1; // Non have been visited before
		}

		System.out.println("Starting node: " + startNode);
		int currentNode = startNode;
		visitedNodes[startNode] = 1; // visited
		int edgesVisited = 0;
		while (edgesVisited < E) {




			// Check neighbors of current node
			for (int neighbor = 0; neighbor < this.graph.length; neighbor++) {
				int outEdge = graph[currentNode][neighbor];

				boolean edgeExists = outEdge != 0;
				boolean newNeighbor = visitedNodes[neighbor] == -1;

				if (edgeExists && newNeighbor) { // If the neighbor hasn't been visited yet, move to it
					visitedNodes[neighbor] = 1; // This node has been visited now
					currentNode = neighbor;
					edgesVisited++;
					print("Visit a new node: " + neighbor);
					break;
				}


				if (neighbor == this.graph.length - 1) {
					// If we've reached here - all neighbors of this node has been visited
					// And - since we have not visited all the edges yet - we need to find a way

					for (int i = 0; i < this.graph.length; i++) {
						if (this.graph[currentNode][i] != 0){
							print("Visit a visited node: " + i);
							currentNode = i;
							edgesVisited++;
							break;
						}
					}
					break; // Move to the next node

				}

			}

		}


	}


	private int findIndexOfOddDegreeNode() {
		for (int i = 0; i < this.graph.length; i++) {
			int degreeOfNodeI = 0;
			for (int j = 0; j < this.graph.length; j++) {
				if (graph[i][j] != 0) {
					degreeOfNodeI++;
				}
			}

			if (degreeOfNodeI % 2 != 0) {
				// degree is odd
				return degreeOfNodeI;
			}
		}
		return -1; // ERROR !
	}

	private int calculateE() {
		int ans = 0;
		for (int i = 0; i < this.graph.length; i++) {
			for (int j = 0; j < this.graph.length; j++) {
				if (this.graph[i][j] != 0) {
					ans++;
				}
			}
		}
		return ans;
	}

	private void print(String msg) {
		System.out.println(msg);
	}

	private boolean doesContainEulerCircuit() {
		return false;
	}
}

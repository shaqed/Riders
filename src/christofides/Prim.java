package christofides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prim {

	public static void main(String[] args) {
		int[][] mst = new Prim().go(2);
		print(mst);
	}


	private int graph[][];


	public Prim() {
		int X = Integer.MAX_VALUE;
		int graph[][] = {
				{0, 1, 1, 1, 2},
				{1, 0, 1, 2, 1},
				{1, 1, 0, 1, 1},
				{1, 2, 1, 0, 1},
				{2, 1, 1, 1, 0}
		};
		this.graph = graph;
	}

	public Prim(int[][] graph) {
		this.graph = cloneArray(graph);
	}


	/**
	 * On the loaded graph in the constructor, creates an MST based on a given source node
	 * Using Prim's algorithm.
	 * Current implementation is terrible in terms of time complextiy
	 * Can be improved by using Fibonacci Heap
	 * @param indexOfSource The index of the node to start building the MST from
	 * @return Two-dimensional array representing the MST (indirect graph)
	 * */
	public int[][] go(int indexOfSource) {
		int mst[][] = new int[this.graph.length][this.graph.length];
		int[] parentOf = new int[this.graph.length];

		// Initialize empty queue
		int N = this.graph.length;

		int Q[] = new int[N];
		for (int i = 0; i < Q.length; i++) {
			Q[i] = Integer.MAX_VALUE;
		}
		Q[indexOfSource] = 0; // change that to source index


		parentOf[indexOfSource] = -1; // root has no source

		while (!isEmpty(Q)) { // While queue isn't empty
			int minimumIndex = getMinimumIndexOf(Q);


			for (int i = 0; i < N; i++) {
				int adjacentEdge = graph[minimumIndex][i]; // From minimumIndex to I

				boolean neighborInQueue = Q[i] != -1;
				boolean weightInGraphSmallerThanQueue = adjacentEdge < Q[i];
				if (neighborInQueue && weightInGraphSmallerThanQueue) {

					// Add it to the MST
					parentOf[i] = minimumIndex;

					// Change the value in the Q for i (which is the v)
					Q[i] = adjacentEdge;
				}
			}
		}
		// construct the adjacency matrix
		for (int child = 0; child < parentOf.length; child++) {
			int parent = parentOf[child];
			if (parent != -1) {
				mst[child][parent] = graph[child][parent];
				mst[parent][child] = graph[parent][child];
			}
		}
		return mst;
	}

	private boolean isEmpty(int a[]) {
		for (int i : a) {
			if (i != -1) {
				return false;
			}
		}
		return true;
	}

	private int getMinimumIndexOf(int a[]) {
		int min = Integer.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < min && a[i] != -1) {
				min = a[i];
				minIndex = i;
			}
		}
		a[minIndex] = -1;
		return minIndex;
	}


	private int[][] cloneArray(int[][] arr) {
		int ans[][] = new int[arr.length][arr.length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				if (arr[i][j] != 0) {
					ans[i][j] = arr[i][j];
				} else {
					ans[i][j] = Integer.MAX_VALUE;
				}
			}
		}
		return ans;
	}

	private void debug(String msg) {
		System.out.println(msg);
	}

	public static void print(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				System.out.print(a[i][j] + ", ");
			}
			System.out.println();
		}
	}
}

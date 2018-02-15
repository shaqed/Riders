package christofides;

public class Prim {

	public static void main(String[] args) {
		new Prim().go(0);
	}


	private int graph[][];


	public Prim() {
		int X = Integer.MAX_VALUE;
		int graph[][] = new int[][]{
				{X, 2, X, 6, X},
				{2, X, 3, 8, 5},
				{X, 3, X, X, 7},
				{6, 8, X, X, 9},
				{X, 5, 7, 9, X},
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

		// Initialize empty queue
		int N = this.graph.length;

		int Q[] = new int[N];
		for (int i = 0; i < Q.length; i++) {
			Q[i] = Integer.MAX_VALUE;
		}
		Q[indexOfSource] = 0; // change that to source index

		while (!isEmpty(Q)) { // While queue isn't empty
			int minimumIndex = getMinimumIndexOf(Q);
			for (int i = 0; i < N; i++) {
				int adjacentEdge = graph[minimumIndex][i]; // From minimumIndex to I

				boolean neighborInQueue = Q[i] != -1;
				boolean weightInGraphSmallerThanQueue = adjacentEdge < Q[i];
				if (neighborInQueue && weightInGraphSmallerThanQueue) {

					// Add it to the MST (symmetrically)
					mst[minimumIndex][i] = adjacentEdge;
					mst[i][minimumIndex] = adjacentEdge;

					// Change the value in the Q for i (which is the v)
					Q[i] = adjacentEdge;
				}
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
				ans[i][j] = arr[i][j];
			}
		}
		return ans;
	}

	private void print(int[][] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				System.out.print(a[i][j] + ", ");
			}
			System.out.println();
		}
	}
}

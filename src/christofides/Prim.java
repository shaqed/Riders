package christofides;

import inputs.GlobalFunctions;

public class Prim {
	private int graph[][];

	public Prim() {
		this.graph = cloneArray(GlobalFunctions.getMatrix());
	}

	public Prim(int[][] graph) {
		this.graph = cloneArray(graph);
	}

	// Find an MST of the given graph... we probably want to return another matrix
	// Use Prim's Algorithm to find that MST with a given source
	public void go(int indexOfSource) {
		int mst [][] = new int[this.graph.length][this.graph.length];

		// Start by adding the first node to your MST


	}



	// HELPER FUNCTIONS

	private int[][] cloneArray(int[][] arr) {
		int ans [][] = new int[arr.length][arr.length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				ans[i][j] = arr[i][j];
			}
		}
		return ans;
	}
}

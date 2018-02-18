package christofides;

public class Christofides {

	private int graph[][];

	public Christofides() {
	}

	public Christofides(int[][] graph) {
		this.graph = graph;
	}

	public void go() {
		// Create MST from the graph
		int [][] mst = new Prim(this.graph).go(0);

		// Find out all the vertices with odd degrees and store them in a list

		// Form a new graph only with those vertices (edges will change to keep the triangle inequality)

		// Get perfect matching

		// Add it to the MST

		// Find Euler circuit on that graph

		// Remove duplicated nodes

		// There's your Traveling Salesman solution
	}
}

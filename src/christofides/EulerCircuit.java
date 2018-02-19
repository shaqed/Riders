package christofides;

import java.util.ArrayList;
import java.util.List;

public class EulerCircuit {

	public static void main(String[] args) {

		List<Integer> a = new ArrayList<>();
		a.add(1);
		a.add(2);

		List<Integer> b = new ArrayList<>();
		b.add(0);
		b.add(4);

		List<Integer> c = new ArrayList<>();
		c.add(0);
		c.add(4);

		List<Integer> d = new ArrayList<>();
		d.add(4);
		d.add(4);

		List<Integer> e = new ArrayList<>();
		e.add(1);
		e.add(2);
		e.add(3);
		e.add(3);

		List<List<Integer>> graph = new ArrayList<>();
		graph.add(a);
		graph.add(b);
		graph.add(c);
		graph.add(d);
		graph.add(e);


		System.out.println(new EulerCircuit().go(graph).toString());
//		new EulerCircuit().go();
	}

	private boolean verbose = false;

	private int graph[][];
	private int E;

	public EulerCircuit() {
		int [][] graph = new int[][]{

//				A		B		C		D		E		F		G
		/*A*/	{0,		1,		0,		1,		0,		1,		1},
		/*B*/	{1,		0,		1,		0,		0,		0,		0},
		/*C*/	{0,		1,		0,		1,		1,		1,		0},
		/*D*/	{1,		0,		1,		0,		0,		0,		0},
		/*E*/	{0,		0,		1,		0,		0,		1,		0},
		/*F*/	{1,		0,		1,		0,		1,		0,		1},
		/*G*/	{1,		0,		0,		0,		0,		1,		0},
		};

		this.graph = cloneGraph(graph);
		this.E = calculateE();

	}

	public EulerCircuit(int[][] graph) throws Exception {
		if (isGraphValid(graph)) {
			this.graph = cloneGraph(graph);
			this.E = calculateE();
		} else {
			throw new Exception("Graph is not valid, not every vertex has an even degree !");
		}

	}


	public List<Integer> go() {
		List<Integer> circuit = new ArrayList<>();

		// Start with a node with an odd degree (Assuming graph is indirect)
		int startNode = 0;

		int visitedNodes[] = new int[this.graph.length];
		for (int i = 0; i < visitedNodes.length; i++) {
			visitedNodes[i] = -1; // Non have been visited before
		}

		print("Starting node: " + ((char) (startNode+65)));
		circuit.add(startNode);
		int currentNode = startNode;
		visitedNodes[startNode] = 1; // visited
		int edgesVisited = 0;
		while (edgesVisited < E) {

			print(edgesVisited + " / " + E);



			// Check neighbors of current node
			for (int neighbor = 0; neighbor < this.graph.length; neighbor++) {
				int outEdge = graph[currentNode][neighbor];

				boolean edgeExists = outEdge != 0;
				boolean newNeighbor = visitedNodes[neighbor] == -1;

				if (edgeExists && newNeighbor) { // If the neighbor hasn't been visited yet, move to it

					graph[currentNode][neighbor] = 0; // Remove that edge from the graph
					graph[neighbor][currentNode] = 0;


					visitedNodes[neighbor] = 1; // This node has been visited now
					currentNode = neighbor;
					edgesVisited += 2;



					print("Visit a new node: " + ((char) (neighbor+65)));
					circuit.add(neighbor);
					break;
				}


				if (neighbor == this.graph.length - 1) {
					// If we've reached here - all neighbors of this node has been visited
					// And - since we have not visited all the edges yet - we need to find a way

					for (int i = 0; i < this.graph.length; i++) {
						if (this.graph[currentNode][i] != 0){


							graph[currentNode][i] = 0; // Remove that edge from the graph
							graph[i][currentNode] = 0;


							print("Visit a visited node: " + ((char) (i+65)));
							currentNode = i;
							edgesVisited += 2;


							circuit.add(i);
							break;
						}
					}
					break; // Move to the next node

				}

			}

		}

		if (verbose) {
			System.out.println("Done: " + circuit.toString());
			for(int i : circuit) {
				System.out.print(((char) (i+65)) + ", ");
			}
			System.out.println();
		}
		return circuit;
	}


	/**
	 * Calculates an euler circuit on a valid graph, this can support multi graphs! (graph with repeated edges)
	 * @param g Adjacency list of the multi graph, the graph has to be valid (all vertices with even degree), and if you
	 *          want to have the same edge twice just add it to the list twice.
	 * @return List of integers representing the indexes which you should follow in the circuit
	 * */
	public List<Integer> go(List<List<Integer>> g) {

		List<Integer> circuit = null;

		int V = g.size();
		int E = calculateE(g);


		boolean foundCircuit = false;
		for (int j = 0; j < V && !foundCircuit; j++) {

			List<List<Integer>> adjacencyList = cloneGraph(g);
			circuit = new ArrayList<>();

			print("Start node: " + j);

			// Init
			boolean visitedNodes[] = new boolean[V];
			for (int i = 0; i < visitedNodes.length; i++) {
				visitedNodes[i] = false; // None have been visited yet
			}

			// Visit the first node (start from it)
			int startNode = j;
			circuit.add(startNode);
			visitedNodes[startNode] = true;


			int edgesVisited = 0;
			int currentNode = startNode;
			while (edgesVisited < E) {

				print(edgesVisited + " / " + E);

				boolean movedToANewVertex = false;
				for (int neighborOfCurrent : adjacencyList.get(currentNode)) {
					boolean newVertex = !visitedNodes[neighborOfCurrent];
					if (newVertex) {
						print("Traveling to a new node: " + neighborOfCurrent);

						// Remove visited edge
						removeEdge(adjacencyList, currentNode, neighborOfCurrent);
						edgesVisited++;


						// Advance towards the neighbor
						circuit.add(neighborOfCurrent);
						currentNode = neighborOfCurrent;
						visitedNodes[neighborOfCurrent] = true;

						movedToANewVertex = true;

						break;
					}
				}

				if (!movedToANewVertex) { // If you haven't found a new vertex to go to, just go to the next available
					for(int neighborOfCurrent : adjacencyList.get(currentNode)){
						print("Traveling to a visited node: " + neighborOfCurrent);

						// Remove visited edge
						removeEdge(adjacencyList, currentNode, neighborOfCurrent);
						edgesVisited++;

						// Advance towards that neighbor
						circuit.add(neighborOfCurrent);
						currentNode = neighborOfCurrent;
						movedToANewVertex = true;
						break;
					}

				}

				if (!movedToANewVertex) {
					print("Haven't moved to a new vertex... are you stuck?\n");
					break; // breaks the while loop
				}

				if (edgesVisited == E) {
					print("DONE!\n");
					foundCircuit = true; // exit big for loop
					break;
				}
			}
		}
		return circuit;

	}


	private int calculateE(List<List<Integer>> adjacencyList) {
		int e = 0;
		for (List<Integer> neighborsOfU : adjacencyList) {
			e += neighborsOfU.size();
		}
		return e/2; // Counting A-B twice, so dividing by 2 at the end
	}

	private void removeEdge(List<List<Integer>> adjacencyList, int u, int v) {
		adjacencyList.get(u).remove(new Integer(v));

		// Remove edge from neighbor to the last current
		adjacencyList.get(v).remove(new Integer(u));

	}

	private List<List<Integer>> cloneGraph(List<List<Integer>> adjacencyList) {
		List<List<Integer>> ans = new ArrayList<>();
		for (List<Integer> neighbors : adjacencyList) {
			List<Integer> newNeighbors = new ArrayList<>();
			for (int node : neighbors) {
				newNeighbors.add(node);
			}
			ans.add(newNeighbors);
		}
		return ans;
	}



	private int[][] cloneGraph(int graph[][]) {
		int ans [][] = new int[graph.length][graph.length];
		for (int i = 0; i < ans.length; i++) {
			for (int j = 0; j < ans.length; j++) {
				ans[i][j] = graph[i][j];
			}
		}
		return ans;
	}

	private boolean isGraphValid(int g[][]) {
		for (int i = 0; i < g.length; i++) {
			int degreeOfNodeI = 0;
			for (int j = 0; j < g.length; j++) {
				if (g[i][j] != 0) {
					degreeOfNodeI++;
				}
			}

			if (degreeOfNodeI % 2 != 0) {
				// degree is odd
				return false;
			}
		}
		return true; // No odd-degree vertex... graph is fine!
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
		if (verbose) {
			System.out.println(msg);
		}
	}

}

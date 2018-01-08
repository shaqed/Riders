package brute_force;

import java.util.Arrays;
import java.util.Stack;

public class Algo1 {


    private int[][] matrix;
    private int A = 0; // Start of driver
    private int B = 0; // Destination of driver

    private int min;
    private int minArray[];

    private int N = 0; // Size of matrix


    public Algo1(int[][] matrix) {
        this.matrix = matrix;
    }

    private void start() throws Exception {
        if (!checkIfMatrixLegal(matrix)){
            throw new Exception("Matrix is invalid");
        }
        this.N = matrix.length;
        this.B = matrix.length - 1;
        print("Begin algorithm on " + N + " x " + N + " matrix:");

    }




    private int count = 0;
    public void permute() {
        N = 8;
        count = 0;
        min = 100000;
        permute(new Stack<Integer>());

        // Print answer in form of S1 S2 etc...
        print("Total amount of permutations: " + count + " min : " + min + ":: " + pathToString(this.minArray));
        count = 0;
        min = 100000;

        // Answer is in minArray
        // TODO: remove to count variable and send it to the recursion and only save minArray as a global
    }

    private void permute(Stack<Integer> visited) {

        if (visited.size() == N) {
            count++;
            for (int i = 0; i < visited.size(); i++) {
                System.out.print(convertIndexToNode(visited.get(i)) + ", ");
            }
            int arr[] = stackToArray(visited);
            int ans = calculatePath(arr);
            print(ans + "");
            saveMin(visited);
        }


        // Looking at sources
        for (int i = 0; i < N/2; i++) {
            // Conditions
            boolean alreadyVisited = visited.search(i) != -1;
            if (!alreadyVisited) {
                visited.push(i);
                permute(visited);
                visited.pop();
            }
        }


        // Looking at destinations
        for (int i = N/2; i < N; i++) {
            boolean alreadyVisited = visited.search(i) != -1;
            boolean itsSourceVisited = visited.search(i - (N/2)) != -1;

            if (!alreadyVisited && itsSourceVisited) {
                visited.push(i);
                permute(visited);
                visited.pop();
            }

        }

    }


    private int calculatePath(int[] nodesInOrder) {
        // From A to first
        int ans = matrix[A][nodesInOrder[0]];

        // From node i to node i+1
        for (int i = 0; i < nodesInOrder.length - 1; i++) {
            ans += matrix[nodesInOrder[i]][nodesInOrder[i+1]];
        }

        // From last to B
        ans += matrix[nodesInOrder[nodesInOrder.length-1]][B];

        return ans;
    }

    private int[][] getGraph() {
        return null;
    }

    private boolean saveMin(Stack<Integer> nodesInOrder) {
        int arr[] = stackToArray(nodesInOrder);
        int value = calculatePath(arr);
        if (value < this.min) {
            this.min = value;
            this.minArray = arr;
            return true;
        }
        return false;
    }

    private int[] stackToArray(Stack<Integer> stack){
        int arr [] = new int[stack.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = stack.get(i);
        }
        return arr;
    }

    private String convertIndexToNode(int i) {
        return i < N/2? "S" + i : "T" + i % (N/2);
    }

    private boolean checkIfMatrixLegal(int[][] matrix) {
        if (matrix == null) {
            print("Matrix is null");
            return false;
        }

        if (matrix.length != matrix[0].length) {
            print("Matrix size is not NxN");
            return false;
        }

        // Todo: Check for 1 subarray that is null
        // Todo: Check for 1 subarray not of size N

        return true;

    }

    private void print(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    private String pathToString(int[] arr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            builder.append(convertIndexToNode(arr[i]));
            builder.append(", ");
        }
        System.out.println();
        return builder.toString();
    }

    private void print(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void print(String msg) {
        System.out.println(msg);
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}

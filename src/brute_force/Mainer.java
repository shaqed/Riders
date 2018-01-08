package brute_force;

import brute_force.Algo1;
import inputs.GlobalFunctions;

public class Mainer {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();
        new Algo1(GlobalFunctions.getMatrix()).permute();
        long endTime = System.currentTimeMillis();

        System.out.println("Took: " + (endTime - startTime) + "ms");


//        new brute_force.AllCombinationsOfArray(new int[] {1,2,3,4,5}, 2);

    }
}

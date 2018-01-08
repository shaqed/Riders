import java.util.Arrays;
import java.util.HashSet;

public class AllCombinationsOfArray {

    public AllCombinationsOfArray() {
        int ans = selectionOfElementsFromArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 4);
        System.out.println("Total amount: " + ans);
    }

    public AllCombinationsOfArray(int[] arr, int elementsToSelect) {
        int ans = selectionOfElementsFromArray(arr, elementsToSelect);
        System.out.println("Total combinations: " + ans);
    }





    /**
     * Iterate over every possible selection of X elements in the array and returns the amount of possibilities.
     * This function PRINTS the elements nicely formatted and only returns the amount of permutations you got
     * [ TODO: Return a set instead of all permutation? (set because you don't care about order ]
     * @param arr              The array to iterate over
     * @param numberOfSelected The number of spaces to select
     */
    public int selectionOfElementsFromArray(int arr[], int numberOfSelected) {
        int counters[] = new int[arr.length];
        return selectionOfElementsFromArray(arr, arr.length - numberOfSelected, counters, 0, numberOfSelected, 0);
    }


    /**
     *  @param arr The array
     *  @param depth How deep to go into the array ? How many for loops to create ?
     *  @param counters Empty array, size of the the 'arr' - will represent the counters that iterate over the arr [TODO: Is it optimal in terms of space?]
     *  @param currentCount Enter 0, this helps to count all possibilites calculated [TODO: Maybe you should just return a set of all combinations?]
     *  @param sizeOfLoop [TODO:Might be redundant if you fix the counter's array to be the size of the amount you want selected]
     *  @param startFrom 0, IMPORTANT! to keep duplications off and actually create the nCr work
     * */
    private int selectionOfElementsFromArray(int[] arr, int depth, int[] counters, int currentCount, int sizeOfLoop, int startFrom) {
        // Base case
        if (depth >= arr.length) {
            for (int i = arr.length - sizeOfLoop; i < counters.length; i++) {
                if (i < counters.length - 1) {
                    System.out.print(arr[counters[i]] + ", ");
                } else {
                    System.out.print(arr[counters[i]] + " ");
                }
            }
            System.out.println();
            return 1;
        }

        // Recursion
        int tempCount = 0;
        for (int i = startFrom; i < arr.length; i++) {
            counters[depth] = i;
            tempCount += selectionOfElementsFromArray(arr, depth + 1, counters, currentCount, sizeOfLoop, i+1);
        }

        return currentCount + tempCount;
    }

}

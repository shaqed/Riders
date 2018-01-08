package inputs;

public class GlobalFunctions {

    public static final int X = 10000;

    /**
     * Generate template short - distances matrix
     *
     * */
    public static int[][] getMatrix() {

        int[][] graph2 = {
//              S0     S1   S2   S3    T0   T1  T2   T3
      /*S0*/    {X  , 58 , 60 , 50 ,  22,  79,  38,  46},
      /*S1*/    {58 , X  , 112, 11 ,  41,  67,  29,  24},
                {60 , 110, X  , 100,  85, 106,  83,  91},
                {49 ,  11, 101,   X,  32,  61,  19,  13},
                {X  ,  42,  88,  33,   X,  64,  26,  30},
                {94 ,   X, 114,  61,  63,   X,  50,  52},
                {38 ,  32,   X,  21,  25,  50,   X,   6},
                {40 ,  24,  91,   X,  26,  51,   5,   X}
        };
        return graph2;
    }
}

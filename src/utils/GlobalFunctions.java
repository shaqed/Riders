package utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GlobalFunctions {

	public static void main(String[] args) {
		try {
			String ans = URLEncoder.encode("|", "utf-8");
			System.out.println(ans);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}



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

    public static void printMatrix(double [][] g) {
		for (int i = 0; i < g.length; i++) {
			System.out.print("[");
			for (int j = 0; j < g[i].length - 1; j++) {
				System.out.print(g[i][j] + ",\t\t\t");
			}
			System.out.println(g[i][g[i].length-1] + "]");
		}
	}

	public static String readInputStream(InputStream inputStream) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			char buffer[] = new char[4096];

			InputStreamReader reader = new InputStreamReader(inputStream);
			int bytesRead = 0;
			while (bytesRead != -1){
				bytesRead = reader.read(buffer);
				for (int i = 0; i < bytesRead; i++) {
					stringBuilder.append(buffer[i]);
//					System.out.print("read: [" + i + "]: " + ((int)buffer[i]) + ". ");
				}

			}
			return stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject readJSONObject(InputStream inputStream) {
		try {
			return (JSONObject) new JSONParser().parse(readInputStream(inputStream));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}

package utils.parser;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

public class JSONmatrix {

	public static void main(String[] args) {
//		getMatrix();
	}

	public static double[][] getMatrix(JSONObject jsonObject) {
		List<List<Double>> list = new ArrayList<>();
		try {
			Object obj = jsonObject;

			JSONObject jsonobj = (JSONObject) obj;


			JSONArray dist = (JSONArray) jsonobj.get("rows");
			for (int i = 0; i < dist.size(); i++) {
				List<Double> rows = new ArrayList<>();
				JSONObject obj2 = (JSONObject) dist.get(i);

				JSONArray disting = (JSONArray) obj2.get("elements");
				for (int j = 0; j < disting.size(); j++) {
					JSONObject obj3 = (JSONObject) disting.get(j);
					JSONObject obj4 = (JSONObject) obj3.get("distance");
					Object obj5 = obj4.get("value");
					String s = String.valueOf(obj5);
					rows.add(Double.valueOf(s));
				}
				list.add(rows);
			}

			double[][] matrix = new double[list.size()][list.size()];
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.size(); j++) {
					matrix[i][j] = list.get(i).get(j);
				}
			}
			return matrix;

		} catch (Exception e) {
			e.printStackTrace();
		}



//   for(int x=0; x<matrix.length; x++){
//       for(int y=0; y<matrix.length; y++){
//           System.out.print(matrix[x][y]);
//       }
//       System.out.println();}
//
//    }
		return null;

	}
}


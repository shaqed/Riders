package utils;

import inputs.AlgorithmInput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import polyline_decoder.Point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class handles HTTP calls for the Google APIs
 * */
public class GoogleClient {
	// Get KML File

	private static final String API_KEY = "AIzaSyD56LHjhdpL7ztyU33rsph0zYYEY136nOo";

	// Distances Matrix API Request
	public static void main(String[] args) {
		JSONArray jsonArray = new JSONArray();


	}



	public JSONObject adjacencyMatrixRequest(String source, List<AlgorithmInput.Passenger> passengers, String dest) {
		List<Point> pointList = new ArrayList<>();

		for(AlgorithmInput.Passenger p : passengers) {
			pointList.add(p.s);
		}

		try {
			HTTPer.Builder builder = new HTTPer.Builder();
			builder.setRootURL("https://maps.googleapis.com/maps/api/distancematrix/json");
			builder.setMethod("GET");


			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(source);
			stringBuilder.append("|");
			for(Point p : pointList) {
				stringBuilder.append(p.getLng());
				stringBuilder.append(",");
				stringBuilder.append(p.getLat());
				stringBuilder.append("|");
			}
//            stringBuilder.deleteCharAt(stringBuilder.length()-1);
			stringBuilder.append(dest);

			builder.addURLParameter("origins", stringBuilder.toString());
			builder.addURLParameter("destinations", stringBuilder.toString());
			builder.addURLParameter("key", API_KEY);

			HTTPer http = builder.build();

			verify(http.toString());

			String ans = http.get();
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(ans);
			return jsonObject;

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}




	private void verify(){
		verify(null);
	}

	private void verify(String msg) {
		Scanner scanner = new Scanner(System.in);

		System.out.println("You're about to make a request to one of Google's APIs, are you sure? (y/n)");
		if (msg != null) {
			System.out.println("Message : " + msg);
		}
		String ans = scanner.nextLine();
		if (ans.equalsIgnoreCase("y")) {
			return;
		}
		System.out.println("Aborting program");
		scanner.close();
		System.exit(1);
	}

}

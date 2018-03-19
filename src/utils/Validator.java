package utils;

import org.json.simple.JSONObject;

public class Validator {

	private static boolean verbose = true;

	public static boolean inputJSON(JSONObject jsonObject) {
		try {
			// Is object given null
			if (jsonObject == null) {
				throw new Exception("JSON Object is null");
			}

			// Does contain a radius key
//			boolean containsRadiusKey = jsonObject.containsKey(Tags.IO_RADIUS);

			// Does contain a source key


			return true;

		} catch (Exception e) {
			debug(e.getMessage());
			return false;
		}

	}


	private static void debug(String msg) {
		if (verbose) {
			System.out.println("Validator: " + msg);
		}
	}
}

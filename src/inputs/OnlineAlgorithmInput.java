package inputs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Tags;
import utils.math.Vectors;
import utils.polyline_decoder.Point;

import java.util.ArrayList;
import java.util.List;

public class OnlineAlgorithmInput {
	private double radius;
	private List<Point> path;
	private AlgorithmInput.Passenger suggestedPassenger;
	private double[] aerialVector;

	private List<Point> mainPoints; // main points in the path

	public OnlineAlgorithmInput(JSONObject jsonObject) throws Exception {

		// Get radius
		double radius = -1;
		if (jsonObject.containsKey(Tags.IO_RADIUS)){
			radius = Double.valueOf(jsonObject.get(Tags.IO_RADIUS).toString());
		} else {
			throw new Exception("Input JSON does not contain a 'radius'");
		}

		// Plength
		long plength = -1;
		if (jsonObject.containsKey(Tags.IO_PATH_LENGTH)) {
			plength = (long) jsonObject.get(Tags.IO_PATH_LENGTH);
		} else {
			throw new Exception("Input JSON does not contain 'plength' field");
		}

		// Path (all of the points)
		List<Point> path = null;
		if (jsonObject.containsKey(Tags.IO_PATH)) {
			JSONArray pathJSONArray = (JSONArray) jsonObject.get(Tags.IO_PATH);
			path = new ArrayList<>();
			for (int i = 0; i < pathJSONArray.size(); i++) {
				path.add(new Point((JSONObject) pathJSONArray.get(i)));
			}
		} else {
			throw new Exception("Input JSON does not contain a 'path' array");
		}

		// The optional passenger
		AlgorithmInput.Passenger newPassenger = null;
		if (jsonObject.containsKey(Tags.IO_NEW_PASSENGER)) {
			newPassenger = new AlgorithmInput.Passenger((JSONObject) jsonObject.get(Tags.IO_NEW_PASSENGER));
		} else {
			throw new Exception("Input JSON does not contain a newPassenger field");
		}

		List<Point> mainPath = null;
		if (jsonObject.containsKey(Tags.IO_MAIN_POINTS)) {
			mainPath = new ArrayList<>();
			JSONArray mainPathJSON = (JSONArray) jsonObject.get(Tags.IO_MAIN_POINTS);
			for (int i = 0; i < mainPathJSON.size(); i++) {
				mainPath.add(new Point((JSONObject) mainPathJSON.get(i)));
			}
		} else {
			throw new Exception("Input JSON does not contain an array in field: " + Tags.IO_MAIN_POINTS);
		}


		// Show errors if there were any
		StringBuilder stringBuilder = new StringBuilder();
		if (radius == -1) {
			stringBuilder.append("Couldn't extract radius. ");
		}
		if (path == null) {
			stringBuilder.append("Couldn't extract the path. ");
		}
		if (newPassenger == null) {
			stringBuilder.append("Couldn't extract the newPassenger. ");
		}
		if (plength == -1) {
			stringBuilder.append("Couldn't extract the plength. ");
		}
		if (mainPath == null) {
			stringBuilder.append("Couldn't extract the main points");
		}

		if (stringBuilder.length() > 0){
			throw new Exception(stringBuilder.toString());
		}

		// Wrap-up
		this.radius = radius * plength;
		this.path = path;
		this.mainPoints = mainPath;
		this.suggestedPassenger = newPassenger;
		this.aerialVector = Vectors.computeVector(getFirstPoint(), getLastPoint());
	}

	public double getRadius() {
		return radius;
	}

	public List<Point> getPath() {
		return path;
	}

	public AlgorithmInput.Passenger getSuggestedPassenger() {
		return suggestedPassenger;
	}

	public double[] getAerialVector() {
		return aerialVector;
	}

	public Point getFirstPoint() {
		return this.path.get(0);
	}

	public Point getLastPoint() {
		return this.path.get(this.path.size()-1);
	}

	public List<Point> getMainPoints() {
		return mainPoints;
	}
}

package servlets.api;

import caraoke.AlgorithmDriver;
import inputs.AlgorithmInput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.polyline_decoder.Point;
import utils.GlobalFunctions;
import utils.Tags;
import utils.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * Main servlet for accessing the algorithm
 * URL:				/api/getroute
 * Method:			POST
 * Expected Input:	JSON Object in the following format:
 *					 	{
 						"radius" : Number,
 						"source" : {
 							"lat": Number,
 							"lng": Number
 						},
 						"dest" : {
 							"lat": Number,
 							"lng": Number
 						},
 						"passengers" : [
 							{
 								"name" : String,
 								"si" : {
 									"lat": Number,
 									"lng": Number
 								},
 								"ti" : {
 									"lat": Number,
 									"lng": Number
 								}
 							},
 						...
 						]
 						}

 * Expected Output:	JSON Object in the following format:
 * This route array returns a list of points which indicate the order
 * For actual Google Maps usage you need to compute the shortest path from each one to each next
 *
 * If you were to use the online algorithm. Include this array as-is to the input of it
 * 						{
 							"route": [
 								"point" : {
 									"lat": Number,
 									"lng": Number
 								},
 								...
 							]
 						}

 * */

@WebServlet("/api/getroute")
public class GetRouteServlet extends MyServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET Method not allowed, use POST instead");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			JSONObject requestJSON = GlobalFunctions.readJSONObject(req.getInputStream());
			if (Validator.inputJSON(requestJSON)) {
				// request json is valid

				// Create a new AlgorithmInput instance from it and run the algorithm
				AlgorithmInput input = AlgorithmInput.getInstance(requestJSON);

				// Run the algorithm
				List<AlgorithmInput.Passenger> passengers = AlgorithmDriver.filterPassengers(input);
				List<Point> points = AlgorithmDriver.tsp(input, passengers); // currently void

				System.out.println("Algorithm done");

				resp.setContentType("application/json");
				resp.getWriter().write(buildAnswerJSON(points).toString());

				resp.setStatus(HttpServletResponse.SC_OK);
				// Return the answer as a JSON object based on the format


			} else {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Bad JSON");
			}
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write(e.getMessage());
		}

	}


}

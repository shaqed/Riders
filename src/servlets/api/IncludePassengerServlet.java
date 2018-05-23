package servlets.api;

import caraoke.AlgorithmDriver;
import inputs.AlgorithmInput;
import inputs.OnlineAlgorithmInput;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.GlobalFunctions;
import utils.Tags;
import utils.polyline_decoder.Point;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Main servlet for handling online-requests after an initial algorithm answer.
 * After you've computed a route, and a new passenger pop up -
 * ask this API whether you should include that passenger in your path or not
 * <p>
 * URL:                 /api/includepassenger
 * Method:              POST
 * Expected Input:      A JSON Object in the following format:
 * 		{
 * 			"mainPoints" : [
 * 				{"lat" : Number, "lng", Number},
 * 				{"lat" : Number, "lng", Number},
 * 				...
 * 			],
 * 			"plength" : Number, // the path in kms
 * 			"radius" : [Number],
 * 			"path" : [
 * 				{"lat" : Number, "lng", Number},
 * 				{"lat" : Number, "lng", Number},
 * 				...
 * 			]
 * 			"newPassenger" : {
 * 				"si" : {"lat": Number, "lng" : Number},
 * 				"ti" : {"lat" : Number, "lng" : Number}
 * 			}
 * <p>
 * }
 * Expected Output:    A JSON Object in the following format - representing the new order of nodes
 * The path array is going to include n+1 elements as n is the number of elements in the request
 * {
 * "route": [
 * 		{
 * 			"lat": Number,
 * 			"lng": Number
 * 		},
 * 		...
 * 	],
 *
 *
 * 	TODO: Issue 1:
 * 	We said that on a given instance there can only be a finite number of additions
 * 	How do we decrement the number of additions?
 * 		Supply a variable that decreases for each addition?
 * 		Or perhaps this needs to be handled by the client [V] -> Chose this
 * }
 */

@WebServlet("/api/includepassenger")
public class IncludePassengerServlet extends MyServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET Method not allowed, use POST instead");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		JSONObject jsonObject = GlobalFunctions.readJSONObject(req.getInputStream());
		resp.setContentType("application/json");
		try {

			OnlineAlgorithmInput input = new OnlineAlgorithmInput(jsonObject);

			// Extract the points in the order

			AlgorithmInput.Passenger passenger = input.getSuggestedPassenger();

			// Compute new path
			List<Point> newPath = AlgorithmDriver.addPassengerToRoute(passenger, input);

			JSONObject ans = buildAnswerJSON(newPath);

			resp.getWriter().write(ans.toString());
			resp.setStatus(HttpServletResponse.SC_OK);


		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write(super.buildErrorMessage("Input JSON Could not be parsed by the server. reason: "
							+ e.getMessage(),
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR).toString());
		}


	}
}

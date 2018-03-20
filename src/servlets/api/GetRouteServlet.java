package servlets.api;

import caraoke.AlgorithmDriver;
import inputs.AlgorithmInput;
import org.json.simple.JSONObject;
import utils.GlobalFunctions;
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
public class GetRouteServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET Method not allowed, use POST instead");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject requestJSON = GlobalFunctions.readJSONObject(req.getInputStream());
		if (Validator.inputJSON(requestJSON)) {
			// request json is valid

			// Create a new AlgorithmInput instance from it and run the algorithm
			AlgorithmInput input = AlgorithmInput.getInstance(requestJSON);

			// Run the algorithm
			List<AlgorithmInput.Passenger> passengers = AlgorithmDriver.filterPassengers(input);
			AlgorithmDriver.tsp(input, passengers); // currently void

			System.out.println("Algorithm done");


			resp.setStatus(HttpServletResponse.SC_OK);
			// Return the answer as a JSON object based on the format


		} else {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Bad JSON");
		}

	}
}

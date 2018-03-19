package servlets.api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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

 * */

@WebServlet("/api/getroute")
public class GetRouteServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().write("hi from java");
	}
}

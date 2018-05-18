package servlets.api;

import caraoke.AlgorithmDriver;
import inputs.AlgorithmInput;
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
import java.util.List;


/**
 * Main servlet for handling online-requests after an initial algorithm answer.
 * After you've computed a route, and a new passenger pop up -
 * ask this API whether you should include that passenger in your path or not
 *
 * URL:                 /api/includepassenger
 * Method:              POST
 * Expected Input:      A JSON Object in the following format:
 *                      {
 *                          "radius" : [Number],
 *                          "path" : [
 *                                      {"lat" : Number, "lng", Number},
 *                                      {"lat" : Number, "lng", Number},
 *                                      ...
 *                                   ]
 *                          "newPassenger" : {
 *                              "source" : {"lat": Number, "lng" : Number},
 *                              "dest" : {"lat" : Number, "lng" : Number}
 *                          }
 *
 *                      }
 * Expected Output:    A JSON Object in the following format - representing the new order of nodes
 *                      The path array is going to include n+1 elements as n is the number of elements in the request
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

@WebServlet("/api/includepassenger")
public class IncludePassengerServlet extends MyServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET Method not allowed, use POST instead");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject jsonObject = GlobalFunctions.readJSONObject(req.getInputStream());
        try {

            // Extract the points in the order
            AlgorithmInput input = AlgorithmInput.getInstance(jsonObject);
            if (input != null) {
                AlgorithmInput.Passenger passenger = new AlgorithmInput.Passenger((JSONObject) jsonObject.get(Tags.IO_NEW_PASSENGER));

                if (AlgorithmDriver.includePassenger(passenger, input)) {

                } else {

                }

            }


        } catch (Exception e) {
            resp.getWriter().write(super.buildErrorMessage("Input JSON Could not be parsed by the server",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR).toString());
        }


    }
}

package servlets.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Tags;
import utils.polyline_decoder.Point;

import javax.servlet.http.HttpServlet;
import java.util.List;

public abstract class MyServlet extends HttpServlet {

    protected JSONObject buildAnswerJSON(List<Point> points) {
        JSONObject jsonObject = new JSONObject();

        JSONArray routeArray = new JSONArray();
        for (Point p : points) {
            JSONObject pointJSON = new JSONObject();
            pointJSON.put(Tags.IO_POINT_LATITUDE, p.getLat());
            pointJSON.put(Tags.IO_POINT_LONGITUDE, p.getLng());

            routeArray.add(pointJSON);
        }
        jsonObject.put(Tags.IO_ROUTE, routeArray);
        return jsonObject;
    }

    protected JSONObject buildErrorMessage(String msg, int code) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(Tags.IO_ERROR_CODE, code);
        jsonObject.put(Tags.IO_ERROR_MESSAGE, msg);

        return jsonObject;
    }


}

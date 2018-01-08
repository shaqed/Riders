package caraoke;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import polyline_decoder.Point;
import polyline_decoder.PolylineDecoder;

public class GetPointsFromJSON {

    public static void main(String[] args) {


//        String url = "/Users/user/Desktop/telaviv.json";
        String url = "C:\\Users\\DELL\\Desktop\\project\\create-points\\data\\telaviv.json";


        List<Point> p = getPoints(url);
        System.out.println(p.size() + "::::: " +Arrays.toString(p.toArray()));


    }


    public static List<Point> getPoints(String URL) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(URL));
            return getPoints(jsonObject);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Point> getPoints(JSONObject obj) {
        JSONParser parser = new JSONParser();
        int i = 0, j = 0;
        List<Point> points = new ArrayList<Point>();
        List<Point> polylinePoints = new ArrayList<Point>();
        try {

//            obj = (JSONObject) parser.parse(new FileReader(URL));


            JSONObject jsonObject = (JSONObject) obj;

            JSONArray jsonObject1 = (JSONArray) jsonObject.get("routes");


            JSONArray pointList = (JSONArray) jsonObject.get("routes");
            Iterator<JSONArray> iterator = pointList.iterator();


            while (iterator.hasNext()) {
                while (i < 16) {

                    JSONObject jsonObject2 = (JSONObject) jsonObject1.get(0);
                    JSONArray jsonObject3 = (JSONArray) jsonObject2.get("legs");
                    JSONObject jsonObject4 = (JSONObject) jsonObject3.get(0);
                    JSONArray jsonObject6 = (JSONArray) jsonObject4.get("steps");
                    JSONObject js5 = (JSONObject) jsonObject6.get(j);

                    JSONObject jsonObject7 = (JSONObject) js5.get("start_location");
                    Point start = new Point((double) jsonObject7.get("lat"), (double) jsonObject7.get("lng"));
                    points.add(start);

                    JSONObject jsonObject8 = (JSONObject) js5.get("end_location");
                    Point end = new Point((double) jsonObject8.get("lat"), (double) jsonObject8.get("lng"));
                    points.add(end);

                    JSONObject jsonObject9 = (JSONObject) js5.get("polyline");
                    String polyline = (String) jsonObject9.get("points");
                    PolylineDecoder p = new PolylineDecoder();
                    polylinePoints = p.decode(polyline);
                    points.addAll(polylinePoints);
                    i++;
                    j++;
                }

                iterator.next();
            }


            System.out.println(points.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }



}




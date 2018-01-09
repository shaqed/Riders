package inputs;

import caraoke.GetPointsFromJSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import polyline_decoder.Point;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateInput {


    public static void main(String[] args) {
//        getInput(); Runs the algorithm

        test();


    }


    public static void test() {
        try {
            JSONObject result = loadPathFromGoogleMaps("Be'er Sheva", "Sderot");

            List<Point> points = GetPointsFromJSON.getPoints(result);
            System.out.println(Arrays.toString(points.toArray()));

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }


    public static AlgorithmInput getInput() {
        // Generate JSON


        // Generate passengers
        // Decide on a radius


        try {
            AlgorithmInput input = new AlgorithmInput(loadJSONFromFileURL("C:\\Users\\DELL\\Desktop\\project\\create-points\\data\\bs-to-sapir-answer.json"),
                    generatePassengersFromTextFile("C:\\\\Users\\\\DELL\\\\Desktop\\\\project\\\\create-points\\\\data\\\\passengers.txt"),
                    getRadius());

            System.out.println("Generation of input was successful");
            return input;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // Return result
        return null;
    }


    private static List<AlgorithmInput.Passenger> generatePassengersFromTextFile(String url) throws IOException {
        List<AlgorithmInput.Passenger> passengerList = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(url));
        int counter = 0;
        String line = "";
        while (line != null) {
            line = bufferedReader.readLine();

            if (line != null) {
                double lat = Double.parseDouble(line.split(", ")[0]);
                double lng = Double.parseDouble(line.split(", ")[1]);

                Point source = new Point(lat, lng);
                Point target = new Point(31.522547, 34.5960228); // TODO Fixed Ti for now
                passengerList.add(new AlgorithmInput.Passenger("P" + counter++, source, target));
            } else {
                break;
            }
        }

        bufferedReader.close();
        return passengerList;

    }

    private static JSONObject loadJSONFromFileURL(String url) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(url));
    }

    private static JSONObject loadPathFromGoogleMaps(String source, String destination) throws IOException, ParseException {
        String apiURL = "https://maps.googleapis.com/maps/api/directions/json?origin=" + URLEncoder.encode(source, "UTF-8") + "&destination=" + URLEncoder.encode(destination, "UTF-8") + "&key=AIzaSyD56LHjhdpL7ztyU33rsph0zYYEY136nOo";

        URL url = new URL(apiURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(connection.getInputStream())));
    }

    private static double getRadius() {
        return 0.003;
    }

}

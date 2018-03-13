package inputs;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GenerateInput {


    public static void main(String[] args) {
//        getInput(); //Runs the algorithm

        test();


    }


    public static void test() {


    }


    public static AlgorithmInput getInput() {
        // Generate JSON


        // Generate passengers
        // Decide on a radius


        try {

//            String passengersURL = "C:\\Users\\DELL\\Desktop\\project\\passengers_data\\KiryatShmonaToHadera.kml";

            String passengersURL = "algo-data/kml/KiryatShmonaToHadera.kml";
            AlgorithmInput input = AlgorithmInput.getTestInstance("Kiryat Shmona", "Hadera", passengersURL, 0.003);

            return input;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // Return result
        return null;
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

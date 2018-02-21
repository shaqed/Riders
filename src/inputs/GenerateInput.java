package inputs;

import caraoke.AlgorithmDriver;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import polyline_decoder.Point;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

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


    private static List<AlgorithmInput.Passenger> generatePassengersFromXMLFile() throws IOException, SAXException, ParserConfigurationException {
        String url = "C:\\Users\\DELL\\Desktop\\project\\passengers_data\\HaifaToJerusalem.kml";
        return generatePassengersFromXMLFile(url);
    }

    private static List<AlgorithmInput.Passenger> generatePassengersFromXMLFile(String url) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(url));

        List<AlgorithmInput.Passenger> pointList = new ArrayList<>();

        /* Used with the extraction that includes the whole path */
        NodeList list = document.getElementsByTagName("Placemark");
        for (int i = 0; i < list.getLength(); i++) {
            Node point = list.item(i).getChildNodes().item(5);

            String pointName = list.item(i).getChildNodes().item(1).getTextContent();

            if (point.getNodeName().equals("Point")) {
                String cordsString = point.getTextContent().replaceAll("\\s+", "");

                String lat = cordsString.split(",")[1];
                String lng = cordsString.split(",")[0];

//                System.out.println("Lat: " + lat + " Lng: " + lng);
                Point Si = new Point(Double.parseDouble(lat), Double.parseDouble(lng));
                Point Ti = new Point(31.522547, 34.5960228); // TODO Fixed Ti for now (sderot)

                pointList.add(new AlgorithmInput.Passenger(pointName, Si, Ti));

            }
        }

//        System.out.println(list.item(5).getTextContent().replaceAll("\\s+", ""));

        return pointList;
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

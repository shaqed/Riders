package inputs;

import caraoke.LineCircleIntersection;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AlgorithmInput {

    private List<Point> pathToDestination;
    private List<Passenger> passengers;
    private double radius;

    private AlgorithmInput(String pathJSONFileURL, String passengersXMLFileURL, double radius) throws IOException, SAXException, ParserConfigurationException {
        this.pathToDestination = GetPointsFromJSON.getPoints(pathJSONFileURL);
        this.passengers = generatePassengersFromXMLFile(passengersXMLFileURL);
        this.radius = radius;
    }

    private AlgorithmInput(JSONObject pathToDestination, List<Passenger> passengers, double radius) {
        this.pathToDestination = GetPointsFromJSON.getPoints(pathToDestination);
        this.passengers = passengers;
        this.radius = radius;
    }

    public List<Point> getPathToDestination() {
        return pathToDestination;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }




    public static AlgorithmInput getInstance(String source, String dest, double radius) {
        // TODO Function
        // Create a Google Maps query based on the source and the path
        // Lookup the correct passengers file and load it

        return null;
    }


    public static AlgorithmInput getInstance(String pathToXMLFile, double radius) {
        // TODO Function
        // Take the passenger's XML file and understand the source, dest, path and passengers from it
        // This is to be done later...
        return null;
    }


    /* -- STATIC FUNCTIONS TO GET INSTANCES FOR THE ALGORITHM INPUT -- */
    public static AlgorithmInput getTestInstance(double radius) throws IOException, SAXException, ParserConfigurationException, ParseException {
        String passengersXMLURL = "C:\\Users\\DELL\\Desktop\\project\\passengers_data\\HaifaToJerusalem.kml";
        String pathFromGoogleMapsURL = "C:\\Users\\DELL\\Desktop\\project\\passengers_data\\KiryatShmonaToHadera.kml";
        AlgorithmInput input = new AlgorithmInput(loadJSONFromFileURL(pathFromGoogleMapsURL),
                                                    generatePassengersFromXMLFile(passengersXMLURL),
                                                    radius);
        return input;
    }

    public static AlgorithmInput getTestInstance(String pathFileURL, String passengersFileURL, double radius) throws ParserConfigurationException, SAXException, IOException {
        return new AlgorithmInput(pathFileURL, passengersFileURL, radius);
    }


    /**
     * CAUTION! GENERATES A GOOGLE MAPS QUERY!
     * */
    public static AlgorithmInput getTestInstance(String source, String dest, String passengersFileURL, double radius) throws IOException, SAXException, ParserConfigurationException, ParseException {
        return new AlgorithmInput(loadPathFromGoogleMaps(source, dest), generatePassengersFromXMLFile(passengersFileURL), radius);
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

        Scanner scanner = new Scanner(System.in);
        System.out.println("You're about to ask Google Maps API for something... are you sure? (y/n)");
        String ans = scanner.nextLine();
        if (ans.equals("y")) {
            URL url = new URL(apiURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            return (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(connection.getInputStream())));
        } else {
            throw new IOException("ABORTED BY USER");
        }
    }


    public static class Passenger{
        public Point s;
        public Point t;
        public String name;
        public Passenger(String name, Point s, Point t) {
            this.name = name;
            this.s = s;
            this.t = t;
        }

        @Override
        public String toString() {
            return "Passenger: " + name + " S: " + s + " T: " + t;
        }
    }

}

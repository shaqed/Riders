package inputs;

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

public class AlgorithmInput {

    private List<Point> pathToDestination;
    private List<Passenger> passengers;
    private double radius;

    // Constructor

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


    // Getters & Setters

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



    // Builder functions

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
        System.out.println("You're about to ask Google Maps API for a path... are you sure? (y/n)");
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

        public Passenger(String name, Point s) {
            this.s = s;
            this.name = name;
        }

        public Passenger(String name, Point s, Point t) {
            this.name = name;
            this.s = s;
            this.t = t;
        }

        public void setT(Point t) {
            this.t = t;
        }

        @Override
        public String toString() {
            return "Passenger: " + name + " S: " + s + " T: " + t;
        }
    }



    // This function extracts the path of the driver
    // As well as the passengers
    private static void passengersAndPathFromKML() throws Exception {
        String kmlFilePath = "C:\\Users\\DELL\\Desktop\\Sderot-Route-1 (1).kml";

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(kmlFilePath);
        NodeList folders = document.getElementsByTagName("Folder");

        List<Point> path = new ArrayList<>();
        String source = null;
        String dest = null;

        List<Passenger> passengers = new ArrayList<>();

        for (int i = 0; i < folders.getLength(); i++) {
            Node folder = folders.item(i);

            List<Node> nodes = getElementsByTagName(folder, "name");

            Node nameTag = nodes.get(0);
            if (nameTag != null) {
                String name = nameTag.getTextContent();
                if (name.equals("Passengers")) {
                    System.out.println("Extracting Passengers...");


                    List<Node> placemarks = getElementsByTagName(folder, "Placemark");

                    for (Node placemark : placemarks) {

                        // Name of passenger
                        Node nameTagOfPassenger = getElementsByTagName(placemark, "name").get(0);
                        String nameOfPassenger = nameTagOfPassenger.getTextContent();

                        // Point
                        Node pointNode = getElementsByTagName(placemark, "Point").get(0);
                        Node coordinates = getElementsByTagName(pointNode, "coordinates").get(0);
                        String cords = coordinates.getTextContent();
                        Point point = new Point(Double.valueOf(cords.split(",")[0]),
                                Double.valueOf(cords.split(",")[1]));

                        // Do not construct a passenger yet because you do not know its Ti or Si
                        // You know only of 1 point

                        passengers.add(new Passenger(nameOfPassenger, point));
                    }


                } else if (name.contains("Directions")) {

                    String drivingPath[] = name.split(" from ")[1].split(" to ");
                    source = drivingPath[0];
                    dest = drivingPath[1];

                    List<Node> placemarks = getElementsByTagName(folder, "Placemark");
                    for(Node placemark : placemarks) {

                        String nameOfPlacemark = getElementsByTagName(placemark, "name").get(0).getTextContent();


                        if (nameOfPlacemark.contains("Directions")) {
                            // A bunch of coordinates to extract

                            Node lineStringNode = getElementsByTagName(placemark, "LineString").get(0);
                            Node cordsNode = getElementsByTagName(lineStringNode, "coordinates").get(0);

                            String cordsStrings []= cordsNode.getTextContent().split("\n");
                            for (String string : cordsStrings) {
                                String splitted[] = string.split(",");

                                if (splitted.length > 1) {
                                    double lat = Double.valueOf(splitted[0]);
                                    double lng = Double.valueOf(splitted[1]);

                                    Point point = new Point(lat, lng);
                                    path.add(point);
                                }
                            }

                        } else if (nameOfPlacemark.equals(source)) {
                            Point point = extractPointFromPlacemark(placemark);
                            path.add(0, point);

                        } else if (nameOfPlacemark.equals(dest)) {
                            Point point = extractPointFromPlacemark(placemark);
                            path.add(point);
                        }

                    }


                }
            }
        }


        System.out.println("Done path contains: " + path.size() + " points from : " + source + " to " + dest);
        for (Passenger p : passengers) {
            System.out.println(p);
        }

        System.out.println("path");
        for (Point p : path) {
            System.out.println(p);
        }

    }


    private static Point extractPointFromPlacemark(Node placemarkNode) {
        Node pointNode = getElementsByTagName(placemarkNode, "Point").get(0);
        Node coordsNode = getElementsByTagName(pointNode, "coordinates").get(0);

        String cord = coordsNode.getTextContent();
        double lat = Double.valueOf(cord.split(",")[0]);
        double lng = Double.valueOf(cord.split(",")[1]);
        Point point = new Point(lat, lng);
        return point;
    }

    private static List<Node> getElementsByTagName(Node node, String name) {
    	List<Node> nodes = new ArrayList<>();
    	NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeName().equals(name)) {
				nodes.add(child);
			}
		}
		return nodes;
	}

    public static void main(String[] args) {
		try {
			passengersAndPathFromKML();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

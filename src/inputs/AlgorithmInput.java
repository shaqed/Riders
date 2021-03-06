package inputs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.math.Vectors;
import utils.parser.KMLParser;
import utils.polyline_decoder.Point;
import utils.Tags;

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

    // This because google maps can handle just addresses names [DEPRECATED]
    private String source;
    private String destination;

    // New way, better way.
    private Point pSource;
    private Point pDestintation;

    private List<Point> mainPoints; // passengers and source & destination

	private long totalPathLength;
	private double aerialVector[];

    // Constructors


	@Deprecated
	private AlgorithmInput(String kmlFile, double radius) throws Exception {
		KMLParser kmlParser = new KMLParser(kmlFile);
		this.pathToDestination = kmlParser.getPath();
		this.passengers = kmlParser.getPassengers();
		this.radius = radius;
		this.source = kmlParser.getSource();
		this.destination = kmlParser.getDestination();

		this.mainPoints = new ArrayList<>();
		this.mainPoints.add(kmlParser.getSourcePoint());
		for(Passenger p : this.passengers) {
		    this.mainPoints.add(p.s);
        }

		this.mainPoints.add(kmlParser.getDestinationPoint());

	}

	private AlgorithmInput(JSONObject jsonObject) throws Exception{
		// raw value from user
		double percentFromUser = (Double.valueOf(jsonObject.get(Tags.IO_RADIUS).toString()));

		if (percentFromUser < 0 || percentFromUser > 100) {
			System.out.println("DEBUG: Warning! radius input was not in range of[1,100] and now set to 0. No passengers will be collected!");
			percentFromUser = 0;
		}

		// As meters... originally from GMaps
		System.out.println("DEBUG: " + jsonObject.get(Tags.IO_PATH_LENGTH).toString());
		this.totalPathLength = (long) jsonObject.get(Tags.IO_PATH_LENGTH);

		// This divisions ans sqrt are to shrink the circle and the radius
		this.radius = Math.sqrt(((percentFromUser/4000000.0) * 0.01) * totalPathLength);

		System.out.println("DEBUG: Radius is " + percentFromUser +"% from " + this.totalPathLength + " is: " + this.radius);

		JSONObject source = (JSONObject) jsonObject.get(Tags.IO_SOURCE);
		double sourceLat = Double.valueOf(source.get(Tags.IO_POINT_LATITUDE).toString());
		double sourceLng = Double.valueOf(source.get(Tags.IO_POINT_LONGITUDE).toString());
		this.pSource = new Point(sourceLat, sourceLng);
		this.source = (this.pSource.getLat() + "," + this.pSource.getLng());

		JSONObject dest = (JSONObject) jsonObject.get(Tags.IO_DESTINATION);
		double destLat = Double.valueOf(dest.get(Tags.IO_POINT_LATITUDE).toString());
		double destLng = Double.valueOf(dest.get(Tags.IO_POINT_LONGITUDE).toString());
		this.pDestintation = new Point(destLat, destLng);
		this.destination = (this.pDestintation.getLat() +"," + this.pDestintation.getLng());

		this.passengers = new ArrayList<>();
		JSONArray passengersArray = (JSONArray) jsonObject.get(Tags.IO_PASSENGERS);
		for (int i = 0; i < passengersArray.size(); i++) {
			JSONObject passenger = (JSONObject) passengersArray.get(i);

			String name = (String) passenger.get(Tags.IO_PASSENGERS_NAME);

			JSONObject si = (JSONObject) passenger.get(Tags.IO_PASSENGERS_SI);
			double siLat = Double.valueOf(si.get(Tags.IO_POINT_LATITUDE).toString());
			double siLng = Double.valueOf(si.get(Tags.IO_POINT_LONGITUDE).toString());
			Point siPoint = new Point(siLat, siLng);

			JSONObject ti = (JSONObject) passenger.get(Tags.IO_PASSENGERS_TI);
			double tiLat = Double.valueOf(ti.get(Tags.IO_POINT_LATITUDE).toString());
			double tiLng = Double.valueOf(ti.get(Tags.IO_POINT_LONGITUDE).toString());
			Point tiPoint = new Point(tiLat, tiLng);

			this.passengers.add(new Passenger(name, siPoint, tiPoint));
		}

		// FILL PATH TO DESTINATION !!
		this.pathToDestination = new ArrayList<>();
		JSONArray path = (JSONArray) jsonObject.get(Tags.IO_PATH);
		for (int i = 0; i < path.size(); i++) {
			JSONObject currentPoint = (JSONObject) path.get(i);
			double lat = Double.valueOf(currentPoint.get(Tags.IO_POINT_LATITUDE).toString());
			double lng = Double.valueOf(currentPoint.get(Tags.IO_POINT_LONGITUDE).toString());

			this.pathToDestination.add(new Point(lat, lng));
		}

		this.aerialVector = this.computeVector(this.pSource, this.pDestintation);

	}

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

	public Point getpSource() {
		return pSource;
	}

	public String getSource() {
		return source;
	}

	public long getTotalPathLength() {
		return totalPathLength;
	}

	public Point getpDestintation() {
		return pDestintation;
	}

	public String getDestination() {
		return destination;
	}

	public double[] getAerialVector() {
		return aerialVector;
	}

	private double[] computeVector(Point p1, Point p2) {
		return Vectors.computeVector(p1, p2);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Source: ");
		stringBuilder.append(this.pSource.toString());

		stringBuilder.append(" Destination: ");
		stringBuilder.append(pDestintation.toString());

		stringBuilder.append(this.passengers.toString());

		return stringBuilder.toString();
	}

	/**
     * Returns a URL to Google Maps that displays the given route
     * Paste that URL to your browser to see the route
     * @param passengers The passengers available to choose from
     * @param path The route that is to be calculated from the passengers provided (this is the output of the algorithm)
     * @return An HTTP url to Google Maps with the parameters.
     * */
	public String getResultOnGoogleMaps(List<Passenger> passengers, List<Integer> path) {
        try {
            final String UTF8 = "utf-8";
            String divider = "/";

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("https://www.google.co.il/maps/dir/");

            stringBuilder.append(URLEncoder.encode(this.getSource(), UTF8));
            stringBuilder.append(divider);

            for (int i = 1; i < path.size() - 1; i++) {
                int index = path.get(i);

                Passenger p = passengers.get(index-1);

                stringBuilder.append(URLEncoder.encode(p.s.getLat() + ", " + p.s.getLng(), UTF8));
                stringBuilder.append(divider);
            }
            stringBuilder.append(URLEncoder.encode(this.getDestination(), UTF8));

            return stringBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }

	public void setRadius(double radius) {
        this.radius = radius;
    }



    // Builder functions
    public static AlgorithmInput getInstance(JSONObject jsonObject) {
		try {
			AlgorithmInput input = new AlgorithmInput(jsonObject);

			// debug
			System.out.println("Creation from JSON was successful");
			System.out.println(input.toString());

			return input;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("COULDNT CREATE AN INPUT FROM JSON");
			return null;
		}
	}


    public static AlgorithmInput getInstance(String pathToXMLFile, double radius) {
        // TODO Function
        // Take the passenger's XML file and understand the source, dest, path and passengers from it
        // This is to be done later...

		AlgorithmInput input = null;

		try {
			input = new AlgorithmInput(pathToXMLFile, radius);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return input;
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
        public double[] aerialVector;

        public Passenger(JSONObject jsonObject) {
            JSONObject siJSON = (JSONObject) jsonObject.get(Tags.IO_PASSENGERS_SI);
            double siLat = (double) siJSON.get(Tags.IO_POINT_LATITUDE);
            double siLng = (double) siJSON.get(Tags.IO_POINT_LONGITUDE);

            JSONObject tiJSON = (JSONObject) jsonObject.get(Tags.IO_PASSENGERS_TI);
            double tiLat = (double) tiJSON.get(Tags.IO_POINT_LATITUDE);
            double tiLng = (double) tiJSON.get(Tags.IO_POINT_LONGITUDE);

            this.s = new Point(siLat, siLng);
            this.t = new Point(tiLat, tiLng);

            if (jsonObject.containsKey(Tags.IO_PASSENGERS_NAME)) {
            	this.name = (String) jsonObject.get(Tags.IO_PASSENGERS_NAME);
			} else {
            	this.name = "UnnamedPassenger";
			}

			this.aerialVector = Vectors.computeVector(this.s, this.t);

        }

        public Passenger(String name, Point s) {
            this.s = s;
            this.name = name;
        }

        public Passenger(String name, Point s, Point t) {
            this.name = name;
            this.s = s;
            this.t = t;
            this.aerialVector = Vectors.computeVector(this.s, this.t);
        }


        public void setT(Point t) {
            this.t = t;
        }

        @Override
        public String toString() {
            return "Passenger: " + name + " S: " + s + " T: " + t;
        }
    }


    public static void main(String[] args) {

	}


}

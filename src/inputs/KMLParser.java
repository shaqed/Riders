package inputs;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import polyline_decoder.Point;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is parsing the XML from the KML file
 * And allows you to easily extract the source, destination, path and passengers from it
 *
 * How to use:
 * 		1. Call the constructor with the path to the KML File
 * 			If any of the following hasn't been extracted from the file for what-ever reason, an exception will be thrown
 * 				1. Source name
 * 				2. Destination name
 * 				3. Path
 * 				4. Passengers
 * 		2. If the constructor didn't throw any exception, call the getters and get what you want
 *
 * For debugging purposes, there's also an implemented toString() for you to use
 *
 * */
public class KMLParser {
	public static void main(String[] args) {
//		String fileURL = "algo-data/kml/Sderot-Route-1.kml";
		String fileURL = "C:\\Users\\DELL\\Desktop\\Sderot-Route-1 (1).kml";

		try {
			KMLParser parser = new KMLParser(fileURL);
			System.out.println(parser.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	private List<Point> path;
	private List<AlgorithmInput.Passenger> passengers;
	private String destination;
	private String source;

	public KMLParser(String kmlFileLocation) throws Exception {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(kmlFileLocation);
			init(document);

			StringBuilder errorBuilder = new StringBuilder();
			if (this.passengers == null) {
				errorBuilder.append("Couldn't find passengers in the file\n");
			}
			if (this.path == null) {
				errorBuilder.append("Couldn't find path in the file\n");
			}
			if (this.destination == null) {
				errorBuilder.append("Couldn't find the source in the file\n");
			}
			if (this.destination == null){
				errorBuilder.append("Couldn't find the destination in the file\n");
			}
			String error = errorBuilder.toString();
			if (error.length() > 0) {
				throw new Exception(error);
			}


		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Creation of KML failed. check the logs above");
		}
	}

	private void init(Document document) {

		NodeList folders = document.getElementsByTagName("Folder");

		for (int i = 0; i < folders.getLength(); i++) {
			Node folder = folders.item(i);
			String nameOfFolder = getNameOfFolder(folder);

			if (nameOfFolder.equals("Passengers")) {

				this.passengers = extractPassengersFromFolder(folder);

			} else if (nameOfFolder.contains("Directions")) {

				this.path = extractPathFromFolder(folder);
				extractSourceAndDestFromFolder(folder);
			}

		}

	}


	public List<Point> getPath() {
		return path;
	}

	public Point getSourcePoint() {
		return this.path.get(0);
	}

	public Point getDestinationPoint() {
		return this.path.get(this.path.size()-1); // get last point in the list
	}

	public List<AlgorithmInput.Passenger> getPassengers() {
		return passengers;
	}

	public String getDestination() {
		return destination;
	}

	public String getSource() {
		return source;
	}

	private void extractSourceAndDestFromFolder(Node folder) {
		List<Node> nodes = getElementsByTagName(folder, "name");
		Node nameTag = nodes.get(0);
		String name = nameTag.getTextContent();

		if (name.contains("Directions")) {

			String drivingPath[] = name.split(" from ")[1].split(" to ");
			this.source = drivingPath[0];
			this.destination = drivingPath[1];

			List<Node> placemarks = getElementsByTagName(folder, "Placemark");
			for(Node placemark : placemarks) {

				String nameOfPlacemark = getElementsByTagName(placemark, "name").get(0).getTextContent();

				// Add the points to the existing path
				if (nameOfPlacemark.equals(this.source)) {
					Point point = extractPointFromPlacemark(placemark);
					this.path.add(0, point);

				} else if (nameOfPlacemark.equals(this.destination)) {
					Point point = extractPointFromPlacemark(placemark);
					this.path.add(point);
				}

			}
		}
	}

	private List<AlgorithmInput.Passenger> extractPassengersFromFolder(Node folder) {
		List<AlgorithmInput.Passenger> passengers = new ArrayList<>();
		List<Node> nodes = getElementsByTagName(folder, "name");

		Node nameTag = nodes.get(0);
		if (nameTag != null) {
			String name = nameTag.getTextContent();
			if (name.equals("Passengers")) {

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

					passengers.add(new AlgorithmInput.Passenger(nameOfPassenger, point));
				}


			}
		}
		return passengers;
	}

	private List<Point> extractPathFromFolder(Node folder) {
		List<Point> path = new ArrayList<>();
		List<Node> nodes = getElementsByTagName(folder, "name");

		Node nameTag = nodes.get(0);
		if (nameTag != null) {
			String name = nameTag.getTextContent();
			if (name.contains("Directions")) {
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
					}
				}
			}
		}
		return path;
	}

	private String getNameOfFolder(Node folder) {
		Node nameTag = getElementsByTagName(folder, "name").get(0);
		return nameTag.getTextContent();
	}

	private Point extractPointFromPlacemark(Node placemarkNode) {
		Node pointNode = getElementsByTagName(placemarkNode, "Point").get(0);
		Node coordsNode = getElementsByTagName(pointNode, "coordinates").get(0);

		String cord = coordsNode.getTextContent();
		double lat = Double.valueOf(cord.split(",")[0]);
		double lng = Double.valueOf(cord.split(",")[1]);
		Point point = new Point(lat, lng);
		return point;
	}

	private List<Node> getElementsByTagName(Node node, String name) {
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


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Source:");
		builder.append(this.getSource());
		builder.append('\n');
		builder.append("Destination: ");
		builder.append(this.getDestination());
		builder.append('\n');

		builder.append("Passengers:\n");
		List<AlgorithmInput.Passenger> passengers = this.getPassengers();
		for (AlgorithmInput.Passenger p: passengers) {
			builder.append(p);
			builder.append('\n');
		}
		List<Point> path = this.getPath();
		builder.append("Path (total of: ");
		builder.append(path.size());
		builder.append(" points)\n");

		for (Point p : path) {
			builder.append(p);
			builder.append('\n');
		}

		return builder.toString();
	}
}

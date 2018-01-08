package inputs;

import org.json.simple.JSONObject;
import polyline_decoder.Point;

import java.util.List;

public class AlgorithmInput {

    private JSONObject pathToDestination;
    private List<Passenger> passengers;
    private double radius;

    public AlgorithmInput(JSONObject pathToDestination, List<Passenger> passengers, double radius) {
        this.pathToDestination = pathToDestination;
        this.passengers = passengers;
        this.radius = radius;
    }

    public JSONObject getPathToDestination() {
        return pathToDestination;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public double getRadius() {
        return radius;
    }

    public static class Passenger{
        public Point s;
        public Point t;
        public String name;
        public Passenger(String name, Point s, Point t) {
            this.s = s;
            this.t = t;
        }

        @Override
        public String toString() {
            return "Passenger: " + name;
        }
    }
}

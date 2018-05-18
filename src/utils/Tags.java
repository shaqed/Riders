package utils;

/**
 * Common strings between the clients and the server.
 * The API will use these constants to process the inputs
 * Details on the input are given in the Servlet documentation
 * */
public class Tags {
	public static final String IO_RADIUS = "radius";
	public static final String IO_SOURCE = "source";
	public static final String IO_DESTINATION = "dest";
	public static final String IO_PATH = "path";

	public static final String IO_POINT_LATITUDE = "lat";
	public static final String IO_POINT_LONGITUDE = "lng";

	public static final String IO_PASSENGERS = "passengers";
	public static final String IO_PASSENGERS_NAME = "name";
	public static final String IO_PASSENGERS_SI = "si";
	public static final String IO_PASSENGERS_TI = "ti";

	// answer

	public static final String IO_ROUTE = "route";
	public static final String IO_ERROR_MESSAGE = "errorMessage";
	public static final String IO_ERROR_CODE = "errorCode";

	// online

	public static final String IO_NEW_PASSENGER = "newPassenger";
}

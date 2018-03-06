package utils;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;


/**
 * Attach this class for a running instance of the algorithm for logs
 * */
public class Logger {
	public static void main(String[] args) {
		JSONObject jsonObject = new JSONObject();

//		jsonObject.get
	}


	private OutputStream outputStream;
	private boolean verbose = true;

	public Logger() throws Exception {
		File logDir = new File("algo-data/logs");
		if (!logDir.exists()) {
			if (!logDir.mkdir()) {
				throw new Exception("Couldn't create the folder");
			}
		}

		File logFile = new File("algo-data/logs/" + getTimeString() + ".txt");
		if (!logFile.createNewFile()) {
			throw new Exception("Couldn't create a log file");
		}
	}

	private String getTimeString() {
		return new SimpleDateFormat("yyyyMMdd-HHmmss").format(System.currentTimeMillis());
	}

	private void debug(String msg) {
		if (verbose) {
			System.out.println(msg);
		}
	}
}

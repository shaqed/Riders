package caraoke;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketDriver {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4040);
            System.out.println("Waiting for the client");
            Socket socket = serverSocket.accept();

//            AlgorithmDriver.main(null);


            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

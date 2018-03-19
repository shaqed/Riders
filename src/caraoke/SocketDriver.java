package caraoke;

import utils.GlobalFunctions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketDriver {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4040);
            System.out.println("Waiting for a client on port: " + serverSocket.getLocalPort());
            Socket socket = serverSocket.accept();

//            AlgorithmDriver.main(null);


            GlobalFunctions.readInputStream(socket.getInputStream());
            System.out.println("End of connection");
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

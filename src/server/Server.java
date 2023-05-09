package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;

public class Server {
	
	private static int port = 5555;
	private static String ip = "localhost";
	private static String username = "Sam";
	
//	private static void readArgs(String[] args) {
//
//		// Check the format of args correct
//		if (args.length != 2) {
//			System.out.println("Usage: java -jar CreateWhiteBoard.jar <serverIPAddress> <serverPort> username");
//			System.exit(1);
//		}
//
//		try {
//			ip = args[0];
//			port = Integer.parseInt(args[1]);
//			username = args[2];
//
//			if (port <= 1023 || port >= 65536) {
//				System.out.println("Port out of range. Please enter a port number between 1024 and 65535 (inclusive)");
//				System.exit(1);
//			}
//			
//		} catch (NumberFormatException e) {
//			System.out.println("Invalid port number. Please enter a port number between 1024 and 65535 (inclusive)");
//			System.exit(1);
//		} catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println("Missing arguments. Usage: java -jar DictionaryServer.jar <port> <dictionary-file>");
//			System.exit(1);
//		} catch (Exception e) {
//			System.out.println("An unexpected error occurred while reading arguments");
//			System.exit(1);
//		}
//	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ServerSocket server = new ServerSocket(port);
			System.out.println("Server is created. Waiting connection ...");
			
			while (true) {
				Socket socket = server.accept();
				DataInputStream in = new DataInputStream(socket.getInputStream());
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

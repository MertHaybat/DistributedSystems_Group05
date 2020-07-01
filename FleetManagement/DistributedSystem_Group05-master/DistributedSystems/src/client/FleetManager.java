package client;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import shared.bo.Car;
import shared.communication.TCPClient;
import shared.message.Message;

public class FleetManager {

	private static Message message;	

		
	public Message start() throws IOException {
		
	
		
		System.out.println("If you want to create a new car please press 1 and enter.");
		
		Scanner scanner = new Scanner(System.in);
		Car car = null;
		
		if (scanner.next().equals("1")) {
				System.out.println("Please name car:");
				car = new Car(scanner.next());
				message = new Message(car.getCar(), "admin", 0);
		}
		return message;
	}
}

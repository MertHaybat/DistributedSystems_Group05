package client;

import java.io.IOException;
import java.util.Scanner;

import shared.bo.Car;
import shared.message.Message;

public class Renter {
	private static Message message;
	
	public Message startRent() throws IOException {
			
		
			
			System.out.println("Please type in your name.");
			
			Scanner scanner = new Scanner(System.in);
			Car car = null;
			String assignedTo = scanner.next();
			
			System.out.println("If you want to (un)rent a car please type in the name.");
			car = new Car(scanner.next());
			message = new Message(car.getCar(), assignedTo, 0);
			
			return message;
	}
}

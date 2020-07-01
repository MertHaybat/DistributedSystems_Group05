package client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import server.cluster.communication.CastController;
import server.cluster.communication.CastPublisher;
import shared.Constants;
import shared.PortUsage;
import shared.bo.Car;
import shared.communication.MulticastReceiver;
import shared.communication.TCPClient;
import shared.message.ClientJoinMessage;
import shared.message.Message;

public class ClientApplication  {
	
	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {

		PortUsage portTest = new PortUsage();
		int tcpPort = 0;
		for(int i = 0; i<Constants.tcpPorts.length; i++) {
			try {
				if(portTest.isPortInUse(null, Constants.tcpPorts[i])==true){
					tcpPort = Constants.tcpPorts[i];
					break;
				}
				
			} catch (Exception e) {
				System.out.println("Port already in use: " + e.getMessage());
			}
			
		}
		
		TCPClient client = new TCPClient(InetAddress.getLocalHost(), tcpPort);
		
		
		List<Car> carList;
		carList = client.sendClientJoinMessage(new ClientJoinMessage());
		
		if(carList.size() == 0) {
			System.out.println("No cars in the system.");
		} else {
		
			String leftAlignFormat = "| %-7s | %-11s | %-20s |%n";

			System.out.format("+---------+--------------+----------------------+%n");
			System.out.format("| Car     |    Status    |       Assigned to    |%n");
			System.out.format("+---------+--------------+----------------------+%n");
			
			for (Car car : carList) {
				System.out.format(leftAlignFormat, car.getCar(), car.getStatus(), car.getAssignedTo());
			}
			
			System.out.format("+---------+--------------+----------------------+%n");
		}
		
		
		InetAddress multicastAddress = Constants.getClientMulticastAddress();
		int multicastPort = Constants.multicastPort;
		
		CastPublisher castPublisher = new CastPublisher(multicastAddress, Constants.multicastPort);
		CastController  castController = new CastController(castPublisher);
		new Thread(new MulticastReceiver()).start();
		FleetManager fleetmanager = new FleetManager();
		Message s = fleetmanager.start();
		
		
		if(s != null) {
			client.sendMessage(s);
		}
		
		Thread.sleep(2000);
	}
	
	
	
	
}

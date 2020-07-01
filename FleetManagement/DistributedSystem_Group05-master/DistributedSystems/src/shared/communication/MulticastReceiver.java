package shared.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.HoldBackQueueClient;
import shared.Constants;
import shared.bo.Car;

public class MulticastReceiver implements Runnable {
	
	protected MulticastSocket socket = null;
    protected byte[] buf = new byte[2048];
    private Map<Long, List<Car>> carMap;
    private Map<Long, List<Car>> carMapAfterQueue;
    private HoldBackQueueClient holdbackQueueClient = new HoldBackQueueClient();
    private List<Car> carList;
    public MulticastReceiver() {

    }
    
    @Override
    public void run() {
    	try {
        socket = new MulticastSocket(Constants.multicastClientPort);
        InetAddress group = Constants.getClientMulticastAddress();
        socket.joinGroup(group);
        socket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()));
     
	        while (true) {
	        	DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
	           
	            
	            carMap = (Map<Long, List<Car>>) inputStream.readObject();
	            
	            holdbackQueueClient.addCarlistToQueue(carMap);
	            
	            carMapAfterQueue = holdbackQueueClient.getMessagesFromCarListQueue();
	            
	            for(Map.Entry <Long, List<Car>> entry : carMapAfterQueue.entrySet()) {
	            	carList = entry.getValue();
	            }
	            
	            String leftAlignFormat = "| %-7s | %-11s | %-20s |%n";

				System.out.format("+---------+--------------+----------------------+%n");
				System.out.format("| Car     |    Status    |       Assigned to    |%n");
				System.out.format("+---------+--------------+----------------------+%n");
				
				for (Car car : carList) {
					System.out.format(leftAlignFormat, car.getCar(), car.getStatus(), car.getAssignedTo());
				}
				
				System.out.format("+---------+--------------+----------------------+%n");
	            
	            
	
	        }
    	}
    	catch(Exception e) {
    		System.err.println("Client Multicast Error: " + e.getMessage());
    	}
    }

}

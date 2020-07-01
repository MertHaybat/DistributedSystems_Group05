package shared.communication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shared.bo.Car;
import shared.message.ClientJoinMessage;
import shared.message.HeartbeatMessage;
import shared.message.Message;
import shared.message.RebootMessage;
import shared.message.ReplicaMessage;

public class TCPClient {
	
	private InetAddress inet;
	private int port;
	
	public TCPClient(InetAddress inet, int port) {
		this.inet = inet;
		this.port = port;
	}
	
	public String sendHeartbeatMessage(HeartbeatMessage message) throws IOException {
		int maxRetries = 5;
		boolean status = true;
		if (maxRetries != 0) {
		while(status) {
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(inet.getHostAddress(), port));
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());				
				
				out.writeObject(message);
				out.flush();
				status = false;
				return in.readUTF();
	
				} catch(IOException e) {
				maxRetries = maxRetries - 1;
				if(maxRetries == 0) {
					return null;
				}
				}
			}
		}
		throw new ConnectException("Connection could not be established after retries.");
	}
	public void sendRebootMessage(RebootMessage message){
		boolean status = true;
		while(status) {

			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(inet.getHostAddress(), port));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			    outputStream.writeObject(message);
			    outputStream.flush();
				
				status = false;

			} catch (Exception e) {
				System.err.println("Sending message failed: " + e.getMessage());
			}
//			return null;
		}
	}
	
	public void sendReplicaFromMulticastToServer(ReplicaMessage message){
		boolean status = true;
		while(status) {

			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(inet.getHostAddress(), port));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
			    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			    outputStream.writeObject(message);
			    outputStream.flush();
				status = false;
//				return input.readUTF();

			} catch (Exception e) {
				System.err.println("Sending message failed: " + e.getMessage());
			}
//			return null;
		}
	}
	public List<Car> sendClientJoinMessage(ClientJoinMessage message) throws ClassNotFoundException{
		while(true) {
			try (Socket socket = new Socket())	{
				socket.connect(new InetSocketAddress(inet.getHostAddress(), port));
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
				
				out.writeObject(message);
				out.flush();
				
				List<Car> carList = new ArrayList<Car>();
				carList = (List<Car>) inputStream.readObject();
				
				return carList;

			} catch (IOException e) {
				System.err.println("Connection failed: " + e.getMessage());
			}
			return null;
		}
	}
	
	public String sendMessage(Message message) {
		
		while(true) {
			try (Socket socket = new Socket())	{
				socket.connect(new InetSocketAddress(inet.getHostAddress(), port));
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				
				out.writeObject(message);
				out.flush();
				
				return in.readUTF();
				
//				try (
//						OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
//						BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
//					if(message.getSenderName().equals("admin")) {
//						System.out.println("Sending message to: " + inet.getHostName() + "c:" + message);
//	                    writer.write("c:" + message + "\n");
//	                    writer.flush();
//					} else {
//						System.out.println("Sending message to: " + inet.getHostName() + "Assign: " + message);
//						writer.write("Assign: " + message.getMessage());
////						+ "To: "+ message.getSenderName()+ "\n");
//	                    writer.flush();
//					}
//	                    
//
//	                    return reader.readLine();
//	                } catch (Exception e) {
//	                	System.err.println("Failed to send message" + e.getMessage());
//	                }
			} catch (IOException e) {
				System.err.println("Connection failed: " + e.getMessage());
			}
			return null;
		}
	
	}
}



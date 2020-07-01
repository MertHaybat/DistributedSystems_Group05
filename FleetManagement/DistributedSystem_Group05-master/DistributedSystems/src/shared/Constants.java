package shared;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Constants {
	public static final int clientServerPort = 8888;
	public static final int multicastPort = 5443;
	public static int[] tcpPorts = {8001, 8002, 8003};
	public static final int multicastClientPort = 4446;
	public static int leaderPort = 0;
	
	
	public static InetAddress getClientMulticastAddress() throws UnknownHostException {
		return InetAddress.getByName("228.5.6.7");
	}
	public static InetAddress getMulticastAddress() throws UnknownHostException {
			return InetAddress.getByName("230.0.0.1");
	}
	
	public static int getTcpPort() {
	PortUsage portTest = new PortUsage();
	int tcpPort = 0;
	for(int i = 0; i<Constants.tcpPorts.length; i++) {
		try {
			
			if(portTest.isPortInUse(null, Constants.tcpPorts[i])==false){
				
				tcpPort = Constants.tcpPorts[i];
				break;
			}
			if(i == 2) {
				System.out.println("Max. Server already created!");
				break;
			}
			
		} catch (Exception e) {
			System.out.println("Port already in use: " + e.getMessage());
		}
		
	}
	return tcpPort;
	}
	

}

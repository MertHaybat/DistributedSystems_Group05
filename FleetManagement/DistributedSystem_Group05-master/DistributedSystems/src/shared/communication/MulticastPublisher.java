package shared.communication;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import shared.Constants;
import shared.bo.Car;

public class MulticastPublisher {
	private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;
 
    public void multicast(Map<Long, List<Car>> mappedCarlist) throws IOException {
    	if (socket == null) {
			socket = new DatagramSocket();
		}
        socket = new DatagramSocket();
        group = Constants.getClientMulticastAddress();

        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(mappedCarlist);
        byte[] listData = out.toByteArray();
    
        
        DatagramPacket packet = new DatagramPacket(listData, listData.length, group, Constants.multicastClientPort);
        socket.send(packet);
        
        
    }
}

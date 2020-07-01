package server.cluster.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shared.Constants;
import shared.bo.Car;
import shared.message.JoinMessage;
import shared.message.MemberInfoMessage;
import shared.message.Message;
import shared.message.ReplicaMessage;

public class CastPublisher {

	private DatagramSocket socket;
	private InetAddress group;
	private int multicastPort;
	private byte[] buf;

	public CastPublisher(InetAddress group, int multicastPort) {
		this.multicastPort = multicastPort;
		this.group = group;
	}

	public void multicastCarlistToReplica(ReplicaMessage message) {
		try {
			if (socket == null) {
				socket = new DatagramSocket();
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(message);
			byte[] listData = out.toByteArray();

			DatagramPacket packet = new DatagramPacket(listData, listData.length, group, multicastPort);
			socket.send(packet);
		} catch (Exception e) {
			System.err.println("Sending replicas failed: " + e.getMessage());
		}
	}

	public void memberinfoToMembers(MemberInfoMessage message) {
		try {
			if (socket == null) {
				socket = new DatagramSocket();
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(message);
			byte[] listData = out.toByteArray();

			DatagramPacket packet = new DatagramPacket(listData, listData.length, group, multicastPort);
			socket.send(packet);
		} catch (Exception e) {
			System.err.println("Sending Cluster to Members failed: " + e.getMessage());
		}
	}

	public void memberJoinMessage(JoinMessage message) {
		try {
			if (socket == null) {
				socket = new DatagramSocket();
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(message);

			byte[] buf = baos.toByteArray();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, group, multicastPort);

			socket.send(dp);
		} catch (IOException e) {
			throw new RuntimeException("Failed to send joining member multicast message.", e);
		}
	}

}

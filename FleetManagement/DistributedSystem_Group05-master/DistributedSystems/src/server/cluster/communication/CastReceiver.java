package server.cluster.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import client.HoldBackQueueClient;
import server.cluster.Member;
import shared.bo.Car;
import shared.communication.TCPClient;
import shared.message.JoinMessage;
import shared.message.MemberInfoMessage;
import shared.message.Message;
import shared.message.ReplicaMessage;


public class CastReceiver implements Runnable {
	
	protected InetAddress group;
	protected InetAddress networkInterface;
	protected int multicastPort;
	protected MulticastSocket socket = null;
	protected byte[] buf = new byte[2048];
	protected Message message;
	private MessageHandler messageHandler;
	private MemberMessageHandler memberMessageHandler;
	private ReplicaHandler replicaHandler;
	
	private HoldBackQueueClient holdbackQueueClient = new HoldBackQueueClient();
	private List<Car> carList;
	private Member member;
	private Map<Integer, Member> membersForReplica = new ConcurrentHashMap<>();
	
	
	public CastReceiver(int multicastPort, InetAddress group, Member member) {
		this.member = member;
		this.multicastPort = multicastPort;
		this.group = group;
		
	}

	public CastReceiver(Member member, int multicastPort, InetAddress group, InetAddress networkInterface,
			MessageHandler messageHandler, MemberMessageHandler memberMessageHandler, ReplicaHandler replicaHandler) {
		this.member = member;
		this.multicastPort = multicastPort;
		this.group = group;
		this.networkInterface = networkInterface;
		this.messageHandler = messageHandler;
		this.memberMessageHandler = memberMessageHandler;
		this.replicaHandler = replicaHandler;

	}
	public interface ReplicaHandler {
        void handle(ReplicaMessage message);
        

        List<Class<? extends ReplicaMessage>> getResponsibleReplicaMessageTypes();
   }
	public interface MessageHandler {
	        void handle(JoinMessage message);
	        

	        List<Class<? extends JoinMessage>> getResponsibleMessageTypes();
	   }
	 public interface MemberMessageHandler {
	        void handle(MemberInfoMessage message);

	        List<Class<? extends MemberInfoMessage>> getMemberMessageTypes();
	    }

		@Override
		public void run() {
			try {
				socket = new MulticastSocket(multicastPort);
				socket.joinGroup(group);
				socket.setBroadcast(true);
				socket.setInterface(networkInterface);

				while (true) {
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					socket.receive(packet);
					ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

					try {
						Object readObject = ois.readObject();
						if (readObject instanceof JoinMessage) {
							JoinMessage message = (JoinMessage) readObject;
							messageHandler.handle(message);

						} else if (readObject instanceof ReplicaMessage) {
							
							if(readObject != null) {
							Map<Long, List<Car>> receivedMap;
							
							ReplicaMessage message = (ReplicaMessage) readObject;
							receivedMap = message.getMappedCarlist();
							holdbackQueueClient.addCarlistToQueue(receivedMap);
							Map<Long, List<Car>> carMapAfterQueue;
							carMapAfterQueue = holdbackQueueClient.getMessagesFromCarListQueue();
							replicaHandler.handle(message);
							
							} 
							
						} else if (readObject instanceof MemberInfoMessage) {
							MemberInfoMessage message = (MemberInfoMessage) readObject;
							membersForReplica = message.getMembers();
							memberMessageHandler.handle(message);

						} else {
							
						}

					} catch (Exception e) {
						System.err.println(
								"No object could be read from the received UDP datagram. " + e.getLocalizedMessage());
					}

				}
			} catch (IOException e) {
				System.err.println("Multicast Error: " + e.getMessage());
			}
		}
	

	

	
	
}

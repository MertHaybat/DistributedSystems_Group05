package shared.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import server.HoldBackQueue;
import server.cluster.Member;
import server.cluster.communication.CastPublisher;
import shared.Constants;
import shared.bo.Car;
import shared.message.ClientJoinMessage;
import shared.message.HeartbeatMessage;
import shared.message.Message;
import shared.message.RebootMessage;
import shared.message.ReplicaMessage;


public class TCPServer implements Runnable{
	
	List<Car> carList = new ArrayList<Car>();
	private int port;
	private ExecutorService executor;
	private AtomicLong sequence = new AtomicLong(0);
	private Map<Long, List<Car>> mappedCarlist = new HashMap<>();
	
	public TCPServer (int port){
		this.port = port;
		this.executor = Executors.newCachedThreadPool();
		
//		run();
	}
	
	
	@Override
	public void run() {
		try {
			System.out.println("Starting TCP Server on port: " + port);
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket socket = server.accept();
                executor.execute(new Handler(socket));
            }
        } catch (IOException e) {
           	System.err.println("Error starting TCP Server: " + e.getMessage());
            throw new RuntimeException(e);
        }
	}
	
	
	
	
	private class Handler implements Runnable{

		private Socket client;
		private final HoldBackQueue holdBackQueue = new HoldBackQueue();
		
		
		public Handler (Socket client) {
			this.client = client;
		}
		
		@Override
		public void run() {
			try {
				MulticastPublisher multicastPublisher;
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());

				Object obj = in.readObject();
				if (obj instanceof Message) {

					Message message = (Message) obj;
					message.setSequence(sequence.getAndIncrement());
					holdBackQueue.addMessageToQueue(message);
					Message messageAfterQueue;
					messageAfterQueue = holdBackQueue.getMessagesFromQueue();

					while (messageAfterQueue != null) {
						if (messageAfterQueue.getSenderName().equals("admin")) {
							carList.add(new Car(messageAfterQueue.getMessage(), "Available"));

							String leftAlignFormat = "| %-7s | %-11s | %-20s |%n";

							System.out.format("+---------+--------------+----------------------+%n");
							System.out.format("| Car     |    Status    |       Assigned to    |%n");
							System.out.format("+---------+--------------+----------------------+%n");

							for (Car car : carList) {
								System.out.format(leftAlignFormat, car.getCar(), "Available", "");
							}

							System.out.format("+---------+--------------+----------------------+%n");

							mappedCarlist = new HashMap<>();
							mappedCarlist.put(message.getSequence(), carList);
							multicastPublisher = new MulticastPublisher();
							multicastPublisher.multicast(mappedCarlist);

							CastPublisher multicasttorepilica = new CastPublisher(Constants.getMulticastAddress(),
									Constants.multicastPort);
							ReplicaMessage replicaMessage = new ReplicaMessage(mappedCarlist);
							multicasttorepilica.multicastCarlistToReplica(replicaMessage);

						} else if (messageAfterQueue.getSenderName().equals("Leader")) {
							System.out.println("Leaderinfo: " + messageAfterQueue.getMessage());
						} else {

							for (Car car : carList) {
								if (car.getCar().equals(messageAfterQueue.getMessage())) {
									if (!car.getStatus().equals("Rented")) {
										car.setStatus("Rented");
										car.setAssignedTo(messageAfterQueue.getSenderName());
										car.setSequence();
										mappedCarlist = new HashMap<>();
										mappedCarlist.put(message.getSequence(), carList);
										multicastPublisher = new MulticastPublisher();
										multicastPublisher.multicast(mappedCarlist);
										CastPublisher multicasttorepilica = new CastPublisher(
												Constants.getMulticastAddress(), Constants.multicastPort);
										ReplicaMessage replicaMessage = new ReplicaMessage(mappedCarlist);
										multicasttorepilica.multicastCarlistToReplica(replicaMessage);

									} else {
										if (car.getAssignedTo().equals(messageAfterQueue.getSenderName())) {
											car.setStatus("Available");
											car.setAssignedTo("");
											car.setSequence();
											mappedCarlist = new HashMap<>();
											mappedCarlist.put(message.getSequence(), carList);
											multicastPublisher = new MulticastPublisher();
											multicastPublisher.multicast(mappedCarlist);
											CastPublisher multicasttorepilica = new CastPublisher(
													Constants.getMulticastAddress(), Constants.multicastPort);
											ReplicaMessage replicaMessage = new ReplicaMessage(mappedCarlist);
											multicasttorepilica.multicastCarlistToReplica(replicaMessage);

											out.writeUTF("Car is unrented");
											out.flush();
										}

										out.writeUTF("Car is already rented");
										out.flush();
										return;
									}

								}
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
						messageAfterQueue = null;
					}
				} else if (obj instanceof HeartbeatMessage) {

					HeartbeatMessage heartbeatMessage = (HeartbeatMessage) obj;

					while (heartbeatMessage != null) {
						heartbeatMessage = null;
					}
				} else if (obj instanceof ClientJoinMessage) {

					ClientJoinMessage clientJoinMessage = (ClientJoinMessage) obj;

					while (clientJoinMessage != null) {
						out.writeObject(carList);
						out.flush();
						clientJoinMessage = null;
					}
				} else if (obj instanceof ReplicaMessage) {
					ReplicaMessage replicaMessage = (ReplicaMessage) obj;
					while (replicaMessage != null) {
						if (replicaMessage.getMappedCarlist().entrySet().size() == 0) {
						} else {
							for (Map.Entry<Long, List<Car>> entry : replicaMessage.getMappedCarlist().entrySet()) {

								carList = entry.getValue();
								System.out.println("Replica sequence: " + entry.getKey());
								sequence.set(entry.getKey());

							}
						}
						replicaMessage = null;
					}
				} else if (obj instanceof RebootMessage) {
					RebootMessage rebootMessage = (RebootMessage) obj;
					Member member = rebootMessage.getMember();
					while (rebootMessage != null) {
						TCPClient tcpclient = new TCPClient(InetAddress.getByName(member.getMemberAddress()),
								member.getPort());
						tcpclient.sendReplicaFromMulticastToServer(new ReplicaMessage(mappedCarlist));
						rebootMessage = null;
					}
				}

			} catch (Exception e) {
				System.err.println("TCP Handler Error: " + e.getMessage());
			}
		}
		
	}

}

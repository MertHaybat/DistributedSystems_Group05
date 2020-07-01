package server.cluster.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

import server.cluster.LeaderElectionImpl;
import server.cluster.Member;
import shared.communication.TCPClient;
import shared.message.HeartbeatMessage;
import shared.message.MemberInfoMessage;
import shared.message.Message;

public class Heartbeat {

	private Supplier<Set<Member>> members;
	private Consumer<Member> transmissionFailureHandler;
	private ExecutorService executorService;
	private HeartbeatMessage message;
	private Map<Integer, Member> memberMapped;

	public Heartbeat(Consumer<Member> transmissionFailureHandler) {
		this.transmissionFailureHandler = transmissionFailureHandler;
		this.executorService = Executors.newFixedThreadPool(20);
		message = new HeartbeatMessage();
	}

	public void sendReplica() {

	}

	public void sendMessageToAllMembers(Map<Integer, Member> members) {
		this.memberMapped = members;
		System.out.println("Heartbeat: " + members.toString());

		for (Map.Entry<Integer, Member> entry : members.entrySet()) {
			executorService.submit(() -> sendMessage(entry.getValue(), message));
		}
	}

	private void sendMessage(Member member, HeartbeatMessage message) {
		try {
			TCPClient tcpClient = new TCPClient(InetAddress.getByName(member.getMemberAddress()), member.getPort());
			tcpClient.sendHeartbeatMessage(message);

		} catch (Exception e) {
			transmissionFailureHandler.accept(member);

			if (member.isLeader() == true) {
				System.out.println("Leader down. Starting election.");
				LeaderElectionImpl impl = new LeaderElectionImpl(memberMapped);
				try {
					impl.electionResult();
				} catch (Exception e1) {
					System.err.println("Election failed " + e1.getMessage());
				}
			}
		}

	}

}

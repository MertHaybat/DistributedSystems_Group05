package server.cluster;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import server.cluster.communication.CastController;
import server.cluster.communication.CastPublisher;
import server.cluster.communication.CastReceiver;
import server.cluster.communication.Heartbeat;
import shared.Constants;
import shared.communication.TCPClient;
import shared.message.HeartbeatMessage;
import shared.message.JoinMessage;
import shared.message.MemberInfoMessage;
import shared.message.Message;
import shared.message.RebootMessage;
import shared.message.ReplicaMessage;


public class MemberImpl implements Runnable, CastReceiver.MessageHandler, CastReceiver.MemberMessageHandler, CastReceiver.ReplicaHandler{

	private static final List<Class<? extends ReplicaMessage>> replicaMessageListClass = List.of(ReplicaMessage.class);
	private static final List<Class<? extends JoinMessage>> joinMessageListClass = List.of(JoinMessage.class);
	private static final List<Class<? extends MemberInfoMessage>> memberInfoMessageListClass = List.of(MemberInfoMessage.class);
	private final Member member;
	private final Map<Integer, Member> members = new ConcurrentHashMap<>();
    private final Heartbeat heartbeatSender;
    private final CastPublisher multicastPublisher;
    
	public MemberImpl(Member member, CastPublisher multicastPublisher) {
		
		this.multicastPublisher = multicastPublisher;
		this.member = member;
		this.heartbeatSender = new Heartbeat(new HeartbeatFailureHandler());
		this.members.put(member.getMemberId(), member);
		sendHeartbeatMessage();
	}
	
	
	@Override
	public void run() {
	
		
	}
	
	public void sendJoinMessage(Member member) {
        multicastPublisher.memberJoinMessage(new JoinMessage(member));
    }
	
	@Override
	public void handle(JoinMessage joinMessage) {
		addMember(joinMessage);	
	}
	
	private void addAllMemberToOneCluster(MemberInfoMessage message) {
		members.putAll(message.getMembers());
		
		LeaderElectionImpl electionImpl = new LeaderElectionImpl(members);
		
		members.putAll(electionImpl.electionResult());
		for(Map.Entry <Integer, Member> entry : members.entrySet()) {
			if(member.getMemberId() == entry.getValue().getMemberId()) {
				member.setLeader(entry.getValue().isLeader());
			}
		}
	}
	
	public void addMember(JoinMessage joinMessage)  {
		Member member;
		try {
			member = joinMessage.getMember();
			members.put(joinMessage.getMember().getMemberId(), member);
			for(Map.Entry <Integer, Member> entry : members.entrySet()) {
				if(entry.getValue().isLeader() == true) {
					try {
						TCPClient tcpClient = new TCPClient(InetAddress.getByName(entry.getValue().getMemberAddress()), 
								entry.getValue().getPort());
						tcpClient.sendRebootMessage(new RebootMessage(member));
					} catch (UnknownHostException e) {
						System.err.println("Error at trying to send rebootmessage: " + e.getMessage());
					}
				}
			}
			
			multicastPublisher.memberinfoToMembers(new MemberInfoMessage(members));
			
		} catch (Exception e) {
			System.err.println("Failed to add Member to cluster: " + e.getMessage());
		}
	}
	
	public Map<Integer, Member> getMembers() {
		return this.members;
	}
	private class HeartbeatFailureHandler implements Consumer<Member> {
		
        @Override
        public void accept(Member member) {
            members.remove(member.getMemberId());
            heartbeatSender.sendMessageToAllMembers(getMembers());
        }
    }
	
	private void sendHeartbeatMessage(){
		
		Runnable heartbeatStart = () -> {
			heartbeatSender.sendMessageToAllMembers(getMembers()); 	
			};

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(heartbeatStart, 5, 5, TimeUnit.SECONDS);
	}


	@Override
	public List<Class<? extends JoinMessage>> getResponsibleMessageTypes() {
		return joinMessageListClass;
	}


	@Override
	public void handle(MemberInfoMessage message) {
		addAllMemberToOneCluster(message);
		
	}


	@Override
	public List<Class<? extends MemberInfoMessage>> getMemberMessageTypes() {
		return memberInfoMessageListClass;
	}


	
	
	@Override
	public void handle(ReplicaMessage message) {
		try {
			if(member.isLeader() == false) {
			TCPClient tcpClient = new TCPClient(InetAddress.getByName(member.getMemberAddress()), member.getPort());
			tcpClient.sendReplicaFromMulticastToServer(message);
			}
		} catch (Exception e) {
			System.err.println("Sending replica to member failed: " + e.getMessage());
		}
	}


	@Override
	public List<Class<? extends ReplicaMessage>> getResponsibleReplicaMessageTypes() {
		return replicaMessageListClass;
	}




	


	
	

}

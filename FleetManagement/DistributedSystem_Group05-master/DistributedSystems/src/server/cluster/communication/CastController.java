package server.cluster.communication;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import server.cluster.Member;
import server.cluster.communication.CastReceiver.MemberMessageHandler;
import server.cluster.communication.CastReceiver.MessageHandler;
import server.cluster.communication.CastReceiver.ReplicaHandler;
import shared.communication.TCPClient;
import shared.message.JoinMessage;
import shared.message.MemberInfoMessage;
import shared.message.Message;
import shared.message.ReplicaMessage;


public class CastController implements MessageHandler, MemberMessageHandler, ReplicaHandler{

	private Map<Class<?>, CastReceiver.MessageHandler> multicastHandler;
	private Map<Class<?>, CastReceiver.MemberMessageHandler> memberInfoCastHandler;
	private Map<Class<?>, CastReceiver.ReplicaHandler> replicaHandler;
	private CastPublisher multicastPublisher;
	private InetAddress serverAddress;
	private int tcpServerPort;
	private Member member;
	
	public CastController(CastPublisher multicastPublisher) {
	
		this.multicastPublisher = multicastPublisher;
	}
	
	
	public CastController(Member member, Map<Class<?>, CastReceiver.MessageHandler> handler, 
			Map<Class<?>, CastReceiver.MemberMessageHandler> memberInfoCastHandler,
			Map<Class<?>, CastReceiver.ReplicaHandler> replicaHandler) {
		this.multicastHandler = handler;
		this.memberInfoCastHandler = memberInfoCastHandler;
		this.replicaHandler = replicaHandler;
		this.member = member;
	}
	
	
	
//	public void sendMessage(Message message) {
//		
//		try {
//			serverAddress = InetAddress.getByName("192.168.178.56");
//			tcpServerPort = 8001;
//			TCPClient tcpClient = new TCPClient(serverAddress, tcpServerPort);
//			tcpClient.sendMessage(message);
//		} catch (Exception e) {
//			System.err.println("Failed to send message from Multicast to Server" + e.getMessage());
//		}
//	}

	@Override
	public void handle(JoinMessage joinMessage) {
		CastReceiver.MessageHandler messageHandler = multicastHandler.get(joinMessage.getClass());
		messageHandler.handle(joinMessage);
	}

	@Override
	public List<Class<? extends JoinMessage>> getResponsibleMessageTypes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void handle(MemberInfoMessage message) {
		
		CastReceiver.MemberMessageHandler memberInfoMessage = memberInfoCastHandler.get(message.getClass());
		memberInfoMessage.handle(message);
	}


	@Override
	public List<Class<? extends MemberInfoMessage>> getMemberMessageTypes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void handle(ReplicaMessage message) {
		CastReceiver.ReplicaHandler messageInfo = replicaHandler.get(message.getClass());
		messageInfo.handle(message);
		
	}


	@Override
	public List<Class<? extends ReplicaMessage>> getResponsibleReplicaMessageTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}

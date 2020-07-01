package server;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import server.cluster.LeaderElectionImpl;
import server.cluster.Member;
import server.cluster.MemberImpl;
import server.cluster.communication.CastController;
import server.cluster.communication.CastPublisher;
import server.cluster.communication.CastReceiver;
import shared.Constants;
import shared.communication.TCPServer;
import shared.message.JoinMessage;

public class ServerMain {
	public static void main(String[] args) throws UnknownHostException, InterruptedException, SocketException {
		InetAddress localhost = InetAddress.getLocalHost();
		CastPublisher multicastPublisher = new CastPublisher(Constants.getMulticastAddress(), Constants.multicastPort);
		NetworkInterface networkInterface =  NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
		AtomicLong testLong = new AtomicLong(0);
		int tcpPort = 0;
		Member member;
		int memberId;
		
		if(Constants.getTcpPort() != 0) {
			tcpPort = Constants.getTcpPort();
			member = new Member(localhost.getHostAddress(), tcpPort);
			MemberImpl memberImpl = null;
			if (tcpPort == 8001) {
				member.setMemberId(3);
				member.setLeader(false);
				memberImpl = new MemberImpl(member, multicastPublisher);
			} else if (tcpPort == 8002) {
				member.setMemberId(2);
				member.setLeader(false);
				memberImpl = new MemberImpl(member, multicastPublisher);
			} else if (tcpPort == 8003) {
				member.setMemberId(1);
				member.setLeader(false);
				memberImpl = new MemberImpl(member, multicastPublisher);
			} 
		
	        
			Map<Class<?>, CastReceiver.MessageHandler> multicastHandlers = MulticastHandlers(memberImpl);
			Map<Class<?>, CastReceiver.MemberMessageHandler>  memberMulticastHandler = MemberInfoMulticastHandler(memberImpl);
			Map<Class<?>, CastReceiver.ReplicaHandler> replicaMulticastHandler = ReplicaHandler(memberImpl);
			
			new Thread(new TCPServer(tcpPort)).start();
			new Thread(new CastReceiver(member, Constants.multicastPort, Constants.getMulticastAddress(), InetAddress.getLocalHost(), 
					new CastController(member, multicastHandlers, memberMulticastHandler, replicaMulticastHandler), memberImpl, memberImpl)).start();
			Thread.sleep(2000);
			
			memberImpl.sendJoinMessage(member);
		} 
		
	
	}
	private static Map<Class<?>, CastReceiver.ReplicaHandler> ReplicaHandler(CastReceiver.ReplicaHandler... handlers) {
        return Stream.of(handlers)
                .map(handler -> handler.getResponsibleReplicaMessageTypes().stream().collect(Collectors.toMap(hel -> hel, hel -> handler)))
                .collect(DisallowDuplicateKeyHashMapReplica::new, Map::putAll, Map::putAll);
    }
	private static Map<Class<?>, CastReceiver.MessageHandler> MulticastHandlers(CastReceiver.MessageHandler... handlers) {
        return Stream.of(handlers)
                .map(handler -> handler.getResponsibleMessageTypes().stream().collect(Collectors.toMap(hel -> hel, hel -> handler)))
                .collect(DisallowDuplicateKeyHashMap::new, Map::putAll, Map::putAll);
    }
	private static Map<Class<?>, CastReceiver.MemberMessageHandler> MemberInfoMulticastHandler(CastReceiver.MemberMessageHandler... handlers) {
        return Stream.of(handlers)
                .map(handler -> handler.getMemberMessageTypes().stream().collect(Collectors.toMap(hel -> hel, hel -> handler)))
                .collect(DisallowDuplicateKeyHashMapMember::new, Map::putAll, Map::putAll);
    }
	private static class DisallowDuplicateKeyHashMapReplica<K, V> extends HashMap<K, V> {

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            List<? extends K> duplicates = m.keySet().stream().filter(this::containsKey).collect(Collectors.toList());
            if (duplicates.isEmpty()) {
                m.forEach(this::put);
                return;
            }
            throw new IllegalArgumentException(String.format("Duplicate keys are not allowed. Duplicates are" + duplicates.toString()));
        }
    }
	private static class DisallowDuplicateKeyHashMapMember<K, V> extends HashMap<K, V> {

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            List<? extends K> duplicates = m.keySet().stream().filter(this::containsKey).collect(Collectors.toList());
            if (duplicates.isEmpty()) {
                m.forEach(this::put);
                return;
            }
            throw new IllegalArgumentException(String.format("Duplicate keys are not allowed. Duplicates are" + duplicates.toString()));
        }
    }
    private static class DisallowDuplicateKeyHashMap<K, V> extends HashMap<K, V> {

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            List<? extends K> duplicates = m.keySet().stream().filter(this::containsKey).collect(Collectors.toList());
            if (duplicates.isEmpty()) {
                m.forEach(this::put);
                return;
            }
            throw new IllegalArgumentException(String.format("Duplicate keys are not allowed. Duplicates are" + duplicates.toString()));
        }
    }
}

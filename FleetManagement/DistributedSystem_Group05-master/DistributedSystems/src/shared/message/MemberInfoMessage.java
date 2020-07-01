package shared.message;

import java.io.Serializable;
import java.util.Map;

import server.cluster.Member;

public class MemberInfoMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Member> members;
	
	public MemberInfoMessage(Map<Integer, Member> members) {
		this.members = members;
	}

	public Map<Integer, Member> getMembers() {
		return members;
	}
	
	

}

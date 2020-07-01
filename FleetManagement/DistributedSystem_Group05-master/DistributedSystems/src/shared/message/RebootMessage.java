package shared.message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import server.cluster.Member;
import shared.bo.Car;

public class RebootMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Member member;
	
	public RebootMessage(Member member) {
		this.member = member;
	}

	public Member getMember() {
		return member;
	}


}

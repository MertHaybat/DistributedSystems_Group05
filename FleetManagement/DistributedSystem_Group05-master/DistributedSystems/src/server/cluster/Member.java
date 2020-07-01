package server.cluster;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Member implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String memberAddress = "";
	private int port = 0;
	private boolean isLeader;
	private int memberId = 0;
	
	public Member(String memberAddress, int port) {
		this.memberAddress = memberAddress;
		this.port = port;
	}
	public Member(int memberId, String memberAddress, int port) {
		this.memberAddress = memberAddress;
		this.port = port;
		this.memberId = memberId;
	}
	
	public Member(int memberId) {
		this.memberId = memberId;
	}
	public int getMemberId() {
		return memberId;
	}
	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}
	public boolean isLeader() {
		return isLeader;
	}
	public void setLeader(boolean isLeader) {
		this.isLeader = isLeader;
	}
	public String getMemberAddress() {
		return memberAddress;
	}
	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	@Override
    public String toString() { 
        return "Member ID: " + this.getMemberId() + " Member IP: " + this.getMemberAddress() + 
        		" Member Port: " + this.getPort() + " Member Leader: " + this.isLeader;
    }

}

package shared.message;

import java.io.Serializable;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String senderName;
	private long sequence = 0L;
	
	public Message(String message, String senderName, long sequence) {
		this.message = message;
		this.senderName = senderName;
		this.sequence = sequence;
	}

	public String getMessage() {
		return message;
	}

	public String getSenderName() {
		return senderName;
	}

	public synchronized long getSequence() {
		return this.sequence;
	}
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	
	public String toString() {
		return this.message;
		
	}
	
}

package shared.message;

public class AdminMessage extends Message{

	private String work = "";
	
	public AdminMessage(String message, String senderName, long sequence, String work) {
		super(message, senderName, sequence);
		this.work = work;
	}

	public String getWork() {
		return work;
	}

	
	
	

}

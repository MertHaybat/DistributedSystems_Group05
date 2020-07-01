package shared.message;

import java.io.Serializable;

public class ClientJoinMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String clientConnectedMessage = "Hello Server!";
	
	public String getHeartbeatMessage(){
		return this.clientConnectedMessage;
	}
}

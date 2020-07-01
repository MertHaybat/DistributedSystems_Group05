package shared.message;

import java.io.Serializable;

public class HeartbeatMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String heartbeatMessage = "Heartbeat";
	
	public String getHeartbeatMessage(){
		return this.heartbeatMessage;
	}
	
	
}

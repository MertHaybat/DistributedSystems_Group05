package shared.message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import shared.bo.Car;

public class ReplicaMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Map<Long, List<Car>> mappedCarlist;
	
	public ReplicaMessage(Map<Long, List<Car>> mappedCarlist) {
		this.mappedCarlist = mappedCarlist;
		
	}

	public Map<Long, List<Car>> getMappedCarlist() {
		return mappedCarlist;
	}
	

	
}

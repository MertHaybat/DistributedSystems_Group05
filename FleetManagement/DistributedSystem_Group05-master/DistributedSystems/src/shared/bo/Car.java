package shared.bo;

import java.io.Serializable;

public class Car implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String car = "";
	private String assignedTo = "";
	private String status = "";
	private long sequence = 0L;
	
	public Car () {
		this.sequence = 0L;
	}
	
	public Car (String car) {
		this.car = car;
		this.sequence = 0L;
	}
	
	public Car (String car, String status) {
		this.car = car;
		this.status = status;
		this.sequence = 0L;
	}
	public Car (String car, String status, String assignedTo) {
		this.car = car;
		this.status = status;
		this.sequence = 0L;
		this.assignedTo = assignedTo;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCar() {
		return car;
	}
	public void setCar(String car) {
		this.car = car;
	}
	public String getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	public synchronized long getSequence() {
		return this.sequence;
	}
	public synchronized void setSequence() {
		this.sequence = sequence++;
	}
}

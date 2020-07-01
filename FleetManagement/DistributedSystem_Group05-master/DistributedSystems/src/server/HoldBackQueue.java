package server;

import java.util.Comparator;
import java.util.PriorityQueue;

import shared.message.Message;

public class HoldBackQueue{
	
	private PriorityQueue<Message> messageQueue = new PriorityQueue<Message>(20, new MessageCompare());

	public void addMessageToQueue (Message message) {
		messageQueue.add(message);
		
	}
	
	public Message getMessagesFromQueue() {
		if (messageQueue.size() == 0) {
			return null;
		}
		Message message = messageQueue.poll();
		return message;
	}
	
	
	private class MessageCompare implements Comparator<Message>{

		@Override
		public int compare(Message o1, Message o2) {
			
			if(o1.getSequence() == o1.getSequence()) {
				return 0;
			} else if (o1.getSequence() < o2.getSequence()) {
				return -1;
			} else if (o1.getSequence() > o2.getSequence()) {
				return 1;
			}
			return 0;
			
		}
		
	}
}

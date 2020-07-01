package client;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import shared.bo.Car;


public class HoldBackQueueClient {
	private PriorityQueue<Map<Long, List<Car>>> carQueue = new PriorityQueue<Map<Long, List<Car>>>(20, new CarCompare());

	public void addCarlistToQueue (Map<Long, List<Car>>car) {
		carQueue.add(car);
		
	}
	
	public Map<Long, List<Car>> getMessagesFromCarListQueue() {
		if (carQueue.size() == 0) {
			return null;
		}
		Map<Long, List<Car>> cars = carQueue.poll();
		return cars;
	}
	
	
	private class CarCompare implements Comparator<Map<Long, List<Car>>>{

		@Override
		public int compare(Map<Long, List<Car>> o1, Map<Long, List<Car>> o2) {
			
			Set<Long> keyo1 = o1.keySet();
			Set<Long> keyo2 = o2.keySet();
			for (Long long1 : keyo1) {
				for (Long long2 : keyo2) {
					if(long1 == long2) {
						return 0;
					} else if (long1 < long2) {
						return -1;
					} else if (long1 > long2) {
						return 1;
					}
				}
			}
			
			
			
			return 0;
			
		}
		
	}
}

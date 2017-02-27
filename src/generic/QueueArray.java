package generic;

import java.util.ArrayList;
import java.util.List;

public class QueueArray {

	private List<Integer> queue;
	private List<Double> dist;

	public QueueArray(List<Double> dist) {
		queue = new ArrayList();
		this.dist = dist;
	}

	public List<Integer> makeQ(int size) {
		for (int i = 0; i < size; i++) {
			queue.add(i);
		}
		return queue;
	}

	public int deleteMin() {
		int minIndex = 0;
		double minDistance = dist.get(minIndex);

		for (int i = 0; i < dist.size(); i++) {
			double distance = dist.get(i);
			if (distance < minDistance) {
				minDistance = distance;
				minIndex = i;
			}
		}
		dist.set(minIndex, Double.MAX_VALUE);
		return minIndex;
	}

	public void decreaseKey(int key, double distance) {
		dist.set(key, distance);
	}

}

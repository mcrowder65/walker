package generic;

import java.util.ArrayList;
import java.util.List;

public class QueueArray {

	private List queue;
	private List dist;

	public QueueArray() {
		queue = new ArrayList();
		dist = new ArrayList();
	}

	public void makeQ(List pnts, int startIndex) {
		for (int i = 0; i < pnts.size(); i++) {
			queue.add(i);
			dist.add(Integer.MAX_VALUE);
		}
		dist.set(startIndex, 0);
	}

}

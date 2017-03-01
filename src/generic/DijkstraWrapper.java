package generic;

import java.util.List;

public class DijkstraWrapper {
	public List<Double> distances;
	public List<Integer> prev;

	public DijkstraWrapper(List<Double> distances, List<Integer> prev) {
		this.distances = distances;
		this.prev = prev;
	}
}

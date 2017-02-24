package generic;

import java.util.List;

public class Graph {

	private double[][] distance;
	private double[][] elevation;
	private List<Node> nodes;

	public Graph(double[][] distance, double[][] elevation) {
		this.distance = distance;
		this.elevation = elevation;
	}

	public Graph() {

	}

	public double[][] getDistance() {
		return distance;
	}

	public void setDistance(double[][] distance) {
		this.distance = distance;
	}

	public void setElevation(double[][] elevation) {
		this.elevation = elevation;
	}

}

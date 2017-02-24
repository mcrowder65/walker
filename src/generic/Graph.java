package generic;

import java.util.List;

public class Graph {

	private double[][] distance;
	private double[][] elevation;
	private List<Node> nodes;

	public Graph(double[][] distance, double[][] elevation, List<Node> nodes) {
		this.distance = distance;
		this.elevation = elevation;
		this.nodes = nodes;
	}

	public Graph() {

	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public void addNode(Node n) {
		nodes.add(n);
	}

	public int getNumNodes() {
		return nodes.size();
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

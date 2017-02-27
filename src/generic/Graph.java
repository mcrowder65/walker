package generic;

import java.util.ArrayList;
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

	public double getDistance(int startNode, int endNode) {
		return distance[startNode][endNode];
	}

	public List<Double> getDistanceList(int startNode) {
		List<Double> distances = new ArrayList();
		for (int i = 0; i < distance.length; i++) {
			double dist = getDistance(startNode, i);
			distances.add(dist);
		}
		return distances;
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

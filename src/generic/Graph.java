package generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import generic.objects.WalkerObject;
import googlemaps.LatLng;
import server.APITools;
import server.JSONTools;

public class Graph extends WalkerObject {

	private double[][] distance;
	private double[][] elevation;
	private List<Node> nodes;
	private List<List<Double>> distanceList;
	private List<List<Double>> elevationList;

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

	public int getNodeIndex(Node n) {
		for (int i = 0; i < nodes.size(); i++) {
			if (n == nodes.get(i)) {
				return i;
			}
		}
		return -1;
	}

	public void setEdge(int start, int end, double dist) {
		distance[start][end] = dist;
	}

	public List<List<Double>> getDistanceList() {
		return distanceList;
	}

	public void setDistanceList(List<List<Double>> distanceList) {
		this.distanceList = distanceList;
	}

	public List<List<Double>> getElevationList() {
		return elevationList;
	}

	public void setElevationList(List<List<Double>> elevationList) {
		this.elevationList = elevationList;
	}

	public double[][] getElevation() {
		return elevation;
	}

	public int start() {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).isStart()) {
				return i;
			}
		}
		return 0;
	}

	public int end() {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).isEnd()) {
				return i;
			}
		}
		return nodes.size() - 1;
	}

	public double calcBulidingDist(Node start, Node end) {
		LatLng locStartNode = start.getPosition();
		LatLng locEndNode = end.getPosition();
		double longDiff = Math.abs(locEndNode.longitude - locStartNode.longitude);
		double latDiff = Math.abs(locEndNode.latitude - locStartNode.latitude);

		double total = latDiff + longDiff;

		return total;
	}

	public void setDistancesFromNodes() {
		distance = new double[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int z = 0; z < nodes.size(); z++) {
				if (i == z) {
					distance[i][z] = 0;
				} else if (nodes.get(i).getBuilding() != null && nodes.get(z) != null) {
					double d = calcBulidingDist(nodes.get(i), nodes.get(z));
					distance[i][z] = d;
				} else {
					Node startNode = nodes.get(i);
					Node endNode = nodes.get(z);
					LatLng locStartNode = startNode.getPosition();
					LatLng locEndNode = endNode.getPosition();
					double longDiff = Math.abs(locEndNode.longitude - locStartNode.longitude);
					double latDiff = Math.abs(locEndNode.latitude - locStartNode.latitude);
					double longSqr = longDiff * longDiff;
					double latSqr = latDiff * latDiff;
					double res = Math.sqrt(longSqr + latSqr);
					distance[i][z] = res;
				}
			}
		}

	}

	public void setElevationsFromNodes() {
		double[] elevs = APITools.GetAllElevations(nodes);

		elevation = new double[nodes.size()][];
		for (int i = 0; i < nodes.size(); i++) {
			elevation[i] = new double[nodes.size()];
			for (int z = 0; z < nodes.size(); z++) {
				elevation[i][z] = Math.abs(elevs[i] - elevs[z]);
			}
		}
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public List<Node> getNodesFromPath(List<Integer> path) {
		List<Node> pathNodes = new ArrayList<>();
		for (int i = 0; i < path.size(); i++) {
			Node n = this.nodes.get(path.get(i));
			pathNodes.add(n);
		}
		return pathNodes;
	}

	public double getDistance(int startNode, int endNode) {
		return distance[startNode][endNode];
	}

	public double getElevation(int startNode, int endNode) {
		return elevation[startNode][endNode];
	}

	public double getCost(int startNode, int endNode, UserPrefs prefs) {
		return (prefs.getDistanceWeight() > 0 ? getDistance(startNode, endNode) * prefs.getDistanceWeight() : 0)
				+ (prefs.getElevationWeight() > 0 ? getElevation(startNode, endNode) * prefs.getElevationWeight() : 0);
	}

	public List<Double> getDistanceList(int startNode) {
		List<Double> distances = new ArrayList<>();
		for (int i = 0; i < distance.length; i++) {
			double dist = getDistance(startNode, i);
			distances.add(dist);
		}
		return distances;
	}

	public void addNode(Node n) {
		nodes.add(n);
	}

	public int numNodes() {
		return nodes.size();
	}

	@Override
	public String toString() {
		return "Graph [nodes=" + nodes + ", distanceList=" + distanceList + ", elevationList=" + elevationList + "]";
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

	private List<List<Double>> convert(double[][] matrix) {
		List<double[]> list = Arrays.asList(matrix);
		List<List<Double>> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			List<Double> dubs = new ArrayList<>();
			for (int x = 0; x < list.get(i).length; x++) {
				dubs.add(list.get(i)[x]);
			}
			result.add(dubs);
		}
		return result;
	}

	public void writeToFirebase() {
		this.distanceList = convert(distance);
		this.elevationList = convert(elevation);
		this.distance = null;
		this.elevation = null;
		Tools.firebase.create("nodes", this);
	}

	@Override
	public String toJson() {
		String jsonString = JSONTools.g.toJson(this);
		return jsonString;
	}

}

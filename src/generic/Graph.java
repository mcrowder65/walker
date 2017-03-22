package generic;

import java.util.ArrayList;
import java.util.List;

import generic.objects.WalkerObject;
import googlemaps.LatLng;
import server.APITools;
import server.JSONTools;

public class Graph extends WalkerObject {

	private double[][] distance;
	private double[][] elevation;
	private List<Node> nodes;

	public List<String> getDistancesChanged() {
		return null;
	}

	public List<String> getElevationsChanged() {
		return null;
	}

	public void setDistancesChanged(List<String> distancesChanged) {
		this.distancesChanged = distancesChanged;
	}

	public void setElevationsChanged(List<String> elevationsChanged) {
		this.elevationsChanged = elevationsChanged;
	}

	private List<String> distancesChanged;
	private List<String> elevationsChanged;

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

	public double[][] getElevation() {
		if (elevation == null && elevationsChanged != null) {
			elevation = new double[elevationsChanged.size()][elevationsChanged.size()];
			for (int x = 0; x < elevationsChanged.size(); x++) {
				String[] arr = elevationsChanged.get(x).split(",");
				for (int y = 0; y < arr.length; y++) {
					elevation[x][y] = Double.valueOf(arr[y]);
				}
			}
		}
		return elevation;
	}

	public double[][] getDistance() {
		if (distance == null && distancesChanged != null) {
			distance = new double[distancesChanged.size()][distancesChanged.size()];
			for (int x = 0; x < distancesChanged.size(); x++) {
				String[] arr = distancesChanged.get(x).split(",");
				for (int y = 0; y < arr.length; y++) {
					distance[x][y] = Double.valueOf(arr[y]);
				}
			}
		}
		return distance;
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
		return getDistance()[startNode][endNode];
	}

	public double getElevation(int startNode, int endNode) {
		return getElevation()[startNode][endNode];
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

	public void setDistance(double[][] distance) {
		this.distance = distance;
	}

	public void setElevation(double[][] elevation) {
		this.elevation = elevation;
	}

	public List<String> convert(double[][] matrix) {

		List<String> temp = new ArrayList<>();
		for (int x = 0; x < matrix.length; x++) {
			StringBuilder str = new StringBuilder();
			for (int y = 0; y < matrix[0].length; y++) {
				if (y != matrix[0].length - 1) {
					str.append(String.valueOf(matrix[x][y]) + ",");
				} else {
					str.append(String.valueOf(matrix[x][y]));
				}
			}
			temp.add(str.toString());
		}
		return temp;
	}

	public void prepareForFirebase() {
		this.distancesChanged = this.convert(this.distance);

		this.elevationsChanged = this.convert(this.elevation);
		if (!isEqual(this.distancesChanged, this.distance)) {
			System.err.println("distancesChanged and distance not equal!");
		}
		if (!isEqual(this.elevationsChanged, this.elevation)) {
			System.err.println("elevationsChanged and elevation are not equal!");
		}
		this.distance = null;

		this.elevation = null;
	}

	private boolean isEqual(List<String> list, double[][] matrix) {
		if (list == null || matrix == null) {
			return false;
		}
		for (int x = 0; x < list.size(); x++) {
			String[] values = list.get(x).split(",");
			for (int y = 0; y < values.length; y++) {
				double value = Double.valueOf(values[y]);
				if (value != matrix[x][y]) {
					return false;
				}
			}
		}
		return true;
	}

	public List<String> distancesChanged() {
		if (distancesChanged == null) {
			this.distancesChanged = new ArrayList<>();
		}
		return distancesChanged;
	}

	public List<String> elevationsChanged() {
		if (elevationsChanged == null) {
			this.elevationsChanged = new ArrayList<>();
		}
		return elevationsChanged;
	}

	@Override
	public String toJson() {
		String jsonString = JSONTools.g.toJson(this);
		return jsonString;
	}

}

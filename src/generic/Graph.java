package generic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import generic.objects.Building;
import generic.objects.Entrance;
import generic.objects.WalkerObject;
import googlemaps.LatLng;
import server.APITools;
import server.dao.BuildingDAO;
import server.dao.GraphFirebaseWrapper;

public class Graph extends WalkerObject {

	private double[][] distance;
	private double[][] elevation;
	private boolean[][] grass;
	private boolean[][] wilderness;
	private boolean[][] building;
	private boolean[][] parking;
	private double[][] stairs;
	private double[][] totalCost;
	private String name;
	private List<Node> nodes;

	public Graph(GraphFirebaseWrapper graphFirebaseWrapper) {
		super();
		this.setId(graphFirebaseWrapper.getId());
		distance = convertToDoubles(graphFirebaseWrapper.gDistance());
		elevation = convertToDoubles(graphFirebaseWrapper.gElevation());
		grass = convertToBooleans(graphFirebaseWrapper.gGrass());
		wilderness = convertToBooleans(graphFirebaseWrapper.gWilderness());
		building = convertToBooleans(graphFirebaseWrapper.gBuilding());
		parking = convertToBooleans(graphFirebaseWrapper.gParking());
		stairs = convertToDoubles(graphFirebaseWrapper.gStairs());
		name = graphFirebaseWrapper.getName();
		nodes = graphFirebaseWrapper.getNodes();
	}

	private double[][] convertToDoubles(List<String> list) {
		if (list == null) {
			return null;
		}
		double[][] matrix = new double[list.size()][list.size()];
		for (int x = 0; x < list.size(); x++) {
			String[] arr = list.get(x).split(",");
			for (int y = 0; y < arr.length; y++) {
				matrix[x][y] = Double.valueOf(arr[y]);
			}
		}
		return matrix;
	}

	private boolean[][] convertToBooleans(List<String> list) {
		if (list == null) {
			return null;
		}
		boolean[][] matrix = new boolean[list.size()][list.size()];
		for (int x = 0; x < list.size(); x++) {
			String[] arr = list.get(x).split(",");
			for (int y = 0; y < arr.length; y++) {
				matrix[x][y] = Boolean.getBoolean(arr[y]);
			}
		}
		return matrix;
	}

	public Graph(double[][] distance, double[][] elevation, boolean[][] grass, boolean[][] wilderness,
			boolean[][] building, boolean[][] parking, double[][] stairs, double[][] totalCost, String name,
			List<Node> nodes) {
		this.distance = distance;
		this.elevation = elevation;
		this.grass = grass;
		this.wilderness = wilderness;
		this.building = building;
		this.parking = parking;
		this.stairs = stairs;
		this.totalCost = totalCost;
		this.name = name;
		this.nodes = nodes;
	}

	public boolean[][] getGrass() {
		return grass;
	}

	public void setGrass(boolean[][] grass) {
		this.grass = grass;
	}

	public boolean[][] getWilderness() {
		return wilderness;
	}

	public void setWilderness(boolean[][] wilderness) {
		this.wilderness = wilderness;
	}

	public boolean[][] getBuilding() {
		return building;
	}

	public void setBuilding(boolean[][] building) {
		this.building = building;
	}

	public boolean[][] getParking() {
		return parking;
	}

	public void setParking(boolean[][] parking) {
		this.parking = parking;
	}

	public double[][] getStairs() {
		return stairs;
	}

	public void setStairs(double[][] stairs) {
		this.stairs = stairs;
	}

	public double[][] getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double[][] totalCost) {
		this.totalCost = totalCost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Graph(double[][] distance, double[][] elevation, List<Node> nodes) {
		this.distance = distance;
		this.elevation = elevation;
		this.nodes = nodes;
	}

	public Graph(double[][] distance, double[][] elevation, List<Node> nodes, boolean[][] grass, boolean[][] wilderness,
			boolean[][] building, boolean[][] parking, double[][] stairs) {
		this.distance = distance;
		this.elevation = elevation;
		this.nodes = nodes;
		this.grass = grass;
		this.wilderness = wilderness;
		this.building = building;
		this.parking = parking;
		this.stairs = stairs;
	}

	public Graph() {

	}

	public void setStartNode(int index) {
		nodes.get(index).setStart(true);
	}

	public int getStartIndex() {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).isStart() == true) {
				return i;
			}
		}
		return -1;
	}

	public int getEndIndex() {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).isEnd() == true) {
				return i;
			}
		}
		return -1;
	}

	public void printNodes() {
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			if (n.isStart() == true) {
				System.out.println(i);
			}
		}
	}

	public void setEndNode(int index) {
		nodes.get(index).setEnd(true);
	}

	public void generateMatrix(BufferedImage img, LatLng southwest, LatLng northeast) {
		building = new boolean[nodes.size()][nodes.size()];
		grass = new boolean[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				Node s_node = nodes.get(i);
				Node e_node = nodes.get(j);
				if (i == j) {
					building[i][j] = false;
					grass[i][j] = false;
				}

				PathConstituents pc = ImageTools.analyzeImage(img, s_node, e_node, southwest, northeast);
				building[i][j] = pc.building;
				grass[i][j] = pc.grass;
				building[i][j] = pc.building;
			}
		}
	}

	public void sumMatricies(UserPrefs up) {
		totalCost = new double[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				totalCost[i][j] = distance[i][j];
				if (up.getGrass()) {
					boolean g = grass[i][j];
					if (g) {
						totalCost[i][j] = Double.MAX_VALUE;
					}
				}
				if (up.getBuildingWeight()) {
					boolean b = building[i][j];
					if (b) {
						totalCost[i][j] = Double.MAX_VALUE;
					}
				}
			}
		}
	}

	public void addEnterExit() {
		BuildingDAO bd = new BuildingDAO();
		List<Building> buildings = bd.getAll();
		for (int i = 0; i < buildings.size(); i++) {
			Building b = buildings.get(i);
			List<Entrance> entrances = b.getResolvedEntrances();
			for (int j = 0; j < entrances.size(); j++) {
				Entrance entrance = entrances.get(j);
				LatLng position = new LatLng(entrance.getLatitude(), entrance.getLongitude());
				Node n = new Node(position, b);
				this.nodes.add(n);
			}

		}
	}

	public int findClosestNodeIndex(Node n) {
		LatLng latLong = n.getPosition();
		int closestNodeIndex = 0;
		double distance = Double.MAX_VALUE;
		for (int i = 0; i < nodes.size(); i++) {
			double longDiff = Math.abs(latLong.longitude - nodes.get(i).getPosition().longitude);
			double latDiff = Math.abs(latLong.latitude - nodes.get(i).getPosition().latitude);
			double total = latDiff + longDiff;
			if (total < distance) {
				distance = total;
				closestNodeIndex = i;
			}
		}

		return closestNodeIndex;
	}

	public void generateWildernessMatrix() {

	}

	public void generateParkingMatrix() {

	}

	public void generateGrassMatrix() {

	}

	public void generateStairsMatrix() {

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
		return elevation;
	}

	public double[][] getDistance() {
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

	public double checkEntrences(Node start, Node end) {

		if (start.getBuilding() == end.getBuilding() && start.getBuilding() != null) {
			LatLng locStartNode = start.getPosition();
			LatLng locEndNode = end.getPosition();
			double longDiff = Math.abs(locEndNode.longitude - locStartNode.longitude);
			double latDiff = Math.abs(locEndNode.latitude - locStartNode.latitude);
			double dist = longDiff + latDiff;
			return dist;
		}
		return -1;

	}

	public void setDistancesFromNodes(BufferedImage img, LatLng southwest, LatLng northeast) {
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
					double checkRes = checkEntrences(startNode, endNode);
					if (checkRes != -1) {
						distance[i][z] = checkRes;
					} else {
						PathConstituents pc = ImageTools.analyzeImage(img, startNode, endNode, southwest, northeast);
						if (pc.building == true) {
							distance[i][z] = Double.MAX_VALUE;
						} else {
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

	// public double getCost(int startNode, int endNode, UserPrefs prefs) {
	// return (prefs.getDistanceWeight() > 0 ? getDistance(startNode, endNode) *
	// prefs.getDistanceWeight() : 0)
	// + (prefs.getElevationWeight() > 0 ? getElevation(startNode, endNode) *
	// prefs.getElevationWeight() : 0);
	// }
	public double getCost(int startNode, int endNode, UserPrefs prefs) {
		return totalCost[startNode][endNode];
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

}

package generic;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import generic.objects.Building;
import generic.objects.Entrance;
import generic.objects.UserPrefs;
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

	public void generateLimitedMatrix(BufferedImage img, LatLng southwest, LatLng northeast) {
		building = new boolean[nodes.size()][nodes.size()];
		grass = new boolean[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println("i = " + i + "/ " + nodes.size());
			for (int j = i; j < nodes.size(); j++) {
				Node s_node = nodes.get(i);
				Node e_node = nodes.get(j);
				if (i == j) {
					building[i][j] = false;
					grass[i][j] = false;
					continue;
				}
				if (s_node.getPosition().distSquared(e_node.getPosition()) > Config.MAX_BLOCK_DIST_SQUARED)
					continue;

				PathConstituents pc = ImageTools.analyzeImage(img, s_node, e_node, southwest, northeast);
				building[i][j] = pc.building;
				grass[i][j] = pc.grass;
			}
		}

		for (int j = 0; j < nodes.size(); j++) {
			for (int i = j + 1; i < nodes.size(); i++) {
				building[i][j] = building[j][i];
				grass[i][j] = grass[j][i];
			}
		}
	}

	public void generateMatrix(BufferedImage img, LatLng southwest, LatLng northeast) {
		building = new boolean[nodes.size()][nodes.size()];
		grass = new boolean[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = i; j < nodes.size(); j++) {
				Node s_node = nodes.get(i);
				Node e_node = nodes.get(j);
				if (i == j) {
					building[i][j] = false;
					grass[i][j] = false;
					continue;
				}

				PathConstituents pc = ImageTools.analyzeImage(img, s_node, e_node, southwest, northeast);
				building[i][j] = pc.building;
				grass[i][j] = pc.grass;
			}
		}

		for (int j = 0; j < nodes.size(); j++) {
			for (int i = j + 1; i < nodes.size(); i++) {
				building[i][j] = building[j][i];
				grass[i][j] = grass[j][i];
			}
		}

	}

	public void sumMatricies(UserPrefs up) {
		totalCost = new double[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				totalCost[i][j] = distance[i][j];
				if (up.isGrass()) {
					boolean g = grass[i][j];
					if (g) {
						totalCost[i][j] = Double.MAX_VALUE;
					}
				}
				if (up.isBuilding()) {
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
				int index = findClosestNodeIndex(n);
				this.nodes.get(index).setBuilding(b);
				;
			}

		}
	}

	public void addBlackNodes(BufferedImage img, LatLng southwest, LatLng northeast) {
		int imgHeight = img.getHeight();
		int imgWidth = img.getWidth();
		int nodesSinceLastNode = 0; // holds how many pixels we have
									// looped over since we last put a node,
		List<Node> allNodes = GraphTools.GenerateUniformNodes(.3, southwest, northeast);
		for (int i = 0; i < allNodes.size(); i++) {
			nodesSinceLastNode++;
			if (nodesSinceLastNode >= 5) {
				Node n = allNodes.get(i);
				Point2D.Double point = APITools.getImagePointFromLatLng(n.getPosition(), southwest, northeast,
						img.getWidth(), img.getHeight());
				int rgb = img.getRGB((int) point.x, (int) point.y);
				boolean isBlack = Tools.colorIsCloseEnough(rgb, Config.MAPS_NORMALPATH_RGB, 3);
				if (isBlack == true) {
					nodes.add(n);
				}
				nodesSinceLastNode = 0;
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
				Node startNode = nodes.get(i);
				Node endNode = nodes.get(z);
				if (i == z) {
					distance[i][z] = 0;

				} else if ((startNode.getBuilding() != null && endNode.getBuilding() != null)
						&& startNode.getBuilding() == endNode.getBuilding()) {
					distance[i][z] = calcBulidingDist(startNode, endNode);
				} else {
					PathConstituents pc = ImageTools.analyzeImage(img, startNode, endNode, southwest, northeast);
					if (pc == null || pc.building == true) {
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

	public void setLimitedDistancesFromNodes(BufferedImage img, LatLng southwest, LatLng northeast) {
		distance = new double[nodes.size()][nodes.size()];
		grass = new boolean[nodes.size()][nodes.size()];
		
		
		for (int i = 0; i < nodes.size(); i++) {
			System.out.println("i = " + i);
			for (int z = i; z < nodes.size(); z++) {
				if (i == z) {
					distance[i][z] = 0;
				}
				else {
					Node startNode = nodes.get(i);
					Node endNode = nodes.get(z);
					double checkRes = checkEntrences(startNode, endNode);
					if (checkRes != -1) {
						distance[i][z] = checkRes;
					} else {
						LatLng locStartNode = startNode.getPosition();
						LatLng locEndNode = endNode.getPosition();
						double longDiff = Math.abs(locEndNode.longitude - locStartNode.longitude);
						double latDiff = Math.abs(locEndNode.latitude - locStartNode.latitude);
						double longSqr = longDiff * longDiff;
						double latSqr = latDiff * latDiff;
						double res = Math.sqrt(longSqr + latSqr);
						distance[i][z] = res;
						
						if (distance[i][z] * distance[i][z] > Config.MAX_BLOCK_DIST_SQUARED )
							distance[i][z] = Double.MAX_VALUE;
						else
						{
							PathConstituents pc = ImageTools.analyzeImage(img, startNode, endNode, southwest, northeast);
							if (pc.building) 
								distance[i][z] = Double.MAX_VALUE;
							
							grass[i][z] = pc.grass;
						}

					}
				}

			}
		}
		
		for (int j = 0; j < nodes.size(); j++)
		{
			for (int i = j + 1; i < nodes.size(); i++)
			{
				distance[i][j] = distance[j][i];
				grass[i][j] = grass[j][i];
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

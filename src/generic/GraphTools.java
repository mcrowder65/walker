package generic;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import com.google.common.collect.Lists;

import generic.objects.Building;
import generic.objects.UserPrefs;
import googlemaps.LatLng;
import googlemaps.PolyUtil;
import server.APITools;

public class GraphTools {

	public static List<Node> CreateNodesFromPolyline(String[] polylinePieces) {
		ArrayList<LatLng> pivots = new ArrayList<LatLng>();
		for (String poly : polylinePieces) {
			pivots.addAll(PolyUtil.decode(poly));
		}
		List<Node> nodes = new ArrayList<Node>();
		for (int n = 0; n < pivots.size(); n++) {
			nodes.add(new Node(pivots.get(n), null));
		}
		return nodes;

	}

	private static Random rand = new Random();

	/**
	 * Generates some number of nodes in a normal distribution around the
	 * existing nodes. Uses the box-muller method
	 * 
	 * @param existingNodes
	 * @param numToGen
	 * @return
	 */

	public static List<Node> GenerateRandomNodes(List<Node> existingNodes, int numToGen, LatLng southwest,
			LatLng northeast) {
		double meanLat = 0;
		double meanLon = 0;
		double latRatio = (northeast.latitude - southwest.latitude) / 6;
		double lonRatio = (northeast.longitude - southwest.longitude) / 6;

		for (Node n : existingNodes) {
			meanLat += n.getPosition().latitude;
			meanLon += n.getPosition().longitude;
		}
		meanLat /= existingNodes.size();
		meanLon /= existingNodes.size();

		List<Node> randomNodes = new ArrayList<Node>();
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for (int n = 0; n < numToGen; n++) {
			double u = rand.nextDouble();
			double v = rand.nextDouble();
			double x = Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
			double y = Math.sqrt(-2 * Math.log(u)) * Math.sin(2 * Math.PI * v);
			if (Math.abs(x) >= 2 || Math.abs(y) >= 2) {
				n--;
				continue;
			}
			double lat = meanLat + (y * latRatio);
			double lon = meanLon + (x * lonRatio);

			if (lat < southwest.latitude || lat > northeast.latitude || lon < southwest.longitude
					|| lon > northeast.longitude) {
				n--;
				continue;
			}

			maxX = Math.max(maxX, x);
			maxY = Math.max(maxY, y);

			Node node = new Node(new LatLng(lat, lon));
			randomNodes.add(node);
		}
		// System.out.println("Max x: " + maxX + ", Max Y: " + maxY);
		return randomNodes;

	}

	public static List<Node> RemoveBuildingNodes(List<Node> nodes, BufferedImage img, LatLng southwest,
			LatLng northeast) {
		for (int i = 0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			Point2D.Double startPnt = APITools.getImagePointFromLatLng(n.getPosition(), southwest, northeast,
					img.getWidth(), img.getHeight());
			int x = (int) startPnt.x;
			int y = (int) startPnt.y;
			int rgb = img.getRGB(x, y);
			if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
				nodes.remove(i);
				i--;
			}
		}
		return nodes;
	}

	public static double calcDist(Node start, Node end) {
		double longDiff = Math.abs(start.getPosition().longitude - end.getPosition().longitude);
		double latDiff = Math.abs(start.getPosition().latitude - end.getPosition().latitude);
		double total = Math.sqrt((latDiff * latDiff) + (longDiff * longDiff));
		return total;
	}

	public static double calcBuildingDist(Node start, Node end) {
		double longDiff = Math.abs(start.getPosition().longitude - end.getPosition().longitude);
		double latDiff = Math.abs(start.getPosition().latitude - end.getPosition().latitude);
		return longDiff + latDiff;
	}

	public static double getCosts(Node start, Node end, UserPrefs up) {
		if (end.code == NodeCode.Building || start.code == NodeCode.Building) {
			return Double.MAX_VALUE;
		}
		// TODO calculate thisasdf
		if (up.getBuilding() > 0) {
			if (start.getBuilding() == end.getBuilding() && start.getBuilding() != null) {
				return calcBuildingDist(start, end);
			}
		}
		// TODO calculate this
		if (up.getPreferDesignatedPaths() > 0) {
			if (start.code == NodeCode.Normal && end.code == NodeCode.Normal) {
				return calcDist(start, end);
			}
			if ((start.code == NodeCode.Normal && end.getBuilding() != null)
					|| (end.code == NodeCode.Normal && start.getBuilding() != null)) {
				return calcDist(start, end);
			}
			return Double.MAX_VALUE;
		}
		// TODO calculate this
		if (up.getGrass() == 0 && (start.code == NodeCode.Grass || end.code == NodeCode.Grass)) {
			return Double.MAX_VALUE;
		}

		return calcDist(start, end);

	}

	public static Node[][] genUniformNodes(double meterSpacing, LatLng southwest, LatLng northeast, BufferedImage img) {
		double latLen = APITools.getLatitudeDifference(southwest, northeast);
		double lonLen = APITools.getLongitudeDifference(southwest, northeast);

		int latNodes = (int) (latLen / meterSpacing);
		int lonNodes = (int) (lonLen / meterSpacing);

		double latOffset = Math.abs((latNodes * meterSpacing) - latLen) / 2;
		double lonOffset = Math.abs((lonNodes * meterSpacing) - lonLen) / 2;

		Node[][] allNodes = new Node[lonNodes][latNodes];

		for (int x = 0; x < lonNodes; x++) {
			double currentLon = APITools.metersToLon(southwest, x * meterSpacing + lonOffset);
			for (int y = 0; y < latNodes; y++) {
				double currentLat = APITools.metersToLat(southwest, y * meterSpacing + latOffset);
				Node n = new Node(currentLat, currentLon, null, false, false);
				Point2D.Double point = APITools.getImagePointFromLatLng(n.getPosition(), southwest, northeast,
						img.getWidth(), img.getHeight());
				int rgb = img.getRGB((int) point.x, (int) point.y);
				boolean isNormalPath = Tools.colorIsCloseEnough(rgb, Config.MAPS_NORMALPATH_RGB, 3);
				boolean isGrass = Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, 3);
				boolean isBuilding = Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, 3);
				if (isNormalPath) {
					n.code = NodeCode.Normal;
				} else if (isGrass) {
					n.code = NodeCode.Grass;
				} else if (isBuilding) {
					n.code = NodeCode.Building;
				} else {
					n.code = NodeCode.Other;
				}
				allNodes[x][y] = n;
			}
		}

		// SET LAT AND LONG STEPPING
		Config.LONG_STEPPING_DIST = Math
				.abs(allNodes[0][0].getPosition().longitude - allNodes[1][0].getPosition().longitude);
		Config.LAT_STEPPING_DIST = Math
				.abs(allNodes[0][0].getPosition().latitude - allNodes[0][1].getPosition().latitude);

		return allNodes;
	}

	public static List<Node> GenerateUniformNodes(double meterSpacing, LatLng southwest, LatLng northeast,
			boolean isBlackPath) {
		double latLen = APITools.getLatitudeDifference(southwest, northeast);
		double lonLen = APITools.getLongitudeDifference(southwest, northeast);

		int latNodes = (int) (latLen / meterSpacing);
		int lonNodes = (int) (lonLen / meterSpacing);

		double latOffset = Math.abs((latNodes * meterSpacing) - latLen) / 2;
		double lonOffset = Math.abs((lonNodes * meterSpacing) - lonLen) / 2;

		ArrayList<Node> nodes = new ArrayList<Node>();

		for (int x = 0; x < lonNodes; x++) {
			double currentLon = APITools.metersToLon(southwest, x * meterSpacing + lonOffset);
			for (int y = 0; y < latNodes; y++) {
				double currentLat = APITools.metersToLat(southwest, y * meterSpacing + latOffset);
				nodes.add(new Node(currentLat, currentLon, null, false, false));

			}
		}

		// Set the MAX_BLOCK_DIST{_BLACK} while we're here

		if (isBlackPath) {
			double blockDistLat = APITools.metersToLat(southwest, Config.MAX_BLOCK_SIZE_BLACK * meterSpacing)
					- southwest.latitude;
			double blockDistLon = APITools.metersToLon(southwest, Config.MAX_BLOCK_SIZE_BLACK * meterSpacing)
					- southwest.longitude;
			Config.MAX_BLOCK_DIST_SQUARED_BLACK = blockDistLat * blockDistLat + blockDistLon * blockDistLon;
		} else {
			double blockDistLat = APITools.metersToLat(southwest, Config.MAX_BLOCK_SIZE * meterSpacing)
					- southwest.latitude;
			double blockDistLon = APITools.metersToLon(southwest, Config.MAX_BLOCK_SIZE * meterSpacing)
					- southwest.longitude;

			Config.MAX_BLOCK_DIST_SQUARED = blockDistLat * blockDistLat + blockDistLon * blockDistLon;
		}

		return nodes;
	}

	private static void bresenham2(BufferedImage img, Point2D.Double start, Point2D.Double end, Color lineColor) {
		int x1 = (int) start.x;
		int x2 = (int) end.x;
		int y1 = (int) start.y;
		int y2 = (int) end.y;

		int cx, cy, ix, iy, dx, dy, ddx = x2 - x1, ddy = y2 - y1;

		if (ddx == 0) { // vertical line special case
			if (ddy > 0) {
				cy = y1;
				do
					Tools.setImageRGB(img, x1, cy++, lineColor);
				while (cy <= y2);
				return;
			} else {
				cy = y2;
				do
					Tools.setImageRGB(img, x1, cy++, lineColor);
				while (cy <= y1);
				return;
			}
		}
		if (ddy == 0) { // horizontal line special case
			if (ddx > 0) {
				cx = x1;
				do
					Tools.setImageRGB(img, cx, y1, lineColor);
				while (++cx <= x2);
				return;
			} else {
				cx = x2;
				do
					Tools.setImageRGB(img, cx, y1, lineColor);
				while (++cx <= x1);
				return;
			}
		}
		if (ddy < 0) {
			iy = -1;
			ddy = -ddy;
		} // pointing up
		else
			iy = 1;
		if (ddx < 0) {
			ix = -1;
			ddx = -ddx;
		} // pointing left
		else
			ix = 1;
		dx = dy = ddx * ddy;
		cy = y1;
		cx = x1;
		if (ddx < ddy) { // < 45 degrees, a tall line
			do {
				dx -= ddy;
				do {
					Tools.setImageRGB(img, cx, cy, lineColor);
					cy += iy;
					dy -= ddx;
				} while (dy >= dx);
				cx += ix;
			} while (dx > 0);
		} else { // >= 45 degrees, a wide line
			do {
				dy -= ddx;
				do {
					Tools.setImageRGB(img, cx, cy, lineColor);
					cx += ix;
					dx -= ddy;
				} while (dx >= dy);
				cy += iy;
			} while (dy > 0);
		}
	}

	public static void WriteGraphToImage(BufferedImage img, Graph g, Color nodeColor, int nodePixelRadius,
			LatLng southwest, LatLng northeast) {
		WriteGraphToImage(img, g, nodeColor, nodePixelRadius, southwest, northeast, null);
	}

	public static void WriteGraphToImage(BufferedImage img, Graph g, Color nodeColor, int nodePixelRadius,
			LatLng southwest, LatLng northeast, Color lineColor) {
		Point2D.Double prevPoint = null;
		for (Node n : g.getNodes()) {
			Point2D.Double p = APITools.getImagePointFromLatLng(n.getPosition(), southwest, northeast, img.getWidth(),
					img.getHeight());
			int startX = (int) p.getX() - nodePixelRadius;
			int startY = (int) p.getY() - nodePixelRadius;
			for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
				for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
					if (x < 0 || y < 0 || x > img.getWidth() - 1 || y > img.getHeight() - 1)
						continue;

					if (!n.isStart() && !n.isEnd()) {
						Tools.setImageRGB(img, x, y, nodeColor);
					} else {
						Tools.setImageRGB(img, x, y, Color.orange);
					}
				}
			}

			if (lineColor != null && prevPoint != null) {
				Point2D.Double minX = p.x < prevPoint.x ? p : prevPoint;
				Point2D.Double maxX = minX == prevPoint ? p : prevPoint;
				bresenham2(img, minX, maxX, lineColor);
			}

			prevPoint = p;
		}
	}

	public static void WriteAStarPathToImage(BufferedImage img, Graph g, List<NodeIndex> backpath, LatLng southwest,
			LatLng northeast, Color lineColor) {
		Point2D.Double prevPoint = null;

		for (int nIndex = 0; nIndex < backpath.size(); nIndex++) {
			Point2D.Double p = APITools.getImagePointFromLatLng(g.getFromIndex(backpath.get(nIndex)).getPosition(),
					southwest, northeast, img.getWidth(), img.getHeight());
			if (prevPoint != null) {
				Point2D.Double minX = p.x < prevPoint.x ? p : prevPoint;
				Point2D.Double maxX = minX == prevPoint ? p : prevPoint;
				bresenham2(img, minX, maxX, lineColor);
			}
			prevPoint = p;
		}

	}

	public static void DrawRouteOnly(BufferedImage img, List<Node> nodes, Color nodeColor, int nodePixelRadius,
			LatLng southwest, LatLng northeast, Color lineColor) {

		Point2D.Double prevPoint = APITools.getImagePointFromLatLng(nodes.get(0).getPosition(), southwest, northeast,
				img.getWidth(), img.getHeight());
		int startX = (int) prevPoint.getX() - nodePixelRadius;
		int startY = (int) prevPoint.getY() - nodePixelRadius;
		for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
			for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
				Tools.setImageRGB(img, x, y, Color.RED);
			}
		}
		for (int i = 1; i < nodes.size(); i++) {
			Point2D.Double p = APITools.getImagePointFromLatLng(nodes.get(i).getPosition(), southwest, northeast,
					img.getWidth(), img.getHeight());
			startX = (int) p.getX() - nodePixelRadius;
			startY = (int) p.getY() - nodePixelRadius;
			for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
				for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
					Tools.setImageRGB(img, x, y, Color.RED);
				}
			}
			if (lineColor != null && prevPoint != null) {
				Point2D.Double minX = p.x < prevPoint.x ? p : prevPoint;
				Point2D.Double maxX = minX == prevPoint ? p : prevPoint;
				bresenham2(img, minX, maxX, lineColor);
			}

			prevPoint = p;
		}
	}

	public static void DrawLines(BufferedImage img, List<Node> nodes, Color nodeColor, int nodePixelRadius,
			LatLng southwest, LatLng northeast, Color lineColor, Graph g) {
		for (Node n : g.getNodes()) {
			if (n.getBuilding() != null) {
				nodeColor = Color.BLUE;
			}
			Point2D.Double p = APITools.getImagePointFromLatLng(n.getPosition(), southwest, northeast, img.getWidth(),
					img.getHeight());
			if (p == null) {
				continue;
			}
			int startX = (int) p.getX() - nodePixelRadius;
			int startY = (int) p.getY() - nodePixelRadius;
			for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
				for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
					if (x < 0 || y < 0 || x > img.getWidth() - 1 || y > img.getHeight() - 1)
						continue;
					Tools.setImageRGB(img, x, y, nodeColor);
				}
			}
		}

		Point2D.Double prevPoint = APITools.getImagePointFromLatLng(nodes.get(0).getPosition(), southwest, northeast,
				img.getWidth(), img.getHeight());
		int startX = (int) prevPoint.getX() - nodePixelRadius;
		int startY = (int) prevPoint.getY() - nodePixelRadius;
		for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
			for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
				if (x < 0 || y < 0 || x > img.getWidth() - 1 || y > img.getHeight() - 1)
					continue;
				Tools.setImageRGB(img, x, y, Color.RED);
			}
		}

		for (int i = 1; i < nodes.size(); i++) {
			Point2D.Double p = APITools.getImagePointFromLatLng(nodes.get(i).getPosition(), southwest, northeast,
					img.getWidth(), img.getHeight());
			startX = (int) p.getX() - nodePixelRadius;
			startY = (int) p.getY() - nodePixelRadius;
			for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
				for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
					if (x < 0 || y < 0 || x > img.getWidth() - 1 || y > img.getHeight() - 1)
						continue;
					Tools.setImageRGB(img, x, y, Color.RED);
				}
			}
			if (lineColor != null && prevPoint != null) {
				Point2D.Double minX = p.x < prevPoint.x ? p : prevPoint;
				Point2D.Double maxX = minX == prevPoint ? p : prevPoint;
				bresenham2(img, minX, maxX, lineColor);
			}

			prevPoint = p;
		}

	}

	public static List<Integer> dijkstra(int startNodeIndex, Graph g, int endNodeIndex) {
		return dijkstra(startNodeIndex, g, endNodeIndex, new UserPrefs(.5, .5, 0, 0, 0, 0, 0));

	}

	public static List<Integer> dijkstra(int startNodeIndex, Graph g, int endNodeIndex, UserPrefs prefs) {
		List<Double> costs = new ArrayList<Double>();
		List<Integer> prev = new ArrayList<Integer>();
		for (int i = 0; i < g.numNodes(); i++) {
			costs.add(Double.MAX_VALUE);
			prev.add(i);
		}
		costs.set(startNodeIndex, (double) 0);
		QueueArray qObj = new QueueArray();
		List<Integer> q = qObj.makeQ(g.numNodes(), startNodeIndex);
		int counter = 0;
		while (counter < q.size()) {
			int u = qObj.deleteMin();
			counter++;
			int size = g.numNodes();
			for (int i = 0; i < size; i++) {
				if (costs.get(i) > (costs.get(u) + g.getCost(u, i, prefs))) {
					double newCost = costs.get(u) + g.getCost(u, i, prefs);
					costs.set(i, newCost);
					prev.set(i, u);
					qObj.decreaseKey(i, newCost);
				}
			}
		}
		List<Integer> path = new ArrayList<Integer>();
		int end = endNodeIndex;
		while (end != startNodeIndex) {
			path.add(end);
			end = prev.get(end);
		}
		path.add(startNodeIndex);
		List<Integer> finalPath = Lists.reverse(path);
		return finalPath;
	}

	/**
	 * TODO: We are probably not going to use this.
	 * 
	 * @param startNodeIndex
	 * @param g
	 * @param endNodeIndex
	 * @param prefs
	 * @return
	 */
	public static List<Integer> iterativeDijkstra(int startNodeIndex, Graph g, int endNodeIndex, UserPrefs prefs) {
		List<Integer> currPath;
		boolean[][] validPaths = new boolean[g.numNodes()][];
		for (int n = 0; n < g.numNodes(); n++)
			validPaths[n] = new boolean[g.numNodes()];

		do {
			currPath = dijkstra(startNodeIndex, g, endNodeIndex, prefs);

			for (int n = 0; n < currPath.size() - 1; n++) {
				// if (validPaths[n][n + 1])
			}
			break;
		} while (true);
		return currPath;
	}

	private static void initInfinity(Graph g, double[][] arr) {
		for (int x = 0; x < g.nodes2.length; x++) {
			for (int y = 0; y < g.nodes2[0].length; y++) {
				arr[x][y] = Double.MAX_VALUE;
			}
		}
	}

	private static NodeIndex getLowestInMap(HashSet<NodeIndex> available, HashMap<NodeIndex, Double> map) {
		double min = Double.MAX_VALUE;
		NodeIndex minNode = null;
		for (NodeIndex n : available) {
			if (map.get(n) < min) {
				min = map.get(n);
				minNode = n;
			}
		}
		return minNode;
	}

	public static List<NodeIndex> reconstructPath(HashMap<NodeIndex, NodeIndex> cameFrom, NodeIndex current) {
		List<NodeIndex> total = new ArrayList<NodeIndex>();
		total.add(current);
		while (cameFrom.keySet().contains(current)) {
			current = cameFrom.get(current);
			total.add(current);
		}
		return total;
	}

	private static void expandSimilarEntrances(Graph g, NodeIndex current, NodeIndex end,
			HashMap<NodeIndex, NodeIndex> cameFrom, PriorityQueue<NodeIndexWithValue> openSet, boolean[][] closedSet,
			double[][] fScore, double[][] gScore, UserPrefs up) {
		Building currBuilding = g.getFromIndex(current).getBuilding();
		if (currBuilding == null)
			return;

		for (int x = 0; x < g.nodes2.length; x++) {
			for (int y = 0; y < g.nodes2[0].length; y++) {
				NodeIndex neighbor = createNodeIndexOrFromCache(g, x, y);
				if (g.nodes2[x][y].getBuilding() == currBuilding && !closedSet[neighbor.x][neighbor.y]) {
					expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, up);
				}
			}
		}
	}

	private static void expand(Graph g, NodeIndex current, NodeIndex neighbor, NodeIndex end,
			HashMap<NodeIndex, NodeIndex> cameFrom, PriorityQueue<NodeIndexWithValue> openSet, double[][] fScore,
			double[][] gScore, UserPrefs up) {
		double tentative_gScore = gScore[current.x][current.y]
				+ getCosts(g.getFromIndex(current), g.getFromIndex(neighbor), up);
		boolean willAdd = false;
		if (!openSet.contains(neighbor))
			willAdd = true;// openSet.add(new NodeIndexWithValue(neighbor);
		if (tentative_gScore >= gScore[neighbor.x][neighbor.y])
			return;

		cameFrom.put(neighbor, current);
		gScore[neighbor.x][neighbor.y] = tentative_gScore;
		fScore[neighbor.x][neighbor.y] = gScore[neighbor.x][neighbor.y]
				+ calcDist(g.getFromIndex(neighbor), g.getFromIndex(end));

		if (willAdd)
			openSet.add(new NodeIndexWithValue(neighbor, fScore[neighbor.x][neighbor.y]));
	}

	private static HashSet<NodeIndex> nodeCache = new HashSet<NodeIndex>();

	private static NodeIndex createNodeIndexOrFromCache(Graph g, int x, int y) {
		if (!g.isValidIndex(x, y))
			return null;

		/*
		 * for (NodeIndex n : nodeCache) { if (n.x == x && n.y == y) return n; }
		 */
		NodeIndex newNode = new NodeIndex(x, y);
		// nodeCache.add(newNode);
		return newNode;
	}

	public static List<NodeIndex> A_Star(Graph g, NodeIndex start, NodeIndex end, UserPrefs prefs) {

		boolean[][] closedSet = new boolean[g.nodes2.length][g.nodes2[0].length];
		FScoreComparator cmprtor = new FScoreComparator();
		PriorityQueue<NodeIndexWithValue> openSet = new PriorityQueue<NodeIndexWithValue>(cmprtor);

		HashMap<NodeIndex, NodeIndex> cameFrom = new HashMap<NodeIndex, NodeIndex>();

		double[][] gScore = new double[g.nodes2.length][g.nodes2[0].length];
		initInfinity(g, gScore);

		gScore[start.x][start.y] = 0d;

		double[][] fScore = new double[g.nodes2.length][g.nodes2[0].length];

		initInfinity(g, fScore);

		fScore[start.x][start.y] = calcDist(g.getFromIndex(start), g.getFromIndex(end));

		openSet.add(new NodeIndexWithValue(start, fScore[start.x][start.y]));

		while (!openSet.isEmpty()) {
			NodeIndex current = openSet.remove().nodeIndex;// getLowestInMap(openSet,
															// fScore);

			if (current.x == end.x && current.y == end.y)
				return reconstructPath(cameFrom, current);

			// openSet.remove(current);
			// closedSet.add(current);
			closedSet[current.x][current.y] = true;

			// Neighbors
			NodeIndex neighbor;

			neighbor = createNodeIndexOrFromCache(g, current.x - 1, current.y - 1);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);
			neighbor = createNodeIndexOrFromCache(g, current.x - 1, current.y);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);
			neighbor = createNodeIndexOrFromCache(g, current.x - 1, current.y + 1);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);

			neighbor = createNodeIndexOrFromCache(g, current.x, current.y - 1);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);
			neighbor = createNodeIndexOrFromCache(g, current.x, current.y + 1);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);

			neighbor = createNodeIndexOrFromCache(g, current.x + 1, current.y - 1);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);
			neighbor = createNodeIndexOrFromCache(g, current.x + 1, current.y);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);
			neighbor = createNodeIndexOrFromCache(g, current.x + 1, current.y + 1);
			if (neighbor != null && !closedSet[neighbor.x][neighbor.y])
				expand(g, current, neighbor, end, cameFrom, openSet, fScore, gScore, prefs);

			expandSimilarEntrances(g, current, end, cameFrom, openSet, closedSet, fScore, gScore, prefs);

		}

		return null;
	}

}

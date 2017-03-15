package generic;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

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

	public static List<Node> GenerateUniformNodes(double meterSpacing, LatLng southwest, LatLng northeast) {
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
					Tools.setImageRGB(img, x, y, nodeColor);
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
			Point2D.Double p = APITools.getImagePointFromLatLng(n.getPosition(), southwest, northeast, img.getWidth(),
					img.getHeight());
			int startX = (int) p.getX() - nodePixelRadius;
			int startY = (int) p.getY() - nodePixelRadius;
			for (int x = startX; x <= startX + (nodePixelRadius * 2); x++) {
				for (int y = startY; y <= startY + (nodePixelRadius * 2); y++) {
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

	public static List<Integer> dijkstra(int startNodeIndex, Graph g, int endNodeIndex) {
		return dijkstra(startNodeIndex, g, endNodeIndex, new UserPrefs(.5, .5, false, false, false, 0, false));

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

}

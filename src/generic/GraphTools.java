package generic;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

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

	/**
	 * Generates some number of nodes in a normal distribution around the
	 * existing nodes.
	 * 
	 * @param existingNodes
	 * @param numToGen
	 * @return
	 */
	public static List<Node> GenerateRandomNodes(List<Node> existingNodes, int numToGen) {
		return null;
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
					img.setRGB(x1, cy++, lineColor.getRGB());
				while (cy <= y2);
				return;
			} else {
				cy = y2;
				do
					img.setRGB(x1, cy++, lineColor.getRGB());
				while (cy <= y1);
				return;
			}
		}
		if (ddy == 0) { // horizontal line special case
			if (ddx > 0) {
				cx = x1;
				do
					img.setRGB(cx, y1, lineColor.getRGB());
				while (++cx <= x2);
				return;
			} else {
				cx = x2;
				do
					img.setRGB(cx, y1, lineColor.getRGB());
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
					img.setRGB(cx, cy, lineColor.getRGB());
					cy += iy;
					dy -= ddx;
				} while (dy >= dx);
				cx += ix;
			} while (dx > 0);
		} else { // >= 45 degrees, a wide line
			do {
				dy -= ddx;
				do {
					img.setRGB(cx, cy, lineColor.getRGB());
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
					img.setRGB(x, y, nodeColor.getRGB());
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

	public void dijkstra(int startNodeIndex, Graph g) {
		List<Double> distance = g.getDistanceList(startNodeIndex);
		List<Integer> prev = new ArrayList();
		for (int i = 0; i < g.getNumNodes(); i++) {
			prev.add(i);
		}
		QueueArray qObj = new QueueArray(distance);
		List<Integer> q = qObj.makeQ(g.getNumNodes());
		int counter = 0;
		while (counter < q.size()) {
			int u = qObj.deleteMin();
			// List <Integer> edges = g.getNodes();
		}

	}

}

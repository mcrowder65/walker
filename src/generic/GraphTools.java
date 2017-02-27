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

	public static List<Node> CreateNodesFromPolyline(String[] polylinePieces, double density) {
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
	 * Expected: start.x < end.x
	 * 
	 * @param img
	 * @param start
	 * @param end
	 * @param lineColor
	 */
	private static void DrawLineBetween(BufferedImage img, Point2D.Double start, Point2D.Double end, Color lineColor) {
		double deltax = (int) (end.x - start.x);
		double deltay = (int) (end.y - start.y);
		int y = (int) start.y;
		if (deltax == 0) {
			int minY = (int) Math.min(start.y, end.y);
			int maxY = (int) Math.max(start.y, end.y);
			for (y = minY; y <= maxY; y++)
				img.setRGB((int) start.x, y, lineColor.getRGB());
			return;
		}
		double deltaerr = Math.abs(deltay / deltax);
		double error = deltaerr - 0.5;

		for (int x = (int) start.x; x <= (int) end.x; x++) {
			img.setRGB(x, y, lineColor.getRGB());
			error += deltaerr;
			if (error >= 0.5) {
				y++;
				error -= 1;
			}

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
				Point2D.Double maxX = p.x > prevPoint.x ? p : prevPoint;
				DrawLineBetween(img, minX, maxX, lineColor);
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

	}

}

package server;

import java.awt.geom.Point2D;

public class Node {

	private Point2D.Double position;
	private Building building;

	public Node(int x, int y) {
		this.position = new Point2D.Double(x, y);
	}

	public Node(int x, int y, Building buliding) {
		this.position = new Point2D.Double(x, y);
		this.building = building;
	}
}

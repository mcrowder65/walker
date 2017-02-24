package generic;

import java.awt.geom.Point2D;

import googlemaps.LatLng;

public class Node {

	private LatLng position;
	private Building building;

	public Node(LatLng position) {
		this.position = position;
	}

	public Node(LatLng position, Building building) {
		this.position = position;
		this.building = building;
	}
}

package generic;

import java.awt.geom.Point2D;

import googlemaps.LatLng;

public class Node {

	public LatLng getPosition() {
		return position;
	}

	public void setPosition(LatLng position) {
		this.position = position;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

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

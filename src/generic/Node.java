package generic;

import generic.objects.Building;
import googlemaps.LatLng;

public class Node {

	private boolean startNode = false;
	private boolean endNode = false;

	public LatLng getPosition() {
		return position;
	}

	public boolean isStart() {
		return startNode;
	}

	public boolean isEnd() {
		return endNode;
	}

	public void setStart(boolean s) {
		this.startNode = s;
	}

	public void setEnd(boolean e) {
		this.endNode = e;
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

	public Node() {
	}
}

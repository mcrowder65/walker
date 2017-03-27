package server.dao;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import generic.Graph;
import generic.Node;
import generic.objects.WalkerObject;

public class GraphFirebaseWrapper extends WalkerObject {
	transient private double[][] originalDistance;
	transient private double[][] originalElevation;
	transient private boolean[][] originalGrass;
	transient private boolean[][] originalWilderness;
	transient private boolean[][] originalBuilding;
	transient private boolean[][] originalParking;
	transient private double[][] originalStairs;

	@SerializedName("name")
	private String name;
	@SerializedName("nodes")
	private List<Node> nodes;
	@SerializedName("distance")
	private List<String> distance;
	@SerializedName("elevation")
	private List<String> elevation;
	@SerializedName("grass")
	private List<String> grass;
	@SerializedName("wilderness")
	private List<String> wilderness;
	@SerializedName("building")
	private List<String> building;
	@SerializedName("grass")
	private List<String> parking;
	@SerializedName("stairs")
	private List<String> stairs;

	public GraphFirebaseWrapper() {
	}

	public GraphFirebaseWrapper(Graph graph) {
		this.originalDistance = graph.getDistance();
		this.originalElevation = graph.getElevation();
		this.originalGrass = graph.getGrass();
		this.originalWilderness = graph.getWilderness();
		this.originalBuilding = graph.getBuilding();
		this.originalParking = graph.getParking();
		this.originalStairs = graph.getStairs();
		this.name = "BYU";// graph.getName();
		this.nodes = graph.getNodes();
		this.setId(graph.getId());
		distance = convert(originalDistance);
		elevation = convert(originalElevation);
		grass = convert(originalGrass);
		wilderness = convert(originalWilderness);
		building = convert(originalBuilding);
		parking = convert(originalParking);
		stairs = convert(originalStairs);
	}

	private boolean isEqual(List<String> list, double[][] matrix) {
		if (list == null || matrix == null) {
			return false;
		}
		for (int x = 0; x < list.size(); x++) {
			String[] values = list.get(x).split(",");
			for (int y = 0; y < values.length; y++) {
				double value = Double.valueOf(values[y]);
				if (value != matrix[x][y]) {
					return false;
				}
			}
		}
		return true;
	}

	private List<String> convert(double[][] matrix) {
		if (matrix == null) {
			return null;
		}
		List<String> temp = new ArrayList<>();
		for (int x = 0; x < matrix.length; x++) {
			StringBuilder str = new StringBuilder();
			for (int y = 0; y < matrix[0].length; y++) {
				if (y != matrix[0].length - 1) {
					str.append(String.valueOf(matrix[x][y]) + ",");
				} else {
					str.append(String.valueOf(matrix[x][y]));
				}
			}
			temp.add(str.toString());
		}
		return temp;
	}

	private List<String> convert(boolean[][] matrix) {
		if (matrix == null) {
			return null;
		}
		List<String> temp = new ArrayList<>();
		for (int x = 0; x < matrix.length; x++) {
			StringBuilder str = new StringBuilder();
			for (int y = 0; y < matrix[0].length; y++) {
				if (y != matrix[0].length - 1) {
					str.append(String.valueOf(matrix[x][y]) + ",");
				} else {
					str.append(String.valueOf(matrix[x][y]));
				}
			}
			temp.add(str.toString());
		}
		return temp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public List<String> getDistance() {
		return null;
	}

	public List<String> getElevation() {
		return null;
	}

	public List<String> getGrass() {
		return null;
	}

	public List<String> getWilderness() {
		return null;
	}

	public List<String> getBuilding() {
		return null;
	}

	public List<String> getParking() {
		return null;
	}

	public List<String> getStairs() {
		return null;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<String> gDistance() {
		return distance;
	}

	public void setDistance(List<String> distance) {
		this.distance = distance;
	}

	public List<String> gElevation() {
		return elevation;
	}

	public void setElevation(List<String> elevation) {
		this.elevation = elevation;
	}

	public List<String> gGrass() {
		return grass;
	}

	public void setGrass(List<String> grass) {
		this.grass = grass;
	}

	public List<String> gWilderness() {
		return wilderness;
	}

	public void setWilderness(List<String> wilderness) {
		this.wilderness = wilderness;
	}

	public List<String> gBuilding() {
		return building;
	}

	public void setBuilding(List<String> building) {
		this.building = building;
	}

	public List<String> gParking() {
		return parking;
	}

	public void setParking(List<String> parking) {
		this.parking = parking;
	}

	public List<String> gStairs() {
		return stairs;
	}

	public void setStairs(List<String> stairs) {
		this.stairs = stairs;
	}

}
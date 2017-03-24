package server.dao;

import java.util.ArrayList;
import java.util.List;

import generic.Graph;
import generic.Node;
import generic.objects.WalkerObject;
import server.JSONTools;

public class GraphFirebaseWrapper extends WalkerObject {
	transient private double[][] originalDistance;
	transient private double[][] originalElevation;
	transient private boolean[][] originalGrass;
	transient private boolean[][] originalWilderness;
	transient private boolean[][] originalBuilding;
	transient private boolean[][] originalParking;
	transient private double[][] originalStairs;

	private String name;
	private List<Node> nodes;
	private List<String> distance;
	private List<String> elevation;
	private List<String> grass;
	private List<String> wilderness;
	private List<String> building;
	private List<String> parking;
	private List<String> stairs;

	public GraphFirebaseWrapper(Graph graph) {
		this.originalDistance = graph.getDistance();
		this.originalElevation = graph.getElevation();
		this.originalGrass = graph.getGrass();
		this.originalWilderness = graph.getWilderness();
		this.originalBuilding = graph.getBuilding();
		this.originalParking = graph.getParking();
		this.originalStairs = graph.getStairs();
		this.name = graph.getName();
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

	public double[][] getOriginalDistance() {
		return originalDistance;
	}

	public void setOriginalDistance(double[][] originalDistance) {
		this.originalDistance = originalDistance;
	}

	public double[][] getOriginalElevation() {
		return originalElevation;
	}

	public void setOriginalElevation(double[][] originalElevation) {
		this.originalElevation = originalElevation;
	}

	public boolean[][] getOriginalGrass() {
		return originalGrass;
	}

	public void setOriginalGrass(boolean[][] originalGrass) {
		this.originalGrass = originalGrass;
	}

	public boolean[][] getOriginalWilderness() {
		return originalWilderness;
	}

	public void setOriginalWilderness(boolean[][] originalWilderness) {
		this.originalWilderness = originalWilderness;
	}

	public boolean[][] getOriginalBuilding() {
		return originalBuilding;
	}

	public void setOriginalBuilding(boolean[][] originalBuilding) {
		this.originalBuilding = originalBuilding;
	}

	public boolean[][] getOriginalParking() {
		return originalParking;
	}

	public void setOriginalParking(boolean[][] originalParking) {
		this.originalParking = originalParking;
	}

	public double[][] getOriginalStairs() {
		return originalStairs;
	}

	public void setOriginalStairs(double[][] originalStairs) {
		this.originalStairs = originalStairs;
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

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<String> getDistance() {
		return distance;
	}

	public void setDistance(List<String> distance) {
		this.distance = distance;
	}

	public List<String> getElevation() {
		return elevation;
	}

	public void setElevation(List<String> elevation) {
		this.elevation = elevation;
	}

	public List<String> getGrass() {
		return grass;
	}

	public void setGrass(List<String> grass) {
		this.grass = grass;
	}

	public List<String> getWilderness() {
		return wilderness;
	}

	public void setWilderness(List<String> wilderness) {
		this.wilderness = wilderness;
	}

	public List<String> getBuilding() {
		return building;
	}

	public void setBuilding(List<String> building) {
		this.building = building;
	}

	public List<String> getParking() {
		return parking;
	}

	public void setParking(List<String> parking) {
		this.parking = parking;
	}

	public List<String> getStairs() {
		return stairs;
	}

	public void setStairs(List<String> stairs) {
		this.stairs = stairs;
	}

	@Override
	public String toJson() {
		return JSONTools.g.toJson(this);
	}

}
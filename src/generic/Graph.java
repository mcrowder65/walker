package generic;

import java.util.ArrayList;
import java.util.List;

import googlemaps.LatLng;
import server.APITools;

public class Graph {

	private double[][] distance;
	private double[][] elevation;
	private List<Node> nodes;

	public Graph(double[][] distance, double[][] elevation, List<Node> nodes) {
		this.distance = distance;
		this.elevation = elevation;
		this.nodes = nodes;
	}

	public Graph() {

	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public void setDistancesFromNodes() {
		distance = new double[nodes.size()][nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			for (int z = 0; z < nodes.size(); z++) {
				if (i == z) {
					distance[i][z] = 0;
				} else {
					Node startNode = nodes.get(i);
					Node endNode = nodes.get(z);
					LatLng locStartNode = startNode.getPosition();
					LatLng locEndNode = endNode.getPosition();
					double longDiff = Math.abs(locEndNode.longitude - locStartNode.longitude);
					double latDiff = Math.abs(locEndNode.latitude - locStartNode.latitude);
					double longSqr = longDiff * longDiff;
					double latSqr = latDiff * latDiff;
					double res = Math.sqrt(longSqr + latSqr);
					distance[i][z] = res;
				}
			}
		}
	}

	public void setElevationsFromNodes() {
		String elevResp = APITools.GetElevationResponse(nodes);
<<<<<<< HEAD
	    double[] elevs = APITools.GetElevations(elevResp, nodes);
		
		elevation = new double[nodes.size()][];
=======
		double[] elevs = APITools.GetElevations(elevResp, nodes);

>>>>>>> 36bdc71a47b9580debfca5d5192a96a15dad2337
		for (int i = 0; i < nodes.size(); i++) {
			elevation[i] = new double[nodes.size()];
			for (int z = 0; z < nodes.size(); z++) {
				elevation[i][z] = Math.abs(elevs[i] - elevs[z]);
			}
		}
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public List<Node> getNodesFromPath(List<Integer> path) {
		List<Node> pathNodes = new ArrayList();
		for (int i = 0; i < path.size(); i++) {
			Node n = this.nodes.get(path.get(i));
			pathNodes.add(n);
		}
		return pathNodes;
	}

	public double getDistance(int startNode, int endNode) {
		return distance[startNode][endNode];
	}

	public List<Double> getDistanceList(int startNode) {
		List<Double> distances = new ArrayList();
		for (int i = 0; i < distance.length; i++) {
			double dist = getDistance(startNode, i);
			distances.add(dist);
		}
		return distances;
	}

	public void addNode(Node n) {
		nodes.add(n);
	}

	public int getNumNodes() {
		return nodes.size();
	}

	public double[][] getDistance() {
		return distance;
	}

	public void setDistance(double[][] distance) {
		this.distance = distance;
	}

	public void setElevation(double[][] elevation) {
		this.elevation = elevation;
	}

}

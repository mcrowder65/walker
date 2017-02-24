package generic;

import java.util.ArrayList;
import java.util.List;

import googlemaps.LatLng;
import googlemaps.PolyUtil;
import server.Node;

public class GraphTools {


	public static Node[] CreateNodesFromPolyline(String[] polylinePieces, double density)
	{
		ArrayList<LatLng> pivots = new ArrayList<LatLng>();
		for (String poly : polylinePieces)
		{
			pivots.addAll(PolyUtil.decode(poly));
		}
		Node[] nodes = new Node[pivots.size()];
		for (int n = 0; n < pivots.size(); n++)
		{
			nodes[n] = new Node(pivots.get(n).longitude, pivots.get(n).latitude, null);
		}
		return nodes;
			
	}
	private double[][] distance;
	private double[][] elevation;

	public GraphTools(double[][] distance, double[][] elevation) {
		this.distance = distance;
		this.elevation = elevation;
	}

	public GraphTools() {

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

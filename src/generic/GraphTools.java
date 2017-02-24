package generic;

import java.util.ArrayList;
import java.util.List;

import googlemaps.LatLng;
import googlemaps.PolyUtil;

public class GraphTools {

	public static Node[] CreateNodesFromPolyline(String[] polylinePieces, double density) {
		ArrayList<LatLng> pivots = new ArrayList<LatLng>();
		for (String poly : polylinePieces) {
			pivots.addAll(PolyUtil.decode(poly));
		}
		Node[] nodes = new Node[pivots.size()];
		for (int n = 0; n < pivots.size(); n++) {
			nodes[n] = new Node(pivots.get(n).longitude, pivots.get(n).latitude, null);
		}
		return nodes;

	}

	public void dijkstra(int startNodeIndex, Graph g) {
		List<Double> distance = new ArrayList();

	}

}

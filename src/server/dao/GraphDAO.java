package server.dao;

import generic.Graph;
import generic.Tools;

public class GraphDAO {
	/**
	 * For name the name is always BYU
	 * 
	 * @param name
	 * @return
	 */
	public static Graph getByName(String name) {
		name = "BYU";
		Graph graph = new Graph();

		return null;
	}

	public static void createOrUpdate(Graph graph) {
		if (graph.getId() == null) {
			// create
			GraphFirebaseWrapper graphFirebaseWrapper = new GraphFirebaseWrapper(graph);
			Tools.firebase.createGraph("graphs", graphFirebaseWrapper);
		} else {
			// update
		}
	}

}

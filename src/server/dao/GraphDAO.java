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
		GraphFirebaseWrapper wrapper = (GraphFirebaseWrapper) Tools.firebase.getAllBy("graphs", "name", name,
				new GraphFirebaseWrapper());
		Graph graph = new Graph(wrapper);
		return graph;
	}

	public static void createOrUpdate(Graph graph) {
		graph.setName("BYU");
		// if (graph.getId() == null || graph.getName() == null) {
		// create
		GraphFirebaseWrapper graphFirebaseWrapper = new GraphFirebaseWrapper(graph);
		Tools.firebase.createGraph("graphs", graphFirebaseWrapper);
		// } else {
		// update

		// }
	}

}

package server;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.Node;
import generic.Tools;
import googlemaps.LatLng;

public class GraphTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDijkstraFakeNodes() {
		double[][] distance = new double[5][5];
		for (int i = 0; i < distance.length; i++) {
			for (int z = 0; z < distance.length; z++) {
				if (i == z) {
					distance[i][z] = 0;
				} else {
					double min = 0;
					double max = 10;
					Random r = new Random();
					double randomValue = min + (max - min) * r.nextDouble();
					distance[i][z] = randomValue;
				}
			}
		}

	}

	@Test
	public void testDijkstraRealNodes() {
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.648338);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		String resp;
		if (Config.USE_MOCK)
			resp = Tools.readMock("BYU_ShortPath");
		else
			resp = APITools.GetDirectionsResponse(Tools.latlngToString(start, true), Tools.latlngToString(end, true));
		String[] polyPieces = server.APITools.GetPolylinePieces(resp);
		String poly = server.APITools.GetOverviewPolyline(resp);
		List<Node> nodes = generic.GraphTools.CreateNodesFromPolyline(polyPieces);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes, 100, southwest, northeast);
		nodes.addAll(newNodes);

		Graph g = new Graph(null, null, nodes);
		g.setDistancesFromNodes();
		List<Integer> path = GraphTools.dijkstra(0, g, newNodes.size() - 1);

	}

}

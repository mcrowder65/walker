package server;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.ImageTools;
import generic.Node;
import generic.PathConstituents;
import generic.Tools;
import generic.UserPrefs;
import googlemaps.LatLng;
import server.processing.ColorOperations;

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

	// @Test
	public void testDijkstraFakeNodes() {
		double[][] distance = new double[5][5];
		for (int i = 0; i < distance.length; i++) {
			for (int z = 0; z < distance.length; z++) {
				if (i == z) {
					distance[i][z] = 0;
				} else {
					distance[i][z] = 5;
				}
			}
		}
		distance[0][2] = 1;
		distance[1][4] = 1;
		distance[2][3] = 1;
		distance[3][1] = 1;
		List<Node> nodes = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			nodes.add(new Node());
		}
		Graph g = new Graph(distance, null, nodes);
		List<Integer> path = GraphTools.dijkstra(0, g, 4, UserPrefs.DISTANCE_ONLY);
		System.out.println(path);

	}

	// // @Test
	// public void testDijkstraRealNodes() {
	// LatLng start = new LatLng(40.249403, -111.650154);
	// LatLng end = new LatLng(40.249218, -111.648338);
	// LatLng center = Tools.getCenter(start, end);
	// int sizeX = 640;
	// int sizeY = 640;
	// int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
	//
	// String resp;
	// if (Config.USE_MOCK)
	// resp = Tools.readMock("BYU_ShortPath");
	// else
	// resp = APITools.GetDirectionsResponse(start.toUrlValue(),
	// end.toUrlValue());
	// String[] polyPieces = server.APITools.GetPolylinePieces(resp);
	// String poly = server.APITools.GetOverviewPolyline(resp);
	// List<Node> nodes =
	// generic.GraphTools.CreateNodesFromPolyline(polyPieces);
	// nodes.get(0).setStart(true);
	// nodes.get(nodes.size() - 1).setEnd(true);
	// BufferedImage img = server.APITools.DownloadStaticMapImage(start, end,
	// sizeX, sizeY, zoom, false);
	//
	// double metersPerPixel = APITools.getMetersPerPixel(center.latitude,
	// zoom);
	// LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX,
	// sizeY);
	// LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX,
	// sizeY);
	//
	// List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes, 10,
	// southwest, northeast);
	// nodes.addAll(newNodes);
	// Graph g = new Graph(null, null, nodes);
	// g.setDistancesFromNodes();
	// g.setElevationsFromNodes();
	//
	// List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g,
	// g.getEndIndex());
	// List<Node> nodesToDraw = g.getNodesFromPath(path);
	//
	// GraphTools.DrawLines(img, nodesToDraw, Color.BLUE, 3, southwest,
	// northeast, Color.ORANGE, g);
	//
	// img = Tools.ClipLogo(img);
	//
	// Tools.WriteImage(img, "testImages/dTest1.png");
	// }

	// @Test
	// public void uniformNodeGenerationTest() throws FileNotFoundException {
	// LatLng start = new LatLng(40.249403, -111.650154);
	// LatLng end = new LatLng(40.249218, -111.648338);
	// LatLng center = Tools.getCenter(start, end);
	// int sizeX = 640;
	// int sizeY = 640;
	// int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
	//
	// String resp;
	// if (Config.USE_MOCK)
	// resp = Tools.readMock("BYU_ShortPath");
	// else
	// resp = APITools.GetDirectionsResponse(start.toUrlValue(),
	// end.toUrlValue());
	//
	// resp = APITools.GetDirectionsResponse(start.toUrlValue(),
	// end.toUrlValue());
	// String[] polyPieces = server.APITools.GetPolylinePieces(resp);
	// String poly = server.APITools.GetOverviewPolyline(resp);
	// List<Node> nodes =
	// generic.GraphTools.CreateNodesFromPolyline(polyPieces);
	//
	// BufferedImage img = server.APITools.DownloadStaticMapImage(start, end,
	// sizeX, sizeY, zoom, true);
	//
	// double metersPerPixel = APITools.getMetersPerPixel(center.latitude,
	// zoom);
	// LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX,
	// sizeY);
	// LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX,
	// sizeY);
	//
	// List<Node> moreNodes = GraphTools.GenerateUniformNodes(10, southwest,
	// northeast);
	// nodes.addAll(moreNodes);
	// Graph g = new Graph(null, null, nodes);
	//
	// GraphTools.WriteGraphToImage(img, g, Color.BLUE, 1, southwest,
	// northeast);
	//
	// Tools.WriteImage(img, "testImages/graphTest1.png");
	//
	// g.setDistancesFromNodes();
	// g.setElevationsFromNodes();
	// nodes.get(0).setStart(true);
	// nodes.get(nodes.size() - 1).setEnd(true);
	//
	// List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g,
	// g.getEndIndex());
	// List<Node> nodesToDraw = g.getNodesFromPath(path);
	//
	// // GraphTools.DrawLines(img, nodesToDraw, Color.BLUE, 3, southwest,
	// // northeast, Color.ORANGE, g);
	//
	// img = Tools.ClipLogo(img);
	//
	// Tools.WriteImage(img, "testImages/dTest3.png");
	// }

	@Test
	public void analyzeImageTest() {
		LatLng start = new LatLng(40.249773, -111.650217);
		LatLng end = new LatLng(40.249798, -111.649540);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		String resp;
		// if (Config.USE_MOCK)
		// resp = Tools.readMock("BYU_ShortPath");
		// else
		resp = APITools.GetDirectionsResponse(start.toUrlValue(), end.toUrlValue());
		String[] polyPieces = server.APITools.GetPolylinePieces(resp);
		String poly = server.APITools.GetOverviewPolyline(resp);
		List<Node> nodes = generic.GraphTools.CreateNodesFromPolyline(polyPieces);
		nodes.get(0).setStart(true);
		nodes.get(nodes.size() - 1).setEnd(true);
		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);
		LatLng buildingPoint = new LatLng(40.249603, -111.650054); // JKB
		BufferedImage filled = ColorOperations.filledImage(img, new Color(Config.MAPS_BUILDING_RGB), southwest,
				northeast, buildingPoint);
		Tools.WriteImage(filled, "testImages/mine.png");

		// List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes,
		// 10, southwest, northeast);
		// nodes.addAll(newNodes);
		Graph g = new Graph(null, null, nodes);
		g.setDistancesFromNodes();
		g.setElevationsFromNodes();
		Node startNode = g.getNodes().get(g.getStartIndex());
		Node endNode = g.getNodes().get(g.getEndIndex());

		List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g, g.getEndIndex());
		List<Node> nodesToDraw = g.getNodesFromPath(path);

		GraphTools.DrawLines(filled, nodesToDraw, Color.BLUE, 3, southwest, northeast, Color.ORANGE, g);

		PathConstituents pc = ImageTools.analyzeImage(filled, startNode, endNode);
		assert pc.building == true;
		assert pc.grass == true;
		assert pc.parkingLot == false;
		assert pc.wilderness == false;
		img = Tools.ClipLogo(filled);

		Tools.WriteImage(filled, "testImages/analyzeTest1.png");
	}

	@Test
	public void findPathTest() {
		LatLng start = new LatLng(40.249773, -111.650217);
		LatLng end = new LatLng(40.249798, -111.649540);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		String resp;

		resp = APITools.GetDirectionsResponse(start.toUrlValue(), end.toUrlValue());
		String[] polyPieces = server.APITools.GetPolylinePieces(resp);
		String poly = server.APITools.GetOverviewPolyline(resp);
		List<Node> nodes = generic.GraphTools.CreateNodesFromPolyline(polyPieces);
		nodes.get(0).setStart(true);
		nodes.get(nodes.size() - 1).setEnd(true);
		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);
		LatLng buildingPoint = new LatLng(40.249603, -111.650054); // JKB
		// BufferedImage filled = ColorOperations.filledImage(img, new
		// Color(Config.MAPS_BUILDING_RGB), southwest,
		// northeast, buildingPoint);
		// buildingPoint = new LatLng(40.249403, -111.651154); // TALMAGE
		// filled = ColorOperations.filledImage(filled, new
		// Color(Config.MAPS_BUILDING_RGB), southwest, northeast,
		// buildingPoint);
		Tools.WriteImage(img, "testImages/b2.png");

		// List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes,
		// 10, southwest, northeast);
		// nodes.addAll(newNodes);
		// Graph g = new Graph(null, null, nodes);
		// g.setDistancesFromNodes();
		// g.setElevationsFromNodes();
		// Node startNode = g.getNodes().get(g.getStartIndex());
		// Node endNode = g.getNodes().get(g.getEndIndex());
		//
		// List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g,
		// g.getEndIndex());
		// List<Node> nodesToDraw = g.getNodesFromPath(path);
		//
		// GraphTools.DrawLines(filled, nodesToDraw, Color.BLUE, 3, southwest,
		// northeast, Color.ORANGE, g);

	}

}

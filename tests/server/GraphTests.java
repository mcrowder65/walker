package server;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.ImageTools;
import generic.Node;
import generic.NodeIndex;
import generic.Tools;
import generic.objects.Building;
import generic.objects.UserPrefs;
import googlemaps.LatLng;
import server.dao.BuildingDAO;

public class GraphTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream("key.json"))
				.setDatabaseUrl("https://walker-73119.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);
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
		// List<Integer> path = GraphTools.dijkstra(0, g, 4,
		// UserPrefs.DISTANCE_ONLY);
		// System.out.println(path);

	}

	// @Test
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

		List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes, 10, southwest, northeast);
		nodes.addAll(newNodes);
		Graph g = new Graph(null, null, nodes);
		g.setLimitedDistancesFromNodes(img, southwest, northeast);
		g.setElevationsFromNodes();

		List<Integer> path = GraphTools.dijkstra(g.start(), g, g.end());
		List<Node> nodesToDraw = g.getNodesFromPath(path);

		GraphTools.DrawLines(img, nodesToDraw, Color.BLUE, 3, southwest, northeast, Color.ORANGE, g);

		img = Tools.ClipLogo(img);

		Tools.WriteImage(img, "testImages/dTest1.png");
	}

	// @Test
	public void findPathTest() {
		LatLng start = new LatLng(40.248904, -111.651412);
		LatLng end = new LatLng(40.249121, -111.648808);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		BufferedImage img_clean = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);
		List<Node> nodes = GraphTools.GenerateUniformNodes(10, southwest, northeast, false);
		List<Node> newNodes = GraphTools.RemoveBuildingNodes(nodes, img, southwest, northeast);
		Graph g = new Graph(null, null, newNodes);
		int startNodeIndex = g.findClosestNodeIndex(new Node(start.latitude, start.longitude, null, true, false));
		int endNodeIndex = g.findClosestNodeIndex(new Node(end.latitude, end.longitude, null, false, true));
		g.setStartNode(startNodeIndex);
		g.setEndNode(endNodeIndex);
		GraphTools.WriteGraphToImage(img, g, Color.BLUE, 1, southwest, northeast);
		Tools.WriteImage(img, "testImages/b2.png");
		g.addEnterExit();
		g.setLimitedDistancesFromNodes(img, southwest, northeast);
		// g.generateMatrix(img, southwest, northeast);
		UserPrefs up = new UserPrefs(1, 0, 0, 0, 0, 0, 0);
		g.sumMatricies(up);
		List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g, g.getEndIndex());
		List<Node> nodesToDraw = g.getNodesFromPath(path);

		GraphTools.DrawRouteOnly(img_clean, nodesToDraw, Color.BLUE, 1, southwest, northeast, Color.ORANGE);
		Tools.WriteImage(img_clean, "testImages/finalClean.png");
		GraphTools.DrawLines(img_clean, nodesToDraw, Color.BLUE, 1, southwest, northeast, Color.ORANGE, g);

		Tools.WriteImage(img_clean, "testImages/final.png");
		// //
	}

	// @Test
	public void addBlackNodesTest() {
		LatLng start = new LatLng(40.248904, -111.651412);
		LatLng end = new LatLng(40.249121, -111.648808);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);
		List<Node> nodes = new ArrayList();
		Graph g = new Graph(null, null, nodes);
		g.addBlackNodes(img, southwest, northeast);
		GraphTools.WriteGraphToImage(img, g, Color.BLUE, 1, southwest, northeast);
		Tools.WriteImage(img, "testImages/blackNodesOnly.png");

	}

	// @Test
	public void testLimitedDist() {
		// LatLng start = new LatLng(40.248904, -111.651412);
		// LatLng end = new LatLng(40.249121, -111.648808);
		LatLng start = new LatLng(40.249304, -111.649816);
		LatLng end = new LatLng(40.250478, -111.648631);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		img = Tools.ClipLogo(img);

		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);

		List<Node> nodes = GraphTools.GenerateUniformNodes(10, southwest, northeast, false);
		List<Node> newNodes = GraphTools.RemoveBuildingNodes(nodes, img, southwest, northeast);
		Graph g = new Graph(null, null, newNodes);
		int startNodeIndex = g.findClosestNodeIndex(new Node(start.latitude, start.longitude, null, true, false));
		int endNodeIndex = g.findClosestNodeIndex(new Node(end.latitude, end.longitude, null, false, true));
		g.setStartNode(startNodeIndex);
		g.setEndNode(endNodeIndex);
		g.addBlackNodes(img, southwest, northeast);

		// GraphTools.WriteGraphToImage(img, g, Color.BLUE, 1, southwest,
		// northeast);
		// Tools.WriteImage(img, "testImages/allNodes.png");

		g.addEnterExit();
		g.setLimitedDistancesFromNodes(img, southwest, northeast);
		UserPrefs up = new UserPrefs(1, 0, 0, 1, 0, 0, 0);
		g.sumMatricies(up);
		List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g, g.getEndIndex());
		List<Node> nodesToDraw = g.getNodesFromPath(path);
		GraphTools.DrawLines(img, nodesToDraw, Color.BLUE, 1, southwest, northeast, Color.ORANGE, g);
		Tools.WriteImage(img, "testImages/throughBuilding.png");
	}

	// @Test
	public void a_starTest() {
		LatLng start = new LatLng(40.249021, -111.650779);
		LatLng end = new LatLng(40.249127, -111.648735);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		img = Tools.ClipLogo(img);

		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);

		Node[][] nodes = GraphTools.genUniformNodes(1, southwest, northeast, img);
		Graph g = new Graph();
		g.nodes2 = nodes;

		NodeIndex startNode = new NodeIndex(2, 6);
		NodeIndex endNode = new NodeIndex(80, 46);

		List<NodeIndex> starPath = GraphTools.A_Star(g, startNode, endNode, UserPrefs.BLACK_PATHS);
		GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);

		Tools.WriteImage(img, "testImages/a_star.png");

	}

	@Test
	public void a_starTest_2() {
		LatLng start = new LatLng(40.249533, -111.650287);
		LatLng end = new LatLng(40.249104, -111.648759);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		img = Tools.ClipLogo(img);

		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);

		Node[][] nodes = GraphTools.genUniformNodes(1, southwest, northeast, img);
		Graph g = new Graph();
		g.nodes2 = nodes;
		NodeIndex startNode = g.getClosestNodeFast(start, southwest);
		NodeIndex endNode = g.getClosestNodeFast(end, southwest);

		List<NodeIndex> starPath = GraphTools.A_Star(g, startNode, endNode, UserPrefs.DEFAULT);
		GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);

		Tools.WriteImage(img, "testImages/a_star_2.png");

	}

	@Test
	public void a_starTest_blackPath2() {
		LatLng start = new LatLng(40.249773, -111.650226);
		LatLng end = new LatLng(40.249104, -111.648759);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		img = Tools.ClipLogo(img);

		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);

		Node[][] nodes = GraphTools.genUniformNodes(1, southwest, northeast, img);
		Graph g = new Graph();
		g.nodes2 = nodes;
		NodeIndex startNode = g.getClosestNodeFast(start, southwest);
		NodeIndex endNode = g.getClosestNodeFast(end, southwest);
		NodeIndex startNodeBlack = g.getClosestBlackNodeFast(start, southwest);
		NodeIndex endNodeBlack = g.getClosestBlackNodeFast(end, southwest);
		System.out.println(startNode);
		System.out.println(endNode);
		System.out.println(startNodeBlack);
		System.out.println(endNodeBlack);
		List<NodeIndex> starPath = GraphTools.A_Star(g, startNodeBlack, endNodeBlack, UserPrefs.BLACK_PATHS);
		starPath.add(startNode);
		starPath.add(0, endNode);
		GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);

		Tools.WriteImage(img, "testImages/a_star_2_black.png");

	}

	// @Test
	public void a_starTest_blackPath() {
		LatLng start = new LatLng(40.249773, -111.650226);
		LatLng end = new LatLng(40.249104, -111.648759);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		img = Tools.ClipLogo(img);

		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);

		Node[][] nodes = GraphTools.genUniformNodes(1, southwest, northeast, img);
		Graph g = new Graph();
		g.nodes2 = nodes;
		NodeIndex startNode = g.getClosestNodeLoc(start);
		NodeIndex endNode = g.getClosestNodeLoc(end);
		NodeIndex startNodeBlack = g.getClosestBlackNodeLoc(start);
		NodeIndex endNodeBlack = g.getClosestBlackNodeLoc(end);
		System.out.println(startNode);
		System.out.println(endNode);
		System.out.println(startNodeBlack);
		System.out.println(endNodeBlack);
		List<NodeIndex> starPath = GraphTools.A_Star(g, startNodeBlack, endNodeBlack, UserPrefs.BLACK_PATHS);
		starPath.add(startNode);
		starPath.add(0, endNode);
		GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);

		Tools.WriteImage(img, "testImages/a_star_2_black.png");

	}

	// @Test
	public void a_starBIGTest() {
		/*
		 * LatLng start = new LatLng(40.249021, -111.650779); LatLng end = new
		 * LatLng(40.249127, -111.648735);
		 * 
		 * LatLng center = Tools.getCenter(start, end); int sizeX = 640; int
		 * sizeY = 640; int zoom = APITools.getAppropriateZoom(start, end,
		 * sizeX, sizeY); double metersPerPixel =
		 * APITools.getMetersPerPixel(center.latitude, zoom);
		 */
		LatLng southwest = new LatLng(40.244803, -111.657854);
		LatLng northeast = new LatLng(40.2519803, -111.643854);

		BufferedImage img = Tools.ReadImage("mock/campus.png");
		Node[][] nodes = GraphTools.genUniformNodes(2, southwest, northeast, img);
		Graph g = new Graph();
		g.nodes2 = nodes;

		NodeIndex startNode = new NodeIndex(200, 53);
		NodeIndex endNode = new NodeIndex(405, 365);

		List<NodeIndex> starPath = GraphTools.A_Star(g, startNode, endNode, UserPrefs.DEFAULT);
		GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);

		Tools.WriteImage(img, "testImages/a_starBIG.png");
	}

	// @Test
	public void testingNormalPaths() {
		LatLng start = new LatLng(40.249021, -111.650779);
		LatLng end = new LatLng(40.249127, -111.648735);

		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		img = Tools.ClipLogo(img);

		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);

		List<Node> nodes = GraphTools.GenerateUniformNodes(10, southwest, northeast, false);
		List<Node> newNodes = GraphTools.RemoveBuildingNodes(nodes, img, southwest, northeast);
		Graph g = new Graph(null, null, newNodes);
		int startNodeIndex = g.findClosestNodeIndex(new Node(start.latitude, start.longitude, null, true, false));
		int endNodeIndex = g.findClosestNodeIndex(new Node(end.latitude, end.longitude, null, false, true));
		g.setStartNode(startNodeIndex);
		g.setEndNode(endNodeIndex);
		g.addBlackNodes(img, southwest, northeast);
		// g.addEnterExit();
		g.setLimitedDistancesFromNodes(img, southwest, northeast);
		UserPrefs up = new UserPrefs(1, 0, 0, 1, 0, 0, 1);
		g.sumMatricies(up);
		if (up.getPreferDesignatedPaths() > 0) {
			int startIndex = g.findClosestBlackNodeIndex(g.getNodes().get(g.getStartIndex()));
			int endIndex = g.findClosestBlackNodeIndex(g.getNodes().get(g.getEndIndex()));
			List<Integer> path = GraphTools.dijkstra(startIndex, g, endIndex);
			List<Node> nodesToDraw = g.getNodesFromPath(path);
			nodesToDraw.add(0, g.getStartNode());
			nodesToDraw.add(g.getEndNode());
			GraphTools.DrawLines(img, nodesToDraw, Color.BLUE, 1, southwest, northeast, Color.ORANGE, g);
			Tools.WriteImage(img, "testImages/normalPath.png");

		} else {
			List<Integer> path = GraphTools.dijkstra(g.getStartIndex(), g, g.getEndIndex());
			List<Node> nodesToDraw = g.getNodesFromPath(path);
			GraphTools.DrawLines(img, nodesToDraw, Color.BLUE, 1, southwest, northeast, Color.ORANGE, g);
			Tools.WriteImage(img, "testImages/normalPath.png");
		}
	}

}

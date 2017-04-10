package server.handlers.travel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.Node;
import generic.NodeIndex;
import generic.Tools;
import generic.objects.Marker;
import generic.objects.UserPrefs;
import googlemaps.LatLng;
import server.APITools;
import server.JSONTools;
import server.dao.GraphDAO;
import server.handlers.WalkerHandler;

public class TravelHandler extends WalkerHandler {
	Object lock;

	public TravelHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// synchronized (lock) {
		String result = getRequestBodyAndSetHeaders(exchange);
		JsonObject jsonObject = JSONTools.g.fromJson(result, JsonObject.class);
		Marker startMarker = JSONTools.g.fromJson(jsonObject.get("startMarker"), Marker.class);
		Marker endMarker = JSONTools.g.fromJson(jsonObject.get("endMarker"), Marker.class);
		// TODO handle!!!!!!!!
		UserPrefs userPrefs = JSONTools.g.fromJson(jsonObject.get("userOptions"), UserPrefs.class);
		Calendar cal = new GregorianCalendar();

		int currentHourInMilitaryTime = cal.get(Calendar.HOUR_OF_DAY);
		System.out.println(userPrefs);
	}

	public void getPath(Marker startMarker, Marker endMarker) {
		LatLng start = new LatLng(startMarker.getLatitude(), startMarker.getLongitude());
		LatLng end = new LatLng(endMarker.getLatitude(), endMarker.getLongitude());
		LatLng southwest = new LatLng(40.244803, -111.657854);
		LatLng northeast = new LatLng(40.2519803, -111.643854);
		BufferedImage img = Tools.ReadImage("mock/campus.png");
		Node[][] nodes = GraphTools.genUniformNodes(2, southwest, northeast, img);
		Graph g = new Graph();
		g.nodes2 = nodes;

		NodeIndex startNode = g.getClosestNodeFast(start, southwest);
		NodeIndex endNode = g.getClosestNodeFast(end, southwest);
		NodeIndex startNodeBlack = g.getClosestBlackNodeFast(start, southwest);
		NodeIndex endNodeBlack = g.getClosestBlackNodeFast(end, southwest);

		List<NodeIndex> starPath = GraphTools.A_Star(g, startNode, endNode, UserPrefs.DEFAULT);
		starPath.add(startNode);
		starPath.add(0, endNode);
		GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);

		Tools.WriteImage(img, "testImages/bigTest.png");
	}

	private void asdf() {
		// this won't work. see graphtests or something for reference
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
		// g.setDistancesFromNodes();
		g.setElevationsFromNodes();
		GraphDAO.createOrUpdate(g);
	}

}

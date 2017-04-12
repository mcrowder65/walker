package server.handlers.travel;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

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

		startMarker.setLatitude(startMarker.getLatitude() + Config.LAT_BIAS);
		startMarker.setLongitude(startMarker.getLongitude() + Config.LON_BIAS);
		endMarker.setLatitude(endMarker.getLatitude() + Config.LAT_BIAS);
		endMarker.setLongitude(endMarker.getLongitude() + Config.LON_BIAS);

		UserPrefs userPrefs = JSONTools.g.fromJson(jsonObject.get("userOptions"), UserPrefs.class);

		// System.out.println(userPrefs);

		try {
			List<Marker> markers = getPath(startMarker, endMarker, userPrefs);
			String json = JSONTools.g.toJson(markers);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();
		} catch (Exception e) {
			// getting ArrayIndexOutOfBoundsException
			e.printStackTrace();
			exchange.sendResponseHeaders(500, 0);
			exchange.getResponseBody().close();
		}

	}

	public List<Marker> getPath(Marker startMarker, Marker endMarker, UserPrefs up) throws Exception {
		NodeIndex startNode = null;
		NodeIndex endNode = null;
		List<NodeIndex> starPath = null;
		Graph g = Config.GRAPH;
		List<Marker> markers = null;
		LatLng start = null;
		LatLng end = null;
		try {
			start = new LatLng(startMarker.getLatitude(), startMarker.getLongitude());
			end = new LatLng(endMarker.getLatitude(), endMarker.getLongitude());
			System.out.println(start);
			System.out.println(end);
			LatLng southwest = new LatLng(40.244803, -111.657854);
			LatLng northeast = new LatLng(40.2519803, -111.643854);
			// BufferedImage img = Tools.ReadImage("mock/campus.png");
			// Node[][] nodes = GraphTools.genUniformNodes(2, southwest,
			// northeast, img);
			// g = new Graph();
			// g.nodes2 = nodes;

			startNode = g.getClosestNodeFast(start, southwest);
			endNode = g.getClosestNodeFast(end, southwest);
			// NodeIndex startNodeBlack = g.getClosestBlackNodeFast(start,
			// southwest);
			// NodeIndex endNodeBlack = g.getClosestBlackNodeFast(end,
			// southwest);
			// UserPrefs up = new UserPrefs(0, 0, 0, 0, 0, 0, 0);
			starPath = GraphTools.A_Star(g, startNode, endNode, up);
			markers = new ArrayList<>();
			starPath.add(startNode);
			starPath.add(0, endNode);
			for (int i = 0; i < starPath.size(); i++) {
				Node n = g.getFromIndex(starPath.get(i));

				Marker m = new Marker(n.getPosition().latitude - Config.LAT_BIAS,
						n.getPosition().longitude - Config.LON_BIAS);

				markers.add(m);
			}

			BufferedImage img = Tools.ReadImage("mock/campus.png");
			GraphTools.WriteAStarPathToImage(img, g, starPath, southwest, northeast, Color.BLUE);
			Tools.WriteImage(img, "testImages/a_star_2.png");

			System.out.println(markers);
			// GraphTools.WriteAStarPathToImage(img, g, starPath, southwest,
			// northeast, Color.BLUE);

			// Tools.WriteImage(img, "testImages/bigTest.png");
		} catch (Exception e) {
			System.err.println("startNode: " + startNode);
			System.err.println("endNode: " + endNode);
			System.err.println("start: " + start);
			System.err.println("end: " + end);
			System.err.println("startMarker: " + startMarker);
			System.err.println("endMarker: " + endMarker);
			System.err.println("userpreferences: " + up);
			throw e;
		}

		return markers;

	}

}

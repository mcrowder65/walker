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
import generic.ZoningTools;
import generic.objects.Marker;
import generic.objects.UserPrefs;
import googlemaps.LatLng;
import server.JSONTools;
import server.handlers.WalkerHandler;

public class TravelHandler extends WalkerHandler {
	Object lock;

	public TravelHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// synchronized (lock) {
		long start = System.currentTimeMillis();
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
			exchange.sendResponseHeaders(200, 0);
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();
		} catch (Exception e) {
			// getting ArrayIndexOutOfBoundsException
			e.printStackTrace();
			String json = "null pointer exception";
			exchange.sendResponseHeaders(400, 0);
			exchange.getResponseBody().write(json.getBytes());

			exchange.getResponseBody().close();
		}
		long end = System.currentTimeMillis();
		System.out.println("done travelling, took " + (((long) end / (long) 1000) - ((long) start / (long) 1000)));

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
			int hour = ZoningTools.GetHour(southwest);

			startNode = g.getClosestNodeFast(start, southwest);
			endNode = g.getClosestNodeFast(end, southwest);
			// if (g.nodes2[startNode.x][startNode.y].code ==
			// NodeCode.Building){
			// NodeIndex temp1 =
			// g.getClosestBuildingNodeFast(g.getNodeFromIndex(endNode).getPosition(),
			// southwest);
			// NodeIndex temp2 =
			// g.getClosestBuildingNodeFast(g.getNodeFromIndex(startNode).getPosition(),
			// southwest);
			// if(g.getNodeFromIndex(temp1).getBuilding() ==
			// g.getNodeFromIndex(temp2).getBuilding()){
			// startNode = temp1;
			// }
			// }

			starPath = GraphTools.A_Star(g, startNode, endNode, up, hour);
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

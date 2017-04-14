package server;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.sun.net.httpserver.HttpServer;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.Node;
import generic.Tools;
import googlemaps.LatLng;
import server.handlers.building.GetBuildingHandler;
import server.handlers.building.GetBuildingsHandler;
import server.handlers.marker.DeleteMarkerHandler;
import server.handlers.marker.GetMarkersHandler;
import server.handlers.marker.SetMarkerHandler;
import server.handlers.travel.TravelHandler;

public class Server {

	private final int MAX_WAITING_CONNECTIONS = 30;
	private HttpServer httpServer;
	private GetMarkersHandler getMarkersHandler;
	private SetMarkerHandler setMarkerHandler;
	private DeleteMarkerHandler deleteMarkerHandler;
	private GetBuildingsHandler getBuildingsHandler;
	private GetBuildingHandler getBuildingHandler;
	private TravelHandler travelHandler;

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		new Server().run(args);
	}

	public void run(String[] args) throws SAXException, IOException, ParserConfigurationException {

		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream("key.json"))
				.setDatabaseUrl("https://walker-73119.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);

		int port = 8081;

		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}

		System.out.println("Port = " + port);
		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), MAX_WAITING_CONNECTIONS);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		httpServer.setExecutor(null);
		getMarkersHandler = new GetMarkersHandler();
		httpServer.createContext("/getMarkers", getMarkersHandler);

		setMarkerHandler = new SetMarkerHandler();
		httpServer.createContext("/setMarker", setMarkerHandler);

		deleteMarkerHandler = new DeleteMarkerHandler();
		httpServer.createContext("/deleteMarker", deleteMarkerHandler);

		getBuildingsHandler = new GetBuildingsHandler();
		httpServer.createContext("/getBuildings", getBuildingsHandler);

		getBuildingHandler = new GetBuildingHandler();
		httpServer.createContext("/getBuilding", getBuildingHandler);

		travelHandler = new TravelHandler();
		httpServer.createContext("/travel", travelHandler);

		httpServer.start();
		reset();
	}

	public static void reset() {
		long start = System.currentTimeMillis();
		try {
			System.out.println("resetting");

			LatLng southwest = new LatLng(40.244803, -111.657854);
			LatLng northeast = new LatLng(40.2519803, -111.643854);
			BufferedImage img = Tools.ReadImage("mock/campus.png");
			Node[][] nodes = GraphTools.genUniformNodes(2, southwest, northeast, img);
			Graph g = new Graph();
			g.nodes2 = nodes;
			Config.GRAPH = g;
			g.addEnterExitFast(southwest);

		} catch (Exception e) {
			System.err.println("This error was handled gracefullyish");
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();

		System.out.println("done resetting. It took " + (getSeconds(end) - getSeconds(start)) + " seconds");

	}

	private static int getSeconds(long milli) {
		return (int) ((long) milli / (long) 1000);
	}
}

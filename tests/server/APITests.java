package server;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Test;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.Node;
import generic.Tools;
import googlemaps.LatLng;

public class APITests {

	@Test
	@SuppressWarnings("unused")
	public void polylineTest() {
		// Point2D.Double start = new Point2D.Double(40.249403, -111.650154);
		// Point2D.Double end = new Point2D.Double(40.249218, -111.648338);
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.638338);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 400;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		String resp = APITools.GetDirectionsResponse(start.toUrlValue(), end.toUrlValue());

		// String resp =
		// APITools.GetDirectionsResponse(Tools.latlngToString(start, true),
		// Tools.latlngToString(end, true));
		String poly = server.APITools.GetOverviewPolyline(resp);
		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, true, poly);
		Tools.WriteImage(img, "testImages/polytest2.png");
	}

	@SuppressWarnings("unused")
	@Test
	public void nodesFromPolylineTest() {
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
		Graph g = new Graph(null, null, nodes);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, true);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		GraphTools.WriteGraphToImage(img, g, new Color(0, 0, 0), 1, southwest, northeast, new Color(255, 255, 255));
		img = Tools.ClipLogo(img);

		Tools.WriteImage(img, "testImages/polytest5.png");
	}

	@SuppressWarnings("unused")
	@Test
	public void genNodesTest() {
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

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, true);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes, 100, southwest, northeast);
		nodes.addAll(newNodes);

		Graph g = new Graph(null, null, nodes);

		GraphTools.WriteGraphToImage(img, g, new Color(255, 0, 0), 2, southwest, northeast);
		img = Tools.ClipLogo(img);

		Tools.WriteImage(img, "testImages/polytest7.png");
	}

	@SuppressWarnings("unused")
	@Test
	public void elevationTest() {
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

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, true);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		List<Node> newNodes = generic.GraphTools.GenerateRandomNodes(nodes, 100, southwest, northeast);
		nodes.addAll(newNodes);
		String elevResp = APITools.GetElevationResponse(nodes);
		double[] elevs = APITools.GetElevations(elevResp, nodes);

	}
}

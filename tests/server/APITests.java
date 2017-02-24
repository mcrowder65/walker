package server;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Test;

import generic.Node;
import generic.Tools;
import googlemaps.LatLng;

public class APITests {


	@Test
	public void polylineTest()
	{
		//Point2D.Double start = new Point2D.Double(40.249403, -111.650154);
		//Point2D.Double end = new Point2D.Double(40.249218, -111.648338);
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.638338);
		int sizeX = 640;
		int sizeY = 400;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);
		
		String resp = APITools.GetDirectionsResponse(Tools.latlngToString(start, true), Tools.latlngToString(end, true));
		String poly = server.APITools.GetOverviewPolyline(resp);
		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, poly);
		Tools.WriteImage(img, "testImages/polytest2.png");
	}
	
	@SuppressWarnings("unused")
	//@Test
	public void nodesFromPolylineTest()
	{
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.638338);
		
		String resp = APITools.GetDirectionsResponse(Tools.latlngToString(start, true), Tools.latlngToString(end, true));
		String[] polyPieces = server.APITools.GetPolylinePieces(resp);
		Node[] nodes = generic.GraphTools.CreateNodesFromPolyline(polyPieces, 3);
		//BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, 640, 400, poly);
		//Tools.WriteImage(img, "testImages/polytest.png");
	}
}

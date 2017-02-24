package server;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.junit.Test;

import generic.Tools;

public class APITests {


	@Test
	public void polylineTest()
	{
		Point2D.Double start = new Point2D.Double(40.249403, -111.650154);
		Point2D.Double end = new Point2D.Double(40.249218, -111.648338);
		
		
		String resp = APITools.GetDirectionsResponse(Tools.pointToString(start, true), Tools.pointToString(end, true));
		String poly = server.APITools.GetOverviewPolyline(resp);
		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, 640, 400, poly);
		Tools.WriteImage(img, "testImages/polytest.png");
	}
	
	@Test
	public void nodesFromPolylineTest()
	{
		Point2D.Double start = new Point2D.Double(40.249403, -111.650154);
		Point2D.Double end = new Point2D.Double(40.249218, -111.648338);
		
		
		String resp = APITools.GetDirectionsResponse(Tools.pointToString(start, true), Tools.pointToString(end, true));
		String poly = server.APITools.GetOverviewPolyline(resp);
		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, 640, 400, poly);
		Tools.WriteImage(img, "testImages/polytest.png");
	}
}

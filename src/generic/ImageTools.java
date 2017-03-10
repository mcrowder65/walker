package generic;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import googlemaps.LatLng;
import server.APITools;

public class ImageTools {

	public static void analyzeImage(BufferedImage img, Node startNode, Node endNode) {
		int zoom = APITools.getAppropriateZoom(startNode.getPosition(), endNode.getPosition(), img.getWidth(),
				img.getHeight());
		LatLng center = Tools.getCenter(startNode.getPosition(), endNode.getPosition());
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, img.getWidth(), img.getHeight());
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, img.getWidth(), img.getHeight());

		Point2D.Double startPnt = APITools.getImagePointFromLatLng(startNode.getPosition(), southwest, northeast,
				img.getWidth(), img.getHeight());
		Point2D.Double endPnt = APITools.getImagePointFromLatLng(endNode.getPosition(), southwest, northeast,
				img.getWidth(), img.getHeight());

		int x1 = (int) startPnt.x;
		int y1 = (int) startPnt.y;
		// int x2 = (int) endPnt.x;
		// int y2 = (int) endPnt.y;
		int x2 = 457;
		int y2 = 320;

		int rgbStart = img.getRGB(x1, y1);
		int rgbEnd = img.getRGB(x2, y2);
		System.out.println(x1);
		System.out.println(y1);
		System.out.println(rgbStart);
		System.out.println(x2);
		System.out.println(y2);
		System.out.println(rgbEnd);

	}
}

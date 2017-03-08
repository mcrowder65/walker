package generic;

import java.awt.image.BufferedImage;

import googlemaps.LatLng;
import server.APITools;

public class ImageTools {

	public static void analyzeImage(BufferedImage img, Node startNode, Node endNode) {
		int zoom = APITools.getAppropriateZoom(startNode.getPosition(), endNode.getPosition(), img.getWidth(),
				img.getHeight());
		LatLng center = Tools.getCenter(startNode.getPosition(), endNode.getPosition());
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);

		// Double pnt = APITools.getImagePointFromLatLng(location, southwest,
		// northeast, sizeX, sizeY)

	}
}

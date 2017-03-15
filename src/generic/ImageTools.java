package generic;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import googlemaps.LatLng;
import server.APITools;

public class ImageTools {

	public static void getNumWhitePixles(int centerX, int centerY, BufferedImage img) {
		List<Integer> vals = new ArrayList();
		vals.add(img.getRGB(centerX, centerY - 1));
		vals.add(img.getRGB(centerX - 1, centerY - 1));
		vals.add(img.getRGB(centerX + 1, centerY - 1));
		vals.add(img.getRGB(centerX - 1, centerY));
		vals.add(img.getRGB(centerX + 1, centerY));
		vals.add(img.getRGB(centerX, centerY + 1));
		vals.add(img.getRGB(centerX - 1, centerY + 1));
		vals.add(img.getRGB(centerX + 1, centerY + 1));
		System.out.println(vals);
	}

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
		int x2 = (int) endPnt.x;
		int y2 = (int) endPnt.y;

		int cx, cy, ix, iy, dx, dy, ddx = x2 - x1, ddy = y2 - y1;

		// getNumWhitePixles(x2, y2, img);
		//
		// if (ddx == 0) { // vertical line special case
		// if (ddy > 0) {
		// cy = y1;
		// do
		// Tools.setImageRGB(img, x1, cy++, lineColor);
		// while (cy <= y2);
		// return;
		// } else {
		// cy = y2;
		// do
		// Tools.setImageRGB(img, x1, cy++, lineColor);
		// while (cy <= y1);
		// return;
		// }
		// }
		// if (ddy == 0) { // horizontal line special case
		// if (ddx > 0) {
		// cx = x1;
		// do
		// Tools.setImageRGB(img, cx, y1, lineColor);
		// while (++cx <= x2);
		// return;
		// } else {
		// cx = x2;
		// do
		// Tools.setImageRGB(img, cx, y1, lineColor);
		// while (++cx <= x1);
		// return;
		// }
		// }
		// if (ddy < 0) {
		// iy = -1;
		// ddy = -ddy;
		// } // pointing up
		// else
		// iy = 1;
		// if (ddx < 0) {
		// ix = -1;
		// ddx = -ddx;
		// } // pointing left
		// else
		// ix = 1;
		// dx = dy = ddx * ddy;
		// cy = y1;
		// cx = x1;
		// if (ddx < ddy) { // < 45 degrees, a tall line
		// do {
		// dx -= ddy;
		// do {
		// Tools.setImageRGB(img, cx, cy, lineColor);
		// cy += iy;
		// dy -= ddx;
		// } while (dy >= dx);
		// cx += ix;
		// } while (dx > 0);
		// } else { // >= 45 degrees, a wide line
		// do {
		// dy -= ddx;
		// do {
		// Tools.setImageRGB(img, cx, cy, lineColor);
		// cx += ix;
		// dx -= ddy;
		// } while (dx >= dy);
		// cy += iy;
		// } while (dy > 0);
		// }

		// int rgbStart = img.getRGB(x1, y1);
		// int rgbEnd = img.getRGB(x2, y2);
		// System.out.println(x1);
		// System.out.println(y1);
		// System.out.println(rgbStart);
		// System.out.println(x2);
		// System.out.println(y2);
		// System.out.println(rgbEnd);
	}

}

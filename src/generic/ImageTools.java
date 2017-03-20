package generic;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import googlemaps.LatLng;
import server.APITools;

public class ImageTools {

	public static void getNumWhitePixles(int centerX, int centerY, BufferedImage img) {
		List<Integer> vals = new ArrayList<>();
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

	public static PathConstituents analyzeImage(BufferedImage img, Node startNode, Node endNode) {
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

		// int rgbEnd = img.getRGB(x2, y2);
		// System.out.println(rgbEnd);

		int cx, cy, ix, iy, dx, dy, ddx = x2 - x1, ddy = y2 - y1;
		PathConstituents path = new PathConstituents(false, false, false, false, 0);

		if (ddx == 0) {
			if (ddy > 0) {
				cy = y1;
				int rgb = -1;
				do {

					rgb = img.getRGB(x1, cy++);
					if (rgb == -65536) {
						path.building = true;
					} else if (rgb == -3414877) {
						path.grass = true;
					}
				} while (cy <= y2);
				return path;
			} else {
				cy = y2;
				int rgb = -1;
				do {
					rgb = img.getRGB(x1, cy++);
					if (rgb == -65536) {
						path.building = true;
					} else if (rgb == -3414877) {
						path.grass = true;
					}
				} while (cy <= y1);
				return path;
			}
		}
		if (ddy == 0) { // horizontal line special case
			if (ddx > 0) {
				cx = x1;
				int rgb = -1;
				do {
					rgb = img.getRGB(cx, y1);
					if (rgb == -65536) {
						path.building = true;
					} else if (rgb == -3414877) {
						path.grass = true;
					}
				} while (++cx <= x2);
				return path;
			} else {
				cx = x2;
				int rgb = -1;
				do {
					rgb = img.getRGB(cx, y1);
					if (rgb == -65536) {
						path.building = true;
					} else if (rgb == -3414877) {
						path.grass = true;
					}
				} while (++cx <= x1);
				return path;
			}
		}
		if (ddy < 0) {
			iy = -1;
			ddy = -ddy;
		} // pointing up
		else
			iy = 1;
		if (ddx < 0) {
			ix = -1;
			ddx = -ddx;
		} // pointing left
		else
			ix = 1;
		dx = dy = ddx * ddy;
		cy = y1;
		cx = x1;
		if (ddx < ddy) { // < 45 degrees, a tall line
			do {
				dx -= ddy;
				int rgb = -1;
				do {
					rgb = img.getRGB(cx, cy);
					if (rgb == -65536) {
						path.building = true;
					} else if (rgb == -3479901) {
						path.grass = true;
					}
					cy += iy;
					dy -= ddx;
				} while (dy >= dx);
				cx += ix;
			} while (dx > 0);
		} else { // >= 45 degrees, a wide line
			do {
				dy -= ddx;
				int rgb = -1;
				do {
					rgb = img.getRGB(cx, cy);
					if (rgb == -65536) {
						path.building = true;
					} else if (rgb == -3414877) {
						path.grass = true;
					}
					cx += ix;
					dx -= ddy;
				} while (dx >= dy);
				cy += iy;
			} while (dy > 0);
		}

		return path;

	}

}

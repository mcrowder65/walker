package generic;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import generic.objects.Building;
import googlemaps.LatLng;
import server.APITools;
import server.processing.ColorOperations;

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

	public static PathConstituents analyzeImage(BufferedImage img, Node startNode, Node endNode, LatLng southwest,
			LatLng northeast) {
		Point2D.Double startPnt = APITools.getImagePointFromLatLng(startNode.getPosition(), southwest, northeast,
				img.getWidth(), img.getHeight());
		Point2D.Double endPnt = APITools.getImagePointFromLatLng(endNode.getPosition(), southwest, northeast,
				img.getWidth(), img.getHeight());
		if (startPnt == null || endPnt == null) {
			return null;
		}
		int x1 = (int) startPnt.x;
		int y1 = (int) startPnt.y;
		int x2 = (int) endPnt.x;
		int y2 = (int) endPnt.y;

		// if (startNode.isStart() == true) {
		// int rgbEnd = img.getRGB(x1, y1);
		// System.out.println(rgbEnd);
		// System.out.println(x1);
		// System.out.println(y1);
		// }

		int cx, cy, ix, iy, dx, dy, ddx = x2 - x1, ddy = y2 - y1;
		PathConstituents path = new PathConstituents(false, false, false, false, 0);

		if (ddx == 0) {
			if (ddy > 0) {
				cy = y1;
				int rgb = -1;
				do {

					rgb = img.getRGB(x1, cy++);
					if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.building = true;
					} else if (Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.grass = true;
					}
				} while (cy <= y2);
				return path;
			} else {
				cy = y2;
				int rgb = -1;
				do {
					rgb = img.getRGB(x1, cy++);
					if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.building = true;
					} else if (Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
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
					if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.building = true;
					} else if (Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.grass = true;
					}
				} while (++cx <= x2);
				return path;
			} else {
				cx = x2;
				int rgb = -1;
				do {
					rgb = img.getRGB(cx, y1);
					if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.building = true;
					} else if (Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
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
					if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.building = true;
					} else if (Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
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
					if (Tools.colorIsCloseEnough(rgb, Config.MAPS_BUILDING_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
						path.building = true;
					} else if (Tools.colorIsCloseEnough(rgb, Config.MAPS_GRASS_RGB, Config.FILLCOLOR_RGB_TOLERANCE)) {
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

	
	
	public static BufferedImage fillBuildings(BufferedImage img, List<Building> buildings, LatLng southwest, LatLng northeast)
	{
		LatLng[] seedPoints = new LatLng[buildings.size()];
		for (int n = 0; n < seedPoints.length; n++)
		{
			seedPoints[n] = new LatLng(buildings.get(n).getLatitude(), buildings.get(n).getLongitude());
		}
		
		
		BufferedImage newImg = ColorOperations.filledImage(img, new Color( Config.MAPS_BUILDING_RGB), southwest, northeast, seedPoints);
		
		
		return newImg;
	}
}

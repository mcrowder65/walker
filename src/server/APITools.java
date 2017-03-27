package server;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import generic.Config;
import generic.Node;
import generic.Tools;
import googlemaps.LatLng;
import server.processing.GenericProcessingOperations;

public class APITools {

	public static String GetDirectionsResponse(String origin, String destination) {
		try {
			return Tools.getHTTPString("https://maps.googleapis.com/maps/api/directions/json?mode=walking&origin="
					+ URLEncoder.encode(origin, "UTF-8") + "&destination=" + URLEncoder.encode(destination, "UTF-8")
					+ "&key=" + generic.Config.DIRECTIONS_KEY);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static double[] GetAllElevations(List<Node> points) {
		final int PARTITION_SIZE = 100;
		List<Node> partition;
		int currPos = 0;
		double[] elevs = new double[points.size()];
		while (currPos < points.size()) {
			partition = points.subList(currPos, Math.min(currPos + PARTITION_SIZE, points.size()));

			String resp = GetElevationResponse(partition);
			double[] elevsTemp = GetElevations(resp, partition);
			for (int n = 0; n < elevsTemp.length; n++)
				elevs[n + currPos] = elevsTemp[n];

			currPos += PARTITION_SIZE;
		}
		return elevs;
	}

	public static String GetElevationResponse(LatLng... points) {
		return Tools.getHTTPString("https://maps.googleapis.com/maps/api/elevation/json?locations="
				+ Tools.latlngsToString('|', points) + "&key=" + Config.ELEVATION_KEY);
	}

	public static String GetElevationResponse(List<Node> points) {
		return Tools.getHTTPString("https://maps.googleapis.com/maps/api/elevation/json?locations="
				+ Tools.nodesToString('|', points) + "&key=" + Config.ELEVATION_KEY);
	}

	public static double[] GetElevations(String apiJSONResponse, List<Node> nodes) {
		LatLng[] lls = new LatLng[nodes.size()];
		for (int n = 0; n < nodes.size(); n++)
			lls[n] = nodes.get(n).getPosition();
		return GetElevations(apiJSONResponse, lls);
	}

	public static double[] GetElevations(String apiJSONResponse, LatLng... points) {
		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray results = rootObj.getJSONArray("results");
		double[] elevations = new double[results.length()];
		if (elevations.length != points.length) {
			System.err.println("ERROR: Request and Response did not have the same length!!");
		}
		for (int n = 0; n < results.length(); n++) {
			// The response might be in the same order as the request, but just
			// to be safe...
			JSONObject result = results.getJSONObject(n);
			JSONObject location = result.getJSONObject("location");
			LatLng resultLatLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
			double elev = result.getDouble("elevation");
			if (LatLng.closeEnoughLatLng(resultLatLng, points[n])) {
				elevations[n] = elev;
			} else {
				for (int m = 0; m < points.length; m++) {
					if (LatLng.closeEnoughLatLng(resultLatLng, points[m])) {
						elevations[m] = elev;
						break;
					}
				}

			}

		}

		for (int n = 0; n < elevations.length; n++) {
			if (elevations[n] == 0) {
				System.err.println("ERROR: Elevation index " + n + " was not initialized!!");

			}
		}
		return elevations;
	}

	public static String GetOverviewPolyline(String apiJSONResponse) {
		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray routes = rootObj.getJSONArray("routes");
		JSONObject primaryRoute = routes.getJSONObject(0);
		JSONObject overviewPolyline = primaryRoute.getJSONObject("overview_polyline");
		String encPolyline = overviewPolyline.getString("points");
		return encPolyline;
	}

	public static String[] GetPolylinePieces(String apiJSONResponse) {

		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray routes = rootObj.getJSONArray("routes");
		JSONObject primaryRoute = routes.getJSONObject(0);
		JSONArray legs = primaryRoute.getJSONArray("legs");
		JSONObject singleLeg = legs.getJSONObject(0);
		JSONArray steps = singleLeg.getJSONArray("steps");
		String[] pieces = new String[steps.length()];

		for (int n = 0; n < steps.length(); n++) {
			JSONObject polylineWrapper = steps.getJSONObject(n).getJSONObject("polyline");
			pieces[n] = polylineWrapper.getString("points");
		}
		return pieces;
	}

	public static String polylineToURLParam(String polyline, int weight, String color) {
		return "&path=weight:" + weight + "%7Ccolor:" + color + "%7Cenc:" + polyline;
	}

	private static String pointsToVisibleURLParam(Point2D.Double pointA, Point2D.Double pointB) {
		return "&visible=" + Tools.pointToString(pointA, false) + "%7C" + Tools.pointToString(pointB, false);
	}

	public static int getAppropriateZoom(LatLng start, LatLng end, int pixelWidth, int pixelHeight) {
		// TODO: Get polyline bounding box instead (because the path may be
		// clipped)
		double meterDistanceX = getLongitudeDifference(start, end);
		double meterDistanceY = getLatitudeDifference(start, end);

		LatLng center = Tools.getCenter(start, end);

		int bestIndex = -1;
		for (int n = 10; n <= 20; n++) {
			double mpp = getMetersPerPixel(center.latitude, n);
			double meterWidth = meterDistanceX * mpp;
			double meterHeight = meterDistanceY * mpp;
			if (meterDistanceX > meterWidth || meterDistanceY > meterHeight)
				break;
			else
				bestIndex = n;
		}
		return bestIndex + 2;

	}

	public static double measureMeterDistance(double lat1, double lon1, double lat2, double lon2) { // generally
																									// used
																									// geo
																									// measurement
																									// function
		double R = 6378.137; // Radius of earth in KM
		double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
		double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = R * c;
		return d * 1000; // meters
	}

	public static double getMetersPerPixel(double latitude, int zoom) {
		return 156543.03392 * Math.cos(latitude * Math.PI / 180) / Math.pow(2, zoom);
	}

	private static double toMetersLat(double latDiff) {
		return latDiff / 0.0000089;
	}

	private static double toMetersLon(double lonDiff, double latitude) {
		return (lonDiff * Math.cos(latitude * 0.018)) / 0.0000089;
	}

	public static double metersToLat(LatLng pivot, double metersLatDistance) {
		double coefY = metersLatDistance * 0.0000089;
		return pivot.latitude + coefY;
	}

	public static double metersToLon(LatLng pivot, double metersLonDistance) {
		double coefX = metersLonDistance * 0.0000089;
		return pivot.longitude + coefX / Math.cos(pivot.latitude * 0.018);
	}

	public static double getLatitudeDifference(LatLng a, LatLng b) {
		return Math.abs(toMetersLat(a.latitude - b.latitude));
	}

	public static double getLongitudeDifference(LatLng a, LatLng b) {

		return Math.abs(toMetersLon(a.longitude - b.longitude, a.latitude));
	}

	public static LatLng getNortheast(LatLng center, double metersPerPixel, int sizeX, int sizeY) {
		double metersX = (metersPerPixel * sizeX) / 2;
		double metersY = (metersPerPixel * sizeY) / 2;
		double coefX = metersX * 0.0000089;
		double coefY = metersY * 0.0000089;
		double new_lat = center.latitude + coefY;
		double new_lon = center.longitude + coefX / Math.cos(center.latitude * 0.018);
		return new LatLng(new_lat, new_lon);

	}

	public static LatLng getSouthwest(LatLng center, double metersPerPixel, int sizeX, int sizeY) {
		double metersX = (metersPerPixel * sizeX) / 2;
		double metersY = (metersPerPixel * sizeY) / 2;
		double coefX = metersX * 0.0000089;
		double coefY = metersY * 0.0000089;
		double new_lat = center.latitude - coefY;
		double new_lon = center.longitude - coefX / Math.cos(center.latitude * 0.018);
		return new LatLng(new_lat, new_lon);
	}

	public static Point2D.Double getImagePointFromLatLng(LatLng location, LatLng southwest, LatLng northeast, int sizeX,
			int sizeY) {
		return getImagePointFromLatLng(location, southwest, northeast, sizeX, sizeY, true);
	}

	public static Point2D.Double getImagePointFromLatLng(LatLng location, LatLng southwest, LatLng northeast, int sizeX,
			int sizeY, boolean quiet) {
		if (location.latitude > northeast.latitude || location.longitude > northeast.longitude
				|| location.latitude < southwest.latitude || location.longitude < southwest.longitude) {
			if (!quiet)
				System.err.println("Error: LatLng could not be mapped to point - it is not visible.");
			return null;
		}
		double lonProportion = (location.longitude - southwest.longitude) / (northeast.longitude - southwest.longitude);
		double latProportion = (location.latitude - southwest.latitude) / (northeast.latitude - southwest.latitude);
		return new Point2D.Double((sizeX - 1) * lonProportion, (sizeY - 1) - ((sizeY - 1) * latProportion));
	}

	public static Point getImagePointFromLatLngNorm(LatLng location, LatLng southwest, LatLng northeast, int sizeX,
			int sizeY) {
		return getImagePointFromLatLngNorm(location, southwest, northeast, sizeX, sizeY, false);
	}

	public static Point getImagePointFromLatLngNorm(LatLng location, LatLng southwest, LatLng northeast, int sizeX,
			int sizeY, boolean quiet) {
		if (location.latitude > northeast.latitude || location.longitude > northeast.longitude
				|| location.latitude < southwest.latitude || location.longitude < southwest.longitude) {
			if (!quiet)
				System.err.println("Error: LatLng could not be mapped to point - it is not visible.");
			return null;
		}
		double lonProportion = (location.longitude - southwest.longitude) / (northeast.longitude - southwest.longitude);
		double latProportion = (location.latitude - southwest.latitude) / (northeast.latitude - southwest.latitude);
		return new Point((int) ((sizeX - 1) * lonProportion), (int) ((sizeY - 1) - ((sizeY - 1) * latProportion)));
	}

	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY,
			boolean isSatellite) {
		return DownloadStaticMapImage(start, end, sizeX, sizeY, getAppropriateZoom(start, end, sizeX, sizeY),
				isSatellite, null);
	}

	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY,
			boolean isSatellite, String polyline) {
		return DownloadStaticMapImage(start, end, sizeX, sizeY, getAppropriateZoom(start, end, sizeX, sizeY),
				isSatellite, polyline);
	}

	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, int zoom,
			boolean isSatellite) {
		return DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, isSatellite, null);
	}

	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, int zoom,
			boolean isSatellite, String polyline) {
		return DownloadStaticMapImage(Tools.getCenter(start, end), sizeX, sizeY, zoom, isSatellite, polyline);
	}

	private static String getBuildingParam() {
		return "&style=feature:landscape.man_made|color:" + Tools.toRGBHex(Config.MAPS_BUILDING_RGB);
	}

	private static String getRoadParam() {
		return "&style=feature:road|color:" + Tools.toRGBHex(Config.MAPS_NORMALPATH_RGB);
	}

	public static BufferedImage DownloadStaticMapImage(LatLng center, int sizeX, int sizeY, int zoom,
			boolean isSatellite, String polyline) {
		try {
			URL url;

			if (polyline == null) {
				if (isSatellite)
					url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&zoom=" + zoom
							+ "&center=" + center.toUrlValue() + "&size=" + sizeX + "x" + sizeY + "&key="
							+ generic.Config.STATICMAP_KEY);
				else
					url = new URL(
							"https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&style=feature:all|element:labels|visibility:off"
									+ getRoadParam() + getBuildingParam() + "&zoom=" + zoom + "&center="
									+ center.toUrlValue() + "&size=" + sizeX + "x" + sizeY + "&key="
									+ generic.Config.STATICMAP_KEY);
			} else {
				if (isSatellite)
					url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&center="
							+ center.toUrlValue() + "&size=" + sizeX + "x" + sizeY
							+ polylineToURLParam(polyline, 3, "red") + "&key=" + generic.Config.STATICMAP_KEY);
				else
					url = new URL(
							"https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&style=feature:all|element:labels|visibility:off"
									+ getRoadParam() + "&center=" + center.toUrlValue() + "&size=" + sizeX + "x" + sizeY
									+ polylineToURLParam(polyline, 3, "red") + "&key=" + generic.Config.STATICMAP_KEY);
			}

			BufferedImage image = ImageIO.read(url);

			BufferedImage rgbImage = Tools.convertICCToRGB(image);
			return rgbImage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage GetTiledImage(LatLng southwest, LatLng northeast, int zoom, boolean isSatellite) {
		assert southwest.latitude < northeast.latitude;
		assert southwest.longitude < northeast.longitude;

		LatLng deadCenter = Tools.getCenter(southwest, northeast);
		double totalSpanningMetersLat = APITools.getLatitudeDifference(southwest, northeast);
		double totalSpanningMetersLon = APITools.getLongitudeDifference(southwest, northeast);

		double metersPerPixel = getMetersPerPixel(deadCenter.latitude, zoom);
		double metersSliceLon = metersPerPixel * Config.GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS;
		double metersSliceLat = metersSliceLon - (metersPerPixel * Config.GOOGLE_LOGO_HEIGHT);

		int imageWidth = (int) ((1 / metersPerPixel) * totalSpanningMetersLon);
		int imageHeight = (int) ((1 / metersPerPixel) * totalSpanningMetersLat);
		BufferedImage totalImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_INDEXED);
		totalImage = Tools.convertICCToRGB(totalImage);

		// These constructor arguments don't really matter
		LatLng currSouthwest = new LatLng(southwest);
		LatLng currNortheast = new LatLng(northeast);

		int counterTEMP = 0;
		int iterX = 0;
		int iterY = 0;
		int spacingX = -1;
		int spacingY = -1;

		LatLng cornerPivotNE = new LatLng(APITools.metersToLat(southwest, metersSliceLat),
				APITools.metersToLon(southwest, metersSliceLon));
		LatLng cornerPivotCenter = Tools.getCenter(southwest, cornerPivotNE);
		BufferedImage cornerPivotImage = APITools.DownloadStaticMapImage(cornerPivotCenter,
				Config.GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS, Config.GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS, zoom, isSatellite,
				null);
		cornerPivotImage = Tools.ClipLogo(cornerPivotImage);
<<<<<<< HEAD
		Tools.WriteImage(cornerPivotImage, "testImages/SLICETEST_" + (0) + ".png");
=======
		// Tools.WriteImage(cornerPivotImage, "testImages/SLICETEST_" + (0) +
		// ".png");
>>>>>>> 1639d1c65382c31c4aa818bc2a43f9284e6ed560
		Point throwawayPoint = new Point();
		HashMap<Point, BufferedImage> lonByLatImages = new HashMap<Point, BufferedImage>();
		lonByLatImages.put(new Point(0, 0), cornerPivotImage);
		Tools.DrawOnImage(totalImage, cornerPivotImage, 0, totalImage.getHeight() - cornerPivotImage.getHeight());

		int xDeltaMemory = 0;
		int yDeltaMemory = 0;
<<<<<<< HEAD
		
		
		int prevStartY = totalImage.getHeight() - 1;
		int prevEndX = 0;
		
		for (double spannedMetersLat = 0;; spannedMetersLat += metersSliceLat, iterY++)
		{
			if (prevStartY <= 0) break;
			
			currSouthwest.latitude = APITools.metersToLat(southwest, spannedMetersLat);
			currNortheast.latitude = APITools.metersToLat(currSouthwest, metersSliceLat);
			iterX = 0;
			prevEndX = 0;
			
			
			
			for (double spannedMetersLon = 0;; spannedMetersLon += metersSliceLon, iterX++) {
				
				if (iterX == 0 && iterY == 0) continue;
				
				
=======

		for (double spannedMetersLat = 0; spannedMetersLat < totalSpanningMetersLat; spannedMetersLat += metersSliceLat, iterY++) {
			currSouthwest.latitude = APITools.metersToLat(southwest, spannedMetersLat);
			currNortheast.latitude = APITools.metersToLat(currSouthwest, metersSliceLat);
			iterX = 0;

			for (double spannedMetersLon = 0; spannedMetersLon < totalSpanningMetersLon; spannedMetersLon += metersSliceLon, iterX++) {

				if (iterX == 0 && iterY == 0)
					continue;

>>>>>>> 1639d1c65382c31c4aa818bc2a43f9284e6ed560
				currSouthwest.longitude = APITools.metersToLon(southwest, spannedMetersLon);
				currNortheast.longitude = APITools.metersToLon(currSouthwest, metersSliceLon);
				LatLng currCenter = Tools.getCenter(currSouthwest, currNortheast);

				currCenter.longitude -= (0.001 * iterX);
				currCenter.latitude -= (0.001 * iterY);

				BufferedImage sliceImg = APITools.DownloadStaticMapImage(currCenter,
						Config.GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS, Config.GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS, zoom,
						isSatellite, null);
				sliceImg = Tools.ClipLogo(sliceImg);
				throwawayPoint.x = iterX;
				throwawayPoint.y = iterY;
				lonByLatImages.put(new Point(iterX, iterY), sliceImg);
				// Tools.WriteImage(sliceImg, "testImages/SLICETEST_" +
				// (++counterTEMP) + ".png");
				System.out.println("slice w: " + sliceImg.getWidth() + ", slice h: " + sliceImg.getHeight());

				Point delta;
				if (iterX == 0) {
					throwawayPoint.x = 0;
					throwawayPoint.y = iterY - 1;
					delta = GenericProcessingOperations.getNorthStitchDelta(lonByLatImages.get(throwawayPoint),
							sliceImg);
					yDeltaMemory = delta.y;
				} else if (iterY == 0) {
					throwawayPoint.x = iterX - 1;
					throwawayPoint.y = 0;
					delta = GenericProcessingOperations.getEastStitchDelta(lonByLatImages.get(throwawayPoint),
							sliceImg);
					xDeltaMemory = delta.x;
				} else {
					delta = new Point(xDeltaMemory, yDeltaMemory);

				}

				
				
				int startDrawX = (Config.GOOGLE_MAX_IMAGE_DIMENSIONS_PIXELS * iterX) - (xDeltaMemory * iterX);
				int posHeight = sliceImg.getHeight() - yDeltaMemory;
				int startDrawY = totalImage.getHeight() - (posHeight * iterY) - (sliceImg.getHeight())   + (iterY * 4);
 				Tools.DrawOnImage(totalImage, sliceImg, startDrawX, startDrawY);
				//Tools.WriteImage(totalImage, "testImages/TOTALTEST_" + (counterTEMP) + ".png");
				
				
				
				
				prevStartY = startDrawY;
				prevEndX = (startDrawX + sliceImg.getWidth());
				
				if (prevEndX >= totalImage.getWidth()) break;

				/*
				 * Point startPoint; Point endPoint;
				 * 
				 * if (spacingX == -1) { startPoint =
				 * APITools.getImagePointFromLatLngNorm(currSouthwest,
				 * southwest, northeast, imageWidth, imageHeight); LatLng
				 * boundedNortheast = new LatLng(Math.min(northeast.latitude,
				 * currNortheast.latitude), Math.min(northeast.longitude,
				 * currNortheast.longitude)); endPoint =
				 * APITools.getImagePointFromLatLngNorm(boundedNortheast,
				 * southwest, northeast, imageWidth, imageHeight); spacingX =
				 * (int) (endPoint.x - startPoint.x); spacingY = (int)
				 * (startPoint.y - endPoint.y); } else { startPoint = new
				 * Point(spacingX * iterX, (imageHeight - 1) - spacingY *
				 * iterY); endPoint = new Point(Math.min(spacingX * (iterX + 1),
				 * imageWidth - 1), Math.max((imageHeight - 1) - spacingY *
				 * (iterY + 1), 0)); }
				 */

				// if ((int)(endPoint.x - startPoint.x) == spacingX &&
				// (int)(startPoint.y - endPoint.y) == spacingY)
				// Tools.DrawOnImage(totalImage, sliceImg, (int)startPoint.x,
				// (int)endPoint.y, true);
				// else
				// {
				// int trueHeight = (int)(startPoint.y - endPoint.y);
				// int trueWidth = (int)(endPoint.x - startPoint.x);

				// BufferedImage trueCrop = sliceImg.getSubimage(0,
				// sliceImg.getHeight() - trueHeight, trueWidth, trueHeight);
				// Tools.DrawOnImage(totalImage, trueCrop, (int)startPoint.x,
				// (int)endPoint.y, true);
				// }
				// Tools.WriteImage(totalImage, "testImages/TOTALTEST_" +
				// (counterTEMP) + ".png");

			}
		}
		return totalImage;

	}
}

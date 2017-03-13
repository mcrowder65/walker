package server;

import org.json.JSONArray;
import org.json.JSONObject;

import generic.Config;
import generic.Node;
import generic.Tools;
import googlemaps.LatLng;

import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;



public class APITools {

	
	
	
	public static String GetDirectionsResponse(String origin, String destination)
	{
		try {
			return Tools.getHTTPString("https://maps.googleapis.com/maps/api/directions/json?mode=walking&origin=" +
			        URLEncoder.encode(origin, "UTF-8") + "&destination=" + URLEncoder.encode(destination, "UTF-8") + "&key=" + generic.Config.DIRECTIONS_KEY);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static double[] GetAllElevations(List<Node> points)
	{
		final int PARTITION_SIZE = 100;
		List<Node> partition;
		int currPos = 0;
		double[] elevs = new double[points.size()];
		while (currPos < points.size())
		{
			partition = points.subList(currPos, Math.min(currPos + PARTITION_SIZE, points.size()));
			
			String resp = GetElevationResponse(partition);
			double[] elevsTemp = GetElevations(resp, partition);
			for (int n = 0; n < elevsTemp.length; n++)
				elevs[n + currPos] = elevsTemp[n];
			
			currPos += PARTITION_SIZE;
		}
		return elevs;
	}
	
	public static String GetElevationResponse(LatLng... points)
	{
		return Tools.getHTTPString("https://maps.googleapis.com/maps/api/elevation/json?locations="+Tools.latlngsToString('|', points)+"&key=" + Config.ELEVATION_KEY);
	}
	public static String GetElevationResponse(List<Node> points)
	{
		return Tools.getHTTPString("https://maps.googleapis.com/maps/api/elevation/json?locations="+Tools.nodesToString('|', points)+"&key=" + Config.ELEVATION_KEY);
	}
	
	public static double[] GetElevations(String apiJSONResponse, List<Node> nodes)
	{
		LatLng[] lls = new LatLng[nodes.size()];
		for (int n = 0; n < nodes.size(); n++)
			lls[n] = nodes.get(n).getPosition();
		return GetElevations(apiJSONResponse, lls);
	}
	public static double[] GetElevations(String apiJSONResponse, LatLng... points)
	{
		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray results = rootObj.getJSONArray("results");
		double[] elevations = new double[results.length()];
		if (elevations.length != points.length) {
			System.err.println("ERROR: Request and Response did not have the same length!!");
		}
		for (int n = 0; n < results.length(); n++)
		{
			//The response might be in the same order as the request, but just to be safe...
			JSONObject result = results.getJSONObject(n);
			JSONObject location = result.getJSONObject("location");
			LatLng resultLatLng=  new LatLng(location.getDouble("lat"), location.getDouble("lng"));
			double elev = result.getDouble("elevation");
			if (LatLng.closeEnoughLatLng(resultLatLng, points[n]))
			{
				elevations[n] = elev;
			}
			else
			{
				for (int m = 0; m < points.length; m++)
				{
					if (LatLng.closeEnoughLatLng(resultLatLng, points[m])){
						elevations[m] = elev;
						break;
					}
				}
			
			}
			
			
		}
		
		
		for (int n = 0; n < elevations.length; n++)
		{
			if (elevations[n] == 0) {
				System.err.println("ERROR: Elevation index " + n + " was not initialized!!");

			}
		}
		return elevations;
	}
	
	public static String GetOverviewPolyline(String apiJSONResponse)
	{
		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray routes = rootObj.getJSONArray("routes");
		JSONObject primaryRoute = routes.getJSONObject(0);
		JSONObject overviewPolyline = primaryRoute.getJSONObject("overview_polyline");
		String encPolyline = overviewPolyline.getString("points");
		return encPolyline;
	}
	public static String[] GetPolylinePieces(String apiJSONResponse)
	{
		
		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray routes = rootObj.getJSONArray("routes");
		JSONObject primaryRoute = routes.getJSONObject(0);
		JSONArray legs = primaryRoute.getJSONArray("legs");
		JSONObject singleLeg = legs.getJSONObject(0);
		JSONArray steps = singleLeg.getJSONArray("steps");
		String[] pieces = new String[steps.length()];
		
		for (int n = 0; n < steps.length(); n++)
		{
			JSONObject polylineWrapper = steps.getJSONObject(n).getJSONObject("polyline");
			pieces[n] = polylineWrapper.getString("points");
		}
		return pieces;
	}
	
	public static String polylineToURLParam(String polyline, int weight, String color)
	{
		return "&path=weight:"+ weight +"%7Ccolor:"+color+"%7Cenc:" + polyline;
	}
	
	private static String pointsToVisibleURLParam(Point2D.Double pointA, Point2D.Double pointB)
	{
		return "&visible=" + Tools.pointToString(pointA, false) + "%7C" + Tools.pointToString(pointB, false);
	}
	

	public static int getAppropriateZoom(LatLng start, LatLng end, int pixelWidth, int pixelHeight)
	{
		//TODO: Get polyline bounding box instead (because the path may be clipped)
		double meterDistanceX = getLongitudeDifference(start, end);
		double meterDistanceY = getLatitudeDifference(start, end);

		LatLng center = Tools.getCenter(start, end);
		
		int bestIndex = -1;
		for (int n = 10; n<= 20; n++)
		{
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
	
	public static double measureMeterDistance(double lat1, double lon1, double lat2, double lon2){  // generally used geo measurement function
	    double R = 6378.137; // Radius of earth in KM
	    double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
	    double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
	    Math.sin(dLon/2) * Math.sin(dLon/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double d = R * c;
	    return d * 1000; // meters
	}
	
	public static double getMetersPerPixel(double latitude, int zoom)
	{
		return 156543.03392 * Math.cos(latitude * Math.PI / 180) / Math.pow(2, zoom);
	}
	private static double toMetersLat(double latDiff)
	{
		return latDiff / 0.0000089;
	}
	private static double toMetersLon(double lonDiff, double latitude)
	{
		return (lonDiff * Math.cos(latitude * 0.018) )/ 0.0000089;
	}
	public static double metersToLat(LatLng pivot, double metersLatDistance)
	{
		double coefY = metersLatDistance * 0.0000089;
		return pivot.latitude + coefY;
	}
	public static double metersToLon(LatLng pivot, double metersLonDistance)
	{
		double coefX = metersLonDistance * 0.0000089;
		return pivot.longitude + coefX / Math.cos(pivot.latitude * 0.018);
	}
	
	
	public static double getLatitudeDifference(LatLng a, LatLng b)
	{
		return Math.abs(toMetersLat(a.latitude - b.latitude));
	}
	public static double getLongitudeDifference(LatLng a, LatLng b)
	{
		
		return Math.abs(toMetersLon(a.longitude - b.longitude, a.latitude));
	}
	public static LatLng getNortheast(LatLng center, double metersPerPixel, int sizeX, int sizeY)
	{
		double metersX = (metersPerPixel * sizeX) / 2;
		double metersY = (metersPerPixel * sizeY) / 2;
		double coefX = metersX * 0.0000089;
		double coefY = metersY * 0.0000089;
		double new_lat = center.latitude + coefY;
		double new_lon = center.longitude + coefX / Math.cos(center.latitude * 0.018);
		return new LatLng(new_lat, new_lon);
		
	
	}
	public static LatLng getSouthwest(LatLng center, double metersPerPixel, int sizeX, int sizeY)
	{
		double metersX = (metersPerPixel * sizeX) / 2;
		double metersY = (metersPerPixel * sizeY) / 2;
		double coefX = metersX * 0.0000089;
		double coefY = metersY * 0.0000089;
		double new_lat = center.latitude - coefY;
		double new_lon = center.longitude - coefX / Math.cos(center.latitude * 0.018);
		return new LatLng(new_lat, new_lon);
	}
	public static Point2D.Double getImagePointFromLatLng(LatLng location, LatLng southwest, LatLng northeast, int sizeX, int sizeY)
	{
		if (location.latitude > northeast.latitude || location.longitude > northeast.longitude || location.latitude < southwest.latitude || location.longitude < southwest.longitude)
		{
			System.err.println("Error: LatLng could not be mapped to point - it is not visible.");
			return null;
		}
		double lonProportion = (location.longitude - southwest.longitude) /(northeast.longitude - southwest.longitude);
		double latProportion = (location.latitude - southwest.latitude) / (northeast.latitude - southwest.latitude);
		return new Point2D.Double(sizeX * lonProportion, sizeY - ( sizeY * latProportion));
	}
	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, boolean isSatellite)
	{
		return DownloadStaticMapImage(start, end, sizeX, sizeY, getAppropriateZoom(start,end,sizeX,sizeY),isSatellite,  null);
	}
	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, boolean isSatellite, String polyline)
	{
		return DownloadStaticMapImage(start, end, sizeX, sizeY, getAppropriateZoom(start,end,sizeX,sizeY), isSatellite, polyline);
	}
	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, int zoom, boolean isSatellite)
	{
		return DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, isSatellite, null);
	}
	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, int zoom, boolean isSatellite, String polyline)
	{
		try {
			URL url;
			LatLng center = Tools.getCenter(start, end);
			
			if (polyline == null) {
				if (isSatellite)
					url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&zoom="+zoom+"&center=" +
							center.toUrlValue() + "&size="+ sizeX + "x" + sizeY +"&key=" + generic.Config.STATICMAP_KEY
							);
				else
					url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&style=feature:all|element:labels|visibility:off&zoom="+zoom+"&center=" +
							center.toUrlValue() + "&size="+ sizeX + "x" + sizeY + "&key=" + generic.Config.STATICMAP_KEY
							);
			}
			else {
				if (isSatellite)
					url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&center=" +
						center.toUrlValue()+ "&size="+ sizeX + "x" + sizeY + polylineToURLParam(polyline, 3, "red") +"&key=" + generic.Config.STATICMAP_KEY
					);
				else
					url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=roadmap&style=feature:all|element:labels|visibility:off&center=" +
						center.toUrlValue()+ "&size="+ sizeX + "x" + sizeY + polylineToURLParam(polyline, 3, "red") +"&key=" + generic.Config.STATICMAP_KEY
					);
			}
		
			BufferedImage image = ImageIO.read(url);
			
			BufferedImage rgbImage = Tools.convertICCToRGB(image);
			return rgbImage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

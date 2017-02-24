package server;

import org.json.JSONArray;
import org.json.JSONObject;

import generic.Tools;
import googlemaps.LatLng;

import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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
		double meterDistanceX = measureMeterDistance(0, start.longitude, 0, end.longitude);
		double meterDistanceY = measureMeterDistance(start.latitude, 0, end.latitude, 0);

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
		return bestIndex;
		
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
	
	public static LatLng getNortheast(LatLng center, double metersPerPixel, int sizeX, int sizeY)
	{
		return new LatLng(center.latitude + (metersPerPixel * sizeY) / 2, center.longitude + (metersPerPixel * sizeX) / 2  );
	}
	public static LatLng getSouthwest(LatLng center, double metersPerPixel, int sizeX, int sizeY)
	{
		return new LatLng(center.latitude - (metersPerPixel * sizeY) / 2, center.longitude - (metersPerPixel * sizeX) / 2  );
	}
	public static Point2D.Double getImagePointFromLatLng(LatLng location, LatLng southwest, LatLng northeast, int sizeX, int sizeY)
	{
		if (location.latitude > northeast.latitude || location.longitude > northeast.longitude || location.latitude < southwest.latitude || location.longitude < southwest.longitude)
		{
			System.err.println("Error: LatLng could not be mapped to point - it is not visible.");
			return null;
		}
		double lonProportion = (northeast.longitude - southwest.longitude) / (location.longitude - southwest.longitude);
		double latProportion = (northeast.latitude - southwest.latitude) / (location.latitude - southwest.latitude);
		return new Point2D.Double(sizeX * lonProportion, sizeY * latProportion);
	}

	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, String polyline)
	{
		return DownloadStaticMapImage(start, end, sizeX, sizeY, getAppropriateZoom(start,end,sizeX,sizeY), polyline);
	}
	public static BufferedImage DownloadStaticMapImage(LatLng start, LatLng end, int sizeX, int sizeY, int zoom, String polyline)
	{
		try {
			URL url;
			LatLng center = Tools.getCenter(start, end);
			
			if (polyline == null)
				url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&zoom="+zoom+"&center=" +
					Tools.latlngToString(center, false) + "&size="+ sizeX + "x" + sizeY +"&key=" + generic.Config.STATICMAP_KEY
					);
			else
				url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&center=" +
						Tools.latlngToString(center, false)+ "&size="+ sizeX + "x" + sizeY + polylineToURLParam(polyline, 3, "red") +"&key=" + generic.Config.STATICMAP_KEY
						);
		
			BufferedImage image = ImageIO.read(url);
			
			return image;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

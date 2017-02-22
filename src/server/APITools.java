package server;

import org.json.JSONArray;
import org.json.JSONObject;

import generic.Tools;

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
			return Tools.getHTTPString("https://maps.googleapis.com/maps/api/directions/json?origin=" +
			        URLEncoder.encode(origin, "UTF-8") + "&destination=" + URLEncoder.encode(destination) + "&key=" + generic.Config.DIRECTIONS_KEY);
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
	
	public static String polylineToURLParam(String polyline, int weight, String color)
	{
		return "&path=weight:"+ weight +"%7Ccolor:"+color+"%7Cenc:" + polyline;
	}
	
	private static String pointsToVisibleURLParam(Point2D.Double pointA, Point2D.Double pointB)
	{
		return "&visible=" + Tools.pointToString(pointA, false) + "%7C" + Tools.pointToString(pointB, false);
	}
	
	public static BufferedImage DownloadStaticMapImage(Point2D.Double start, Point2D.Double end, int sizeX, int sizeY)
	{
		return DownloadStaticMapImage(start, end, sizeX, sizeY, null);
	}
	
	public static BufferedImage DownloadStaticMapImage(Point2D.Double start, Point2D.Double end, int sizeX, int sizeY, String polyline)
	{
		try {
			URL url;
			Point2D.Double center = Tools.getCenter(start, end);
			
			if (polyline == null)
				url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&center=" +
					center.x + "," + center.y + pointsToVisibleURLParam(start,end) + "&size="+ sizeX + "x" + sizeY +"&key=" + generic.Config.STATICMAP_KEY
					);
			else
				url = new URL("https://maps.googleapis.com/maps/api/staticmap?maptype=satellite&center=" +
						center.x + "," + center.y +pointsToVisibleURLParam(start,end) + "&size="+ sizeX + "x" + sizeY + polylineToURLParam(polyline, 3, "orange") +"&key=" + generic.Config.STATICMAP_KEY
						);
		
			BufferedImage image = ImageIO.read(url);
			
			return image;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}

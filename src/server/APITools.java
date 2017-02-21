package server;

import org.json.JSONArray;
import org.json.JSONObject;
public class APITools {

	public static String GetOverviewPolyline(String apiJSONResponse)
	{
		JSONObject rootObj = new JSONObject(apiJSONResponse);
		JSONArray routes = rootObj.getJSONArray("routes");
		JSONObject primaryRoute = routes.getJSONObject(0);
		JSONObject overviewPolyline = primaryRoute.getJSONObject("overview_polyline");
		String encPolyline = overviewPolyline.getString("points");
		return encPolyline;
	}
}

package server.handlers.building;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Building;
import generic.objects.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class GetBuildingHandler extends WalkerHandler {
	Building buildingClass;
	Object lock;

	public GetBuildingHandler() {
		buildingClass = new Building();
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			String result = getRequestBodyAndSetHeaders(exchange);
			Marker marker = JSONTools.g.fromJson(result, Marker.class);
			String path = "buildings/" + marker.getId();
			Building building = (Building) Tools.firebase.get(path, buildingClass);
			String json = JSONTools.g.toJson(building);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();

		}

	}

}

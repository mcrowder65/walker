package server.handlers.marker;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Building;
import generic.objects.Entrance;
import generic.objects.Stairs;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class GetMarkersHandler extends WalkerHandler {
	Building building;
	Object lock;
	Entrance entrance;

	public GetMarkersHandler() {
		building = new Building();
		entrance = new Entrance();
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {

			String data = getRequestBodyAndSetHeaders(exchange);
			List<String> buildings = Tools.firebase.getAllAsJson("buildings", building);
			List<String> entrances = Tools.firebase.getAllAsJson("entrances", entrance);
			List<String> stairs = Tools.firebase.getAllAsJson("stairs", new Stairs());
			buildings.addAll(entrances);
			buildings.addAll(stairs);
			String json = JSONTools.g.toJson(buildings);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();

		}

	}

}
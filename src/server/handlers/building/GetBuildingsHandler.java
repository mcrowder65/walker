package server.handlers.building;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Building;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class GetBuildingsHandler extends WalkerHandler {
	Building building;
	Object lock;

	public GetBuildingsHandler() {
		building = new Building();
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			getRequestBodyAndSetHeaders(exchange);
			List<String> buildings = Tools.firebase.getAllAsJson("buildings", building);
			String json = JSONTools.g.toJson(buildings);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();
		}
	}

}

package server.handlers.travel;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import generic.objects.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;

public class TravelHandler extends WalkerHandler {
	Object lock;

	public TravelHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			String result = getRequestBodyAndSetHeaders(exchange);
			System.out.println("result: " + result);
			JsonObject jsonObject = JSONTools.g.fromJson(result, JsonObject.class);
			Marker startMarker = JSONTools.g.fromJson(jsonObject.get("startMarker"), Marker.class);
			Marker endMarker = JSONTools.g.fromJson(jsonObject.get("endMarker"), Marker.class);
			// TODO handle!!!!!!!!
		}

	}

}

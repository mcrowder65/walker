package server.handlers.marker;

import java.io.IOException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class GetMarkersHandler extends WalkerHandler {
	Marker marker;
	Object lock;

	public GetMarkersHandler() {
		marker = new Marker();
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			getRequestBodyAndSetHeaders(exchange);
			List<String> objects = Tools.firebase.getAllAsJson("markers", marker);

			String json = JSONTools.g.toJson(objects);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();

		}

	}

}
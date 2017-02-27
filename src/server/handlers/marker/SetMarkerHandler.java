package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class SetMarkerHandler extends WalkerHandler {
	Object lock;

	public SetMarkerHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			String result = getRequestBodyAndSetHeaders(exchange);

			if (!result.equals("")) {

				Marker marker = JSONTools.g.fromJson(result, Marker.class);
				System.out.println(marker);
				if (!marker.getId().equals("")) {
					String path = "markers/" + marker.getId();
					Tools.firebase.update(path, marker, exchange);
				} else {
					Tools.firebase.set("markers", marker, exchange);
				}

			} else {
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				String json = "working on some things...";
				exchange.getResponseBody().write(json.getBytes());
				exchange.getResponseBody().close();
			}
		}

	}

}

package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class DeleteMarkerHandler extends WalkerHandler {
	private Object lock;

	public DeleteMarkerHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			String json = this.getRequestBodyAndSetHeaders(exchange);
			if (!json.equals("")) {
				Marker marker = JSONTools.g.fromJson(json, Marker.class);
				Tools.firebase.delete("markers/" + marker.getId(), exchange);

			}
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().close();

		}

	}

}

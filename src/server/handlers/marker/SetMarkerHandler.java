package server.handlers.marker;

import generic.Tools;
import generic.objects.Marker;

import java.io.IOException;

import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

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
					Tools.firebase.update(path, marker);
				} else {
					Tools.firebase.create("markers", marker);

				}
				try {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					exchange.getResponseBody().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				String json = "This set marker request asked for empty things :(";
				exchange.getResponseBody().write(json.getBytes());
				exchange.getResponseBody().close();
			}
		}

	}

}

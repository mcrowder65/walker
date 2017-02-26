package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class SetMarkerHandler extends WalkerHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String result = getRequestBodyAndSetHeaders(exchange);

		if (!result.equals("")) {
			Marker marker = JSONTools.g.fromJson(result, Marker.class);
			// TODO set this in database now.
			System.out.println(marker);
			Tools.firebase.set("markers", marker, exchange);

		} else {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			String json = "working on some things...";
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();
		}

	}

}

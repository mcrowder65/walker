package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Marker;
import server.handlers.WalkerHandler;

public class GetMarkersHandler extends WalkerHandler {
	Marker marker;

	public GetMarkersHandler() {
		marker = new Marker();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		getRequestBodyAndSetHeaders(exchange);
		Tools.firebase.sendAllToClientAsJSON("markers", marker, exchange);
	}

}
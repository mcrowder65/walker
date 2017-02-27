package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Marker;
import server.handlers.WalkerHandler;

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
			Tools.firebase.sendAllToClientAsJSON("markers", marker, exchange);
		}

	}

}
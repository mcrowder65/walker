package server.handlers.travel;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import server.handlers.WalkerHandler;

public class TravelHandler extends WalkerHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String result = getRequestBodyAndSetHeaders(exchange);
		System.out.println("result");

	}

}

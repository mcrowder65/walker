package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class SetMarkerHandler extends WalkerHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String result = this.getRequestBodyAndSetHeaders(exchange);

		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		String json = result;
		exchange.getResponseBody().write(json.getBytes());
		exchange.getResponseBody().close();
	}

}

package server.handlers.marker;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sun.net.www.protocol.http.HttpURLConnection;

public class SetMarkerHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println(exchange.toString());
		StringWriter writer = new StringWriter();
		IOUtils.copy(exchange.getRequestBody(), writer, "UTF-8");
		String theString = writer.toString();
		System.out.println(theString);
		exchange.getResponseHeaders().add("Content-type", "application/json");
		exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
		exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET");
		exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
		exchange.getResponseHeaders().add("Access-Control-Allow-Headers",
				"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		String json = theString;
		exchange.getResponseBody().write(json.getBytes());
		exchange.getResponseBody().close();
	}

}

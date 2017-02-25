package server.handlers;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class WalkerHandler implements HttpHandler {

	public abstract void handle(HttpExchange arg0) throws IOException;

	public String getRequestBodyAndSetHeaders(HttpExchange exchange) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(exchange.getRequestBody(), writer, "UTF-8");
			exchange.getResponseHeaders().add("Content-type", "application/json");
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET");
			exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers",
					"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
		} catch (IOException e) {
			e.printStackTrace();
		}
		String response = writer.toString();
		return response;
	}
}

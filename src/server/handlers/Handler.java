package server.handlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sun.net.www.protocol.http.HttpURLConnection;

public class Handler implements HttpHandler {

	public Handler() {
	}

	/**
	 * If you go to http://localhost:8081/handler while your browser is running,
	 * it'll output handle!
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {

		exchange.getResponseHeaders().add("Content-type", "application/json");
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		exchange.getResponseBody().write("Your java server is running.".getBytes());
		exchange.getResponseBody().close();
		readFile("../../client/components/walker/walker.html");
	}

	private void readFile(String filename) {
		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(filename));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
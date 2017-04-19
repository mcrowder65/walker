package server.handlers.generic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import server.handlers.WalkerHandler;

public class NohupHandler extends WalkerHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		String FILENAME = "nohup.out";
		StringBuilder json = new StringBuilder();
		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				json.append(sCurrentLine + '\n');
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

		exchange.sendResponseHeaders(200, 0);
		exchange.getResponseBody().write(json.toString().getBytes());

		exchange.getResponseBody().close();
	}

}

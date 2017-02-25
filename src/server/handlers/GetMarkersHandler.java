package server.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import generic.Marker;
import sun.net.www.protocol.http.HttpURLConnection;

public class GetMarkersHandler implements HttpHandler {

	private Gson g;

	public GetMarkersHandler() {

		g = new Gson();
	}

	/**
	 * If you go to http://localhost:8081/handler while your browser is running,
	 * it'll output the buildings!
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("markers");
		ref.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {
					List<String> markers = new ArrayList<>();
					for (DataSnapshot child : dataSnapshot.getChildren()) {
						Marker marker = child.getValue(Marker.class);
						markers.add(marker.toJson());
					}
					exchange.getResponseHeaders().add("Content-type", "application/json");
					exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
					exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, GET");
					exchange.getResponseHeaders().add("Access-Control-Max-Age", "3600");
					exchange.getResponseHeaders().add("Access-Control-Allow-Headers",
							"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					String json = g.toJson(markers);
					exchange.getResponseBody().write(json.getBytes());
					exchange.getResponseBody().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

}
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

import server.Building;
import sun.net.www.protocol.http.HttpURLConnection;

public class Handler implements HttpHandler {

	private Gson g;

	public Handler() {

		g = new Gson();
	}

	/**
	 * If you go to http://localhost:8081/handler while your browser is running,
	 * it'll output the buildings!
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("buildings");
		ref.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {
					List<String> buildings = new ArrayList<>();
					for (DataSnapshot child : dataSnapshot.getChildren()) {
						Building building = child.getValue(Building.class);
						buildings.add(building.toJson());
					}
					exchange.getResponseHeaders().add("Content-type", "application/json");

					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					String json = g.toJson(buildings);
					exchange.getResponseBody().write(json.getBytes());
					exchange.getResponseBody().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

}
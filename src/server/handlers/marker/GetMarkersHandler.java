package server.handlers.marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sun.net.httpserver.HttpExchange;

import generic.Marker;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class GetMarkersHandler extends WalkerHandler {

	public GetMarkersHandler() {

	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("markers");
		getRequestBodyAndSetHeaders(exchange);
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

					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					String json = JSONTools.g.toJson(markers);
					exchange.getResponseBody().write(json.getBytes());
					exchange.getResponseBody().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

}
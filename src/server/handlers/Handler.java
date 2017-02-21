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
	private List<String> buildings;
	private Gson g;

	public Handler() {
		buildings = new ArrayList<>();
		g = new Gson();
	}

	/**
	 * If you go to http://localhost:8081/handler while your browser is running,
	 * it'll output handle!
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference("buildings");
		ref.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// TODO Auto-generated method stub
				System.out.println("onDataChange");
				System.out.println(dataSnapshot.getValue());
				String dataSnapshotStr = (String) dataSnapshot.getValue();
				for (DataSnapshot child : dataSnapshot.getChildren()) {
					System.out.println(child);
					Building building = dataSnapshot.getValue(Building.class);
				}

				System.out.println("hello");

			}
		});

		exchange.getResponseHeaders().add("Content-type", "application/json");
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		exchange.getResponseBody().write("your java server is running".getBytes());
		exchange.getResponseBody().close();

	}

}
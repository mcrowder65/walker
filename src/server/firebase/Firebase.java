package server.firebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sun.net.httpserver.HttpExchange;

import generic.objects.WalkerObject;
import server.JSONTools;
import sun.net.www.protocol.http.HttpURLConnection;

public class Firebase {

	public Firebase() {
	}

	/**
	 * This sends json back to the client this is a getter
	 * 
	 * @param path
	 *            String
	 * @param desiredClass
	 *            WalkerObject
	 * @param exchange
	 *            HttpExchange
	 */
	public void sendAllToClientAsJSON(String path, WalkerObject desiredClass, HttpExchange exchange) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);

		ref.addValueEventListener(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {
			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {
					List<String> objects = new ArrayList<>();
					for (DataSnapshot child : dataSnapshot.getChildren()) {
						WalkerObject obj = child.getValue(desiredClass.getClass());
						objects.add(obj.toJson());
					}
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					String json = JSONTools.g.toJson(objects);
					exchange.getResponseBody().write(json.getBytes());
					exchange.getResponseBody().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * setter
	 * 
	 * @param path
	 *            String
	 * @param obj
	 *            WalkerObject
	 */
	public void set(String path, WalkerObject obj, HttpExchange exchange) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		DatabaseReference pushedRef = ref.push();
		pushedRef.setValue(obj);
		String id = pushedRef.getKey();
		DatabaseReference idRef = database.getReference(path + "/" + id);
		obj.setId(id);
		idRef.setValue(obj);
		try {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			String json = "working on some things...";
			exchange.getResponseBody().write(json.getBytes());
			exchange.getResponseBody().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

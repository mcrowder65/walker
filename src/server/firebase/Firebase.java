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
	 * This sends this to the client.. I need to figure out a way to return this
	 * with the server...
	 * 
	 * @param path
	 * @param desiredClass
	 * @param exchange
	 * @return
	 */
	public void getAll(String path, WalkerObject desiredClass, HttpExchange exchange) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		List<String> objects = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				try {

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

	public void update(String path, WalkerObject obj, HttpExchange exchange) {

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(obj);

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

	}

	public void delete(String path, HttpExchange exchange) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(null);

	}
}

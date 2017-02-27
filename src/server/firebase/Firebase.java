package server.firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sun.net.httpserver.HttpExchange;

import generic.objects.WalkerObject;

public class Firebase {

	private Semaphore semaphore;

	public Firebase() {
		semaphore = new Semaphore(0);
	}

	public List<String> getAllAsJson(String path, WalkerObject desiredClass) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		List<String> objects = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {

			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				for (DataSnapshot child : dataSnapshot.getChildren()) {
					WalkerObject obj = child.getValue(desiredClass.getClass());
					objects.add(obj.toJson());
				}
				semaphore.release();
			}

		});
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return objects;

	}

	public List<WalkerObject> getAllAsObjects(String path, WalkerObject desiredClass) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		List<WalkerObject> objects = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {

			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				for (DataSnapshot child : dataSnapshot.getChildren()) {
					WalkerObject obj = child.getValue(desiredClass.getClass());
					objects.add(obj);
				}
				semaphore.release();
			}

		});

		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return objects;


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

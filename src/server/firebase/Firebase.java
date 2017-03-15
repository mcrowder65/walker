package server.firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import generic.Graph;
import generic.objects.WalkerObject;

public class Firebase {

	private Semaphore semaphore;

	public Firebase() {
		semaphore = new Semaphore(0);
	}

	public List<String> getAllAsJson(String path, final WalkerObject desiredClass) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		final List<String> objects = new ArrayList<>();
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

	public List<WalkerObject> getAllAsObjects(String path, final WalkerObject desiredClass) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		final List<WalkerObject> objects = new ArrayList<>();
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

	public WalkerObject get(String path, WalkerObject obj) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		List<WalkerObject> objects = new ArrayList<>();
		ref.addListenerForSingleValueEvent(new ValueEventListener() {

			@Override
			public void onCancelled(DatabaseError arg0) {

			}

			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				WalkerObject object = dataSnapshot.getValue(obj.getClass());
				objects.add(object);

				semaphore.release();
			}

		});

		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return objects.get(0);
	}

	public void update(String path, WalkerObject obj) {

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(obj);
	}

	public void update(String path, String str) {

		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(str);
	}

	/**
	 * setter
	 * 
	 * @param path
	 *            String
	 * @param obj
	 *            WalkerObject
	 */
	public void create(String path, WalkerObject obj) {
		System.out.println("create daddy");
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		DatabaseReference pushedRef = ref.push();
		pushedRef.setValue(obj);
		String id = pushedRef.getKey();
		System.out.println("id: " + id);
		DatabaseReference idRef = database.getReference(path + "/" + id);
		obj.setId(id);

		idRef.setValue(obj);
		System.out.println("idRef: " + idRef);
		WalkerObject graph = this.get("nodes/" + id, new Graph());
		System.out.println(graph);

	}

	public void delete(String path) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(null);

	}
}

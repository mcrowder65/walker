package server.firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import generic.objects.WalkerObject;
import server.dao.GraphFirebaseWrapper;

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
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		DatabaseReference pushedRef = ref.push();
		pushedRef.setValue(obj);
		String id = pushedRef.getKey();
		obj.setId(id);
		String setIdPath = path + "/" + id + "/id";
		this.set(setIdPath, id);
	}

	public void createGraph(String path, GraphFirebaseWrapper graph) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		DatabaseReference pushedRef = ref.push();
		pushedRef.setValue(graph);
		String id = pushedRef.getKey();

		String setIdPath = path + "/" + id + "/id";
		set(setIdPath, id);

		String setDistancesChangedPath = path + "/" + id + "/distancesChanged";
		// this.set(setDistancesChangedPath, g.distancesChanged());
		// String setElevationsChangedPath = path + "/" + id +
		// "/elevationsChanged";
		// this.set(setElevationsChangedPath, g.elevationsChanged());
		// System.out.println("set some distances and elevations!");
		// TODO do me
	}

	public void delete(String path) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(null);
	}

	public void set(String path, Object value) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(value);
	}

}

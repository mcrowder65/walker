package server.firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import generic.objects.Building;
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

	public String getBuildingIdByTitle(String buildingTitle) {
		List<WalkerObject> buildings = this.getAllAsObjects("buildings", new Building());
		for (int i = 0; i < buildings.size(); i++) {
			Building building = (Building) buildings.get(i);
			if (building.getTitle().equals(buildingTitle)) {
				return building.getId();
			}
		}
		return null;
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

	public void update(String path, WalkerObject obj) {

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
	public void create(String path, WalkerObject obj) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		DatabaseReference pushedRef = ref.push();
		pushedRef.setValue(obj);
		String id = pushedRef.getKey();
		DatabaseReference idRef = database.getReference(path + "/" + id);
		obj.setId(id);
		idRef.setValue(obj);

	}

	public void delete(String path) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference ref = database.getReference(path);
		ref.setValue(null);

	}
}

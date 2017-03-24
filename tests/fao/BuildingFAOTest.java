package fao;

import java.io.FileInputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import generic.objects.Building;
import server.fao.BuildingFAO;

public class BuildingFAOTest {

	@Before
	public void setUp() throws Exception {
		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream("key.json"))
				.setDatabaseUrl("https://walker-73119.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);
	}

	@Test
	public void test() {
		BuildingFAO buildingFAO = new BuildingFAO();
		List<Building> buildings = buildingFAO.getAll();
		for (int i = 0; i < buildings.size(); i++) {
			buildings.get(i).getResolvedEntrances();
			System.out.println(buildings.get(i));
		}
	}

}

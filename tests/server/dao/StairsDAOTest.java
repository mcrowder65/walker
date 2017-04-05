package server.dao;

import java.io.FileInputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import generic.objects.Stairs;

public class StairsDAOTest {

	@Before
	public void setUp() throws Exception {
		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream("key.json"))
				.setDatabaseUrl("https://walker-73119.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);
	}

	@Test
	public void test() {
		List<Stairs> stairs = StairsDAO.getAll();
		System.out.println("hello");
	}

}

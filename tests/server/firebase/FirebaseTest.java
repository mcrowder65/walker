package server.firebase;

import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import generic.Tools;

public class FirebaseTest {

	@Before
	public void setUp() throws Exception {

		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream("key.json"))
				.setDatabaseUrl("https://walker-73119.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);
	}

	@Test
	public void test() {
		Tools.firebase.delete("graphs");
	}

}

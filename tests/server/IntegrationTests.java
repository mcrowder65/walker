package server;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.ImageTools;
import generic.Node;
import generic.Tools;
import generic.objects.Building;
import googlemaps.LatLng;
import server.dao.BuildingDAO;
import server.handlers.building.GetBuildingsHandler;

public class IntegrationTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream("key.json"))
				.setDatabaseUrl("https://walker-73119.firebaseio.com/").build();
		FirebaseApp.initializeApp(options);
	}
	
	@Test
	public void createCampusGraphTest() {
		//Generates the tiled image of the campus, creates the graph, and stores it in firebase
		
		LatLng southwest = new LatLng(40.244803, -111.657854);
		LatLng northeast = new LatLng(40.2519803, -111.643854);
		int zoom = 18;
		BufferedImage img;
		if (Config.USE_MOCK)
			img = Tools.ReadImage("mock/campus.png");
		else
			img = APITools.GetTiledImage(southwest, northeast, zoom, false);

		
		List<Building> buildings = BuildingDAO.getAll();
		img = ImageTools.fillBuildings(img, buildings, southwest, northeast);
		
		
		List<Node> nodes = generic.GraphTools.GenerateUniformNodes(6, southwest, northeast, false);
		GraphTools.RemoveBuildingNodes(nodes, img, southwest, northeast);
		

		
	//	Tools.WriteImage(img, "testImages/fillCampusTest.png");
		
		
		Graph g = new Graph(null, null, nodes);
		g.setLimitedDistancesFromNodes(img, southwest, northeast);
		
		GraphTools.WriteGraphToImage(img, g, Color.BLUE, 1, southwest, northeast);
		img = Tools.ClipLogo(img);
		Tools.WriteImage(img, "testImages/fullCampus.png");
		
		
		
	
	}

}

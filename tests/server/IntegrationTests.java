package server;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.Test;

import generic.Config;
import generic.Graph;
import generic.GraphTools;
import generic.Node;
import generic.Tools;
import googlemaps.LatLng;

public class IntegrationTests {

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

		
		List<Node> nodes = generic.GraphTools.GenerateUniformNodes(6, southwest, northeast);
		GraphTools.RemoveBuildingNodes(nodes, img, southwest, northeast);
		
		Graph g = new Graph(null, null, nodes);
		g.generateLimitedMatrix(img, southwest, northeast);
		GraphTools.WriteGraphToImage(img, g, Color.BLUE, 1, southwest, northeast);
		img = Tools.ClipLogo(img);
		Tools.WriteImage(img, "testImages/fullCampus.png");
		
		
		
	
	}

}

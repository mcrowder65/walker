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
import server.processing.ColorOperations;

public class ProcessingTests {

	//@Test
	public void downloadRoadmapTest() {
		
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.648338);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);

		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		img = Tools.ClipLogo(img);

		Tools.WriteImage(img, "testImages/roadmapTest1.png");
	}
	
	//@Test
	public void grayscaleTest() {
		
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.648338);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);


		img = Tools.ClipLogo(img);
		img = ColorOperations.toGrayscale(img);

		Tools.WriteImage(img, "testImages/roadmapTest2.png");
	}
	@Test
	public void thresholdTest() {
		
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.648338);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);

		BufferedImage img = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);


		img = Tools.ClipLogo(img);
		img = ColorOperations.toGrayscale(img);
		img = ColorOperations.threshold(img, 255);

		Tools.WriteImage(img, "testImages/roadmapTest3.png");
	}

	
	//@Test
	public void maskRoadmapTest() {
		
		LatLng start = new LatLng(40.249403, -111.650154);
		LatLng end = new LatLng(40.249218, -111.648338);
		LatLng center = Tools.getCenter(start, end);
		int sizeX = 640;
		int sizeY = 640;
		int zoom = APITools.getAppropriateZoom(start, end, sizeX, sizeY);


		BufferedImage satell = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, true);
		BufferedImage roadmap = server.APITools.DownloadStaticMapImage(start, end, sizeX, sizeY, zoom, false);
		satell = Tools.ClipLogo(satell);
		roadmap = Tools.ClipLogo(roadmap);
		
		double metersPerPixel = APITools.getMetersPerPixel(center.latitude, zoom);
		LatLng southwest = APITools.getSouthwest(center, metersPerPixel, sizeX, sizeY);
		LatLng northeast = APITools.getNortheast(center, metersPerPixel, sizeX, sizeY);

		

		//Tools.WriteImage(img, "testImages/roadmapTest1.png");
	}
	
}

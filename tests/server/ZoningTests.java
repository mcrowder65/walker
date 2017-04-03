package server;

import static org.junit.Assert.*;

import org.junit.Test;

import generic.Config;
import generic.ZoningTools;
import googlemaps.LatLng;

public class ZoningTests {

	@SuppressWarnings("unused")
	@Test
	public void getHourTest()
	{
		LatLng coord = new LatLng(40.244803, -111.657854);
		int zone = ZoningTools.GetTimeZone(coord);
		assert zone == Config.LOCAL_TIMEZONE + Config.DAYLIGHT_SAVINGS;
		
		int hour = ZoningTools.GetHour(coord);
		
	}

}

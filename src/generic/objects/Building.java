package generic.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import generic.Tools;
import generic.ZoningTools;
import googlemaps.LatLng;

public class Building extends WalkerObject {
	private double latitude;
	private double longitude;
	private String title;
	private String openingTime;
	private String closingTime;
	private HashMap<String, String> entrances;
	transient private List<Entrance> resolvedEntrances;

	public Building(double latitude, double longitude, String title, String openingTime, String closingTime,
			HashMap<String, String> entrances) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.entrances = entrances;

	}

	public List<Entrance> getResolvedEntrances() {
		if (resolvedEntrances == null && entrances != null) {
			resolvedEntrances = new ArrayList<>();
			for (String key : entrances.keySet()) {
				Entrance entrance = (Entrance) Tools.firebase.get("entrances/" + key, new Entrance());
				resolvedEntrances.add(entrance);
			}
		}
		return resolvedEntrances;
	}

	public HashMap<String, String> getEntrances() {
		return entrances;
	}

	public void setEntrances(HashMap<String, String> entrances) {
		this.entrances = entrances;
	}

	public Building(Marker marker) {
		this.latitude = marker.getLatitude();
		this.longitude = marker.getLongitude();
		this.title = marker.getTitle();
		this.openingTime = marker.getOpeningTime();
		this.closingTime = marker.getClosingTime();
		this.id = marker.getId();
	}

	public double getLatitude() {
		return latitude;
	}

	public Building(double latitude, double longitude, String title, String openingTime, String closingTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public LatLng toLatLng()
	{
		return new LatLng(latitude, longitude);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(String openingTime) {
		this.openingTime = openingTime;
	}

	public String getClosingTime() {
		return closingTime;
	}

	public void setClosingTime(String closingTime) {
		this.closingTime = closingTime;
	}
	
	private int getHourOfTime(String time)
	{
		int h;
		if (time.length() == 4) //e.g. 7:00
			h = Integer.parseInt(time.substring(0, 1));
		else
			h = Integer.parseInt(time.substring(0, 2));
		
		return h;
	}
	
	public boolean isCurrentlyOpen()
	{
		LatLng coord = toLatLng();
	    int hour = ZoningTools.GetHour(coord);
	    
	    int openingHour = getHourOfTime(openingTime);
	    int closingHour = getHourOfTime(closingTime); //~~~I know who I want to take me home~~~
	    
	    return hour >= openingHour && hour < closingHour;
	    
	}
	

	public Building() {

	}

}

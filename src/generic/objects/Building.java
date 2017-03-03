package generic.objects;

import java.util.List;

import server.JSONTools;

public class Building extends WalkerObject {
	private double latitude;
	private double longitude;
	private String title;
	private String openingTime;
	private String closingTime;
	// private List<String> entrances;

	public Building(double latitude, double longitude, String title, String openingTime, String closingTime,
			List<String> entrances) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		// this.entrances = entrances;
	}

	// public List<String> getEntrances() {
	// return entrances;
	// }
	//
	// public void setEntrances(List<String> entrances) {
	// this.entrances = entrances;
	// }

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

	public Building() {

	}

	public String toJson() {

		String jsonString = JSONTools.g.toJson(this);
		return jsonString;
	}
}

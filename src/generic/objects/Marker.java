package generic.objects;

import java.util.HashMap;

public class Marker extends WalkerObject {
	private double latitude;
	private double longitude;
	private String title;
	private String openingTime;
	private String closingTime;
	private boolean building;
	private String buildingId;
	private HashMap<String, String> entrances;

	public Marker(double latitude, double longitude, String title, String openingTime, String closingTime,
			boolean building, String buildingId, HashMap<String, String> entrances) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.building = building;
		this.buildingId = buildingId;
		this.entrances = entrances;
	}

	public HashMap<String, String> getEntrances() {
		return entrances;
	}

	public void setEntrances(HashMap<String, String> entrances) {
		this.entrances = entrances;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public Marker(double latitude, double longitude, String title, String openingTime, String closingTime,
			boolean building, String buildingId) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.building = building;
		this.buildingId = buildingId;
	}

	public Marker(double latitude, double longitude, String title, String openingTime, String closingTime,
			boolean building) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
		this.building = building;
	}

	public boolean isBuilding() {
		return building;
	}

	public void setBuilding(boolean building) {
		this.building = building;
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

	public Marker(double latitude, double longitude, String title, String openingTime, String closingTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.openingTime = openingTime;
		this.closingTime = closingTime;
	}

	public double getLatitude() {
		return latitude;
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

	public Marker(double latitude, double longitude, String title, String id) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.id = id;
	}

	public Marker(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Marker() {

	}

}

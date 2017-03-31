package generic.objects;

public class Entrance extends WalkerObject {

	private double latitude;
	private double longitude;
	private String buildingId;

	public Entrance(double latitude, double longitude, String buildingId, String id) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.buildingId = buildingId;
		this.id = id;
	}

	public Entrance(double latitude, double longitude, String buildingId) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.buildingId = buildingId;
	}

	public String getBuildingId() {
		return buildingId;
	}

	public void setBuildingId(String buildingId) {
		this.buildingId = buildingId;
	}

	public Entrance(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
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

	public Entrance(Marker marker) {
		this.latitude = marker.getLatitude();
		this.longitude = marker.getLongitude();
		this.id = marker.getId();
		this.buildingId = marker.getBuildingId();
	}

	public Entrance() {

	}

}

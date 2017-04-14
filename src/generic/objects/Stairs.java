package generic.objects;

public class Stairs extends WalkerObject {
	private double longitude;
	private double latitude;
	private boolean isStairs;

	public Stairs() {
	}

	public Stairs(double longitude, double latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Stairs(Marker marker) {
		this.setId(marker.getId());
		this.setTimestamp(marker.getTimestamp());
		this.setLongitude(marker.getLongitude());
		this.setLatitude(marker.getLatitude());
	}

	public void setIsStairs(boolean bool) {
		this.isStairs = bool;
	}

	public boolean isStairs() {
		return isStairs;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setStairs(boolean bool) {
		this.isStairs = bool;
	}
}

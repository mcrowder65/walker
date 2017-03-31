package generic.objects;

import server.JSONTools;

public abstract class WalkerObject {

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private long timestamp;

	public WalkerObject() {
		this.timestamp = System.currentTimeMillis();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	final public String toJson() {
		return JSONTools.g.toJson(this);
	}

	final public String toString() {
		return toJson();
	}

}

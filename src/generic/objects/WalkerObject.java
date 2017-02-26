package generic.objects;

public abstract class WalkerObject {
	public abstract String toJson();

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

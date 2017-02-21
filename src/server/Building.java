package server;

import com.google.gson.Gson;

public class Building {
	public String name;
	transient private Gson g;

	@Override
	public String toString() {
		return "Building [name=" + name + "]";
	}

	public Building(String name) {
		g = new Gson();
	}

	public Building() {
		g = new Gson();
	}

	public String toJson() {

		String jsonString = g.toJson(this);
		return jsonString;
	}
}

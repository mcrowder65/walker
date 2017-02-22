package server;

import com.google.gson.Gson;

public class Building {
	public String name;

	@Override
	public String toString() {
		return "Building [name=" + name + "]";
	}

	public Building(String name) {
		this.name = name;
	}

	public Building() {
		
	}

	public String toJson() {

		String jsonString = JSONTools.g.toJson(this);
		return jsonString;
	}
}

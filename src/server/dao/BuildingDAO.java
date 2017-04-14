package server.dao;

import java.util.ArrayList;
import java.util.List;

import generic.Tools;
import generic.objects.Building;
import generic.objects.WalkerObject;

public class BuildingDAO {
	public static List<Building> getAll() {
		List<WalkerObject> buildingsAsWalkerObjects = Tools.firebase.getAllAsObjects("buildings", new Building());
		List<Building> buildings = new ArrayList<>();
		for (int i = 0; i < buildingsAsWalkerObjects.size(); i++) {
			buildings.add((Building) buildingsAsWalkerObjects.get(i));
		}
		return buildings;

	}

	public static Building get(String id) {
		Building building = (Building) Tools.firebase.get("buildings/" + id, new Building());

		return building;
	}
}

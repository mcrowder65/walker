package server.dao;

import java.util.ArrayList;
import java.util.List;

import generic.Tools;
import generic.objects.Stairs;
import generic.objects.WalkerObject;

public class StairsDAO {
	public static List<Stairs> getAll() {
		List<WalkerObject> stairsAsWalkerObjects = Tools.firebase.getAllAsObjects("stairs", new Stairs());
		List<Stairs> stairs = new ArrayList<>();
		for (int i = 0; i < stairsAsWalkerObjects.size(); i++) {
			stairs.add((Stairs) stairsAsWalkerObjects.get(i));
			stairs.get(i).setIsStairs(true);
		}
		return stairs;

	}
}

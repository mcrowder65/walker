package generic;

import java.awt.Point;

import server.JSONTools;

public class NodeIndex extends Point {

	public NodeIndex(int x, int y) {
		super(x, y);
	}

	@Override
	public String toString() {
		return JSONTools.g.toJson(this);
	}

}

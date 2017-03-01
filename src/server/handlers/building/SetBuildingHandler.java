package server.handlers.building;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Building;
import server.JSONTools;
import server.handlers.WalkerHandler;

public class SetBuildingHandler extends WalkerHandler {
	private Object lock;

	public SetBuildingHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		synchronized (lock) {
			String response = this.getRequestBodyAndSetHeaders(exchange);
			Building building = JSONTools.g.fromJson(response, Building.class);
			if (!building.getId().equals("")) {
				// update
				Tools.firebase.update("buildings", building);
			} else {
				// create
				Tools.firebase.create("buildings", building);
			}

		}

	}

}

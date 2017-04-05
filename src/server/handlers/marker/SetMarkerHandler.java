package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Building;
import generic.objects.Entrance;
import generic.objects.Marker;
import generic.objects.Stairs;
import server.JSONTools;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class SetMarkerHandler extends WalkerHandler {
	Object lock;

	public SetMarkerHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			String result = getRequestBodyAndSetHeaders(exchange);
			if (!result.equals("")) {

				Marker marker = JSONTools.g.fromJson(result, Marker.class);
				if (!marker.getId().equals("")) {
					if (marker.isBuilding()) {
						String path = "buildings/" + marker.getId();
						Building building = new Building(marker);
						Tools.firebase.update(path, building);

					} else if (marker.isStairs()) {
						String path = "stairs/" + marker.getId();
						Stairs stairs = new Stairs(marker);
						Tools.firebase.update(path, stairs);
					} else if (!marker.isBuilding()) {
						String path = "entrances/" + marker.getId();
						Entrance entrance = new Entrance(marker);
						Tools.firebase.update(path, entrance);
						Tools.firebase.update(
								"buildings/" + entrance.getBuildingId() + "/entrances/" + entrance.getId(),
								entrance.getId());
					}
				} else {
					if (marker.isBuilding()) {
						String path = "buildings" + marker.getId();
						Building building = new Building(marker);
						Tools.firebase.create(path, building);

					} else if (marker.isStairs()) {
						String path = "stairs" + marker.getId();
						Stairs stairs = new Stairs(marker);
						Tools.firebase.create(path, stairs);
					} else if (!marker.isBuilding()) {
						String path = "entrances";
						Entrance entrance = new Entrance(marker);
						Tools.firebase.create(path, entrance);
						Tools.firebase.update(
								"buildings/" + entrance.getBuildingId() + "/entrances/" + entrance.getId(),
								entrance.getId());

					}

				}
				try {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
					exchange.getResponseBody().close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {
				exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				String json = "This set marker request asked for empty things :(";
				exchange.getResponseBody().write(json.getBytes());
				exchange.getResponseBody().close();
			}
		}

	}

}

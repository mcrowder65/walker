package server.handlers.marker;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import generic.Tools;
import generic.objects.Building;
import generic.objects.Entrance;
import generic.objects.Marker;
import server.JSONTools;
import server.Server;
import server.handlers.WalkerHandler;
import sun.net.www.protocol.http.HttpURLConnection;

public class DeleteMarkerHandler extends WalkerHandler {
	private Object lock;

	public DeleteMarkerHandler() {
		lock = new Object();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		synchronized (lock) {
			String json = this.getRequestBodyAndSetHeaders(exchange);
			if (!json.equals("")) {
				Marker marker = JSONTools.g.fromJson(json, Marker.class);
				if (marker.isBuilding()) {

					Building building = (Building) Tools.firebase.get("buildings/" + marker.getId(), new Building());
					Tools.firebase.delete("buildings/" + marker.getId());
					for (String key : building.getEntrances().keySet()) {
						Tools.firebase.delete("entrances/" + key);
					}

				} else if (marker.isStairs()) {
					String path = "stairs" + marker.getId();
					Tools.firebase.delete(path);
				} else if (!marker.isBuilding()) {
					Entrance entrance = new Entrance(marker);
					Tools.firebase.delete("entrances/" + marker.getId());
					Tools.firebase.delete("buildings/" + entrance.getBuildingId() + "/entrances/" + entrance.getId());
				}

			}
			Server.reset();
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			exchange.getResponseBody().close();

		}

	}

}

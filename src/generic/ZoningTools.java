package generic;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.json.JSONObject;

import googlemaps.LatLng;

public class ZoningTools {

	public static int GetTimeZone(LatLng coord) {
		final long TIMESTAMP = 14912590945l; // This doesn't matter
		String resp = Tools.getHTTPString("https://maps.googleapis.com/maps/api/timezone/json?location="
				+ coord.toUrlValue() + "&key=" + Config.TIMEZONE_KEY + "&timestamp=" + TIMESTAMP);
		JSONObject rootObj = new JSONObject(resp);

		int rawOffset = rootObj.getInt("rawOffset");
		int dstOffset = rootObj.getInt("dstOffset");
		// This is in seconds, convert to hours
		int hours = (rawOffset + dstOffset) / (60 * 60);
		return hours;

	}

	private static int GetLocalHour() {
		Calendar time = new GregorianCalendar();
		int hour = time.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

	public static int GetHour(LatLng coord) {
		int timezone = GetTimeZone(coord);
		ZoneId zId = ZoneId.of("UTC");
		TimeZone tZone = TimeZone.getTimeZone(zId);
		Calendar time = Calendar.getInstance(tZone);

		int hour = (time.get(Calendar.HOUR_OF_DAY)) % 24; // Mountain time

		int convHour = (hour + timezone) % 24;
		if (convHour < 0) convHour += 24;
		System.out.println("if this isn't correct then something is going wrong - local time hour:" + convHour);

		return convHour;

	}

}

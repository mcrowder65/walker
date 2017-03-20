/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package googlemaps;

import java.awt.geom.Point2D;
import java.util.Locale;

import googlemaps.StringJoin.UrlValue;

/**
 * A place on Earth, represented by a Latitude/Longitude pair.
 */
public class LatLng implements UrlValue {

	public static final double EPSILON = 0.0000001;
	/**
	 * The latitude of this location.
	 */
	public double latitude;

	/**
	 * The longitude of this location.
	 */
	public double longitude;

	/**
	 * Construct a location with a latitude longitude pair.
	 */
	public LatLng(double lat, double lng) {
		this.latitude = lat;
		this.longitude = lng;
	}

	public LatLng(Point2D.Double pnt) {
		this.latitude = pnt.y;
		this.longitude = pnt.x;
	}

	public LatLng(LatLng other) {
		this.latitude = other.latitude;
		this.longitude = other.longitude;
	}

	@Override
	public String toString() {
		return toUrlValue();
	}

	@Override
	public String toUrlValue() {
		// Enforce Locale to English for double to string conversion
		return String.format(Locale.ENGLISH, "%.8f,%.8f", latitude, longitude);
	}

	public static boolean closeEnoughLatLng(LatLng a, LatLng b) {
		return Math.abs(a.latitude - b.latitude) < EPSILON && Math.abs(a.longitude - b.longitude) < EPSILON;

	}
}
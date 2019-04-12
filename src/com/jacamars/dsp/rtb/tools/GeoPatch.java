package com.jacamars.dsp.rtb.tools;

import java.io.File;
import java.net.InetAddress;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.openrtb.OpenRtb.BidRequest;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.exchanges.Atomx;
import com.jacamars.dsp.rtb.tools.IsoTwo2Iso3;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Location;

/**
 * A singleton that adds geo information to a device, if IP is available.
 * 
 * @author Ben M. Faul
 *
 */
public enum GeoPatch {
	GEOPATCH;
	static final JsonNodeFactory factory = JsonNodeFactory.instance;
	static IsoTwo2Iso3 isoMap;
	static DatabaseReader reader = null;

	public static void main(String[] args) throws Exception {
		new IsoTwo2Iso3("@ISO2-3", "data/adxgeo.csv");
		GeoPatch.getInstance("/home/ben/GeoLite2-City.mmdb");
		Atomx br = new Atomx("SampleBids/nogeo.txt");
	}

	public static GeoPatch getInstance(String fileName) throws Exception {
		reader = new DatabaseReader.Builder(new File(fileName)).build();
		isoMap = (IsoTwo2Iso3) LookingGlass.symbols.get("@ISO2-3");
		InetAddress ipAddress = InetAddress.getByName("47.180.117.78");
		reader.city(ipAddress);
		return GEOPATCH;
	}

	public static GeoPatch getInstance() {
		return GEOPATCH;
	}

	public double[] patch(JsonNode idev) {

		double lat = 0;
		double lon = 0;
		double[] rc = new double[2];
		rc[0] = lat;
		rc[1] = lon;
		try {
			// long timer = System.currentTimeMillis();
			/**
			 * No patch if not initialized
			 */
			if (reader == null || idev == null || idev instanceof MissingNode)
				return rc;

			ObjectNode device = (ObjectNode) idev;
			/**
			 * Make sure iso2 to 3 converter is available.
			 */
			if (isoMap == null)
				isoMap = (IsoTwo2Iso3) LookingGlass.symbols.get("@ISO2-3");

			String ip = device.get("ip").asText("");
			if (ip.equals(""))
				return rc;

			InetAddress ipAddress = InetAddress.getByName(ip);
			CityResponse response = null;
			City city = null;
			Country country = null;
			Subdivision subdivision = null;
			Postal postal = null;
			Location location = null;

			/**
			 * Make a geo node if necessary
			 */
			ObjectNode geo = (ObjectNode) device.get("geo");
			if (geo == null) {
				geo = factory.objectNode();
				device.set("geo", geo);
				response = reader.city(ipAddress);
				city = response.getCity();
				country = response.getCountry();
				subdivision = response.getMostSpecificSubdivision();
				postal = response.getPostal();
				location = response.getLocation();

				device.set("geo", geo);
			}

			/**
			 * Add country, convert iso2 to iso3
			 */
			if (geo.get("country") == null) {
				if (response == null)
					response = reader.city(ipAddress);
				if (city == null)
					city = response.getCity();
				if (country == null)
					country = response.getCountry();
				if (subdivision == null)
					subdivision = response.getMostSpecificSubdivision();
				if (postal == null)
					postal = response.getPostal();
				if (location == null)
					location = response.getLocation();
				String iso = country.getIsoCode();
				if (iso.length() == 2 && isoMap != null) {
					iso = isoMap.query(iso);
				}
				geo.put("country", iso);
			}

			/**
			 * Add city
			 */
			if (geo.get("city") == null) {
				if (response == null)
					response = reader.city(ipAddress);
				if (city == null)
					city = response.getCity();
				if (country == null)
					country = response.getCountry();
				if (subdivision == null)
					subdivision = response.getMostSpecificSubdivision();
				if (postal == null)
					postal = response.getPostal();
				if (location == null)
					location = response.getLocation();
				String scity = city.getName();
				if (scity != null)
					geo.put("city", city.getName());
			}

			/**
			 * Add state/region
			 */
			if (geo.get("region") == null) {
				if (response == null)
					response = reader.city(ipAddress);
				if (city == null)
					city = response.getCity();
				if (country == null)
					country = response.getCountry();
				if (subdivision == null)
					subdivision = response.getMostSpecificSubdivision();
				if (postal == null)
					postal = response.getPostal();
				if (location == null)
					location = response.getLocation();
				String sregion = subdivision.getIsoCode();
				if (sregion != null)
					geo.put("region", sregion);
			}
			if (geo.get("zip") == null) {
				if (response == null)
					response = reader.city(ipAddress);
				if (city == null)
					city = response.getCity();
				if (country == null)
					country = response.getCountry();
				if (subdivision == null)
					subdivision = response.getMostSpecificSubdivision();
				if (postal == null)
					postal = response.getPostal();
				if (location == null)
					location = response.getLocation();
				String szip = postal.getCode();
				if (szip != null)
					geo.put("zip", szip);
			}

			/**
			 * Add latitude and longitude
			 */
			if (geo.get("lat") == null) {
				if (response == null)
					response = reader.city(ipAddress);
				if (city == null)
					city = response.getCity();
				if (country == null)
					country = response.getCountry();
				if (subdivision == null)
					subdivision = response.getMostSpecificSubdivision();
				if (postal == null)
					postal = response.getPostal();
				if (location == null)
					location = response.getLocation();
				if (location != null) {
					lat = location.getLatitude();
					lon = location.getLongitude();
					geo.put("lat", lat);
					geo.put("lon", lon);
					rc[0] = lat;
					rc[1] = lon;
				}
			}
		} catch (Exception error) {
			// don't crap out on database errors when ip is not found.
		}
		return rc;

		// timer = System.currentTimeMillis() - timer;
		// System.out.println(timer);
	}

}

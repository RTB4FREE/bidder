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
	
	public void patch(JsonNode idev) throws Exception {
		
		//long timer = System.currentTimeMillis();
		/**
		 * No patch if not initialized
		 */
		if (reader == null || idev == null || idev instanceof MissingNode)
			return;
		
		ObjectNode device = (ObjectNode)idev;
		/**
		 * Make sure iso2 to 3 converter is available.
		 */
		if (isoMap == null) 
			isoMap = (IsoTwo2Iso3) LookingGlass.symbols.get("@ISO2-3");
		
		String ip = device.get("ip").asText("");
		if (ip.equals(""))
			return;
		
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
		ObjectNode geo = (ObjectNode)device.get("geo");
		if (geo == null) {
			geo = factory.objectNode();
			device.set("geo", geo);
			response = reader.city(ipAddress);
			city = response.getCity();
			country = response.getCountry();
			subdivision = response.getMostSpecificSubdivision();
			postal = response.getPostal();
			location = response.getLocation();
		}
		
		/**
		 * Add country, convert iso2 to iso3
		 */
		JsonNode x = null;
		if ((x = geo.get("country"))==null) {
			if (x == null) {
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
			}
			String iso = country.getIsoCode();
			if (iso.length()==2 && isoMap != null) {
				iso = isoMap.query(iso);
			}
			geo.set("country", new TextNode(iso));
		}	
		
		/**
		 * Add city
		 */
		x = null;
		if ((x = geo.get("city"))==null) {
			if (x == null) {
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
			}
			geo.set("city", new TextNode(city.getName()));
		}
		
		/**
		 * Add state/region
		 */
		x = null;;
		if ((x = geo.get("region"))==null) {
			if (x == null) {
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
			}
			geo.set("state",new TextNode(subdivision.getIsoCode()));
		}	
		x = null;;
		if ((x = geo.get("zipcode"))==null) {
			if (x == null) {
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
			}
			geo.set("zip", new TextNode(postal.getCode()));
		}
		
		/**
		 * Add latitude and longitude
		 */
		x = null;;
		if ((x = geo.get("lat"))==null) {
			if (x == null) {
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
			}
			geo.set("lat", new DoubleNode(location.getLatitude()));
			geo.set("lon", new DoubleNode(location.getLatitude()));
		}
		
		//timer = System.currentTimeMillis() - timer;
		//System.out.println(timer);
	}
	
}

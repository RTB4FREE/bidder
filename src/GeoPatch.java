import java.io.File;
import java.net.InetAddress;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.tools.IsoTwo2Iso3;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.maxmind.geoip2.record.Location;

public enum GeoPatch {
	GEOPATCH;
	static final JsonNodeFactory factory = JsonNodeFactory.instance;
	static IsoTwo2Iso3 isoMap;
	static DatabaseReader reader = null;

	public static void main(String[] args) throws Exception {


		InetAddress ipAddress = InetAddress.getByName("128.101.101.101");

		// Replace "city" with the appropriate method for your database, e.g.,
		// "country".
		CityResponse response = reader.city(ipAddress);

		Country country = response.getCountry();
		System.out.println(country.getIsoCode());            // 'US'
		System.out.println(country.getName());               // 'United States'
		System.out.println(country.getNames().get("zh-CN")); // '美国'

		Subdivision subdivision = response.getMostSpecificSubdivision();
		System.out.println(subdivision.getName());    // 'Minnesota'
		System.out.println(subdivision.getIsoCode()); // 'MN'

		City city = response.getCity();
		System.out.println(city.getName()); // 'Minneapolis'

		Postal postal = response.getPostal();
		System.out.println(postal.getCode()); // '55455'
		System.out.println(response.getLocation().getLatitude());  // 44.9733
		System.out.println(response.getLocation().getLongitude()); // -93.2323)
	}
	
	public static GeoPatch getInstance(String fileName) throws Exception {
		reader = new DatabaseReader.Builder(new File(fileName)).build();
		isoMap = (IsoTwo2Iso3) LookingGlass.symbols.get("@ISO2-3");
		return GEOPATCH;
	}
	
	public void patch(ObjectNode device) throws Exception {
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
		
		JsonNode x = null;
		if ((x = geo.get("country"))==null) {
			if (response == null) {
				response = reader.city(ipAddress);
				city = response.getCity();
				country = response.getCountry();
				subdivision = response.getMostSpecificSubdivision();
				postal = response.getPostal();
				location = response.getLocation();
			}
			String iso = country.getIsoCode();
			if (iso.length()==2 && isoMap != null) {
				iso = isoMap.query(iso);
			}
			geo.set("country", new TextNode(iso));
		}	
		x = null;
		if ((x = geo.get("city"))==null) {
			if (response == null) {
				response = reader.city(ipAddress);
				city = response.getCity();
				country = response.getCountry();
				subdivision = response.getMostSpecificSubdivision();
				postal = response.getPostal();
				location = response.getLocation();
			}
			geo.set("city", new TextNode(city.getName()));
		}
		x = null;;
		if ((x = geo.get("state"))==null) {
			if (response == null) {
				response = reader.city(ipAddress);
				city = response.getCity();
				country = response.getCountry();
				subdivision = response.getMostSpecificSubdivision();
				postal = response.getPostal();
				location = response.getLocation();
			}
			geo.set("state",new TextNode(subdivision.getIsoCode()));
		}	
		x = null;;
		if ((x = geo.get("zipcode"))==null) {
			if (response == null) {
				response = reader.city(ipAddress);
				city = response.getCity();
				country = response.getCountry();
				subdivision = response.getMostSpecificSubdivision();
				postal = response.getPostal();
				location = response.getLocation();
			}
			geo.set("zip", new TextNode(postal.getCode()));
		}
		x = null;;
		if ((x = geo.get("lat"))==null) {
			if (response == null) {
				response = reader.city(ipAddress);
				city = response.getCity();
				country = response.getCountry();
				subdivision = response.getMostSpecificSubdivision();
				postal = response.getPostal();
				location = response.getLocation();
			}
			geo.set("lat", new DoubleNode(location.getLatitude()));
			geo.set("lon", new DoubleNode(location.getLatitude()));
		}
	}
	
}

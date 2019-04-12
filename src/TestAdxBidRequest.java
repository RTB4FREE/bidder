import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteSource;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.exchanges.adx.AdxBidRequest;
import com.jacamars.dsp.rtb.exchanges.adx.AdxGeoCodes;
import com.jacamars.dsp.rtb.exchanges.adx.DoubleClick;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.tools.GeoPatch;
import com.jacamars.dsp.rtb.tools.IsoTwo2Iso3;

public class TestAdxBidRequest {

	public static ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static String proto = "EhBcq7AjAAlPbwqBCRa5BjSdIgP5IdcybU1vemlsbGEvNS4wIChpUGhvbmU7IENQVSBpUGhvbmUgT1MgMTJfMiBsaWtlIE1hYyBPUyBYKSBBcHBsZVdlYktpdC82MDUuMS4xNSAoS0hUTUwsIGxpa2UgR2Vja28pIE1vYmlsZS8xNUUxNDhaKGh0dHBzOi8vaXR1bmVzLmFwcGxlLmNvbS9hcHAvaWQ0NzU5NzY1NzdiAmVuaggIyw0V7sucPmoICOoBFe7LnD5qCAi2CxXuy5w+aggI1QgVeFcpPWoICPENFUGKIz1y4wEIARDAAhCsAhgyGDIiKBYiHhsaRg8NDjAZEBESExQNDg8QERITFBkaGw0ODxAREhMUGRobRhYyZSqQAe0B7gHyAf8BiwLLAswC1gKeA70D2gPdA+ED5QPmA+kD6gORBJkEngSfBKYEsgS0BLwEvQS+BL8E7wS6BcAF5wXpBf8FiAaMBpEGmQadBp4GqAayBroGvAbABvAG9Ab1BvkGSgsQ9+SR69UBKLDjLWABcNjyk1h5GR/5g+Vv8+F5fcAul0UWIh+oAVDQAQH6AQCiAgEAsAIBuAIBwAIB4gIA8gIEAwUGB3gAoAEBqgEbQ0FFU0VOY0E4MGFsMjhzdkFpWGZMN2JHVVZvyAGQ/v////////8B0gECVyfiAZYBMgk0NzU5NzY1Nzc4AVjc2ANY4tgDWNTYA1j8ngSiASRcq7AjAAlPbwqBCRYBBjSdmGE4Ub4lO5aFkkNNbrmNZwE4/a/CAQpQZXJmZWN0MzY12gEQ8hdRFJxSTyeVO/fXIeMfuxoGaXBob25lQAFIAWIFYXBwbGVqBmlwaG9uZXIECAwQAnj3AoABmwWQAQCYAdAP+AEB+AGAx0S4AsC9qATCAlJcq7AjAAlPbwqBCRYCBjSdrlFeQy+kwi9tbKzC3LT6a0dfV1sXnTYhQgH1Dd310qO+GmLM1xbNUxVtI4V26sTstLSS3bA88NQ4PnUtX2Z4eMJZyAKTP5gDAaoDPgowCgoNcbkHQhVAiqjCCgoND64HQhVAiqjCCgoNj60HQhXbhKjCCgoN8bgHQhXbhKjCEgoNgLMHQhWOh6jCsgM2CAESBmlwaG9uZRoFYXBwbGUiBmlwaG9uZSoECAwQAjAAOPcCQJsFSNAPUAFaCGlwaG9uZSA4ugMCQ0HCAxRwdWItMTIzMjI2NTM5OTQxNzMwMsgDrALaA0pBTnktekpGUFhtQUN2MFlXMFBkN19iN2FucnJ2bGxDcHktMkJyTEtfUmNqUlpvRWduQ1NXZVlJOUM5djRKWlJReERfZzhWTHM1Z+ADAg==";
	
	public static void main(String []args) throws Exception {
		/** See crypto file **/
	    
	    new AdxGeoCodes("@ADXGEO", "data/adxgeo.csv");
	    new LookingGlass("@ZIPCODES","data/zip_codes_states.csv");
	    
	    AdxBidRequest.lookingGlass = (AdxGeoCodes) LookingGlass.symbols.get("@ADXGEO");
	    
	    GeoPatch.getInstance("/home/ben/GeoLite2-City.mmdb");
	    
		byte[] data = DatatypeConverter.parseBase64Binary(proto);
		InputStream in = ByteSource.wrap(data).openStream();
		BidRequest.compileBuiltIns();
		AdxBidRequest br = new AdxBidRequest(in);
		
		System.out.println(mapper.writer().withDefaultPrettyPrinter().writeValueAsString(br.root));
	}
}

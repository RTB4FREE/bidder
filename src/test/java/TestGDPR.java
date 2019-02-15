package test.java;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.pojo.BidRequest;

public class TestGDPR {
	static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
	
	  /**
	   * Test that opt in works
	   * @throws Exception on networking errors.
	   */
	  @Test 
	  public void testConsent() throws Exception {

			BidRequest br = new BidRequest("./SampleBids/gdprCONSENT.txt");
			br.setExchange("nexage");
			br.enforceGDPR();
			
			System.out.println(br.toString());
			
			Map m = (Map)mapper.readValue(br.toString(), Map.class);
			
			Map user = (Map)m.get("user");
			assertNotNull(user);
	  }
	  
	  /**
	   * Test that opt in works
	   * @throws Exception on networking errors.
	   */
	  @Test 
	  public void testNoConsent() throws Exception {

			BidRequest br = new BidRequest("./SampleBids/gdprNOCONSENT.txt");
			br.setExchange("nexage");
			br.enforceGDPR();
			
			System.out.println(br.toString());
			
			Map m = (Map)mapper.readValue(br.toString(), Map.class);
			
			Map user = (Map)m.get("user");
			assertNull(user);
	  }
	  
	  
	  

}

package test.java;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.exchanges.Fyber;
import com.jacamars.dsp.rtb.exchanges.bidswitch.Bidswitch;
import com.jacamars.dsp.rtb.pojo.BidRequest;


/**
 * A class for testing that the bid has the right parameters
 * @author Ben M. Faul
 *
 */
public class TestBidswitch  {
	static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
	static Controller c;
	public static String test = "";
	
	@BeforeClass
	  public static void testSetup() {		
		try {
			Config.setup();
			System.out.println("******************  TestBidswitch");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

	  @AfterClass
	  public static void testCleanup() {
		  Config.teardown();
	  }
	  
	  /**
	   * Test a valid bid response.
	   * @throws Exception on networking errors.
	   */
	  @Test 
	  public void testBannerAd() throws Exception {
		  /**
		   * This one returns an APP creative.
		   */
		  HttpPostGet http = new HttpPostGet();
			String bid = Charset.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/Bidswitch/AppNoDomain.txt")))).toString();
			String s = null;
			long time = 0;

			String xtime = null;
			try {
				time = System.currentTimeMillis();
				s = http.sendPost("http://" + Config.testHost + "/rtb/bids/bidswitch", bid, 3000000, 3000000);
				time = System.currentTimeMillis() - time;
				xtime = http.getHeader("X-TIME");
			} catch (Exception error) {
				fail("Can't connect to test host: " + Config.testHost);
			}
			System.out.println(s);
			assertNotNull(s);
			assertTrue(http.getResponseCode()==200);
			
			Map m = mapper.readValue(s,Map.class);
			String id = (String)m.get("id");
			assertTrue(id.equals("123"));
			assertNotNull(m.get("ext"));
			Map<String,String> mx = (Map)m.get("ext");
			assertNotNull(mx.get("protocol"));
			assertTrue(mx.get("protocol").equals("5.3"));
			
			List<Map> seatbids = (List)m.get("seatbids");
			assertNotNull(seatbids);
			assertTrue(seatbids.size()==1);
			Map seatbid = seatbids.get(0);
			String str = (String)seatbid.get("seat");
			assertNotNull(str);
			assertTrue(str.equals("4"));
			
			List<Map> bids = (List)seatbid.get("bids");
			assertNotNull(bids);
			assertTrue(bids.size()==1);
			Map xbid = (Map)bids.get(0);
			
			str = (String)xbid.get("nurl");
			assertFalse(str.contains(" "));
			
			/**
			 * This one will return a Banner ad.
			 */
			bid = Charset.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/Bidswitch/Bannerad.txt")))).toString();

			time = 0;

			xtime = null;
			try {
				time = System.currentTimeMillis();
				s = http.sendPost("http://" + Config.testHost + "/rtb/bids/bidswitch", bid, 3000000, 3000000);
				time = System.currentTimeMillis() - time;
				xtime = http.getHeader("X-TIME");
			} catch (Exception error) {
				fail("Can't connect to test host: " + Config.testHost);
			}
			
			System.out.println(s);
			
			assertNotNull(s);
			int code = http.getResponseCode();
			assertEquals(code,200);
			
			m = mapper.readValue(s,Map.class);
			id = (String)m.get("id");
			assertTrue(id.equals("123"));
			assertNotNull(m.get("ext"));
			mx = (Map)m.get("ext");
			assertNotNull(mx.get("protocol"));
			assertTrue(mx.get("protocol").equals("5.3"));
			
			seatbids = (List)m.get("seatbids");
			assertNotNull(seatbids);
			assertTrue(seatbids.size()==1);
			seatbid = seatbids.get(0);
			str = (String)seatbid.get("seat");
			assertNotNull(str);
			assertTrue(str.equals("4"));
			
			bids = (List)seatbid.get("bids");
			assertNotNull(bids);
			assertTrue(bids.size()==1);
			xbid = (Map)bids.get(0);
			
			str = (String)xbid.get("nurl");
			assertNotNull(str);
			assertTrue(str.equals("http://localhost:8080/rtb/win/answers.com/SITE/bidswitch/${AUCTION_PRICE}/26.638/-80.237/bidswitch-test/bannerad/123")); 
			
			str = (String)xbid.get("crid");
			assertNotNull(str);
			assertTrue(str.equals("bannerad"));
			
			str = (String)xbid.get("cid");
			assertNotNull(str);
			assertTrue(str.equals("bidswitch-test"));
			
			str = (String)xbid.get("id");
			assertNotNull(str);
			assertEquals(str,"1");
			
			Double d = (Double)xbid.get("price");
			assertNotNull(d);
			assertTrue(d == 4.0);
			
			List<String> cat = (List)xbid.get("cat");
			assertNotNull(cat);
			assertTrue(cat.size()==2);
			assertTrue(cat.contains("IAB12"));
			assertTrue(cat.contains("IAB1"));
			
			List<String> adomain = (List)xbid.get("adomain");
			assertNotNull(adomain);
			assertTrue(adomain.size()==1);
			assertTrue(adomain.get(0).equals("originator.com"));
			
			str = (String)xbid.get("imageurl");
			assertNotNull(str);
			assertTrue(str.equals("http://localhost:8080/images/320x50.jpg?adid=bidswitch-test&bidid=123"));
	
			str = (String)xbid.get("adm");
			assertNotNull(str);
			assertTrue(str.equals("http://localhost:8080/contact.html?gumgum_www.answers.com_ed2265d8&adid=bidswitch-test&crid=bannerad"));
			
			Map<String,String> ext = (Map)xbid.get("ext");
			assertNotNull(ext);
			assertTrue(ext.get("advertiser_name").equals("Coca-Cola"));
			assertTrue(ext.get("agency_name").equals("CC-advertising"));
	  }
	  
}
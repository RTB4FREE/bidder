package test.java;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
	  public void testBannserAd() throws Exception {
		  HttpPostGet http = new HttpPostGet();
			String bid = Charset.defaultCharset()
					.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/Bidswitch/Bannerad.txt")))).toString();
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
			assertNull(s);
			int code = http.getResponseCode();
			assertEquals(code,204);
	  }
	  
}
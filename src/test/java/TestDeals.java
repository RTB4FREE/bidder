package test.java;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.xml.bind.DatatypeConverter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Deal;
import com.jacamars.dsp.rtb.common.Deals;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * A class for testing that the bid has the right parameters
 *
 * @author Ben M. Faul
 *
 */
public class TestDeals {
	static Controller c;
	public static String test = "";

	static BidResponse response;
	static CountDownLatch latch;

	@BeforeClass
	public static void testSetup() {
		try {
			Config.setup();
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	@AfterClass
	public static void testCleanup() {
		Config.teardown();
	}


	/**
	 * Test a private auction with a deal not present in the campaigns.
	 * @throws Exception
	 */
	@Test
	public void testPrivateBidNoResponse() throws Exception {
		HttpPostGet http = new HttpPostGet();
		String bid = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePrivateAuction.txt")))).toString();
		String s = null;
		long time = 0;

		String xtime = null;
		try {
			time = System.currentTimeMillis();
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 300000, 300000);
			time = System.currentTimeMillis() - time;
			xtime = http.getHeader("X-TIME");
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		assertNull(s);
		int code = http.getResponseCode();
		assertEquals(code,204);
	}

	/**
	 * Test a private auction with a deal not present in the campaigns.
	 * @throws Exception
	 */
	@Test
	public void testPreferredOkToBid() throws Exception {
		HttpPostGet http = new HttpPostGet();
		String bid = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePreferredAuction.txt")))).toString();
		String s = null;
		long time = 0;

		String xtime = null;
		try {
			time = System.currentTimeMillis();
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 300000, 300000);
			time = System.currentTimeMillis() - time;
			xtime = http.getHeader("X-TIME");
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		/**
		 * Any bid will do
		 */
		assertNotNull(s);
		int code = http.getResponseCode();
		assertEquals(code,200);
	}

	/**
	 * The bid floor of the deal will be too high, but the open auction bid floor is lower, but still deal should not bid
	 * as this is a private auction.
	 * Deal bid floor is higher than deal price but open auction bid floor is lower than private auction
	 * but should not bid.
	 * @throws Exception
	 */
	@Test
	public void testPrivateResponseBidFloorScenario1() throws Exception {
		HttpPostGet http = new HttpPostGet();
		String bid = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePrivateAuction1.txt")))).toString();
		String s = null;
		long time = 0;

		String xtime = null;
		try {
			time = System.currentTimeMillis();
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 30000000, 30000000);
			time = System.currentTimeMillis() - time;
			xtime = http.getHeader("X-TIME");
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		System.out.println("----------->" + s);
		assertNull(s);
		int code = http.getResponseCode();
		assertEquals(code,204);
	}

	/**
	 * Campaign price < request bid floor and request deal price. Campaign deal price is < bid floor but greater than request bid floor
	 * and should bid at the campaign deal price
	 * @throws Exception
	 */
	@Test
	public void testPrivateResponseBidFloorScenario2() throws Exception {
		HttpPostGet http = new HttpPostGet();
		String bid = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePrivateAuction2.txt")))).toString();
		String s = null;
		long time = 0;

		String xtime = null;
		try {
			time = System.currentTimeMillis();
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 30000000, 30000000);
			time = System.currentTimeMillis() - time;
			xtime = http.getHeader("X-TIME");
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		assertNotNull(s);
		int code = http.getResponseCode();
		assertEquals(code,200);
	}

	/**
	 * Campaign price > request bid floor and less than request deal. Campaign deal price is < request deal bid floor but
	 * should bid at regular auction price.
	 * @throws Exception
	 */
	@Test
	public void testPrivateResponseBidFloorScenario3() throws Exception {
		HttpPostGet http = new HttpPostGet();
		String bid = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePrivateAuction3.txt")))).toString();
		String s = null;
		long time = 0;

		String xtime = null;
		try {
			time = System.currentTimeMillis();
			s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 30000000, 30000000);
			time = System.currentTimeMillis() - time;
			xtime = http.getHeader("X-TIME");
		} catch (Exception error) {
			fail("Can't connect to test host: " + Config.testHost);
		}
		assertNull(s);
		int code = http.getResponseCode();
		assertEquals(code,204);
	}

    /**
     * Campaign price == request bid floor and Deal price == Deal bid floor . Campaign deal price is < request deal bid floor but
     * should bid at Deal price.
     * @throws Exception
     */
    @Test
    public void testPrivateResponseBidFloorScenario4() throws Exception {
        HttpPostGet http = new HttpPostGet();
        String bid = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePrivateAuction4.txt")))).toString();
        String s = null;
        long time = 0;

        String xtime = null;
        try {
            time = System.currentTimeMillis();
            s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 30000000, 30000000);
            time = System.currentTimeMillis() - time;
            xtime = http.getHeader("X-TIME");
        } catch (Exception error) {
            fail("Can't connect to test host: " + Config.testHost);
        }
        assertNotNull(s);
        System.out.println(s);
        int code = http.getResponseCode();
        assertEquals(code,200);
        assertTrue(s.contains("dealid"));
		assertTrue(s.contains("dealid"));
		assertTrue(s.contains("ThisShouldBid"));
		assertTrue(s.contains("price\":0.8"));
    }

    /**
     * Campaign price < request bid floor and Deal price ></> Deal bid floor.
     * should bid at Deal price.
     * @throws Exception
     */
    @Test
    public void testPrivateResponseBidFloorScenario5() throws Exception {
        HttpPostGet http = new HttpPostGet();
        String bid = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexagePrivateAuction5.txt")))).toString();
        String s = null;
        long time = 0;

        String xtime = null;
        try {
            time = System.currentTimeMillis();
            s = http.sendPost("http://" + Config.testHost + "/rtb/bids/nexage", bid, 30000000, 30000000);
            time = System.currentTimeMillis() - time;
            xtime = http.getHeader("X-TIME");
        } catch (Exception error) {
            fail("Can't connect to test host: " + Config.testHost);
        }
        assertNotNull(s);
        System.out.println(s);
        int code = http.getResponseCode();
        assertEquals(code,200);
        assertTrue(s.contains("dealid"));
        assertTrue(s.contains("ThisShouldBid"));
        assertTrue(s.contains("price\":0.8"));
    }

    @Test
    public void testPrivateResponseOpenx() throws Exception {
        HttpPostGet http = new HttpPostGet();
        String bid = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/openxpvt.txt")))).toString();
        String s = null;
        long time = 0;

        String xtime = null;
        try {
            time = System.currentTimeMillis();
            s = http.sendPost("http://" + Config.testHost + "/rtb/bids/openx", bid, 30000000, 30000000);
            time = System.currentTimeMillis() - time;
            xtime = http.getHeader("X-TIME");
        } catch (Exception error) {
            fail("Can't connect to test host: " + Config.testHost);
        }
        System.out.println(s);
        assertNotNull(s);
        int code = http.getResponseCode();
        assertEquals(code,200);
        assertTrue(s.contains("dealid"));
    }

	/**
	 * Test the deal object
	 */
	@Test
	public void testDealsObject() {

		Deals deals = new Deals();
		Deal a = new Deal("a",1);
		Deal b = new Deal("b",2);
		deals.add(a);
		deals.add(b);

		List test = new ArrayList();
		test.add("c");
		Deal x = deals.findDealRandom(test);
		assertNull(x);

		// Must be a
		test.add("a");
		x = deals.findDealRandom(test);
		assertTrue(x.id.equals("a"));

		test.add("b");

		// Can be a or B
		int isA = 0;
		for (int i=0;i<10;i++) {
			x = deals.findDealRandom(test);
			if (x.id.equals("a"))
				isA++;
		}
		assertTrue(isA != 10);


		// Must be b
		int isB = 0;
		for (int i=0;i<10;i++) {
			x = deals.findDealHighest(test);
			if (x.id.equals("b"))
				isB++;
		}
		assertTrue(isB == 10);

		Deal z = new Deal("z",2);
		deals.add(z);
		test.add("z");

		// Can be b or z
		isB = 0;
		for (int i=0;i<10;i++) {
			x = deals.findDealHighest(test);
			if (x.id.equals("b"))
				isB++;
		}
	}
}

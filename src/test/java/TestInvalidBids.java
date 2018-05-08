package test.java;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.tools.DbTools;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestInvalidBids {

	/**
	 * Setup the RTB server for the test
	 */
	@BeforeClass
	public static void setup() {
		try {
			Config.setup();
			Controller.getInstance().deleteCampaign("ben","ben:extended-device");
			System.out.println("******************  TestRanges");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void stop() {
		Config.teardown();
	}
	
	@Test
	public void testInvalidJson() throws Exception{
		HttpPostGet http = new HttpPostGet();
		String s = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/c1xbad.txt")))).toString();
		String content = null;
		long time = 0;

		try {
			content = http.sendPost("http://" + Config.testHost + "/rtb/bids/c1x", s);
		} catch (Exception error) {
			fail("Network error");
		}
		assertTrue(http.getResponseCode()==204);
		assertNull(content);
	}
}

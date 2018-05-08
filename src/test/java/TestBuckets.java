package test.java;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.HttpPostGet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class TestBuckets {

	/**
	 * Setup the RTB server for the test
	 */
	@BeforeClass
	public static void setup() {
		try {
			Config.setupBucket("intango");
			System.out.println("******************  TestBuckets");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void stop() {
		Config.teardown();
	}
	
	@Test
	public void testIntangoOnly() throws Exception{
		HttpPostGet http = new HttpPostGet();
		String s = Charset.defaultCharset()
				.decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexage50x50.txt")))).toString();
		String content = null;
		long time = 0;

		try {
			content = http.sendPost("http://" + Config.testHost + "/rtb/bids/intango", s);
		} catch (Exception error) {
			fail("Network error");
		}
		assertTrue(http.getResponseCode()==200);
		assertNotNull(content);
		assertTrue(content.contains("intango-test"));

		int size = Configuration.getInstance().getCampaignsList().size();
		assertTrue(size==1);

		size = Configuration.getInstance().getCampaignsListReal().size();
		assertTrue(size != 1);
	}
}

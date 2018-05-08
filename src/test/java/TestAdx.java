package test.java;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.jboss.netty.handler.ipfilter.CIDR;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.blocks.NavMap;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.db.DataBaseObject;
import com.jacamars.dsp.rtb.db.User;
import com.jacamars.dsp.rtb.exchanges.adx.AdxBidRequest;
import com.jacamars.dsp.rtb.exchanges.adx.AdxBidResponse;
import com.jacamars.dsp.rtb.fraud.ForensiqClient;
import com.jacamars.dsp.rtb.fraud.FraudLog;
import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * A class for testing that the bid has the right parameters
 * 
 * @author Ben M. Faul
 *
 */
public class TestAdx {

	@BeforeClass
	public static void testSetup() {
		try {
			Config.setup();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	@AfterClass
	public static void testCleanup() {
		Config.teardown();
	}

	/**
	 * Test a valid bid response.
	 * 
	 * @throws Exception
	 *             on networking errors.
	 */
	@Test
	public void testAdx() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("SampleBids/adxrequests"));
		String data;
		ObjectMapper mapper = new ObjectMapper();
		HttpPostGet http = new HttpPostGet();
		while ((data = br.readLine()) != null) {
			Map map = mapper.readValue(data, Map.class);
			String protobuf = (String) map.get("protobuf");
			if (protobuf != null) {
				byte[] protobytes = DatatypeConverter.parseBase64Binary(protobuf);
				InputStream is = new ByteArrayInputStream(protobytes);
				byte [] returns = http.sendPost("http://" + Config.testHost + "/rtb/bids/adx", protobytes);
				// AdxBidResponse resp = new AdxBidResponse(returns);
				// System.out.println(resp.toString());
			/*	try {
					AdxBidRequest bidRequest = new AdxBidRequest(is);
					System.out.println(bidRequest.internal);
					System.out.println("============================================");
					System.out.println(bidRequest.root);
					System.out.println("--------------------------------------------");
				} catch (Exception error) {
error.printStackTrace();
				} */
			}
		}

	}
}

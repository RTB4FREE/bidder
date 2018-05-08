package test.java;

import com.jacamars.dsp.rtb.bidder.ZPublisher;
import com.jacamars.dsp.rtb.commands.AddSSP;
import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.commands.DeleteSSP;
import com.jacamars.dsp.rtb.commands.ListSSP;
import com.jacamars.dsp.rtb.exchanges.google.GoogleBidRequest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;



/**
 * Tests miscellaneous classes.
 * @author Ben M. Faul
 *
 */

public class TestMetaSSP {

	static ZPublisher commands;
	static volatile BasicCommand rcv = null;
	static volatile CountDownLatch latch;

	@BeforeClass
	public static void setup() {
		try {

			Config.setup("Campaigns/metassp.json");
			com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic("tcp://localhost:6001&responses");
			channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<BasicCommand>() {
				@Override
				public void onMessage(String channel, BasicCommand cmd) {
					System.out.println("<<<<<<<<<<<<<<<<<" + cmd);
					rcv = cmd;
					latch.countDown();
				}
			});

			commands = new ZPublisher("tcp://localhost:6000&commands");
			System.out.println("******************  TestMetaSSP");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void stop() {
		Config.teardown();
	}
	
	/**
	 * Test the string replace functions used for macro substitutions.
	 */
	@Test
	public void testA_listSSP() throws Exception  {
		ListSSP s = new ListSSP("");
		rcv = null;
		latch = new CountDownLatch(1);

		commands.add(s);
		latch.await(60, TimeUnit.SECONDS);
		System.out.println("RECEIVED: " + rcv);
	}

	@Test
	public void testB_delSSP() throws Exception  {
		DeleteSSP s = new DeleteSSP("","index");
		rcv = null;
		latch = new CountDownLatch(1);

		commands.add(s);
		latch.await(60, TimeUnit.SECONDS);
		System.out.println("RECEIVED: " + rcv);
	}

	@Test
	public void testC_addSSP() throws Exception {
		String content = new String(Files.readAllBytes(Paths.get("testvirtualdsp.json")), StandardCharsets.UTF_8);
		AddSSP add = new AddSSP("","index",content);

		rcv = null;
		latch = new CountDownLatch(1);

		commands.add(add);
		latch.await(60, TimeUnit.SECONDS);
		System.out.println("RECEIVED: " + rcv);
	}

	@Test
	public void testFormatInRule() throws Exception {
		String protobuf = "ChZXU1dzK1FBSFhITUtBWjhJQWdyTlRREmoKATESJAjQAhCYAiADMgIGB3oGCNACEJgCegYIrAIQ+gF6Bgj6ARD6AToKMjMyNjk5ODY0MkEfhetRuB7VP0oDVVNEYAGKPyIIsqWIpp0BEayIqSE93XarEagDoN604YS0EeBVZQ9k6ipaGmM6S2h0dHBzOi8vd3d3LnNpbnRhZ2VzcGFyZWFzLmdyL3NpbnRhZ2VzL3BldGl4aW1lbm8tcGFudGVzcGFuaS1zb2tvbGF0YXMuaHRtbFoHCgUxMDE4NWILUgREVi1HmgECZWwq1AESiAFNb3ppbGxhLzUuMCAoWDExOyBVOyBMaW51eCB4ODZfNjQ7IGVsLWdyKSBBcHBsZVdlYktpdC81MzcuMzYgKEtIVE1MLCBsaWtlIEdlY2tvKSBDaHJvbWUvNDIuMC4yMzExLjEzNSBTYWZhcmkvNTM3LjM2IFB1ZmZpbi82LjAuOS4xNTg2M0FUGgw5Mi4yMjEuMTYyLjAiJwkAAADAsWBNQBEAAADAl4wWQBoDTk9SIgVOTy0xMUIENDM1M1C0AXIDWDExkAEC4QEAAAAAAADwPzJ6ChtDQUVTRUtPYXM0enR6ZVNMR0JSVmJtaEM0UEFCWwoRRGV0ZWN0ZWRWZXJ0aWNhbHMSC0RvdWJsZUNsaWNrGgsKBDE1MjYaAzAuNRoKCgM5MDYaAzAuMxoICgMxMjIaATEaCgoDNDU2GgMwLjgaCgoDMjk3GgMwLjVaA1VTRA==";
		byte[] protobytes = DatatypeConverter.parseBase64Binary(protobuf);
		ByteArrayInputStream bis = new ByteArrayInputStream(protobytes);
		GoogleBidRequest brx = new GoogleBidRequest(bis);


	}
}

package test.java;

import com.jacamars.dsp.rtb.bidder.ZPublisher;
import com.jacamars.dsp.rtb.commands.AddSSP;
import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.commands.DeleteSSP;
import com.jacamars.dsp.rtb.commands.ListSSP;
import com.jacamars.dsp.rtb.common.HttpPostGet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

public class TestWebMetaSSP {

	@BeforeClass
	public static void setup() {
		try {
			Config.setup("Campaigns/metassp.json");
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
	public void testWeblistSSP() throws Exception  {
		ListSSP s = new ListSSP("");
		HttpPostGet http = new HttpPostGet();
		String data = s.toString();
		String returns = http.sendPost("http://localhost:8080/metassp",data);
		System.out.println("<--------\n" + returns);
	}

	@Test
	public void tesWebDeleteSSP() throws Exception  {
		DeleteSSP s = new DeleteSSP("","index");
		System.out.println("-------->\n" + s);
		HttpPostGet http = new HttpPostGet();
		String data = s.toString();
		String returns = http.sendPost("http://localhost:8080/metassp",data);
		System.out.println("<--------\n" + returns);
	}

	@Test
	public void testWebAddSSP() throws Exception {
		String content = new String(Files.readAllBytes(Paths.get("testvirtualdsp.json")), StandardCharsets.UTF_8);
		AddSSP add = new AddSSP("","index",content);

		System.out.println("-------->\n" + add);
		HttpPostGet http = new HttpPostGet();
		String data = add.toString();
		String returns = http.sendPost("http://localhost:8080/metassp",data);
		System.out.println("<--------\n" + returns);
	}
}

package test.java;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.bidder.ZPublisher;
import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.blocks.ProportionalRandomCollection;
import com.jacamars.dsp.rtb.blocks.WeightedSelector;
import com.jacamars.dsp.rtb.commands.SetWeights;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.exchanges.google.GoogleBidRequest;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.tools.DbTools;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * A class for testing that the bid has the right parameters
 * 
 * @author Ben M. Faul
 *
 */
public class TestRotating {
	static Controller c;
	public static String test = "";

	static BidResponse response;
	static CountDownLatch latch;

	@BeforeClass
	public static void testSetup() {
		try {
			Config.setup();
			System.out.println("******************  Test Rotating");

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	@AfterClass
	public static void testCleanup() {
		Config.teardown();
	}

    @Test
    public void testStrategyRandom() throws Exception {
        HttpPostGet http = new HttpPostGet();
        String bid = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/rotating.txt")))).toString();
        Multiset<String> mm =  HashMultiset.create();

        try {
            String s = null;
            int i = 0;
            double limit = 100;
            double isNull = 0;
            for (i=0;i<limit;i++) {
                try {
                    s = http.sendPost("http://" + Config.testHost + "/rtb/bids/c1x", bid, 100000, 100000);
                } catch (Exception error) {
                    fail("Can't connect to test host: " + Config.testHost);
                }
                if (s != null) {
                    assertNotNull(s);
                    String crid = extractCrid(s);
                    mm.add(crid);
                } else
                    isNull++;
            }
            //int count = mm.count("c1");
           // assertNotEquals(count,10);
            if (isNull > 5) {
                fail("Failed to reach 100%, only got " + (limit-isNull/limit));
            }
            System.out.println("C1 = " + mm.count("c1"));
            System.out.println("C2 = " + mm.count("c2"));
            System.out.println("C3 = " + mm.count("c3"));
            int c1 = mm.count("c1");
            int c2 = mm.count("c2");
            int c3 = mm.count("c3");

            assertTrue(c1*3 < c3);
            assertTrue(c2*3 < c3);

            System.out.println(mm);

            SetWeights x = new SetWeights("*","ROTATING","c1=80,c2=10,c3=10");
            x.from="crosstalk-junit";
            ZPublisher commands = new ZPublisher("tcp://localhost:6000&commands");
            Thread.sleep(1000);
            commands.add(x);
            Thread.sleep(1000);

            isNull = 0;
            mm.clear();
            for (i=0;i<limit;i++) {
                try {
                    s = http.sendPost("http://" + Config.testHost + "/rtb/bids/c1x", bid, 100000, 100000);
                } catch (Exception error) {
                    fail("Can't connect to test host: " + Config.testHost);
                }
                if (s != null) {
                    assertNotNull(s);
                    String crid = extractCrid(s);
                    mm.add(crid);
                } else
                    isNull++;
            }
            if (isNull > 5) {
                fail("Failed to reach 100%, only got " + (limit-isNull/limit));
            }

            System.out.println("C1 = " + mm.count("c1"));
            System.out.println("C2 = " + mm.count("c2"));
            System.out.println("C3 = " + mm.count("c3"));
            c1 = mm.count("c1");
            c2 = mm.count("c2");
            c3 = mm.count("c3");

            assertTrue(c3*3 < c1);
            assertTrue(c2*3 < c1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());

        }
    }

    // @Test
    void testStrategyFixed() throws Exception {
        HttpPostGet http = new HttpPostGet();
        String bid = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/rotating.txt")))).toString();
        Multiset<String> mm =  HashMultiset.create();

        LookingGlass.remove("apples");

        try {
            String s = null;
            for (int i=0;i<10;i++) {
                try {
                    s = http.sendPost("http://" + Config.testHost + "/rtb/bids/c1x", bid, 100000, 100000);
                } catch (Exception error) {
                    fail("Can't connect to test host: " + Config.testHost);
                }
                assertNotNull(s);
                String crid = extractCrid(s);
                mm.add(crid);
            }
            int count = mm.count("c1");
            assertNotEquals(count,10);
            System.out.println("XX = " + count);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());

        }
    }

   // @Test
    public void testStrategyProportional() throws Exception {
        HttpPostGet http = new HttpPostGet();
        String bid = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/rotating.txt")))).toString();
        Multiset<String> mm =  HashMultiset.create();

        ProportionalRandomCollection pr = new ProportionalRandomCollection("apples");
        pr.addEntry("ASFFJKL","c3",10000);
        pr.addEntry("c2",500);
        pr.addEntry("c1",100);
        pr.commit();

        try {
            String s = null;
            for (int i=0;i<100;i++) {
                try {
                    s = http.sendPost("http://" + Config.testHost + "/rtb/bids/c1x", bid, 100000, 100000);
                } catch (Exception error) {
                    fail("Can't connect to test host: " + Config.testHost);
                }
                assertNotNull(s);
                String crid = extractCrid(s);
                mm.add(crid);
            }
            //int count = mm.count("c1");
            //assertNotEquals(count,10);
            System.out.println("-------------->"+mm);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());

        }
    }


    static String extractCrid(String s) throws Exception {

        Map m = null;
        m = DbTools.mapper.readValue(s, Map.class);
        List list = (List) m.get("seatbid");
        m = (Map) list.get(0);
        list = (List) m.get("bid");
        m = (Map) list.get(0);
        return (String) m.get("crid");

    }
}

package test.java;

import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.exchanges.openx.OpenX;
import com.jacamars.dsp.rtb.pojo.*;
import com.jacamars.dsp.rtb.tools.DbTools;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by ben on 7/3/17.
 */
public class TestOpenx {

    String price = null;
    String adId = null;
    String creativeId = null;

    @BeforeClass
    public static void setup() {
        try {
            Config.setup();
            System.out.println("******************  TestOpenx");
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
    public void testFormat() throws Exception {
        /* BidRequest br = new BidRequest("./SampleBids/nexageFmt.txt");
        Impression imp = br.getImpression(0);
        assertNotNull(imp);
        assertNotNull(imp.format);
        assertTrue(imp.format.size()==2);
        Format f = imp.format.get(0);
        assertNotNull(f);
        f = imp.format.get(1);
        assertNotNull(f); */

        HttpPostGet http = new HttpPostGet();
        String s = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexageFmt.txt")))).toString();

        try {
            s = http.sendPost("http://" + Config.testHost + "/rtb/bids/openx", s, 300000000, 300000000);
            assertNotNull(s);
            assertTrue(http.getResponseCode()==200);
            System.out.println(s);
            Map m  = DbTools.mapper.readValue(s, Map.class);
            List list = (List)m.get("seatbid");
            assertNotNull(list);
            m = (Map)list.get(0);
            assertNotNull(m);
            list = (List)m.get("bid");
            assertNotNull(list);

            System.out.println(list);

        } catch (Exception error) {
            fail("Can't connect to test host: " + Config.testHost);
        }


    }
    @Test

    public void testPiggyBackedWins() throws Exception {

        HttpPostGet http = new HttpPostGet();
        final CountDownLatch latch = new CountDownLatch(1);
        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic("kafka://[localhost:9092]&topic=wins");
        channel.subscribe("wins");

        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<WinObject>() {
            @Override
            public void onMessage(String channel, WinObject win) {
                price = win.price;
                adId = win.adId;
                creativeId = win.cridId;
                latch.countDown();
            }
        });

        BidRequest.setUsesMultibid(OpenX.OPENX,false);

        String pixel = "http://localhost:8080/pixel/domain=chive.com/bid_type=APP/exchange=openx/ad_id=thead/creative_id=thecreative/bid_id=123456/price=AAABXYr0lqIfSYmAThfNj9EDThjlrixQwBx34Q";
        http.sendPost(pixel, "",300000,300000);
        latch.await(5, TimeUnit.SECONDS);

        List<Map> maps = BidRequest.getExchangeCounts();
        Map x = (Map)maps.get(0);

        System.out.println("=================>" + x + " price: " + price);
        long test = (Long)x.get("wins");

        assertNotNull(price);
        assertNotNull(creativeId);
    }

    @Test
    public void testBid() throws Exception {

        BidRequest.setUsesMultibid(OpenX.OPENX,false);

        HttpPostGet http = new HttpPostGet();
        String s = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexage.txt")))).toString();

        s = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexage.txt")))).toString();
        try {
            s = http.sendPost("http://" + Config.testHost + "/rtb/bids/openx", s);

            System.out.println(s);
        } catch (Exception error) {
            fail("Can't connect to test host: " + Config.testHost);
        }
    }

    @Test
    public void testMultiBid() throws Exception {

        BidRequest.setUsesMultibid(OpenX.OPENX,true);

        HttpPostGet http = new HttpPostGet();
        String s = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexage.txt")))).toString();

        s = Charset.defaultCharset()
                .decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("./SampleBids/nexage.txt")))).toString();
        try {
            s = http.sendPost("http://" + Config.testHost + "/rtb/bids/openx", s, 3000000, 3000000);
            assertNotNull(s);
            assertTrue(http.getResponseCode()==200);
            System.out.println(s);
            Map m  = DbTools.mapper.readValue(s, Map.class);
            List list = (List)m.get("seatbid");
            assertNotNull(list);
            m = (Map)list.get(0);
            assertNotNull(m);
            list = (List)m.get("bid");
            assertNotNull(list);
            assertTrue(list.size()>1);

        } catch (Exception error) {
            fail("Can't connect to test host: " + Config.testHost);
        }
    }
}

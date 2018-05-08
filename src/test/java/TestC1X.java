package test.java;

import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.WinObject;

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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by ben on 7/3/17.
 */
public class TestC1X {

    String price = null;
    String adId = null;
    String creativeId = null;
    String bidType = null;
    String domain = null;

    @BeforeClass
    public static void setup() {
        try {
            Config.setup();
            System.out.println("******************  TestC1X");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void stop() {
        Config.teardown();
    }


    //@Test
    public void testPiggyBackedWins() throws Exception {

        HttpPostGet http = new HttpPostGet();
        final CountDownLatch latch = new CountDownLatch(1);
        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic("tcp://*:5572&wins");
        channel.subscribe("wins");

        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<WinObject>() {
            @Override
            public void onMessage(String channel, WinObject win) {
                price = win.price;
                adId = win.adId;
                creativeId = win.cridId;
                bidType = win.bidtype;
                domain = win.domain;
                latch.countDown();
            }
        });


        //String pixel = "http://localhost:8080/pixel/exchange=c1x/ad_id=thead/creative_id=thecreative/bid_id=123456/price=0.23";
        String pixel = "http://localhost:8080/pixel/exchange=c1x/ad_id=1442/creative_id=2296/price=.23/bid_id=FMT2-BidGovernor-Test_13/site_domain=junck.com/bid_type=SITE/app_name=BidderTestMobileWEB";
        http.sendPost(pixel, "",300000,300000);
        latch.await(5, TimeUnit.SECONDS);

        List<Map> maps = BidRequest.getExchangeCounts();
        Map x = (Map)maps.get(0);

        System.out.println("=================>" + x);
        long test = (Long)x.get("wins");

        assertTrue(price.equals("0.23"));
        assertTrue(creativeId .equals("2296"));
        assertTrue(adId.equals("thead"));
        assertTrue(bidType.equals("SITE"));
        assertTrue(domain.equals("junkck.com"));
    }

}

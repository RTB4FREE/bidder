package test.java;

import com.jacamars.dsp.rtb.bidder.ZPublisher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * A class for testing that the bid has the right parameters
 * 
 * @author Ben M. Faul
 *
 */
public class TestFileSubscriber {


	/**
	 * Test a valid bid response.
	 * 
	 * @throws Exception
	 *             on networking errors.
	 */
	@Test
	public void testPublisherAndSubscriber() throws Exception {

        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic("file:///media/twoterra/samples/request-2017-03-22-18:13&whence=bof");
        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<String>() {
            @Override
            public void onMessage(String channel, String data) {
                System.out.println("<<<<<<<<<<<<<<<<<" + data);
             //   latch.countDown();
            }
        });


        Thread.sleep(15000);
	}
}

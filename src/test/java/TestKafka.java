package test.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.ZPublisher;
import com.jacamars.dsp.rtb.commands.BasicCommand;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.jmq.EventIF;
import com.jacamars.dsp.rtb.jmq.MSubscriber;
import com.jacamars.dsp.rtb.jmq.SubscriberIF;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

/**
 * A class for testing that the bid has the right parameters
 * 
 * @author Ben M. Faul
 *
 */
public class TestKafka  {


	/**
	 * Test a valid bid response.
	 * 
	 * @throws Exception
	 *             on networking errors.
	 */
	@Test
	public void testPublisherAndSubscriber() throws Exception {
		String address = "kafka://[localhost:9092]&topic=junk";

		ZPublisher z = new ZPublisher(address);
        CountDownLatch latch = new CountDownLatch(2);

        List list = new ArrayList();

        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic("kafka://[localhost:9092]&topic=junk");
        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<String>() {
            @Override
            public void onMessage(String channel, String data) {
                System.out.println("<<<<<<<<<<<<<<<<<" + data);
                list.add(data);
             //   latch.countDown();
            }
        });

        com.jacamars.dsp.rtb.jmq.RTopic channel2= new com.jacamars.dsp.rtb.jmq.RTopic("kafka://[localhost:9092]&topic=junk");
        channel2.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<String>() {
            @Override
            public void onMessage(String channel, String data) {
                System.out.println("================" + data);
                list.add(data);

            }
        });


        z.add("Hello world");
        z.add("Another Hello world");

        Thread.sleep(15000);
	}
}

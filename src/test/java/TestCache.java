package test.java;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.redisson.RedissonClient;
import com.jacamars.dsp.rtb.services.Zerospike;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.io.File;

import static org.junit.Assert.*;

;


/**
 * A class to test all aspects of the win processing.
 *
 * @author Ben M. Faul
 */
public class TestCache {
    /**
     * Setup the RTB server BindPublisherfor the test
     */
    static Zerospike service;
    static RedissonClient client1, client2;
    static int pub = 6001;
    static int sub = 6000;
    static int listen = 6002;
    static String fileName = "test.db";
    static boolean trace = false;
    static String spub, ssub;

    @BeforeClass
    public static void setup() {
        try {

            File f = new File(fileName);
            f.delete();

            service = new Zerospike(6000, 6001, 6002, "test.db", "http://[localhost:9092]", false, 1);

            client1 = new RedissonClient();
            client1.setSharedObject("localhost",6000,6001,listen);


            client2 = new RedissonClient();
            client2.setSharedObject("localhost",6000,6001,listen);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Shut the RTB server down.
     */
    @AfterClass
    public static void testCleanup() {
       try {

       } catch (Exception error) {

       }
    }

    /**
     * Test a valid bid response with no bid, the campaign doesn't match width or height of the bid request
     *
     * @throws Exception on network errors.
     */
    @Test
    public void testSetAndGet() throws Exception {
        client1.set("testing","hello",2);
        String test  = (String)client1.get("testing");
        assertTrue(test.equals("hello"));
        assertNull(client1.get("nothere"));

        Thread.sleep(100);
        test = (String)client2.get("testing");
        assertNotNull(test);
        assertTrue(test.equals("hello"));

        Thread.sleep(2000);

        test = (String)client1.get("testing");
        assertNull(test);

        test = (String)client2.get("testing");
        assertNull(test);

    }

    @Test
    public void testDelete() throws Exception {
        client1.set("testing","hello",10000);
        String test  = (String)client1.get("testing");

        Thread.sleep(100);
        test = (String)client2.get("testing");
        assertNotNull(test);
        assertTrue(test.equals("hello"));

        client1.del("testing");

        test = (String)client1.get("testing");
        assertNull(test);

        Thread.sleep(200);
        test = (String)client2.get("testing");
        assertNull(test);
    }

    @Test
    public void testIncrement() throws Exception {
        client1.increment("incr",3,null);
        Long test  = (Long)client1.get("incr");
        assertTrue(test==1);

        Thread.sleep(100);
        test = (Long)client2.get("incr");
        assertTrue(test==1);

        test = client1.incr("incr");
        assertTrue(test==2);

       Thread.sleep(100);
        test = client2.incr("incr");
        assertTrue(test==3);

        Thread.sleep(100);
        test = (Long)client1.get("incr");
        assertTrue(test==3);

        Thread.sleep(6000);
        Object x = client1.get("incr");
        assertNull(x);

        x = client2.get("incr");
        assertNull(x);

    }


    @Test
    public void testStopAndRestartService() throws Exception  {

        service.clear();

        client1.increment("junk",5,null);


        while(client1.get("junk") != null) {
            Thread.sleep(1000);
        }

        Thread.sleep(2000);

        assertNull(client2.get("junk"));

        assertTrue(service.getSize()==0);
    }

    @Test
    public void testNetworkLoad() throws Exception {

        for (int i=0;i<1000;i++) {
            client1.set("test"+i,i,600);
        }

        while(service.getSize()<1000) {
            Thread.sleep(1000);
//            System.out.println("Count: " + service.getSize());
        }


        RedissonClient client3 = new RedissonClient();
        client3.setSharedObject("localhost",6000,6001,listen);
        while(client3.loadComplete()==false)
            Thread.sleep(1000);
    }


}

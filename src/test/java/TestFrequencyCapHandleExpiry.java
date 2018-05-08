package test.java;


import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.FrequencyCap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class TestFrequencyCapHandleExpiry {

    private static String capKey = "FrequencyCapTestKey";

    @BeforeClass
    public static void beforeAll() throws Exception {
        Config.setup();
    }

    @AfterClass
    public static void stop() {
        Config.teardown();
    }

    @Test
    public void TestHandleExpirtySimple() throws Exception {
        Controller.bidCachePool.del(capKey);
        FrequencyCap.handleExpiry(capKey, 5, null);
        TimeUnit.SECONDS.sleep(6);
        assertEquals("Record has invalid value.", -1, FrequencyCap.getCapValue(capKey));
    }
    @Test

    public void testHandleExpiryForOneWinNotification() throws Exception {
        Controller.bidCachePool.del(capKey);
        FrequencyCap.handleExpiry(capKey, 5, null);
        TimeUnit.SECONDS.sleep(4);
        assertEquals("Record has invalid value.", 1, FrequencyCap.getCapValue(capKey));
        TimeUnit.SECONDS.sleep(2);

        String str = (String)Controller.bidCachePool.get(capKey);
        assertNull("Record should have expired.", str);

    }

    @Test
    public void testHandleExpiryForTwoWinNotification() throws Exception {
        Controller.bidCachePool.del(capKey);
        FrequencyCap.handleExpiry(capKey, 5, null);
        TimeUnit.SECONDS.sleep(4);
        assertEquals("Record has invalid value.", 1, FrequencyCap.getCapValue(capKey));
        FrequencyCap.handleExpiry(capKey, 5, null);
        assertEquals("Record has invalid value.", 2, FrequencyCap.getCapValue(capKey));
        TimeUnit.SECONDS.sleep(2);

        String str = (String)Controller.bidCachePool.get(capKey);
        assertNull("Record should have expired.", str);
    }

    @Test
    public void testHandleExpiryForThreeWinNotification() throws Exception {
        Controller.bidCachePool.del(capKey);
        FrequencyCap.handleExpiry(capKey, 5, null);
        TimeUnit.SECONDS.sleep(1);
        assertEquals("Record has invalid value.", 1, FrequencyCap.getCapValue(capKey));
        FrequencyCap.handleExpiry(capKey, 5, null);
        assertEquals("Record has invalid value.", 2, FrequencyCap.getCapValue(capKey));
        TimeUnit.SECONDS.sleep(2);
        FrequencyCap.handleExpiry(capKey, 5, null);
        assertEquals("Record has invalid value.", 3, FrequencyCap.getCapValue(capKey));
        TimeUnit.SECONDS.sleep(3);
        assertEquals("Record should have expired.", -1, FrequencyCap.getCapValue(capKey));
    }

}

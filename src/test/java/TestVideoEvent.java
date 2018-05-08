package test.java;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jacamars.dsp.rtb.commands.VideoEventLog;
import com.jacamars.dsp.rtb.tools.DbTools;

import static org.junit.Assert.assertTrue;

/**
 * Test video events and their logging
 * Created by Ben M. Faul on 7/3/17.
 */
public class TestVideoEvent {



    @BeforeClass
    public static void setup() {
        try {
            Config.setup();
            System.out.println("******************  TestViedoEvent");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Test
    public void testPayloadParse() throws Exception {
        VideoEventLog v = new VideoEventLog("event=creativeView/adid=23433/crid=66666/imp=210993398098301");
        assertTrue(v.adid.equals("23433"));
        assertTrue(v.crid.equals("66666"));
        assertTrue(v.impid.equals("210993398098301"));
        assertTrue(v.vastevent.equals("creativeView"));

        System.out.println(DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(v));
    }

}

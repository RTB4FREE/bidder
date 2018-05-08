package test.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.jmq.RedisProto;
import com.jacamars.dsp.rtb.jmq.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ben on 12/30/17.
 */
public class TestSerialize {


    public static void main(String args[]) throws Exception {
        AtomicLong lv = new AtomicLong(100);
    //    Map lv = new HashMap();
     //   lv.put("a","ben");
     //   lv.put("b",100);
        ObjectMapper m = new ObjectMapper();

        long time = System.currentTimeMillis();
        String str = null;
        for (int i=0;i<1000000;i++) {
             str =  Tools.serialize(m, lv);
        }
        time = System.currentTimeMillis() - time;
        System.out.println(time);
        System.out.println(str);

        time = System.currentTimeMillis();
        for (int i=0;i<1000000;i++) {
             str = Tools.serialize2(m,lv);
        }
        time = System.currentTimeMillis() - time;
        System.out.println(time);
        System.out.println(str);
    }
}

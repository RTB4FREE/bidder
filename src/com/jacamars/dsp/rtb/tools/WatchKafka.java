package com.jacamars.dsp.rtb.tools;

import com.jacamars.dsp.rtb.bidder.ZPublisher;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.jmq.EventIF;
import com.jacamars.dsp.rtb.jmq.MSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ben on 12/23/17.
 */
public class WatchKafka {
    int count = 0;

    public static void main(String [] args) throws Exception {

        String address = "kafka://[$BROKERLIST]&topic=requests&groupid=reader";

        if (args.length > 0)
            address = args[0];

        address = Configuration.substitute(address);

        System.out.println("WatchKafka starting: " + address);
        new WatchKafka(address);
    }

    public WatchKafka(String address)throws Exception  {
        com.jacamars.dsp.rtb.jmq.RTopic channel = new com.jacamars.dsp.rtb.jmq.RTopic(address);
        channel.addListener(new com.jacamars.dsp.rtb.jmq.MessageListener<Object>() {
            @Override
            public void onMessage(String channel, Object data) {
                System.out.println(channel + " [" + count + "] = " + data + "\n");
                count++;
            }
        });

    }
}

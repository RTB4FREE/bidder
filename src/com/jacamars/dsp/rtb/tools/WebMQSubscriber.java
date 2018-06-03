package com.jacamars.dsp.rtb.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.jmq.MessageListener;
import com.jacamars.dsp.rtb.jmq.RTopic;

public class WebMQSubscriber {

    ObjectMapper mapper = new ObjectMapper();
    static volatile int count  = 1;
    RTopic chan;
    volatile boolean running = true;

    public WebMQSubscriber(HttpServletResponse response, String port, String topics) throws Exception {
        // Prepare our context and subscriber

        String address = "kafka://[$BROKERLIST]&topic=logs&groupid=webreader"+count;
        address = Configuration.substitute(address);
        count++;
        chan = new com.jacamars.dsp.rtb.jmq.RTopic(address);
        chan.addListener(new MessageListener<Object>() {
            @Override
            public void onMessage(String channel, Object data) {
                try {
                    String contents = mapper.writeValueAsString(data);
                    response.getWriter().println(contents);
                    response.flushBuffer();
                } catch (Exception e) {
                    // The other side closed, we are outta here!
                    chan.shutdown();
                    running = false;
                    return;
                }
            }
        });
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;

            }
        }
    }
}

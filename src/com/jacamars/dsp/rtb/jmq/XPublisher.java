package com.jacamars.dsp.rtb.jmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.zeromq.ZMQ;

public class XPublisher  extends Publisher {

    public XPublisher(String binding, String topicName) throws Exception {
        super();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        context = ZMQ.context(1);
        publisher = context.socket(ZMQ.XPUB);
        publisher.bind(binding);

        Thread.sleep(100);
        //System.out.println("Starting Publisher..");
        publisher.setIdentity("B".getBytes());
        publisher.setLinger(5000);
        publisher.setHWM(0);

        this.topicName = topicName;
    }

    public String recvStr() throws Exception {
       return publisher.recvStr();
    }


}

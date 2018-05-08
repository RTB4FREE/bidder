package com.jacamars.dsp.rtb.redisson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.ZPublisher;
import com.jacamars.dsp.rtb.jmq.EventIF;
import com.jacamars.dsp.rtb.jmq.MSubscriber;
import com.jacamars.dsp.rtb.jmq.Tools;
import com.jacamars.dsp.rtb.shared.SharedObjectIF;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/** A class that sends messages to a swarm - used to make shared objects
 * Created by Ben M. Faul on 10/4/17.
 */
public class SharedRedissonObject implements EventIF {

    /** jackson object mapper */
    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setDefaultPropertyInclusion(
                JsonInclude.Value.construct(JsonInclude.Include.ALWAYS, JsonInclude.Include.NON_NULL));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    /** The subscriber pool */
    MSubscriber pool;

    /** The handler to process the message */
    SharedObjectIF handler;

    /** Sends messages out to the rest of the swarm on an object update */
    ZPublisher publisher;

    public void transmit(String id, List list) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "List";
        r.type = "db";
        r.op = "add";
        r.value = list;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }

    public void transmit(String id, Map m) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "Map";
        r.type = "db";
        r.op = "add";
        r.value = m;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }

    public void transmit(String id, Map m, long timeout) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "Map";
        r.type = "cache";
        r.op = "add";
        r.value = m;
        r.timeout = timeout;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }

    public void transmit(String id, Set s) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "Set";
        r.type = "db";
        r.op = "add";
        r.value = s;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }

    public void transmit(String id, String s) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "String";
        r.type = "cache";
        r.op = "add";
        r.value = s;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }

    public void transmit(String id, String s, long timeout) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "String";
        r.type = "cache";
        r.op = "add";
        r.timeout = timeout;
        r.value = s;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }

    public void transmit(String id, AtomicLong v, long timeout) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        Long value = v.get();
        r.key = id;
        r.type = "AtomicLong";
        r.type = "cache";
        r.op = "add";
        r.timeout = timeout;
        r.value = value;
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }



    public void remove(String id) {
        RedissonObjectMessage r = new RedissonObjectMessage();
        r.key = id;
        r.type = "cache";
        r.op = "del";
        String str = Tools.serialize(mapper,r);
        publisher.add(str);
    }



    /**
     * Handle a message from the swarm, send it to the local object handler.
     * @param id String. The channel id.
     * @param msg String. The JSON marshalled message received.
     */
    @Override
    public void handleMessage(String id, String msg) {
        try {
            Object[] obj = Tools.deSerialize(mapper, msg);
            handler.handleMessage(obj[1]);
        } catch (Exception error) {
            System.out.println(error);
        }
    }

    /**
     * Got a 0MQ shutdown.
     */
    @Override
    public void shutdown() {
        pool.shutdown();
    }
}


package com.jacamars.dsp.rtb.jmq;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RTopic implements EventIF {
	
	Socket publisher = null;
	SubscriberIF subscriber = null;
	Context context = null;
	boolean running = false;
	String topicName = null;
	public Map<String,MessageListener> m = new HashMap();
	ObjectMapper mapper = new ObjectMapper();
	
	
	public RTopic(String address) throws Exception {
		context = ZMQ.context(1);
		
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		if (address.contains("kafka")==false && address.contains("&")) {
			String [] parts = address.split("&");
			subscriber = new Subscriber(this,parts[0]);
			topicName = parts[1];
			subscriber.subscribe(parts[1]);
		} else
			subscriber = new Subscriber(this,address);
	}

	public void subscribe(String str) {
		subscriber.subscribe(str);
	}
	
	public void close() {
		shutdown();
	}
	
	public void shutdown() {
		if (subscriber != null) {
	        subscriber.close ();
	        subscriber = null;
		}
		if (publisher != null) {
			publisher.close();
			publisher = null;
		}
        context.term ();
		if (subscriber != null)
        	subscriber.shutdown();
	}
	
	public void publishAsync(String topic, Object message) {
		
	}
	
	public void publish(String topic, Object message) {

	}
	
	public void addListener(MessageListener<?> z) {
		Type type = z.getClass().getGenericInterfaces()[0];
		String theType = null;

		if (type instanceof ParameterizedType) {
		    Type actualType = ((ParameterizedType) type).getActualTypeArguments()[0];
		    theType = actualType.getTypeName();
		}
		m.put(theType,z);

	}
	
	/**
	 * Handle messages from msubscriber
	 */

	@Override
	public void handleMessage(String id, String msg) {
		try {
			if (msg.contains("rtb.jmq.Ping"))
				return;

			Object[] x = Tools.deSerialize(mapper, msg);
			String name = (String) x[0];
			Object o = m.get(name);
			if (o != null) {
				MessageListener z = (MessageListener) o;
				z.onMessage(id, x[1]);
			} else {
				//MessageListener z = m.get("com.xrtb.commands.BasicCommand");
				Set set = m.keySet();
				if (set.size()==0) {
				    MessageListener z = m.get("java.lang.String");
				    if (z == null) {
				        System.out.println("Fudge!");
				        return;
                    }
                    z.onMessage(id,x[1]);
                }
				Iterator<String> it = set.iterator();
				if (it.hasNext()) {
                    name = it.next();
                    MessageListener z = m.get(name);
                    z.onMessage(id, x[1]);
                } else {
				    //System.out.println("No listener for: " + name);
                    if (name.contains("rtb.commands")) {
                        o = m.get("com.jacamar.dsp.rtb.commands.BasicCommand");
                        it = set.iterator();
                        if (it.hasNext()) {
                            name = it.next();
                            MessageListener z = m.get(name);
                            z.onMessage(id, x[1]);
                        } else {
                            System.out.println("Nothing to match");
                        }
                    } else {
                        MessageListener z = m.get("java.lang.String");
                        if (z == null) {
                            System.out.println("Fudge!");
                            return;
                        }
                        z.onMessage(id,x[1]);
                    }
				}
			}
		} catch (Exception error) {
		    error.printStackTrace();
			System.out.println("Error in topic: " + topicName + " on id " + id + " msg: " + msg + ", error: " + error);
			shutdown();
		}
	}
}

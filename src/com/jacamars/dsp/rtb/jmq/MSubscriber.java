package com.jacamars.dsp.rtb.jmq;

import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * A class that implements an event Loop. It sets up mulltiple point-point
 * workers on separate channels. Then, it also publishes whatever it receives
 * from those point-point workers to its own publisher port.
 * 
 * @author ben
 *
 */

public class MSubscriber implements Runnable, EventIF, SubscriberIF {

	Thread me = null;
	Context context = null;
	volatile List<Subscriber> workers = new ArrayList<Subscriber>();
	public EventIF handler = null;
	String topic;

	public static String logDir;

    /**
     * Subscribe to subscriber, could be kafka or 0mq instance.
     * @param handler EventIF: The event handler when a messaage is received.
     * @param address String. The kafka configuration strung.
     * @param topic String. The topic subscribed to.
     * @throws Exception on network errors.
     */
	public MSubscriber(EventIF handler, String address, String topic) throws Exception {

		this.topic = topic;
		this.handler = handler;
		if (address.contains("kafka://")==false) {
			Subscriber w = new Subscriber(this,address);
			w.subscribe(topic);
			workers.add(w);
			return;
		} else {
			KafkaConfig c = new KafkaConfig(address);
			Subscriber w = new Subscriber(this, c.getProperties(), c.getTopic());
			workers.add(w);
		}

		me = new Thread(this);
		me.start();

	}
	
	public void subscribe(String topic) {
		for (Subscriber w : workers) {
			w.subscribe(topic);
		}
	}

	@Override
	public void run() {
		try {
			while (!me.isInterrupted()) {
				Thread.sleep(1);
			}

		} catch (Exception e) {
			// System.out.println("Interrupt!");
			e.printStackTrace();
		}
	}

	public void handleMessage(String key, String message) {
		if (key.equals(topic)==false)
			return;

		if (message != null && message.contains("rtb.jmq.Ping\"}"))
			return;

		if (handler != null)
			handler.handleMessage(key, message);
	}

	public void close() {
		shutdown();
	}

	public void shutdown() {

		for (Subscriber w : workers) {
			w.shutdown();
		}

		me.interrupt();
		if (handler != null)
			handler.shutdown();
	}
}
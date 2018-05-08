package com.jacamars.dsp.rtb.jmq;


import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import java.io.File;
import java.util.*;

/**
 * Subscriber. This CONNECTS to a publisher. It is S (many) -> P (single)
 * 
 * @author ben
 *
 */
public class Subscriber extends TailerListenerAdapter implements Runnable, SubscriberIF, ConsumerRebalanceListener {

	Context context = JMQContext.getInstance();
	EventIF handler;
	Socket subscriber;
	Thread me;

	KafkaConsumer<String, String> consumer;
	String topic;
	
	PipeTailer piper;

    Tailer tailer;
    int delay = 100;
    boolean reopen = false;
    boolean end = false;
    int bufsize = 4096;

	public static void main(String... args) {
		// Prepare our context and subscriber
	}

	/**
	 * A subscriber using Kafka
	 * @param handler EventIF, the classes that will get the messages.
	 * @param props Properties. The kafka properties
	 * @throws Exception on Errors setting up with Kafka
	 */
	public Subscriber(EventIF handler, Properties props, String topic) throws Exception {
		this.handler = handler;
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList(topic), this);
		this.topic = topic;

		me = new Thread(this);
		me.start();
	}

	public Subscriber(EventIF handler, String address) throws Exception {
		this.handler = handler;

		if (address.startsWith("file://")) {
		    processLogTailer(address);
		    return;
        }
		
        if (address.startsWith("pipe://")) {
		    processPipeTailer(address);
		    return;
        }

		if (address.startsWith("kafka")==false) {
			subscriber = context.socket(ZMQ.SUB);
			subscriber.connect(address);
			subscriber.setHWM(100000);
		} else {
			if (address.contains("groupid")==false) {
                address += "&groupid=a";
            }
			KafkaConfig c = new KafkaConfig(address);
			consumer = new KafkaConsumer<>(c.getProperties());
			consumer.subscribe(Collections.singletonList(c.getTopic()), this);

			this.topic = c.topic;
		}
		me = new Thread(this);
		me.start();
	}

	public void subscribe(String topic) {
		if (consumer != null) {
		//	consumer.subscribe(Collections.singletonList(topic), this);
		} else
			subscriber.subscribe(topic.getBytes());
	}

    /**
     * Setup a file tailer.
     * @param buf String. The Address buffer.
     * @throws Exception on setup exception.
     */
	void processLogTailer(String buf) throws Exception {
		   // String buf = "file://log.txt&whence=eof&delay=500&reopen=false&bufsize=4096";
        buf = buf.substring(7);
        String parts [] = buf.split("&");
        String fileName = parts[0];
        for (int i=1;i<parts.length;i++) {
            String t[] = parts[i].trim().split("=");
            if (t.length != 2) {
                throw new Exception("Not a proper parameter (a=b)");
            }
            t[0] = t[0].trim();
            t[1] = t[1].trim();
            switch(t[0]) {
                case "whence":
                    if (t[1].equalsIgnoreCase("bof"))
                        end = false;
                    else
                        end = true;
                    break;
                case "delay":
                    delay = Integer.parseInt(t[1]);
                    break;
                case "reopen":
                    reopen = Boolean.parseBoolean(t[1]);
                    break;
                case "bufsize":
                    bufsize = Integer.parseInt(t[1]);
                    break;
            }
        }
        topic = fileName;
        tailer = Tailer.create(new File(fileName), this, delay, end, reopen, bufsize  );
	}
	
	 /**
     * Process a named pipe
     * @param fileName String. The name of the pipe.
     * @throws Exception if the file does not exist or is not a file.
     */
	public void processPipeTailer(String fileName) throws Exception {
	    fileName = fileName.substring(7);
	    topic = fileName;
        piper = new PipeTailer(this, fileName);
    }

    /**
     * Handle a file tailer entry.
     * @param line String. The line we got from the file.
     */
    @Override
    public void handle(String line) {
        handler.handleMessage("", line.trim());
    }

    /**
     * Catch fileNotFound - can happen on log rotation
     */
    @Override
    public void fileNotFound() {

    }

    /**
     * Catch file rotated event.
     */
    @Override
    public void fileRotated() {

    }

    /**
     * Handle an error, this probably fatal
     * @param ex
     */
    @Override
    public void handle(Exception ex) {
        ex.printStackTrace();
    }


    /**
     * Handles 0MQ and kafka. File subscriber handled by the tailer.
     */
	@Override
	public void run() {

		if (consumer != null) {
			while(true) {
				ConsumerRecords<String, String> records = consumer.poll(1000);
				for (ConsumerRecord<String,String> record : records) {
					handler.handleMessage(topic,record.value());
				}

				if (consumer == null)
					return;

				consumer.commitSync();
			}
		}

		while (me.isInterrupted()==false) {
			// Read envelope with address
			String address = subscriber.recvStr();
			// Read message contents
			String contents = subscriber.recvStr();
			handler.handleMessage(address, contents);
		}

        if (subscriber != null)
            subscriber.close();
        if (consumer != null) {
            consumer.close();
            consumer = null;
        }
	}

    /**
     * Signal the thread it is time to shutdown
     */
	public void shutdown() {

	    if (tailer != null) {
	        tailer.stop();
	        return;
        }

        me.interrupt();

	}
	
	public void close() {
		shutdown();
	}

	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> collection) {

	}

	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> collection) {

	}
}

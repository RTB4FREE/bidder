package com.jacamars.dsp.rtb.jmq;

public interface EventIF {

	public void handleMessage(String id, String msg);
	public void shutdown();
}

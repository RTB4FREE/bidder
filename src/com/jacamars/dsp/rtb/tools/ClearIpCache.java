package com.jacamars.dsp.rtb.tools;
import com.jacamars.dsp.rtb.db.Database;

public class ClearIpCache {

	public static void main(String [] args) throws Exception {
		String key = "capped_" + args[1] + args[2];
		System.out.println("key: " + key);
		com.jacamars.dsp.rtb.redisson.RedissonClient redisson = new com.jacamars.dsp.rtb.redisson.RedissonClient();
		redisson.setSharedObject("tcp://localhost:6000","tcp://localhost:6001");
		Database.getInstance(redisson);
		long s = redisson.incr(key);
		System.out.println(s);
	}
}

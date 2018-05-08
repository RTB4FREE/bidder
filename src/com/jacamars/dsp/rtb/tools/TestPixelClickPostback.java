package com.jacamars.dsp.rtb.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jacamars.dsp.rtb.common.HttpPostGet;

public class TestPixelClickPostback {
	public static String host = "http://localhost:8080";
	public static int nPixels = 1000;
	public static int nClicks = 200;
	public static int overlap = 10;

	static int wait = 10;
	
	static List<String> bidids = new ArrayList<String>();
	static List<String> uids = new ArrayList<String>();
	static List<String> clickids = new ArrayList<String>();
	static List<String> clickUids = new ArrayList<String>();
	static List<String> webOverBidIds = new ArrayList<String>();
	static List<String> appOverBidIds = new ArrayList<String>();
	
	static List<String> webOverUids = new ArrayList<String>();
	static List<String> appOverUids = new ArrayList<String>();
	
	static String pixel = "/pixel/bid_id={bid_id}/uid={uid}/ad_id=123/creative_id=456/domain=testdomain/exchange=testexchange";
	static String click = "/redirect/bid_id={bid_id}/uid={uid}/ad_id=123/creative_id=456/domain=testdomain/exchange=testexchange";
	static String webPostback = "/postback/uid={uid}";
	static String appPostback = "/postback/et=appinstall/bid_id={bid_id}";
	
	static HttpPostGet post = new HttpPostGet();
	
	static boolean debug = false;
	
	public static void main(String[] args) throws Exception {
		
		int i = 0;
		while(i < args.length) {
			switch (args[i]) {
			case "-h":
				host = args[i+1];
				i+=2;
				break;
			case "-p":
				nPixels = Integer.parseInt(args[i+1]);
				i+= 2;
				break;
			case "-c":
				nClicks = Integer.parseInt(args[i+1]);
				i+= 2;
				break;
			case "-o":
				overlap = Integer.parseInt(args[i+1]);
				i+=2;
				break;
			case "-d":
				debug = true;
				i++;
				break;
				case "-w":
                wait = Integer.parseInt(args[i+1]);
                i+=2;
			default:
				System.out.println("Huh? " + args[i]);
				return;
			}
			
		}
		
		if (nClicks < overlap*2) {
			System.out.println("Error: Number of clicks must be 2x kthe overlap");
			return;
		}
		
		System.out.println("Pixels: " +  nPixels);
		System.out.println("Clicks: " +  nClicks);
		System.out.println("Overlap: " + overlap);
		
		for(i=0;i<nPixels;i++) {
			bidids.add(UUID.randomUUID().toString());
			uids.add(UUID.randomUUID().toString());
		}
		
		for (i=0;i<nClicks;i++) {
			clickids.add(bidids.get(i));
			clickUids.add(uids.get(i));
		}
		
		
		for (i=0;i<overlap;i++) {
			webOverBidIds.add(clickids.get(i));
			appOverBidIds.add(clickids.get(overlap+i));
			
			webOverUids.add(clickUids.get(i));
			appOverUids.add(clickUids.get(overlap+i));
		}
		
		sendPixels();
		sendClicks();
		sendPostbacks();
	}
	
	public static void sendPixels() throws Exception {
		for (int i=0;i<nPixels;i++) {
			String x = pixel;
			x = x.replace("{bid_id}", bidids.get(i));
			x = x.replace("{uid}", uids.get(i));
			String url = host + "/" + x;
			
			post.sendGet(url,5000,5000);
			if (debug)
				System.out.println(i+ "\t" + url);
			Thread.sleep(wait);
		}
	}
	
	public static void sendClicks() throws Exception {
		for (int i=0;i<nClicks;i++) {
			String x = click;
			x = x.replace("{bid_id}", clickids.get(i));
			x = x.replace("{uid}", clickUids.get(i));
			String url = host + "/" + x;
			
			post.sendGet(url,5000,5000);
			if (debug)
                System.out.println(i + "\t" + url);
			Thread.sleep(wait);
		}
	}
	
	public static void sendPostbacks()  throws Exception {
		for (int i=0;i<overlap;i++) {
			String x = webPostback;
			x = x.replace("{bid_id}", webOverBidIds.get(i));
			x = x.replace("{uid}", webOverUids.get(i));
			String url = host + "/" + x;
			post.sendGet(url,5000,5000);
			if (debug)
                System.out.println(i+ "\t" + url);
			Thread.sleep(wait);
			
		}
		
		for (int i=0;i<overlap;i++) {
			String x = appPostback;
			x = x.replace("{bid_id}", appOverBidIds.get(i));
			x = x.replace("{uid}", appOverUids.get(i));
			String url = host + "/" + x;
			post.sendGet(url,5000,5000);
			if (debug)
                System.out.println(i+ "\t" + url);
			Thread.sleep(wait);
		}
	}
	
}

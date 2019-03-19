package com.jacamars.dsp.rtb.fraud;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.common.URIEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A Singleton class that implements the Forenciq.com anti-fraud bid checking system.
 * @author Ben M. Faul
 *
 */
public enum  AnuraClient implements FraudIF {
	
	ANURACLIENT;

	static final Logger logger = LoggerFactory.getLogger(AnuraClient.class);
	
	static CloseableHttpClient httpclient;
	
	/** Anura round trip time */
	public static AtomicLong forensiqXtime = new AtomicLong(0);
	/** forensiq count */
	public static AtomicLong forensiqCount = new AtomicLong(0);
	
	/** Endpoint of the forensiq api */
	public static String endpoint = "https://direct.anura.io/direct.json?"; //"http://api.forensiq.com/check";
	/** Your Forensiq key */
	public static String key = "yourkeygoeshere";
	/** Default threshhold for non bidding */
	public static int threshhold = 64;
	/** If the forensiq site throws an error or is not available, bid anyway? */
	public boolean bidOnError = false;
	/** connection pool size */
	public static int connections = 100;
	
	static boolean printxtime = false;
	
	/** Http client connection manager */
	static PoolingHttpClientConnectionManager cm;
	
	static volatile BasicHttpContext context = new BasicHttpContext();
	
	/** The precompiled preamble */
	@JsonIgnore
	transient public static String preamble;
	
	/** The object mapper for converting the return from anura */
	@JsonIgnore
	transient ObjectMapper mapper = new ObjectMapper();
	
	public static void main(String [] args) throws Exception {
		AnuraClient q = AnuraClient.build(args[0]);
		FraudLog f =null; 
		printxtime = true;
		
		for (int i=0;i<100;i++) {
			q.bid("",args[1],"","","","");
			if (f != null) {
				System.out.println("FRAUD: " + f.toString());
			}
		}
		
	}
	
	/**
	 * Default constructor
	 */
	public static AnuraClient build() {
		preamble = endpoint  + "instance=" + key + "&";
		setup();
		return ANURACLIENT;
	}
	
	/**
	 * Build the fornsiq client.
	 * @param ck String. Check key that you get from Check key.
	 * @return ForensiqClient. The forensiq client object to use.
	 */
	public static AnuraClient build(String ck) {
		key = ck;
		preamble = endpoint  + "instance=" + key + "&";
		setup();
		return ANURACLIENT;
	}
	
	/**
	 * Get the instance of the Anura client
	 * @return
	 */
	public static AnuraClient getInstance() {
		return ANURACLIENT;
	}
	
	public static void setup() {
		 cm = new PoolingHttpClientConnectionManager();
	     cm.setMaxTotal(connections);
	     cm.setDefaultMaxPerRoute(connections);

	    httpclient = HttpClients.custom().setConnectionManager(cm).build();
	}
	
	public static void reset() {
		cm.closeIdleConnections(1000,TimeUnit.SECONDS);
		cm.closeExpiredConnections();
	}
	/**
	 * Should I bid, or not?
	 * @param rt String. The type, always "display".
	 * @param ip String. The IP address of the user.
	 * @param url String. The URL of the publisher.
	 * @param ua String. The user agent.
	 * @param seller String. The seller's domain.
	 * @param crid String. The creative id
	 * @return boolean. If it returns true, good to bid. Or, false if it fails the confidence test.
	 * @throws Exception on missing rwquired fields - seller and IP.
	 */
	public FraudLog bid(String rt, String ip, String url, String ua, String seller, String crid) throws Exception {
		byte[] bytes = null;
		StringBuilder sb = new StringBuilder(preamble);
		JsonNode rootNode = null;		

		if (httpclient == null) {
			return null;
		}
		
		if (ip == null) {
			if (bidOnError)
				return null;
			else
				throw new Exception("Required field ip is missing");
		}
		
		sb.append("ip=").append(ip);
		
		HttpGet httpget = new HttpGet(sb.toString());
		
		try {

			long xtime = System.currentTimeMillis();
			
			 CloseableHttpResponse response = httpclient.execute(httpget, context);

			 HttpEntity entity = response.getEntity();
             if (entity != null) {
                 bytes = EntityUtils.toByteArray(entity);
             }
             response.close();
			
			 String content = new String(bytes);;
			
			//System.out.println("--->"+content);
			
			// System.err.println("---->" + RTBServer.);
			
			rootNode = mapper.readTree(content);
			String result = rootNode.get("result").asText("error: does not conform");
			
			xtime = System.currentTimeMillis() - xtime;
			
			if (printxtime)
				System.out.println("XTIME: " + xtime);
			
			forensiqXtime.addAndGet(xtime);
			forensiqCount.incrementAndGet();

			if (!result.startsWith("non")) {
				FraudLog m = new FraudLog();
				m.source = "Anura";
				m.ip = ip;
				m.url = url;
				m.ua = ua;
				m.seller = seller;
				m.risk = 100;
				m.xtime = xtime;
				return m;
			}
			
			return null;
		} catch (Exception e) {
			logger.error("{}",e.getMessage());
		} finally {

		}
		
		FraudLog m = new FraudLog();
		m.source = "Forensiq";
		m.ip = ip;
		m.url = url;
		m.ua = ua;
		m.seller = seller;
		m.xtime = 5;
		return m;
	}
	
	public boolean bidOnError() {
		return bidOnError;
	}
}

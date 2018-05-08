package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.tools.DbTools;

/**
 * Create a generic video event log.
 * Created by Ben M. Faul on 2/12/18.
 */
public class PostbackEventLog {

	    /** Debug flag */
	    public boolean debug=false;
	    /** The ad id */
	    public String adid;

	    /** The creative id */
	    public String crid;

	    /** The imptession id */
	    public String impid;

	    /** The event type */
	    public String postbackevent;

	    /** The timestamp of when we saw this event */
	    public long timestamp;

	    /** The original payload from the vent */
	    public String payload;

	    /** The bid id */
	    public String bidid;

	    /** The domain id */
	    public String domain;

	    /** The exchange */
	    public String exchange;

	    /** The type */
	    public String subtype;

	    /** Extension */
	    public String extension;

	    /** The cookie id, if applicable */
	    public String uid;

	    /** The transaction id, if applicable */
	    public String tid;

	    /** The device id, if present */
	    public String deviceid;

	    /**
	     * Constructor for the log messgae
	     * @param payload String. The URI of the event
	     */
	    public PostbackEventLog(String payload) {
	        create(payload);
	    }

	    /**
	     * Empty constructor for use with JACKSON JSON
	     */
	    public PostbackEventLog() {

	    }

	    /**
	     * Populate the log from the URI.
	     * @param payload String. The URI that contains the component pieces.
	     */
	    public void create(String payload) {
	        this.payload = payload;
	        timestamp = System.currentTimeMillis();
	        postbackevent = "undefined";
	        String [] parts = payload.replaceAll("&","/").split("/");;
	        for (int i=0;i<parts.length;i++) {
	            String what = parts[i];
	            String [] t2 = what.split("=");
	            if (t2.length == 2) {
	                t2[0] = t2[0].trim();
	                t2[1] = t2[1].trim();
	                switch(t2[0]) {
	                    case "adid":
	                    case "ad_id":
	                        adid = t2[1];
	                        break;
	                    case "crid":
	                    case "creative_id":
	                        crid = t2[1];
	                        break;
	                    case "imp":
	                    case "impression_id":
	                        impid = t2[1];
	                        break;
	                    case "event":
	                    case "et":
	                        postbackevent = t2[1];
	                        break;
	                    case "bidid":
	                    case "bid_id":
	                        bidid = t2[1];
	                        break;
	                    case "domain":
	                        domain = t2[1];
	                        break;
	                    case "exchange":
	                        exchange = t2[1];
	                        break;
	                    case "subtype":
	                        subtype = t2[1];
	                        break;
	                    case "uid":
	                        uid = t2[1];
	                        break;
	                    case "tid":
	                        tid = t2[1];
	                        break;
	                    case "debug":
	                    case "DEBUG":
	                        debug = Boolean.parseBoolean(t2[1]);
	                        break;
	                    case "extension":
	                    case "ext":
	                        extension = t2[1];
	                        break;
	                    case "deviceid":
	                        deviceid = t2[1];
	                        break;
	                    default:
	                        break;
	                }
	            }
	        }
	    }

	    /**
	     * Returns a string representation in JSON
	     * @return a JSOn formatted string of this item.
	     */
	    @Override
	    public String toString() {
	        try {
	            return DbTools.mapper.writer().writeValueAsString(this);
	        } catch (Exception error) {

	        }
	        return null;
	    }
}
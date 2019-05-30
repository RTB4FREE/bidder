package com.jacamars.dsp.rtb.probe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jacamars.dsp.rtb.jmq.Tools;
import com.jacamars.dsp.rtb.tools.DbTools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * Reason you don't bid probe.
 * @author Ben M. Faul
 *
 */
public class Probe {

	public static volatile Map<String, ExchangeProbe> probes;

	public static final String DEAL_PRICE_ERROR = new String("This creative price is 0, with no set deals\n");
	public static final String PRIVATE_AUCTION_LIMITED = new String("This creative price is 0, with no set deals, and this is a private auction\n");
	public static final String NO_WINNING_DEAL_FOUND = new String("Error in finding the winning deal in the bid request\n");
	public static final String NO_APPLIC_DEAL = new String("This creative price is 0, with no matching deals in the bid request, and is a private auction\n");
	public static final String BID_FLOOR = new String("Bid floor greater than bid\n");
	public static final String BID_CREAT_IS_VIDEO = new String("Creative is video, request is not\n");
	public static final String BID_CREAT_IS_BANNER = new String("Creative is banner, request is not\n");
	public static final String BID_CREAT_IS_NATIVE = new String("Creative is native content, request is not\n");
	public static final String NATIVE_LAYOUT = new String("Native ad layouts don't match\n");
	public static final String NATIVE_TITLE = new String("Native ad request wants a title, creative has none\n");
	public static final String NATIVE_TITLE_LEN = new String("Native ad title length is too long\n");
	public static final String NATIVE_WANTS_IMAGE = new String("Native ad request wants an img, creative has none\n");
	public static final String NATIVE_IMAGEW_MISMATCH = new String("Native ad img widths dont match\n");
	public static final String NATIVE_IMAGEH_MISMATCH = new String("Native ad img heights dont match\n");
	public static final String NATIVE_WANTS_VIDEO = new String("Native ad request wants a video, creative has none\n");
	public static final String NATIVE_AD_TOO_SHORT = new String("Native ad video duration is < what request wants");
	public static final String NATIVE_AD_TOO_LONG = new String("Native ad video duration is > what request wants\n");
	public static final String NATIVE_LINEAR_MISMATCH = new String("Native ad video linearity doesn't match the ad\n");
	public static final String NATIVE_AD_PROTOCOL_MISMATCH = new String("Native ad video protocol doesn't match the ad\n");
	public static final String NATIVE_AD_DATUM_MISMATCH = new String("Native ad data item mismatch\n");
	public static final String WH_INTERSTITIAL = new String("Request intersitial but campaign is not.\n");
	public static final String WH_MATCH = new String("Creative  w or h attributes dont match\n");
	public static final String VIDEO_LINEARITY = new String("Video linearity does not match\n");
	public static final String VIDEO_TOO_SHORT = new String("Video Creative min duration not long enough\n");
	public static final String VIDEO_TOO_LONG = new String("Video Creative max duration too short\n");
	public static final String VIDEO_PROTOCOL = new String("Video Creative protocols don't match\n");
	public static final String VIDEO_MIME = new String("Video Creative mime type mismatch\n");
	public static final String VIDEO_MISSING_DELIVERY = new String("Video creative missing delivery\n");
	public static final String VIDEO_DELIVERY_MISMATCH = new String("Video creative delivery mismatch\n");
	public static final String CREATIVE_MISMATCH = new String("Creative mismatch: ");
	public static final String FREQUENCY_CAPPED = new String("Frequency capped\n");
	public static final String FREQUENCY_GOVERNED = new String("Frequency governed\n");
	public static final String CREATIVE_NOTACTIVE = new String("Creative is not in active state\n");
	public static final String WRONG_EXCHANGE = new String("Wrong exchange\n");
	public static final String SPEND_RATE_EXCEEDED = new String("Spend Rate Exceeded\n");
	public static final String SITE_OR_APP_DOMAIN = new String("site.domain OR app.domain\n");
	public static final String FORMAT_NOMATCH = new String("Format in impression does not match any creative size\n");
	public static final String GLOBAL = new String("Global\n");
	public static final String MISSING_BATTR = new String("Creative is missing creative attributes, but impression requires it\n");
	public static final String CREATIVE_BLOCKED_ATTRIBUTES = new String("Creative has blocked attributes in this impression\n");
	public static final String BID_CREAT_IS_AUDIO = new String("Creative is audio, request is not\n");
	public static final String AUDIO_MISSING_MIME = new String("Audio creative missing mimeTypes\n");
	public static final String AUDIO_MIME_MISMATCH = new String("Audio creative mimetype mismatch\n");
	public static final String AUDIO_MISSING_DURATION = new String("Audio creative missing duration\n");
	public static final String AUDIO_MINDURATION_MISMATCH = new String("Audio creative duration too short\n");
	public static final String AUDIO_MAXDURATION_MISMATCH = new String("Audio creative duration too long\n");
	public static final String AUDIO_MISSING_PROTOCOLS = new String("Audio creative missing protocol\n");
	public static final String AUDIO_PROTOCOLS_MISMATCH = new String("Audio creative protocol mismatch\n");
	public static final String AUDIO_MISSING_STARTDELAY = new String("Audio creative missing startdelay\n");
	public static final String AUDIO_STARTDELAY_MISMATCH = new String("Audio creative startdelay mismatch\n");
	public static final String AUDIO_MISSING_BITRATE = new String("Audio creative missing bitrate\n");
	public static final String AUDIO_BITRATE_TOO_SMALL = new String("Audio creative bitrate too low\n");
	public static final String AUDIO_BIRATE_TOO_LARGE = new String("Audio creative bitrate too high\n");
	public static final String AUDIO_MISSING_DELIVERY = new String("Audio creative missing delivery\n");
	public static final String AUDIO_DELIVERY_MISMATCH = new String("Audio creative delivery mismatch\n");
	public static final String AUDIO_MISSING_API = new String("Audio creative missing api\n");
	public static final String AUDIO_API_MISMATCH = new String("Audio creative api mismatch\n");
	public static final String AUDIO_MISSING_FEED = new String("Audio creative missing feed\n");
	public static final String AUDIO_FEED_MISMATCH = new String("Audio creative feed mismatch\n");
	public static final String AUDIO_MISSING_STITCHED = new String("Audio creative missing stitched\n");
	public static final String AUDIO_STITCHED_MISMATCH = new String("Audio creative stitch mismatch\n");


	LongAdder total = new LongAdder();

	public Probe() {
		probes = new HashMap();
	}

	public ExchangeProbe add(String exchange) {
		ExchangeProbe probe = probes.get(exchange);
		if (probe == null) {
			probe = new ExchangeProbe(exchange);
			probes.put(exchange, probe);
		}
		return probe;
	}

	public String toJson() {
		try {
			String content = DbTools.mapper.writer().withDefaultPrettyPrinter().writeValueAsString(probes);
			return content;
		} catch (Exception e) {
			return e.toString();
		}
	}
	
	/**
	 * Reset the probes to 0.
	 */
	public void reset() {
		for (Map.Entry<String, ExchangeProbe> entry : probes.entrySet()) {
			entry.getValue().reset();
		}
		total.reset();
	}
	
	public void process(String exchange, String campaign, String creative, String key) throws Exception {
		if (key == null || key.length()==0)
			throw new Exception("Can't use an empty reason");
		ExchangeProbe probe = probes.get(exchange);
		if (probe == null) {
			probe = add(exchange);
		}
		probe.process(campaign, creative, key);
	}
	
	
	public void incrementTotal(String exchange, String campaign) {
		ExchangeProbe probe = probes.get(exchange);
		if (probe == null) {
			probe = add(exchange);
		}
		probe.incrementTotal(campaign);
		total.increment();
	}
	
	public void incrementBid(String exchange, String campaign) {
		ExchangeProbe probe = probes.get(exchange);
		if (probe == null) {
			probe = add(exchange);
		}
		probe.incrementBids(campaign);
	}
	
	public void process(String exchange, String campaign, String creative) {
		ExchangeProbe probe = probes.get(exchange);
		if (probe == null) {
			probe = add(exchange);
		}
		probe.process(campaign, creative);
	}
	
	public String report() {
		StringBuilder report = new StringBuilder();
		for (Map.Entry<String, ExchangeProbe> entry : probes.entrySet()) {
			String key = entry.getKey();
			report.append(key);
			report.append("\n");
			report.append(entry.getValue().report());
		}		
		return report.toString();
	}
	
	public String reportCsv() {
		StringBuilder report = new StringBuilder();
		for (Map.Entry<String, ExchangeProbe> entry : probes.entrySet()) {
			entry.getValue().reportCsv(report,total.sum());
		}		
		return report.toString();
	}

    public String reportJson() throws Exception {
        StringBuilder report = new StringBuilder();
        for (Map.Entry<String, ExchangeProbe> entry : probes.entrySet()) {
            entry.getValue().reportJson(report,total.sum());
        }
        return report.toString();
    }
	
	/**
	 * Return a List of objects that denote the exchange, bids, total, and a list of maps of the campaigns.
	 * @return List. The list of report maps for the exchanges.
	 */
	public List<Map<String,Object>> getMap() {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (Map.Entry<String, ExchangeProbe> entry : probes.entrySet()) {
			Map<String,Object> m = new HashMap<String,Object>();
			String key = entry.getKey();
			m.put("exchange", key);
			m.put("bids", entry.getValue().getBids());
			m.put("total", entry.getValue().getTotal());
			m.put("campaigns",entry.getValue().getMap());
			list.add(m);
		}		
		return list;
	}
	
	public String getTable() {
		StringBuilder table = new StringBuilder();
		
		table.append("<table border='1'>\n");
		
		List list = new ArrayList();
		for (Map.Entry<String, ExchangeProbe> entry : probes.entrySet()) {
			Map m = new HashMap();
			String key = entry.getKey();
			table.append("<tr><td>");
			table.append(key);
			table.append("</td>");
			table.append("<td>");
			table.append(entry.getValue().getTable());
			table.append("</td></tr>\n");
		}	
		table.append("</table>");
		return table.toString();

	}
}

class KKKV {
	
	Map<Object,Map> K1 = new HashMap();
	
	public KKKV() {
		
	}
	
	public Object get(String k1, String k2, String k3) {
		Map<Object,Map> x = K1.get(k1);
		if (x == null)
			return null;
		Map y = x.get(k2);
		if (y == null)
			return null;
		return y.get(k3);
	}
	
	public void put(String k1,  String k2,  String k3, Object v) {
		Map<Object,Map> x = K1.get(k1);
		if (x == null) {
			x = new HashMap();
			K1.put(k1, x);
		}
		Map y = x.get(k2);
		if (y == null) {
			y = new HashMap();
			y.put(k2, y);
		}
		Map z = (Map)y.get(k3);
		if (z == null) {
			z = new HashMap();
			z.put(k3,v);
		}
	}
	
}

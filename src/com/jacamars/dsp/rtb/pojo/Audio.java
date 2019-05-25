package com.jacamars.dsp.rtb.pojo;


import java.util.ArrayList;
import java.util.List;

import com.jacamars.dsp.rtb.common.AudioCreative;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.probe.Probe;

/**
 * A class that comprises the audio part of a regular ad specified in the bid request
 * @author Ben M. Faul
 */

public class Audio {
	/** The required field as set forth in the rtb native a spec */
	public int required = 0;
	/** The min duration as set forth in the rtb native a spec */
	public int minduration = -1;
	/** The max duration field as set forth in the rtb spec */
	public int maxduration = -1;
	/** Start delay in seconds for pre-roll, mid-roll, or post-roll ad placement */
	public int startdelay = -1;

	public int maxextended = -1;
	
	public int minbitrate = -1;
	
	public int maxbitrate = -1;
	
	public List<Integer> delivery;
	
	public List<Integer> api;
	
	public int maxseq = -1;
	
	public int feed = -1;
	
	public int stitched = -1;
	
	public int nvol = -1;
	
	/** The protocol supported as set forth in the rtb  spec */
	public List<Integer> protocols;
	/** The mime types I like */
	public List<String> mimeTypes = new ArrayList<String>();
	
	public Audio() {
		
	}
	
	public boolean matched(Creative c, BidRequest br, String adId,  StringBuilder errorString, Probe probe) throws Exception {
	
		AudioCreative a = c.audioAd;
		if (a.mimeType == null) {
			if (errorString != null) {
				errorString.append("Mime type is not defined in creative");
			}
			probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_MIME);
			return false;
		}
		if (mimeTypes.contains(a.mimeType)==false) {
			if (errorString != null) {
				errorString.append("Mime type of impression does not match creative");
			}
			probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MIME_MISMATCH);
			return false;
		}
		if (minduration != -1) {
			if (a.duration ==null) {
				if (errorString != null) {
					errorString.append("Duration of audio creative not specified but impresssion demands it");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_DURATION);
				return false;
			}
			if (a.duration != null && a.duration < minduration) {
				if (errorString != null) {
					errorString.append("Duration of audio creative is less than duration of impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MINDURATION_MISMATCH);
				return false;
			}
		}
		if (maxduration != -1) {
			if (a.duration ==null) {
				if (errorString != null) {
					errorString.append("Duration of audio creative not specified but impression demands it");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_DURATION);
				return false;
			}
			if (a.duration != null && a.duration > maxduration) {
				if (errorString != null) {
					errorString.append("Duration of audio creative is greater than duration of impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MAXDURATION_MISMATCH);
				return false;
			}
		}
		if (protocols != null) {
			if (a.protocol == null) {
				if (errorString != null) {
					errorString.append("Protocol of audio creative not specified but impression demands it");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_PROTOCOLS);
				return false;
			}
			if (protocols.contains(a.protocol)==false) {
				if (errorString != null) {
					errorString.append("Protocol of audio creative not supported by impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_PROTOCOLS_MISMATCH);
				return false;
			}
		}
		if (startdelay != -1) {
			if (a.startdelay == null) {
				if (errorString != null) {
					errorString.append("Start delay specification required by audio impression but creative doesn't");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_STARTDELAY);
				return false;
			}
			if (a.startdelay != null && a.startdelay > startdelay) {
				if (errorString != null) {
					errorString.append("Start delay of creative > than what impression specified");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_STARTDELAY_MISMATCH);
				return false;
			}
		}
		
		if (minbitrate != -1) {
			if (a.bitrate == null) {
				if (errorString != null) {
					errorString.append("Impression requires a bitrate, but creative doesn't provide one.");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_BITRATE);
				return false;
			}
			if (a.bitrate < minbitrate) {
				if (errorString != null) {
					errorString.append("Bitrate of creative is less than min bit rate of the impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_BITRATE_TOO_SMALL);
				return false;
			}
		}
		
		if (maxbitrate != -1) {
			if (a.bitrate == null) {
				if (errorString != null) {
					errorString.append("Impression requires a bitrate, but creative doesn't provide one.");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_BITRATE);
				return false;
			}
			if (a.bitrate > maxbitrate) {
				if (errorString != null) {
					errorString.append("Bitrate of creative is greater than min bit rate of the impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_BIRATE_TOO_LARGE);
				return false;
			}
		}
		
		if (delivery != null) {
			if (a.delivery == null) {
				if (errorString != null) {
					errorString.append("Impression requires delivery specified but creative doesn't provide one.");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_DELIVERY);
				return false;
			}
			if (delivery.contains(a.delivery)==false) {
				if (errorString != null) {
					errorString.append("Delivery of the audio creative is not supported by the impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_DELIVERY_MISMATCH);
				return false;
			}
		}
		
		if (api != null) {
			if (a.api == null) {
				if (errorString != null) {
					errorString.append("Impression specifies audio api but creative does not");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_API);
				return false;
			}
			if (api.contains(a.api)==false) {
				if (errorString != null) {
					errorString.append("Audio api of the creative is not supported by the creative");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_API_MISMATCH);
				return false;
			}
		}
		
		if (feed != -1) {
			if (a.feed == null) {
				if (errorString != null) {
					errorString.append("Audio impression specifies a feed but the creative does not");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_FEED);
				return false;
			}
			if (feed != a.feed) {
				if (errorString != null) {
					errorString.append("Audio creative feed is not supported by the impression");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_FEED_MISMATCH);
				return false;
			}
		}
		
		if (stitched != -1) {
			if (a.stitched == null) {
				if (errorString != null) {
					errorString.append("Audio impression specifies stitched attribute, but creative does not");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_MISSING_STITCHED);
				return false;
			}
			if (a.stitched != stitched) {
				if (errorString != null) {
					errorString.append("Stiching of the audio impression did not match that specified by the creative");
				}
				probe.process(br.getExchange(),  adId, c.impid, Probe.AUDIO_STITCHED_MISMATCH);
				return false;
			}
		}
		
		return true;
	}
}

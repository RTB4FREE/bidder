package com.jacamars.dsp.rtb.exchanges.bidswitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.pojo.Impression;

public class BidswitchBidResponse extends BidResponse{
	
	static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

	private static String protocol = "5.3";
	
	/**
	 * Bid response object for multiple bids per request support. 
	 * @param br BidRequest used 
	 * @param multi List. The multiple creatives that bid.
	 * @param xtime int. The time to process.
	 * @throws Exception
	 */
	public BidswitchBidResponse(Bidswitch br, Impression imp, Campaign camp, Creative creat, double price, String dealId,
			int xtime) throws Exception {
		
		this.br = br;
		this.imp = imp;
		this.camp = camp;
		this.oidStr = oidStr;
		this.creat = creat;
		this.xtime = xtime;
		this.price = Double.toString(price);
		this.dealId = dealId;
		this.camp = camp;
		this.creat = creat;
		this.price = Double.toString(price);
		this.adid = camp.adId;
		this.imageUrl = substitute(creat.imageurl);
		this.crid = creat.impid;
		this.domain = br.siteDomain;
		this.imp = imp;
		this.impid = imp.getImpid();
		if (imp.nativead)
			this.adtype="native";
		else
		if (imp.video != null)
			this.adtype="video";
		else
			this.adtype="banner";
		this.timestamp = System.currentTimeMillis();

		impid = imp.getImpid();
		adid = camp.adId;
		crid = creat.impid;
		this.domain = br.siteDomain;
		
		forwardUrl = substitute(creat.getForwardUrl()); // creat.getEncodedForwardUrl();
		imageUrl = substitute(creat.imageurl);
		exchange = br.getExchange();

		if (!creat.isNative()) {
			if (imp.w != null) {
				width = imp.w.intValue();
				height = imp.h.intValue();
			}
		}

		makeResponse(price);
	}
	
	public void makeResponse(double price) {
		
		response = new StringBuilder();
		try {
		this.exchange = br.getExchange();
		this.xtime = xtime;
		this.oidStr = br.id;
		this.timestamp = System.currentTimeMillis();

		/******************************************/
		
		/** The configuration used for generating this response */
		Configuration config = Configuration.getInstance();
		
		StringBuilder nurl = new StringBuilder();
		///////////////////////////// PROB NOT NEEDED /////////////////////
		StringBuilder linkUrlX = new StringBuilder();
		linkUrlX.append(config.redirectUrl);
		linkUrlX.append("/");
		linkUrlX.append(oidStr.replaceAll("#", "%23"));
		linkUrlX.append("/?url=");

		// //////////////////////////////////////////////////////////////////

		if (br.lat != null)
			lat = br.lat.doubleValue();
		if (br.lon != null)
			lon = br.lon.doubleValue();
		seat = Configuration.getInstance().seats.get(exchange);

		Map m = new HashMap();
		Map mx = new HashMap();
		mx.put("protocol", protocol);
		
		m.put("ext", mx);
		m.put("id", oidStr);
		
		List<Map> seatbids = new ArrayList();
		m.put("seatbids", seatbids);
		
		Map seatbid = new HashMap();
		seatbids.add(seatbid);
		seatbid.put("seat", seat);
		
		List bids = new ArrayList();
		Map bid = new HashMap();
		bids.add(bid);
		seatbid.put("bids", bids);
		bid.put("id", imp.getImpid());
		if (creat.currency != null && creat.currency.length() != 0) {
			bid.put("cur", creat.currency);
		}
		bid.put("price", price);
		bid.put("cid", adid);
		bid.put("crid", creat.impid);


		if (dealId != null) 
			bid.put("dealid", dealId);
		
		if (imageUrl != null) 
			bid.put("imageurl", imageUrl);
		
		List<String> adomain = new ArrayList();
		adomain.add(camp.adomain);
		bid.put("adomain", adomain);
		
		if (this.creat.isVideo()) {
			if (br.usesEncodedAdm) {
				bid.put("adm",this.creat.encodedAdm);
				this.forwardUrl = this.creat.encodedAdm;   // not part of protocol, but stuff here for logging purposes
			} else {
				//response.append(this.creat.getForwardUrl());
				//this.forwardUrl = this.creat.getForwardUrl();
				bid.put("adm,",this.creat.unencodedAdm );
				this.forwardUrl = this.creat.unencodedAdm ;
			}
		} else if (this.creat.isNative()) {
			if (br.usesEncodedAdm)
				nativeAdm = this.creat.getEncodedNativeAdm(br);
			else
				nativeAdm = this.creat.getUnencodedNativeAdm(br);
			bid.put("adm", nativeAdm);
		} else 
			bid.put("adm", getTemplate());
		
		if (camp.category != null && camp.category.size() > 0) {
			bid.put("cat", camp.category);
		}
		
		snurl = new StringBuilder(config.winUrl);
		snurl.append("/");
		snurl.append(br.siteDomain);
		snurl.append("/");
		if (br.isSite())
			snurl.append("SITE");
		else
			snurl.append("APP");
		snurl.append("/");
		snurl.append(br.getExchange());
		snurl.append("/");
		snurl.append("${AUCTION_PRICE}"); // to get the win price back from the
											// Exchange....
		snurl.append("/");
		snurl.append(lat);
		snurl.append("/");
		snurl.append(lon);
		snurl.append("/");
		snurl.append(adid);
		snurl.append("/");
		snurl.append(creat.impid);
		snurl.append("/");
		snurl.append(oidStr.replaceAll("#", "%23"));
		bid.put("nurl", snurl.toString());
		if (creat.extensions != null) {
			bid.put("ext", creat.extensions);
		}

		response.append(mapper.writeValueAsString(m));
		

		this.cost = creat.price; // pass this along so the bid response object // has a copy of the price
		macroSubs(response);
		} catch (Exception error) {
			error.printStackTrace();
			throw (RuntimeException)error;
		}
	}
}

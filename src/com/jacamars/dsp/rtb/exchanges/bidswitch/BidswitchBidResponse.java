package com.jacamars.dsp.rtb.exchanges.bidswitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.pojo.Impression;

public class BidswitchBidResponse extends BidResponse {

	static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	private static String protocol = "5.3";

	/**
	 * Bid response object for multiple bids per request support.
	 * 
	 * @param br    BidRequest used
	 * @param multi List. The multiple creatives that bid.
	 * @param xtime int. The time to process.
	 * @throws Exception
	 */
	public BidswitchBidResponse(Bidswitch br, Impression imp, Campaign camp, Creative creat, double price,
			String dealId, int xtime) throws Exception {

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
			this.adtype = "native";
		else if (imp.video != null)
			this.adtype = "video";
		else
			this.adtype = "banner";
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
			
			response.append("{\"ext\":{\"protocol\":\"").append(protocol).append("\"},");
			response.append("\"id\":\"").append(oidStr).append("\",");

			response.append("\"seatbid\":[{\"seat\":\"").append(seat).append("\",");
			response.append("\"bid\":[{\"ext\":{");
			response.append("\"agency_name\":\"").append(creat.extensions.get("agency_name")).append("\",");
			response.append("\"advertiser_name\":\"").append(creat.extensions.get("advertiser_name")).append("\"},");
			
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
			response.append("\"nurl\":\"").append(snurl.toString()).append("\",");
			
			response.append("\"cid\":\"").append(camp.adId).append("\",");
			
			response.append("\"crid\":\"").append(creat.impid).append("\",");
			
			response.append("\"adomain\":[\"").append(camp.adomain).append("\"],");
			
			response.append("\"price\":").append(price).append(",");
			
			response.append("\"iurl\":\"").append(imageUrl).append("\",");
			
			response.append("\"id\":\"").append(imp.getImpid()).append("\",");
			
			response.append("\"impid\":\"").append(imp.getImpid()).append("\",");
			

			if (creat.currency != null && creat.currency.length() != 0) {
				response.append("\"cur\":\"").append(creat.currency).append("\",");
			}
	
			if (dealId != null)
				response.append("\"dealid\":\"").append(dealId).append("\",");

			if (this.creat.isVideo()) {
				if (br.usesEncodedAdm) {
					response.append("\"adm\":\"").append(this.creat.encodedAdm).append("\",");
					this.forwardUrl = this.creat.encodedAdm; // not part of protocol, but stuff here for logging
																// purposes
				} else {
					// response.append(this.creat.getForwardUrl());
					// this.forwardUrl = this.creat.getForwardUrl();
					response.append("\"adm\":\"").append(this.creat.unencodedAdm).append("\",");
					this.forwardUrl = this.creat.unencodedAdm;
				}
			} else if (this.creat.isNative()) {
				if (br.usesEncodedAdm)
					nativeAdm = this.creat.getEncodedNativeAdm(br);
				else
					nativeAdm = this.creat.getUnencodedNativeAdm(br);
				response.append("\"adm\":\"").append(nativeAdm).append("\",");
			} else
				response.append("\"adm\":\"").append(getTemplate()).append("\",");

			if (creat.categories != null && creat.categories.size() > 0) {
				response.append("\"cat\":[");
				for (int i=0;i<creat.categories.size();i++) {
					response.append("\"").append(creat.categories.get(i)).append("\"");
					if (i+1 < creat.categories.size())
						response.append(",");
				}
				response.append("]");
			}
			response.append("}]}]}");
			this.cost = creat.price; // pass this along so the bid response object // has a copy of the price
			macroSubs(response);
		} catch (Exception error) {
			error.printStackTrace();
			throw (RuntimeException) error;
		}
	}
}

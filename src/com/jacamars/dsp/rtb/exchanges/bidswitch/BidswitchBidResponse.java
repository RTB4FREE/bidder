package com.jacamars.dsp.rtb.exchanges.bidswitch;

import java.util.List;

import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.pojo.Impression;

public class BidswitchBidResponse extends BidResponse{

	private static String protocol = "5.3";
	
	/**
	 * Bid response object for multiple bids per request support. 
	 * @param br BidRequest used 
	 * @param multi List. The multiple creatives that bid.
	 * @param xtime int. The time to process.
	 * @throws Exception
	 */
	public BidswitchBidResponse(BidRequest br, List<SelectedCreative> multi, int xtime) throws Exception {
		this.br = br;
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
		seat = br.getExchange();

		response = new StringBuilder("{");
		response.append("\"id\":\"").append(oidStr).append("\"");
		response.append(",\"ext\":{\"protocol\":\"5.3\"}");
		
		response.append("\",\"seatbid\":[{\"seat\":\"");
		response.append(Configuration.getInstance().seats.get(exchange));
		response.append("\",");
		
		response.append("\"bid\":[");
			
		for (int i=0; i<multi.size();i++) {
			
			SelectedCreative x = multi.get(i);
			this.camp = x.getCampaign();
			this.creat = x.getCreative();
			this.price = Double.toString(x.price);
			this.dealId = x.dealId;
			this.adid = camp.adId;
			this.imageUrl = substitute(creat.imageurl);
			this.crid = creat.impid;
			this.domain = br.siteDomain;

			this.imp = x.getImpression();
			this.impid = imp.getImpid();
			/** Set the response type ****************/
			if (imp.nativead)
				this.adtype="native";
			else
			if (imp.video != null)
				this.adtype="video";
			else
				this.adtype="banner";
			
			makeMultiResponse();
			if (i+1 < multi.size()) {
				response.append(",");
			}
		}
		
		response.append("]}]}");

		this.cost = creat.price; // pass this along so the bid response object // has a copy of the price
		macroSubs(response);
	}
	
	/**
	 * Make a multi bid response. It has multiple bids in the seatbid.
	 * @throws Exception
	 */
	public void makeMultiResponse() throws Exception  {
		response.append("{\"impid\":\"");
		response.append(impid);							// the impression id from the request
		response.append("\",\"id\":\"");
		response.append(br.id);						// the request bid id
		response.append("\"");

		if (creat.currency != null && creat.currency.length() != 0) {
			response.append(",");
			response.append("\"cur\":\"");
			response.append(creat.currency);
			response.append("\"");
		}

		response.append(",\"price\":");
		response.append(price);
		response.append(",\"adid\":\"");
		
		// Use SSP assignd adid
		if (creat.alternateAdId == null)
			response.append(adid);
		else
			response.append(adid);
		
		
		if (BidRequest.usesPiggyBackedWins(exchange)) {
			// don't do anything
		} else {
			response.append("\",\"nurl\":\"");
			response.append(snurl);
		}
		
		response.append("\",\"cid\":\"");
		response.append(adid);
		response.append("\",\"crid\":\"");
		response.append(creat.impid);
		if (dealId != null) {
			response.append("\",\"dealid\":\"");
			response.append(dealId);
		}
		response.append("\",\"iurl\":\"");
		response.append(imageUrl);
		response.append("\",\"adomain\": [\"");
		response.append(camp.adomain);

		response.append("\"],\"adm\":\"");
		
		if (this.creat.isVideo()) {
			if (br.usesEncodedAdm) {
				response.append(this.creat.encodedAdm);
				this.forwardUrl = this.creat.encodedAdm;   // not part of protocol, but stuff here for logging purposes
			} else {
				//response.append(this.creat.getForwardUrl());
				//this.forwardUrl = this.creat.getForwardUrl();
				response.append(this.creat.unencodedAdm );
				this.forwardUrl = this.creat.unencodedAdm ;
			}
		} else if (this.creat.isNative()) {
			if (br.usesEncodedAdm)
				nativeAdm = this.creat.getEncodedNativeAdm(br);
			else
				nativeAdm = this.creat.getUnencodedNativeAdm(br);
			response.append(nativeAdm);
		} else {
			response.append(getTemplate());
		}

		response.append("\"}");
	}
}

package com.jacamars.dsp.rtb.common;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.core.util.BufferRecyclers;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.exchanges.Nexage;
import com.jacamars.dsp.rtb.exchanges.adx.AdxCreativeExtensions;
import com.jacamars.dsp.rtb.nativeads.assets.Entity;
import com.jacamars.dsp.rtb.nativeads.creative.Data;
import com.jacamars.dsp.rtb.nativeads.creative.NativeCreative;
import com.jacamars.dsp.rtb.pojo.*;
import com.jacamars.dsp.rtb.probe.Probe;
import com.jacamars.dsp.rtb.tools.MacroProcessing;

/**
 * An object that encapsulates the 'creative' (the ad served up and it's
 * attributes). The creative represents the physical object served up by the
 * bidder to the mobile device. The creative contains the image url, the pixel
 * url, and the referring url. The creative will them be used to create the
 * components of the RTB 2 bid
 * 
 * @author Ben M. Faul
 *
 */
public class Creative {
	/** The forward URL used with this creative */
	public String forwardurl;
	/** The encoded version of the forward url used by this creative */
	private transient String encodedFurl;
	/* The image url used by this creative */
	public String imageurl;
	/** The encoded image URL used by this creative */
	private transient String encodedIurl;
	/** The impression id of this creative */
	public String impid;
	
	/** The width of this creative */
	public Integer w;
	/** The height of this creative */
	public Integer h;
	
	public Dimensions dimensions;
	
	/** sub-template for banner */
	public String subtemplate;
	/** Private/preferred deals */
	public Deals deals;
	/** String representation of w */
	transient public String strW;
	/** String representation of h */
	transient public String strH;
	/** String representation of price */
	transient public String strPrice;
	/** Attributes applied to all impressions */
	public List<Node> attributes = new ArrayList<Node>();
	/** Input ADM field */
	public List<String> adm;
	/** The encoded version of the adm as a single string */
	public transient String encodedAdm;
	// unencoded adm of the
	public transient String unencodedAdm;
	/** currency of this creative */
	public String currency = null;
	/** Extensions needed by SSPs */
	public Map<String,String> extensions = null;
	// Currency
	public String cur = "USD";
	
	/** if this is a video creative (NOT a native content video) its protocol */
	public Integer videoProtocol;
	/**
	 * if this is a video creative (NOT a native content video) , the duration
	 * in seconds
	 */
	public Integer videoDuration;
	/** If this is a video (Not a native content, the linearity */
	public Integer videoLinearity;
	/** The videoMimeType */
	public String videoMimeType;
	/**
	 * vast-url, a non standard field for passing an http reference to a file
	 * for the XML VAST
	 */
	public String vasturl;
	/** The price associated with this creative */
	public double price = .01;

	/** Selection weight. Weight is 1 to n. >> is heavier weight. */
	public int weight = 1;
	
	// If this creative is tagged with categories. Used by bidswitch for example
	public List<String> categories;

	// /////////////////////////////////////////////
	/** Native content assets */
	public NativeCreative nativead;

	/**
	 * Don't use the template, use exactly what is in the creative for the ADM
	 */
	public boolean adm_override = false;

	/** If this is an Adx type creative, here is the payload */
	public AdxCreativeExtensions adxCreativeExtensions;

	@JsonIgnore
	public transient StringBuilder smaatoTemplate = null;
	// //////////////////////////////////////////////////

	/** The macros this particular creative is using */
	@JsonIgnore
	public transient List<String> macros = new ArrayList<String>();
	
	// Alternate to use for the adid, instead of the one in the creative. This cab
	// happen if SSPs have to assign the id ahead of time.
	public transient String alternateAdId;

	/* creative's status */
	public String status;

	/* Only Active creative is allowed to bid */
	public static String ALLOWED_STATUS = "Active";

	/* ad-exchange name */
	public String exchange;

	/** When this is not null, this means this creative is a proxy for a list of rotating creatives */
	public Map<String,Creative> subCreatives;

	/** These are common attributes across all impressions. */
	@JsonIgnore
	public List<Node> fixedNodes = new ArrayList<Node>();

	/** A sorter for the campaign/creative attributes, who is most likely to cause a false will bubble up */
	private SortNodesFalseCount nodeSorter = new SortNodesFalseCount();

	public FrequencyCap frequencyCap = null;
	

	/**
	 * Empty constructor for creation using json.
	 */
	public Creative() {

	}

	/**
	 * A shallow copy. This is used to create a 'rotating creative'. The rotating creative will inherit all the
	 * attributes of the proxy, but, the adm will be changed. as needed.
	 * @return
	 */
	public Creative copy() {
		Creative c = new Creative();
		c.alternateAdId = alternateAdId;
		c.adm_override = adm_override;
		c.cur = cur;
		c.currency = currency;
		c.w = w;
		c.h = h;
		c.strW = strW;
		c.strH = strH;
		c.strPrice = strPrice;
		c.videoDuration = videoDuration;
		c.videoLinearity = videoLinearity;
		c.videoProtocol = videoProtocol;
		c.macros = macros;
		c.price = price;
		c.weight = weight;
		c.forwardurl = forwardurl;
		c.imageurl = imageurl;
		c.dimensions = dimensions;
		c.categories = categories;
		c.frequencyCap = frequencyCap;

		c.encodeUrl();
		return c;
	}

	/**
	 * Find a deal by id, if exists, will bid using the deal
	 * 
	 * @param id
	 *            String. The of the deal in the bid request.
	 * @return Deal. The deal, or null, if no deal.
	 */
	public Deal findDeal(String id) {
		if (deals == null || deals.size() == 0)
			return null;
		for (int i = 0; i < deals.size(); i++) {
			Deal d = deals.get(i);
			if (d.id.equals(id)) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Given a list of deals, find out if we have a deal that matches.
	 * 
	 * @param ids
	 *            List. A list of ids.
	 * @return Deal. A matching deal or null if no deal.
	 */
	public Deal findDeal(List<String> ids) {
		if (deals == null || deals.size() == 0)
			return null;
		
		for (int i = 0; i < ids.size(); i++) {
			Deal d = findDeal(ids.get(i));
			if (d != null)
				return d;
		}
		return null;
	}

	/**
	 * Find a deal by id, if exists, will bid using the deal
	 * 
	 * @param id
	 *            String. The of the deal in the bid request.
	 * @return Deal. The deal, or null, if no deal.
	 */
	public Deal findDeal(long id) {
		if (deals == null || deals.size() == 0)
			return null;
		for (int i = 0; i < deals.size(); i++) {
			Deal d = deals.get(i);
			if (Long.parseLong(d.id) == id) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Does the HTTP encoding for the forward url and image url. The bid will
	 * use the encoded form.
	 */
	void encodeUrl() {
		MacroProcessing.findMacros(macros, forwardurl);
		MacroProcessing.findMacros(macros, imageurl);
		
		/**
		 * Docker environment variable substitutions too
		 */
		try {
			imageurl = Configuration.getInstance().substitute(imageurl);
			forwardurl = Configuration.getInstance().substitute(forwardurl);
		} catch (Exception error) {
			error.printStackTrace();
		}

		if (w != null) {
			if (dimensions == null)
				dimensions = new Dimensions();
			Dimension d = new Dimension(w,h);
			dimensions.add(d);
		}
		/*
		 * Encode JavaScript tags. Redis <script src=\"a = 100\"> will be
		 * interpeted as <script src="a=100"> In the ADM, this will cause
		 * parsing errors. It must be encoded to produce: <script src=\"a=100\">
		 */
		 if (forwardurl != null) {
			 JsonStringEncoder encoder = BufferRecyclers.getJsonStringEncoder();
			 char[] output =  encoder.quoteAsString(forwardurl);
	     	forwardurl = new String(output);
		 }
		/*if (forwardurl != null) {
			if (forwardurl.contains("<script") || forwardurl.contains("<SCRIPT")) {
				if (forwardurl.contains("\"") && (forwardurl.contains("\\\"") == false)) { 
					forwardurl = forwardurl.replaceAll("\"", "\\\\\"");
				}
			}
		}*/

		encodedFurl = URIEncoder.myUri(forwardurl);
		encodedIurl = URIEncoder.myUri(imageurl);

		if (adm != null && adm.size() > 0) {
			String s = "";
			for (String ss : adm) {
				s += ss;
			}
			unencodedAdm = s.replaceAll("\r\n", "");
			//unencodedAdm = unencodedAdm.replaceAll("\"", "\\\\\"");
			JsonStringEncoder encoder = BufferRecyclers.getJsonStringEncoder();
			char[] output =  encoder.quoteAsString(unencodedAdm);
			unencodedAdm = new String(output);
			MacroProcessing.findMacros(macros, unencodedAdm);
			encodedAdm = URIEncoder.myUri(s);
		}

		//strW = Integer.toString(w);
		//strH = Integer.toString(h);
		strPrice = Double.toString(price);
		
		/**
		 * Always contain these!
		 */
		if (!macros.contains("{pixel_url}"))
			macros.add("{pixel_url}");
		if (!macros.contains("{win_url}"))
			macros.add("{win_url}");
	}

	/**
	 * Getter for the forward URL, unencoded.
	 * 
	 * @return String. The unencoded url.
	 */
	@JsonIgnore
	public String getForwardUrl() {
		return forwardurl;
	}

	/**
	 * Return the encoded forward url
	 * 
	 * @return String. The encoded url
	 */
	@JsonIgnore
	public String getEncodedForwardUrl() {
		if (encodedFurl == null)
			encodeUrl();
		return encodedFurl;
	}

	/**
	 * Return the encoded image url
	 * 
	 * @return String. The returned encoded url
	 */
	@JsonIgnore
	public String getEncodedIUrl() {
		if (encodedIurl == null)
			encodeUrl();
		return encodedIurl;
	}

	/**
	 * Setter for the forward url, unencoded.
	 * 
	 * @param forwardUrl
	 *            String. The unencoded forwardurl.
	 */
	public void setForwardUrl(String forwardUrl) {
		this.forwardurl = forwardUrl;
	}

	/**
	 * Setter for the imageurl
	 * 
	 * @param imageUrl
	 *            String. The image url to set.
	 */
	public void setImageUrl(String imageUrl) {
		this.imageurl = imageUrl;
	}

	/**
	 * Returns the impression id for this creative (the database key used in
	 * wins and bids).
	 * 
	 * @return String. The impression id.
	 */
	public String getImpid() {
		return impid;
	}

	/**
	 * Set the impression id object.
	 * 
	 * @param impid
	 *            String. The impression id to use for this creative. This is
	 *            merely a databse key you can use to find bids and wins for
	 *            this id.
	 */
	public void setImpid(String impid) {
		this.impid = impid;
	}

	/**
	 * Set the price on this creative
	 * 
	 * @param price
	 *            double. The price to set.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Get the price of this campaign.
	 * 
	 * @return double. The price associated with this creative.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Determine if this is a native ad
	 * 
	 * @return boolean. Returns true if this is a native content ad.
	 */
	@JsonIgnore
	public boolean isNative() {
		if (nativead != null)
			return true;
		return false;
	}

	/**
	 * Determine if this creative is video or not
	 * 
	 * @return boolean. Returns true if video.
	 */
	@JsonIgnore
	public boolean isVideo() {
		if (this.videoDuration != null)
			return true;
		return false;
	}

	/**
	 * Encodes the attributes of the node after the node is instantiated.
	 * 
	 * @throws Exception
	 *             on JSON errors.
	 */
	public void encodeAttributes() throws Exception {
		for (Node n : attributes) {
			n.setValues();
		}

		if (nativead != null) {
			nativead.encode();
		}

		// assign the fixed nodes
		fixedNodes.add(new FixedNodeStatus());
        fixedNodes.add(new FixedNodeNonStandard());
        fixedNodes.add(new FixedNodeExchange());

        // These are impression releated
		attributes.add(new FixedNodeRequiresDeal());
		attributes.add(new FixedNodeNoDealMatch());
		attributes.add(new FixedNodeIsVideo());
		attributes.add(new FixedNodeIsNative());
		attributes.add(new FixedNodeIsBanner());
		attributes.add(new FixedNodeDoNative());
        attributes.add(new FixedNodeDoSize());
		attributes.add(new FixedNodeDoVideo());
	}

    /**
     * Sort the selection criteria in descending order of number of times false was selected.
     * Then, after doing that, zero the counters.
     */
	public void sortNodes() {
        Collections.sort(fixedNodes, nodeSorter);
        Collections.sort(attributes, nodeSorter);

        for (int i = 0; i<fixedNodes.size();i++) {
            fixedNodes.get(i).clearFalseCount();
        }

        for (int i = 0; i<attributes.size();i++) {
            attributes.get(i).clearFalseCount();
        }
    }

	/**
	 * Returns the native ad encoded as a String.
	 * 
	 * @param br
	 *            BidRequest. The bid request.
	 * @return String. The encoded native ad.
	 */
	@JsonIgnore
	public String getEncodedNativeAdm(BidRequest br) {
		return nativead.getEncodedAdm(br);
	}
	
	/**
	 * Returns the native ad escaped.
	 * @param br BidRequest. The bid request.
	 * @return String. The returned escaped string.
	 */
	@JsonIgnore
	public String getUnencodedNativeAdm(BidRequest br) {
		return nativead.getEscapedAdm(br);
	}

	/**
	 * Process the bid request against this creative.
	 * 
	 * @param br
	 *            BidRequest. Returns true if the creative matches.
	 * @param errorString
	 *            StringBuilder. The string to hold any campaign failure
	 *            messages
	 * @return boolean. Returns true of this campaign matches the bid request,
	 *         ie eligible to bid
	 */
	public SelectedCreative process(BidRequest br, String adId, StringBuilder errorString , Probe probe,  Map<String, String> capSpecs) throws Exception {

        /**
         * Fixed nodes do not access deals or the br impressions
         */
        for (int i=0;i<fixedNodes.size();i++) {
            Deal d = null;
            if (!fixedNodes.get(i).test(br,this,adId,null,errorString,probe,null))
                return null;

        }

        /**
         * Ok, the standard set has been dealt with, let's work with impressions and deals rules.
         */
		int n = br.getImpressions();
		StringBuilder sb = new StringBuilder();
		Impression imp;
	
		for (int i=0; i<n;i++) {
			imp = br.getImpression(i);
			SelectedCreative cr = xproc(br,adId,imp,errorString, probe, capSpecs);
			if (cr != null) {
				cr.setImpression(imp);
				return cr;
			}
		}
		return null;
	}
	
	public SelectedCreative xproc(BidRequest br, String adId, Impression imp, StringBuilder errorString, Probe probe,  Map<String, String> capSpecs) throws Exception {
		//List<Deal> newDeals = null;
		String dealId = null;
		double xprice = price;
		String impid = this.impid;

		Node n = null;
		/**
		 * Attributes that are specific to the creative (additional to the
		 * campaign
		 */
		try {
            Deal deal = null;
            List<Deal> deals = new ArrayList<Deal>();
			for (int i = 0; i < attributes.size(); i++) {
				n = attributes.get(i);
				if (n.test(br,this,adId,imp,errorString,probe,deals) == false) {
				    if (n.hierarchy == null) {
                        if (errorString != null)
                            errorString.append("Creative mismatch: ");
                        if (errorString != null) {
                            if (n.operator == Node.OR)
                                errorString.append("OR failed on all branches\n");
                            else
                                errorString.append(n.hierarchy);
                        }
                        probe.process(br.getExchange(), adId, impid, Probe.CREATIVE_MISMATCH + n.hierarchy);
                    }
					return null;
				}

				if (deals.size()!=0) {
                    deal = deals.get(0);
                    dealId = deal.id;
                    xprice = deal.price;
                }
			}
		} catch (Exception error) {
		    error.printStackTrace();
			if (errorString != null) {
				errorString.append("Internal error processing bid request with: "+ n.name);
				errorString.append(error.toString());
				errorString.append("\n");
			}
			return null;
		}
		
		/**
		 * If there was no deal, then make sure the bid floor is not blown
		 */
		if (dealId == null) {
			if(imp.bidFloor != null && xprice < imp.bidFloor) {
				if (errorString != null) {
					errorString.append("Price of creative: ");
					errorString.append(xprice);
					errorString.append(" < bidfloor: ");
					errorString.append(imp.bidFloor);
				}
				probe.process(br.getExchange(), adId, impid, Probe.BID_FLOOR);
				return null;
			}
		}

		// Creative frequency cap check
		if (isCapped(br, capSpecs,  adId)) {
			if (errorString != null) {
				errorString.append("Creative capped by frequency cap ");
			}
			probe.process(br.getExchange(), adId, impid, Probe.FREQUENCY_CAPPED_CREATIVE);
			return null;
		}

		return new SelectedCreative(this, dealId, xprice, impid);
	}

	/**
	 * Creates a sample of the ADM field, useful for testing your ad markup to
	 * make sure it works.
	 * 
	 * @param camp
	 *            Campaign. The campaign to use with this creative.
	 * @return String. The ad markup HTML.
	 */
	public String createSample(Campaign camp) {
		BidRequest request = new Nexage();

		String page = null;
		String str = null;
		File temp = null;
		
		Impression imp = new Impression();

		imp.w = 666;
		imp.h = 666;

		BidResponse br = null;

		try {
			if (this.isVideo()) {
				br = new BidResponse(request, imp, camp, this, "123", 1.0, null, 0);
				imp.video = new Video();
				imp.video.linearity = this.videoLinearity;
				imp.video.protocol.add(this.videoProtocol);
				imp.video.maxduration = this.videoDuration + 1;
				imp.video.minduration = this.videoDuration - 1;

				str = br.getAdmAsString();
				/**
				 * Read in the stubbed video page and patch the VAST into it
				 */
				page = new String(Files.readAllBytes(Paths.get("web/videostub.html")), StandardCharsets.UTF_8);
				page = page.replaceAll("___VIDEO___", "http://localhost:8080/vast/onion270.xml");
			} else if (this.isNative()) {

				// br = new BidResponse(request, camp, this,"123",0);
				// request.nativead = true;
				// request.nativePart = new NativePart();
				// str = br.getAdmAsString();

				page = "<html><title>Test Creative</title><body><img src='images/under-construction.gif'></img></body></html>";
			} else {
				br = new BidResponse(request, imp, camp, this, "123", 1.0, null, 0);
				str = br.getAdmAsString();
				page = "<html><title>Test Creative</title><body><xmp>" + str + "</xmp>" + str + "</body></html>";
			}
			page = page.replaceAll("\\{AUCTION_PRICE\\}", "0.2");
			page = page.replaceAll("\\$", "");
			temp = File.createTempFile("test", ".html", new File("www/temp"));
			temp.deleteOnExit();

			Files.write(Paths.get(temp.getAbsolutePath()), page.getBytes());
		} catch (Exception error) {
			error.printStackTrace();
		}
		return "temp/" + temp.getName();
	}

	/**
	 * Find a node of the named hierarchy.
	 * 
	 * @param hierarchy
	 *            String. The hierarchy you are looking for. Note, nodes can have a null
     *            hierarchy if this is a fixed node.
	 * @return Node. The node with this hierarchy, or, null if not found.
	 */
	public Node findAttribute(String hierarchy) {
		for (int i = 0; i < attributes.size(); i++) {
		    Node n = attributes.get(i);
			if (n.hierarchy != null && n.hierarchy.equals(hierarchy))
				return n;
		}
		return null;
	}

	/**
	 * Is this creative capped on the item in this bid request?
	 * @param br BidRequest. The bid request to query.
	 * @param capSpecs Map. The current cap spec.
	 * @param adId String. The adId needed to form the key
	 * @return boolean. Returns true if the item is capped, else false.
	 */
	public boolean isCapped(BidRequest br, Map<String, String> capSpecs, String adId) {
		if (frequencyCap == null)
			return false;
		return frequencyCap.isCapped(br,capSpecs,adId.concat("B").concat(impid).concat("_"));
	}

}

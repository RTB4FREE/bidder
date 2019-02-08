package com.jacamars.dsp.rtb.exchanges.bidswitch;

import java.io.InputStream;
import java.util.List;

import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.exchanges.google.GoogleBidRequest;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.BidResponse;
import com.jacamars.dsp.rtb.pojo.Impression;

// https://protocol.bidswitch.com/standards/standards.html
// Bidswitch 5.3anObject

public class Bidswitch extends BidRequest {

	public static final String BIDSWITCH = "bidswitch";
	
	public Bidswitch() {
		super();
		parseSpecial();
	}
	
	   /**
     * Make a Bidswitch bid request using a String.
     * @param in String. The JSON bid request for Epom
     * @throws Exception on JSON errors.
     */
    public Bidswitch(String  in) throws Exception  {
            super(in);
            parseSpecial();
    }

    /**
     * Make a Bidswitch bid request using an input stream.
     * @param in InputStream. The contents of a HTTP post.
     * @throws Exception on JSON errors.
     */
    public Bidswitch(InputStream in) throws Exception {
            super(in);
            parseSpecial();
    }

    /**
     * Create a Bisdwitch bid request from a string builder buffer
     * @param in StringBuilder. The text.
     * @throws Exception on parsing errors.
     */
    public Bidswitch(StringBuilder in) throws Exception {
		super(in);
        multibid = BidRequest.usesMultibids(BIDSWITCH);
		parseSpecial();
	}
    
    /**
	 * Create a new c1x object from this class instance.
	 * @throws Exception on stream reading errors
	 */
	@Override
	public Bidswitch copy(InputStream in) throws Exception  {
		Bidswitch copy = new Bidswitch(in);
		copy.usesEncodedAdm = usesEncodedAdm;
        copy.multibid = BidRequest.usesMultibids(BIDSWITCH);
        return copy;
	}
	
	@Override
	public boolean parseSpecial() {
		setExchange(BIDSWITCH);
		return true;
	}
	
	@Override
	public BidResponse buildNewBidResponse(Impression imp, Campaign camp, Creative creat, double price, String dealId,
			int xtime) throws Exception {
		return new BidswitchBidResponse(this, imp, camp, creat, price, dealId, xtime);
	}

	@Override
	public BidResponse buildNewBidResponse(List<SelectedCreative> list, int xtime) throws Exception {
		return new BidResponse(this, list, xtime);
	}

	

}

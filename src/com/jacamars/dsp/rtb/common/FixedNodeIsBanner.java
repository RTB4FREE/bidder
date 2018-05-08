package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * Determines if this is a banner ad.
 */
public class FixedNodeIsBanner extends Node {

    public FixedNodeIsBanner() {
        super();
        name = "FixedNodeDoBanner";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deal) throws Exception {
        if ((creative.isVideo() == false && creative.isNative() == false) != (imp.nativePart == null && imp.video == null)) {  //NodeIsBanner
            probe.process(br.getExchange(), adId, creative.impid, Probe.BID_CREAT_IS_BANNER);
            if (errorString != null)
                errorString.append(Probe.BID_CREAT_IS_BANNER);
            falseCount.incrementAndGet();
            return false;
        }
        return true;
    }
}

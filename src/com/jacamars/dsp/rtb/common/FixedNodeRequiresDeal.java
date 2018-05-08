package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * Determines if a creative requires a deal.
 */
public class FixedNodeRequiresDeal extends Node {

    public FixedNodeRequiresDeal() {
        super();
        name = "FixedNodeRequiresDeal";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {

        if (creative.price == 0 && (creative.deals == null || creative.deals.size() == 0)) {
            probe.process(br.getExchange(), adId, creative.impid, Probe.DEAL_PRICE_ERROR);
            if (errorString != null) {
                errorString.append(Probe.DEAL_PRICE_ERROR);
            }
            falseCount.incrementAndGet();
            return false;
        }
        return true;
    }
}

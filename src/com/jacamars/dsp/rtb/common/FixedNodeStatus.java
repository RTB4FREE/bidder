package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Format;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * This handles status of the creative.
 */
public class FixedNodeStatus extends Node {

    public FixedNodeStatus() {
        super();
        name = "FixedNodeStatus";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
        if (!creative.ALLOWED_STATUS.equalsIgnoreCase(creative.status)) {
            probe.process(br.getExchange(), adId, creative.impid,Probe.CREATIVE_NOTACTIVE );
            if (errorString != null) {
                errorString.append(Probe.CREATIVE_NOTACTIVE);
            }
            falseCount.incrementAndGet();
            return false;
        }
        return true;
    }
}

package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * Determines if this is a native ad.
 */
public class FixedNodeIsNative extends Node {

    public FixedNodeIsNative() {
        super();
        name = "FixedNodeIsNative";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId,
                        Impression imp, StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
        if (creative.isNative() && imp.nativePart == null) {                                   // NodeIsNative
            probe.process(br.getExchange(), adId, creative.impid, Probe.BID_CREAT_IS_NATIVE);
            if (errorString != null)
                errorString.append(Probe.BID_CREAT_IS_NATIVE);
            falseCount.incrementAndGet();
            return false;
        }
        return true;
    }
}

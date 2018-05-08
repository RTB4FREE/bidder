package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * Handles a non standard. Like appnexus and stroer which require additional configuration before they can be used.
 */
public class FixedNodeNonStandard extends Node {

    public FixedNodeNonStandard() {
        super();
        name = "FixedNodeNonStandard";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {

        boolean test = br.checkNonStandard(creative, errorString);
        if (!test)
            falseCount.incrementAndGet();
        return test;
    }
}

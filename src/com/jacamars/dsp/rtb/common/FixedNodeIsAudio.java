package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * Processes ab audio ad, if possible.
 */
public class FixedNodeIsAudio extends Node {

    public FixedNodeIsAudio() {
        super();
        name = "FixedNodeIsAudio";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
        if (creative.isAudio() && imp.audio == null) {                                           // NodeIsAudin
            probe.process(br.getExchange(), adId, creative.impid, Probe.BID_CREAT_IS_AUDIO);
            if (errorString != null)
                errorString.append(Probe.BID_CREAT_IS_AUDIO);
            falseCount.incrementAndGet();
            return false;
        }
        return true;
    }
}

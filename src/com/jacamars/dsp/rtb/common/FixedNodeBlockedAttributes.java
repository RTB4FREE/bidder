package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Format;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * This handles blocked creative attributes of the creative.
 */
public class FixedNodeBlockedAttributes extends Node {

    public FixedNodeBlockedAttributes() {
        super();
        name = "FixedNodeBlockedAttributes";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp,
                        StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
        if (imp.battr != null) {
        	if (creative.creativeAttributes == null) {
        		probe.process(br.getExchange(), adId, creative.impid,Probe.MISSING_BATTR );
        		if (errorString != null) {
        			errorString.append(Probe.MISSING_BATTR);
        		}
        		falseCount.incrementAndGet();
        		return false;
        	}
        	/**
        	 * Test for intersection, if there is an intersection, then the impression is blocking one of the attributes of the creative
        	 */
        	Set<Integer> copy = new HashSet(imp.battr);
        	copy.retainAll(creative.creativeAttributes);
        	if (copy.size() == 0)
        		return true;
        	if (errorString != null) {
    			errorString.append(Probe.CREATIVE_BLOCKED_ATTRIBUTES);
    		}
        	probe.process(br.getExchange(), adId, creative.impid,Probe.CREATIVE_BLOCKED_ATTRIBUTES );
    		falseCount.incrementAndGet();
    		return false;
        }
        return true;
    }
}

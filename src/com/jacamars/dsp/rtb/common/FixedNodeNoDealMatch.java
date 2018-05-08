package com.jacamars.dsp.rtb.common;

import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import com.jacamars.dsp.rtb.probe.Probe;

import java.util.List;

/**
 * Fixed node implements a chunk of fixed code, once found in the Creative
 * Handles deals
 */

public class FixedNodeNoDealMatch extends Node {

    public FixedNodeNoDealMatch() {
        super();
        name = "FixedNodeNoDealMatch";
    }

    @Override
    public boolean test(BidRequest br, Creative creative, String adId, Impression imp, StringBuilder errorString, Probe probe, List<Deal> deals) throws Exception {
        Deal deal = null;
        if (imp.deals != null) {                                     /// FixedNodeNoDealMatch
            if ((creative.deals == null || creative.deals.size() == 0) && creative.price == 0) {
                probe.process(br.getExchange(), adId, creative.impid, Probe.PRIVATE_AUCTION_LIMITED);
                if (errorString != null)
                    errorString.append(Probe.PRIVATE_AUCTION_LIMITED);
                falseCount.incrementAndGet();
                return false;
            }

            if (creative.deals != null && creative.deals.size() > 0) {
                /**
                 * Ok, find a deal!
                 */
                deal = creative.deals.findDealHighestList(imp.deals);
                if (deal == null && creative.price == 0) {
                    probe.process(br.getExchange(), adId, creative.impid, Probe.PRIVATE_AUCTION_LIMITED);
                    if (errorString != null)
                        errorString.append(Probe.NO_WINNING_DEAL_FOUND);
                    falseCount.incrementAndGet();
                    return false;
                }

              //  if (d != null) {
              //      xprice = new Double(d.price);
              //      dealId = d.id;
              //  }

                if (imp.privateAuction == 1 && deal == null) {
                    if (imp.bidFloor == null || imp.bidFloor > creative.price) {
                        probe.process(br.getExchange(), adId, creative.impid, Probe.PRIVATE_AUCTION_LIMITED);
                        if (errorString != null)
                            errorString.append(Probe.PRIVATE_AUCTION_LIMITED);
                        falseCount.incrementAndGet();
                        return false;
                    }
                }

            } else {
                if (imp.privateAuction == 1) {
                    probe.process(br.getExchange(), adId, creative.impid, Probe.PRIVATE_AUCTION_LIMITED);
                    if (errorString != null)
                        errorString.append(Probe.PRIVATE_AUCTION_LIMITED);
                    falseCount.incrementAndGet();
                    return false;
                }
            }

        } else {
            if (creative.price == 0) {
                probe.process(br.getExchange(), adId, creative.impid, Probe.NO_WINNING_DEAL_FOUND);
                if (errorString != null)
                    errorString.append(Probe.NO_WINNING_DEAL_FOUND);
                falseCount.incrementAndGet();
                return false;
            }
        }
        if (deal != null)
            deals.add(deal);
        return true;
    }

}

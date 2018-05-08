package com.jacamars.dsp.rtb.blocks;

import com.Sample.WeightedItem;
import com.Sample.WeightedItemSelector;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.Weighted;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.tools.AmalgamatedKey;

import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WeightedSelector extends LookingGlass {

    Map<String,Map<String,Integer>> wMap = new ConcurrentHashMap<String, Map<String, Integer>>();
    public WeightedSelector(String name) {
        logger = LoggerFactory.getLogger(WeightedSelector.class);

        symbols.put(name,this);
    }

    public void addWeight(String item) throws Exception {
       String [] parts = item.split(",");
       if (parts.length != 2) {
           throw new Exception("WeightedSelector requires a K/V entry");
       }
       String key = parts[0];
       String [] weights = parts[1].split(";");
       Map<String,Integer> m = new HashMap<String, Integer>();
       for (int i=0; i<weights.length;i++) {
           parts = weights[i].split("=");
           if (parts.length != 2) {
               throw new Exception("WeightedSelector requires weights in K=V; pairs");
           }
           try {
               Integer weight = Integer.parseInt(parts[1]);
               if (weight <= 1)
                   throw new Exception("WeightedSelector requires a weight of >= 1, not " + weights[i]);
               m.put(parts[0], weight);
           } catch (Exception error) {
               throw new Exception("WeightedSelector requires weights in K=INTEGER; pairs");
           }
       }

       wMap.put(key,m);
    }

    public Map<String,Integer> getWeight(String key) {
        Map<String, Integer> m = wMap.get(key);
        return m;
    }

    /**
     * Select a rotating creative, based on the algorithm found in the first creative in the list.
     * @param br BidRequest. This is the bid request being considered.
     * @param list List. A list of selected creatives. If its a proportional, then the list is only 1 deep and
     *             contains the proxy and all its consituents.
     * @return SelectedCreative. The Selected creative to use.
     * @throws Exception on I/O errors.
     */
    public static SelectedCreative applyAlgorithm(BidRequest br, List<SelectedCreative> list) throws Exception {

        Campaign c = list.get(0).getCampaign();
        if (c.weights == null)
            return list.get(0);

        String crid = c.weights.next();
        for (int i=0;i<list.size();i++) {
            SelectedCreative cr = list.get(i);
            if (cr.impid.equals(crid))
                return cr;
        }
        return null;

    }

    public static SelectedCreative applyAlgorithmTBD(BidRequest br, List<SelectedCreative> list) throws Exception {
        SelectedCreative sc = list.get(0);
        Campaign c = sc.getCampaign();
        Creative cr = sc.getCreative();
        String algorithm = c.algorithm;

        /**
         * If the algorithm is null. Then it chooses the first creative in the list. As the creatives
         * are placed in the list randomly. We can just choose the first in the list.
         */
        if (algorithm == null) {
            return sc;
        }

        /**
         * If the algorithm is "fixed", then we just pick the next heavy
         */
        if (algorithm == "fixed") {
            return weightedPickHeavy(list);
        }

        /**
         * Now we choose the rotation based on algorithm name. If null, it means it is undefined, and we use
         * random selection.
         */
        Object ws = symbols.get(algorithm);
        if (ws == null) {
            if (cr.subCreatives == null)
                return list.get(0);
            else {
                // This is an error condition.
                logger.error("Error obtaining algorithm {} for campaign {}, choosing the deault", c.algorithm, c.adId);
                return sc;
            }
        }


        ProportionalRandomCollection rc = (ProportionalRandomCollection)ws;
        if (rc == null) {
            // This is an error condition.
            logger.error("Error obtaining algorithm {} for campaign {}, choosing the deault", c.algorithm, c.adId);
            return sc;
        }

        /**
         * Build the search key, then retrieve the next one.
         */
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c.algorithmKeys.size(); i++) {
            String key = c.algorithmKeys.get(i);
            key = AmalgamatedKey.asString(br.getNode(key));
            sb.append(key);
        }
        String key = sb.toString();
        String creativeName = rc.next(key);
        if (creativeName != null) {
            Creative copy = cr.copy();
            copy.adm = cr.subCreatives.get(creativeName).adm;
            copy.vasturl = cr.vasturl;
            copy.impid = cr.subCreatives.get(creativeName).impid;
            sc.impid = copy.impid;
            sc.setCreative(copy);
        }

        return sc;
    }

    // https://stackoverflow.com/questions/23971365/weighted-randomized-ordering
    public static void weightedSort2(List<Weighted> values) {
        // Calculate the total weight.
        int total = 0;
        for (int i=0;i<values.size(); i++) {
            total += values.get(i).getWeight();
        }
        // Start with all of them.
        List<Weighted> remaining = new ArrayList<Weighted>(values);
        // Take each at random - weighted by it's weight.
        int which = 0;
        do {
            // Pick a random point.
            //int random = (int) (Math.random() * total);
            Random rr = new Random();
            int random = rr.nextInt(total -  1) + 1;
            // Pick one from the list.
            Weighted picked = null;
            int pos = 0;
            for (int i=0;i<remaining.size();i++) {
                Weighted v = remaining.get(i);
                // Pick this ne?
                if (pos + v.getWeight() > random) {
                    picked = v;
                    break;
                }
                // Move forward by that much.
                pos += v.getWeight();
            }
            // Removed picked from the remaining.
            remaining.remove(picked);
            // Reduce total.
            total -= picked.getWeight();
            // Record picked.
            values.set(which++,picked);
        } while (!remaining.isEmpty());
    }

    // https://stackoverflow.com/questions/23971365/weighted-randomized-ordering
    public static SelectedCreative weightedPickHeavy(List<SelectedCreative> values) {
        // Calculate the total weight.
        int total = 0;
        for (int i=0;i<values.size(); i++) {
            total += values.get(i).getWeight();
        }
        // Start with all of them.
        List<SelectedCreative> remaining = new ArrayList<SelectedCreative>(values);
        // Take each at random - weighted by it's weight.
        int which = 0;
        // Pick a random point.
        int random = (int) (Math.random() * total);
        // Pick one from the list.
        SelectedCreative picked = null;
        int pos = 0;
        for (int i=0;i<remaining.size();i++) {
            SelectedCreative v = remaining.get(i);
            // Pick this ne?
            if (pos + v.getWeight() > random) {
                picked = v;
                break;
            }
            // Move forward by that much.
            pos += v.getWeight();
        }
        remaining.remove(picked);
        // Reduce total.
        total -= picked.getWeight();
        // Record picked.
        return picked;

    }
}




package com.jacamars.dsp.rtb.blocks;

import java.io.Serializable;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 * A Navmap of creatives and their weights.
 */
public class ProportionalEntry implements Serializable {
    private final NavigableMap<Double, String> map = new TreeMap<Double, String>();
    private Random random = new Random();
    private double total = 0;

    public ProportionalEntry() {

    }

    public ProportionalEntry(String weights) throws Exception {
        String [] parts = weights.split(",");
        for (String s : parts) {
            s = s.trim();
            String [] t = s.split("=");
            Integer weight = Integer.parseInt(t[1].trim());
            String crid = t[0].trim();
            add(weight,crid);
        }
    }
    /**
     * Add a creative to the weighted collection
     * @param weight Integer. The weight of the creative being added.
     * @param creativeName String. The name of this creative.
     * @return double. The current weight tally.
     */
    public double add(Integer weight, String creativeName) {
        if (weight <= 0) return total;
        total += weight;
        map.put(total, creativeName);
        return total;
    }

    /**
     * Return the next higher entry. This is a random selection of creatives where the heavier ones
     * are selected proportional to the total weight.
     * @return String. The next campaign selected.
     */
    public String next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    @Override
    public String toString() {
        String str = "";
        double d = 0;
        for(Map.Entry<Double, String> entry: map.entrySet()) {
            str += entry.getValue() + ":" + entry.getKey() + ",";
        }
        str = str.substring(0,str.length()-1);
        return str;
    }
}

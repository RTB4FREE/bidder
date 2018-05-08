package com.jacamars.dsp.rtb.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.pojo.BidRequest;

import java.util.zip.CRC32;

/**
 * A class for creating an transaction key composed of:  user.id + device.ip, + CRC32(device.ua) + device.ifa + (device.didsha1 OR device.didmd5) from a bid id.
 * Created by Ben M. Faul on 10/5/17.
 */
public class TidKey {
    /**
     * Build an amalgamated transaction key = ad.id + imp.ip,  + (device.didsha1 OR device.didmd5)
     * @param br BidRequest. The bid request in question.
     * @param adId String. The campaign id.
     * @oaram creat Creative. The creative id.
     * @return String. The key value.
     */
    public static String get(BidRequest br, String adId, Creative creat) {
        String did = null;

        if ((did = TidKey.asString(br.getNode("device.didsha1"))) == null)
            did = TidKey.asString(br.getNode("device.didmd5"));

        StringBuilder key = new StringBuilder();
        key.append(adId);
        key.append(":");
        key.append(creat.impid);
        key.append(":");
        key.append(did);

        return key.toString();
    }

    /**
     * Return a JsonNode value as a key
     * @param obj Object.
     * @return
     */
    public static String asString(Object obj) {
        if (obj == null)
            return null;

        if (obj instanceof JsonNode == false)
            return obj.toString();

        JsonNode node = (JsonNode)obj;
        if (node instanceof MissingNode)
            return null;
        return node.asText();
    }

}

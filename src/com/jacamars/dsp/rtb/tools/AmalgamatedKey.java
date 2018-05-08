package com.jacamars.dsp.rtb.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.jacamars.dsp.rtb.pojo.BidRequest;

import java.util.zip.CRC32;

/**
 * A class for creating an amalgamated key composed of:  user.id + device.ip, + CRC32(device.ua) + device.ifa + (device.didsha1 OR device.didmd5) from a bid id.
 * Created by Ben M. Faul on 10/5/17.
 */
public class AmalgamatedKey {
    /**
     * Build an amalgamated key = user.id + device.ip, + CRC32(device.ua) + device.ifa + (device.didsha1 OR device.didmd5)
     * @param br BidRequest. The bid request in question.
     * @return String. The key value.
     */
    public static String get(BidRequest br) {
        String did = null;

        String userid = AmalgamatedKey.asString(br.getNode("user.id"));
        String ip = AmalgamatedKey.asString(br.getNode("device.ip"));
        String ua = AmalgamatedKey.asString(br.getNode("device.ua"));
        String ifa = AmalgamatedKey.asString(br.getNode("device.ifa"));
        if ((did = AmalgamatedKey.asString(br.getNode("device.didsha1"))) == null)
            did = AmalgamatedKey.asString(br.getNode("device.didmd5"));

       if (ua != null) {
            CRC32 crc = new CRC32();
            crc.update(ua.getBytes());
            ua = Long.toString(crc.getValue());
        }
        StringBuilder key = new StringBuilder();
        key.append(userid);
        key.append(":");
        key.append(ip);
        key.append(":");
        key.append(ua);
        key.append(":");
        key.append(ifa);
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

package com.jacamars.dsp.rtb.tools;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by ben on 9/28/17.
 */
public class ListIP {

    public static void main(String [] args) throws Exception {
        Multiset<String> ms = HashMultiset.create();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String fileName = "/media/twoterra/requests";
        String data;
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        double noDevice = 0;
        double noIp = 0;
        double total = 0;

        while((data = br.readLine()) != null) {
            Map m =  mapper.readValue(data, Map.class);
            m =  (Map)m.get("device");
            if (m != null) {
                String id = (String) m.get("ip");
                if (id != null) {
                    ms.add(id);
                } else
                    noIp++;
            } else {
                noDevice++;
            }
            total++;
        }

        double count = ms.size();
        int k = 0;
        double adx = 0;
        double mt3 = 0;
        System.out.println("REQUESTS: " + total);
        System.out.println("TOTAL = " + count + "/" + (count/total));
        System.out.println("NO DEVICE: " + noDevice);
        System.out.println("NO IP IN DEVICE: " + noIp);
        double tc = 0;
        for (String ip : Multisets.copyHighestCountFirst(ms).elementSet()) {
            double x = ms.count(ip);
            double perc = 100 * x / count;
            mt3++;
            if (x > 3) {
                tc += perc;
            }
            if (ip.endsWith(".0")) {
                adx++;
            }
            if (k++ < 20)
                System.out.println(ip + ": " + ms.count(ip) + ", perc = " + perc);
        }
        System.out.println("ADX = " + adx + ", " + adx/mt3);
        System.out.println("TC = " + tc);
    }
}

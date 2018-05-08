package com.jacamars.dsp.rtb.tools;

import java.util.List;

/**
 * A simple program to print IP addresses on this instance.
 */
public class ReportIp {

    public static void main(String [] args) throws Exception {

        List<String> addrs = Performance.getIP4Addresses();
        System.out.println(addrs);
    }
}

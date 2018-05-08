package com.jacamars.dsp.rtb.tools;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.openrtb.OpenRtb;
import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.exchanges.adx.Base64;
import com.jacamars.dsp.rtb.exchanges.google.GoogleBidRequest;
import com.jacamars.dsp.rtb.exchanges.google.GoogleBidResponse;

import test.java.Config;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GoogleTestArul {

    public static void main(String[] args) throws Exception {

        OpenRtb.BidRequest bidRequest = OpenRtb.BidRequest.newBuilder().setId("747CEAC3220563C477722").setAt(OpenRtb.AuctionType.SECOND_PRICE)
                .addImp(OpenRtb.BidRequest.Imp.newBuilder()
                        .setId("83187405165")
                        .setInstl(false)
                        .setBanner(OpenRtb.BidRequest.Imp.Banner.newBuilder()
                                .setId("1")
                                .setW(300)
                                .setH(250)
                                .build())
                        .build())
                .setSite(OpenRtb.BidRequest.Site.newBuilder()
                        .setName("BidderTestMobileWEB")
                        .setDomain("washingtonpost.com")
                        .setPage("hhttps://blogs.wsj.com/puzzle/2017/08/18/sleep-over-saturday-crossword-august-19")
                        .build())
                .setDevice(OpenRtb.BidRequest.Device.newBuilder()
                        .setGeo(OpenRtb.BidRequest.Geo.newBuilder()
                                .setZip("21228")
                                .setCountry("USA")
                                .setCity("Catonsville")
                                .setMetro("512")
                                .setRegion("US-MD")
                                .build())
                        .build())
                .build();

        System.out.println("-------------------------------------------------------");
        System.out.println("Google's ORTB Request");
        System.out.println("-------------------------------------------------------");
        System.out.println(bidRequest.toString());
        System.out.println("-------------------------------------------------------");

        HttpPostGet http = new HttpPostGet();

        byte[] returns = http.sendPost("http://18.221.87.237:8080/rtb/bids/google", bidRequest.toByteArray(), 20000, 2500); // production endpoint
        int code = http.getResponseCode();
        System.out.println("-------------------------------------------------------");
        System.out.println("Google's ORTB Response");
        System.out.println("-------------------------------------------------------");
        System.out.println("-----------Http code " + code + " --------------------------");
        if (code == 200) {
            GoogleBidResponse rr = new GoogleBidResponse(returns);
            System.out.println(rr.getInternal());
        }
        System.exit(0);
    }
}
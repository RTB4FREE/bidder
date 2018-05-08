package com.jacamars.dsp.rtb.tools;

import com.jacamars.dsp.rtb.exchanges.google.GoogleWinObject;
import com.jacamars.dsp.rtb.exchanges.openx.OpenXWinObject;

import java.io.BufferedReader;
import java.io.FileReader;

/** A class for decoding encypted win data: timestamp,site,exchange,encrypted-price
 *  Outputs: timestamp,site,exchange,encrypted-price,decrypted-price
 * Created by ben on 10/14/17.
 */
public class DecodeWinData {

    static String g_ekey = "kB8RQtv1rlArbt1YFRoHUiCn3mtP3d88VdfRRT+ujMA=";
    static String g_ikey = "+b5gBm7mJcmZgF/YT4bxZoQUsU+vpwqm2sShqc5rcPk=";

    static String o_ekey = "7EC9482F7636405087F6EC9851ED0E3D376182163B6646F69E9527EB9F45BE12";
    static String o_ikey = "6E5240C73E4A48FF961E943925BA3C5D9E9810BB918E4FB5B949BE98CED2E9F6";

    public static void main(String [] args) throws Exception {
        GoogleWinObject.encryptionKeyBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(g_ekey);
        GoogleWinObject.integrityKeyBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(g_ikey);

        OpenXWinObject.setKeys(o_ekey,o_ikey);

        BufferedReader br = null;

        // BufferedReader br = new BufferedReader(new FileReader("/media/twoterra/out_openx.txt"));

        String fileName = null;
        String exchange = null;

        int i = 0;
        while(i<args.length) {
            switch(args[i]) {
                case "-f":
                    fileName = args[i+1];
                    i+=2;
                    break;
                case "-e":
                    exchange = args[i+1];
                    i+=2;
                    break;
                case "-h":
                    System.out.println("-e exchange -f fileName");
                    System.exit(1);
                default:
                    System.out.println("Huh? " + args[i]);
                    System.exit(1);

            }
        }
        br = new BufferedReader(new FileReader(fileName));

        String line = null;
        double d = 0;
        while((line=br.readLine()) != null) {
            String [] parts = line.split(",");
           if (parts[3].equals("AUDIT"))
                d = 0;
            else {
               if (exchange.equals("google"))
                   d = GoogleWinObject.decrypt(parts[3], System.currentTimeMillis());
               else
                   d = OpenXWinObject.decrypt(parts[3]);
               d = d / 1000;
           }
            line = line + "," + d;
            System.out.println(line);
        }
    }
}

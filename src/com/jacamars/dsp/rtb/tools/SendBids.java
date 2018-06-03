package com.jacamars.dsp.rtb.tools;

import com.jacamars.dsp.rtb.common.HttpPostGet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Test program for loading maximum bids into a RTB4FREE bidder.
 * @author Ben M. Faul
 *
 */
public class SendBids  {

	public static void main(String [] args) throws  Exception {
		int i = 0;
		String host = "localhost";
		String port = "8080";
		String exchange = "openx";
		String fileName = "SampleBids/rotateweights.txt";
		int count = 100;
		boolean multi = false;

		while(i<args.length) {
			switch(args[i]) {
			case "-h":
				System.out.println("-h                  [This message                               ]");
				System.out.println("-host host-or-ip    [Where to send the bid (default is localhost]");
				System.out.println("-port n             [Port number, default is 8080               ]");
				System.out.println("-exchange name      [Name of exchange, default is nexage        ]");
				System.out.println("-file f name        [The filename to use                        ]");
				System.out.println("-count n            [Number to send                             ]");
				System.out.println("-multi _            [Overrides count, file has multiple bids    ]");
			case "-host":
				host = args[i+1];
				i+=2;
				break;
			case "-port":
				port = args[i+1];
				i+= 2;
				break;
			case "-exchange":
				exchange = args[i+1];
				i+=2;
				break;
			case "-file":
				fileName = args[i+1];
				i+=2;
				break;
			case "-count":
				count = Integer.parseInt(args[i+1]);
				i+=2;
				break;
                case "-multi":
                    multi = true;
                    i++;
                    break;
			default:
				System.err.println("Huh? " + args[i]);
			}
		}

		HttpPostGet post = new HttpPostGet();
		String url = "http://" + host + ":" + port + "/rtb/bids/" + exchange;
		if (exchange.equals("appnexus")) {
		    url =  "http://" + host + ":" + port + "/rtb/" + exchange;
        }
        String content = null;
		BufferedReader br = null;
		if (!multi) {
		    content = new String(Files.readAllBytes(Paths
                    .get(fileName)), StandardCharsets.UTF_8);
            for (i=0;i<count;i++) {
                String bid = content;
                String id = UUID.randomUUID().toString();
                bid = bid.replaceAll("1234",id);
                String rc = post.sendPost(url, bid,1000,1000);
                if (rc != null)
                    System.out.println(rc);
            }
        } else {
            br = new BufferedReader(new FileReader(fileName));
            String bid = null;
            while((bid=br.readLine()) != null) {
                System.out.println(bid);
                String rc = post.sendPost(url, bid,1000,1000);
                if (rc != null)
                    System.out.println(rc);
            }
        }

	}
}

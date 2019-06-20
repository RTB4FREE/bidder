package test.java;

import static org.junit.Assert.fail;

import com.jacamars.dsp.rtb.bidder.RTBServer;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.services.Zerospike;
import com.jacamars.dsp.rtb.shared.FrequencyGoverner;
import com.jacamars.dsp.rtb.tools.Commands;
import com.jacamars.dsp.rtb.tools.DbTools;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Map;


/**
 * The JUNIT common configuration is done here. Start the server if not running. If it is running, then
 * reload the campaigns from REDIS as tests can monmkey with them.
 *
 * @author Ben M. Faul
 *
 */
public class Config {

	/** The hostname the test programs will use for the RTB bidder */
	public static final String testHost = "localhost:8080";
  public static final String testPayday = "Campaigns/payday-dev.json";
  public static final String testDatabase = "database.json";

	static String redisHost;

	static {
  	try {
  		String content = new String(Files.readAllBytes(Paths.get(testPayday)), StandardCharsets.UTF_8);
  		Map test =  DbTools.mapper.readValue(content, Map.class);
  	} catch (Exception error) {
  		error.printStackTrace();
  	}
	}

	static DbTools tools;

	/** The RTBServer object used in the tests. */
	static RTBServer server;

  public static void setupNoFg() throws Exception {
    try {

      new Zerospike(6000, 6001, 6002, "cache.db", "http://[localhost:9092]", false, 1);

      FrequencyGoverner.silent = true;

      Commands c = new Commands("localhost", 6000, 6001, 6002);
      c.loadDatabase(testDatabase);

      if (server == null) {
        server = new RTBServer(testPayday);
        int wait = 0;
        while(!server.isReady() && wait < 10) {
          Thread.sleep(1000);
          wait++;
        }
        if (wait == 10) {
          fail("Server never started");
        }

        Thread.sleep(1000);
      }
      c.sendLoadCampaign("*","*");
    } catch (Exception e) {
        e.printStackTrace();
        fail(e.toString());
    }
  }

	public static void setup() throws Exception {
		try {

			new Zerospike(6000, 6001, 6002, "cache.db", "http://[localhost:9092]", false, 1);

			FrequencyGoverner.silent = true;

			Commands c = new Commands("localhost", 6000, 6001, 6002);
			c.loadDatabase(testDatabase);
			if (server == null) {
				server = new RTBServer(testPayday);
				int wait = 0;
				while(!server.isReady() && wait < 10) {
					Thread.sleep(1000);
					wait++;
				}
				if (wait == 10) {
					fail("Server never started");
				}

				Thread.sleep(1000);
			}
			c.sendLoadCampaign("*","*");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

  public static void setupBucket(String exchanges) throws Exception {
    try {

        new Zerospike(6000, 6001, 6002, "cache.db", "http://[localhost:9092]", false, 1);

        FrequencyGoverner.silent = true;

        Commands c = new Commands("localhost", 6000, 6001, 6002);
        c.loadDatabase(testDatabase);


        if (server == null) {
            server = new RTBServer(testPayday,  "", 8080, 8081, exchanges);
            int wait = 0;
            while(!server.isReady() && wait < 10) {
                Thread.sleep(1000);
                wait++;
            }if (wait == 10) {
                fail("Server never started");
            }

            Thread.sleep(1000);
        }
        c.sendLoadCampaign("*","*");
    } catch (Exception e) {
        e.printStackTrace();
        fail(e.toString());
    }
  }

	public static void setup(String configFile) throws Exception {
		try {

            new Zerospike(6000, 6001, 6002, "cache.db", "http://[localhost:9092]", false, 1);

            FrequencyGoverner.silent = true;


			Commands c = new Commands("localhost", 6000, 6001, 6002);
			c.loadDatabase(testDatabase);

			if (server == null) {
				server = new RTBServer(configFile);
				int wait = 0;
				while(!server.isReady() && wait < 10) {
					Thread.sleep(1000);
					wait++;
				}
				if (wait == 10) {
					fail("Server never started");
				}
				c.sendLoadCampaign("*","*");
				Thread.sleep(1000);

			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	/**
	 * JUNIT Test configuration for shards.
	 *
	 */
	public static void setup(String shard, int port) throws Exception {
		try {
			if (server == null) {
                new Zerospike(6000, 6001, 6002, "cache.db", "http://[localhost:9092]", false, 1);

				Commands c = new Commands("localhost", 6000, 6001, 6002);
				c.loadDatabase("database.json");

				server = new RTBServer(testPayday, shard, port, port + 1,null);
				int wait = 0;
				while(!server.isReady() && wait < 10) {
					Thread.sleep(1000);
					wait++;
				}
				if (wait == 10) {
					fail("Server never started");
				}
				c.sendLoadCampaign("*","*");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public static void teardown() {
//		if (server != null) {
//			server.halt();
//		}
		Configuration c = Configuration.getInstance();
		if (c != null)
			c.clearCampaigns();
	}
}

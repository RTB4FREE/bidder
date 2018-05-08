package test.java;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jacamars.dsp.rtb.bidder.AbortableCountDownLatch;
import com.jacamars.dsp.rtb.bidder.CampaignProcessor;
import com.jacamars.dsp.rtb.bidder.SelectedCreative;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.geo.GeoTag;
import com.jacamars.dsp.rtb.geo.Solution;
import com.jacamars.dsp.rtb.pojo.BidRequest;

import junit.framework.TestCase;

/**
 * A class to test the Geo tagged node subclass works properly.
 * @author Ben M. Faul
 *
 */
public class TestGeoNode  {

	@BeforeClass
	public static void setup() {
		System.out.println("******************  TestGeoNode");
	}
	/**
	 * Test the solution doesn't return null
	 * @throws Exception on file errors
	 */
	@Test
	public void testSolution() throws Exception {
		GeoTag z = new GeoTag();
		z.initTags("data/zip_codes_states.csv",
					"data/unique_geo_zipcodes.txt");
		Solution s = z.getSolution(42.378,71.227);
		assertNotNull(s);
		//System.out.println(s.toString());
	}
}

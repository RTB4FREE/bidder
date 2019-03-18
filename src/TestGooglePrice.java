import static org.junit.Assert.assertTrue;

import com.jacamars.dsp.rtb.commands.PixelClickConvertLog;

public class TestGooglePrice {

	public static void main(String [] args) {
		String pixel = "/pixel/exchange=google/ad_id=1419/creative_id=2258/price=WeaZ_gAJnSoK0xGqAAKHTBlbhHqTkOzPFSNZJA/bid_id=WeaZ%2FwAKLUQKUYvLxw53mA/site_domain=muslima.com";
		PixelClickConvertLog log = new PixelClickConvertLog();
		log.create(pixel);
		System.out.println(log.price);;
	}
}

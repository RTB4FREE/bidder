import static org.junit.Assert.assertTrue;

import com.jacamars.dsp.rtb.commands.PixelClickConvertLog;
import com.jacamars.dsp.rtb.exchanges.adx.AdxBidRequest;
import com.jacamars.dsp.rtb.exchanges.adx.AdxWinObject;
import com.jacamars.dsp.rtb.pojo.BidRequest;

public class TestGooglePrice {

	public static void main(String [] args) {
		//String pixel = "/pixel/exchange=google/ad_id=1419/creative_id=2258/price=WeaZ_gAJnSoK0xGqAAKHTBlbhHqTkOzPFSNZJA/bid_id=WeaZ%2FwAKLUQKUYvLxw53mA/site_domain=muslima.com";
		BidRequest.setUsesPiggyBackWins("adx");
		//AdxBidRequest.setCrypto("=","=");
		String pixel= "type=pixel&exchange=adx&ad_id=3&creative_id=12&bid_id=5cafa07e0acb2ca165cf8e114d9&price=XK-09gAJk3gKZQz1AA6E7OWbN0vJKCU5x-UH_w&lon=-96.807";
		
		PixelClickConvertLog log = new PixelClickConvertLog();
		log.create(pixel);
		log.handleFakeWin();
		System.out.println(log.price);;
	}
}

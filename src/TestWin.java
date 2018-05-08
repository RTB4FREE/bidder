import com.jacamars.dsp.rtb.pojo.WinObject;

/**
 * Created by ben on 8/9/17.
 */
public class TestWin {

    public static void main(String [] args) throws Exception {
        String test = "http://bidder3-europe.c1exchange.com/rtb/win/google/WYuRWwAGdVEK0x94AANFDPfZEZaM1Qrm-d-XrQ/0.0/0.0/623/929/WYuRWwAGdVEK0x949QNFDA";
        WinObject.getJson(test);
    }
}

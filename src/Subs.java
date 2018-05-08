import com.jacamars.dsp.rtb.common.Configuration;

public class Subs {

    public static void main(String args[]) throws Exception {

        String test = "This is a test of a very long string, the address is $IPADDRESS#docker0# this should have worked!";

        test = Configuration.GetIpAddressFromInterface(test);
        System.out.println(test);
    }
}

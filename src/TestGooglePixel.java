import com.jacamars.dsp.rtb.common.HttpPostGet;
import com.jacamars.dsp.rtb.pojo.WinObject;

/**
 * Created by ben on 9/24/17.
 */
public class TestGooglePixel {

    public static void main(String [] args) throws Exception {
        String content = "rtb/win/google/Wcbj_wADv1UKjqJGAAuQYmZXqeejagjY798QeQ/0.0/0.0/1179/1727/Wcbj/wADv1UKjqJGyQuQYg";
        HttpPostGet hp = new HttpPostGet();

        String url = "http://localhost:8080/" + content;
        hp.sendGet(url,300000,30000);

        //WinObject.getJson(url);
    }
}

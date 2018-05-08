import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.redisson.RedissonClient;
import com.jacamars.dsp.rtb.services.Zerospike;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestZerospike {

    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setDefaultPropertyInclusion(
                JsonInclude.Value.construct(JsonInclude.Include.ALWAYS, JsonInclude.Include.NON_NULL));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void main(String [] args) throws Exception {

        Map x = new HashMap();
        x.put(null,"HEllo");
        x.put("Ben","Faul");
        System.out.println(x.get(null));

        x.remove(null);
        String s = mapper.writeValueAsString(x);
        System.out.println(s);

    }
}



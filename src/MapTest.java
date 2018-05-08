import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacamars.dsp.rtb.common.Campaign;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.tools.DbTools;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;


public class MapTest {
    public static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
	public static void main(String args[]) throws Exception {

        HTreeMap objects;
        DB db = DBMaker
                .fileDB("junk.db")
                .fileMmapEnable()            // Always enable mmap
                .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
                .fileMmapPreclearDisable()   // Make mmap file faster

                // Unmap (release resources) file when its closed.
                // That can cause JVM crash if file is accessed after it was unmapped
                // (there is possible race condition).
                .cleanerHackEnable()
                .transactionEnable()
                .make();

//optionally preload file content into disk cache
        db.getStore().fileLoad();

        objects = db.hashMap("objects").createOrOpen();

        double time = System.currentTimeMillis();

        Map m = new HashMap();

        for (int i=0;i<1000000;i++) {
            objects.put("ben", "faul");
        }

        time = System.currentTimeMillis() - time;

        time = time/1000;
        double d = 1000000/time;

        System.out.println("D: " + d + " writes/second");

        db.close();

	}
}

package com.jacamars.dsp.rtb.blocks;

import com.amazonaws.services.s3.model.S3Object;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.*;
import java.util.*;

/**
 * Proportional Random Collection selector. Probability of selection of a weighted entity is proportional to the total weight in the set.
 * Ben M. Faul
 */
public class ProportionalRandomCollection extends LookingGlass implements Serializable {
    /** Last entry accessesd */
    private ProportionalEntry lastE;
    /** Last key used to obtain the last entry */
    private String lastKEY;

    /** The treemap of proportional navmaps */
    HTreeMap<String, ProportionalEntry> dbMap;
    /** Offheap storage for the navmam */
    DB db = DBMaker.tempFileDB()
            .fileMmapEnable()            // Always enable mmap
            .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
            .fileMmapPreclearDisable()   // Make mmap file faster
            // Unmap (release resources) file when its closed.
            // That can cause JVM crash if file is accessed after it was unmapped
            // (there is possible race condition).
            .cleanerHackEnable()
            .make();

    /**
     * Constructor used a file name.
     *
     * @param name String. The name of the symbol in the symbol table this will be stored as.
     * @param file String. The filename containing the data.
     * @throws Exception on file I/O exceptions.
     */
    public ProportionalRandomCollection(String name, String file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        ;
        String message = "Initialize ProportionalRandomCollection: " + file + " as " + name;
        makeFilter(br);
        symbols.put(name, this);
        logger.info("{}", message);
    }

    /**
     * Constructor for the S3 version of the ProportionalRandomCollection.
     *
     * @param name   String. The name of the object.
     * @param object S3Object. The object that contains the file.
     * @throws Exception on S3 errors.
     */
    public ProportionalRandomCollection(String name, S3Object object) throws Exception {
        InputStream objectData = object.getObjectContent();
        String message = "Initialize ProportionalRandomCollection: " + object.getBucketName() + " as " + name;
        BufferedReader br = new BufferedReader(new InputStreamReader(objectData));
        makeFilter(br);
        symbols.put(name, this);
        logger.info("{}", message);
    }

    /**
     * Constructor for a hand-made ProportionalRandomCollection.
     *
     * @param name String. The name of the symbol.
     * @throws Exception on file i/o errors opening the navamp file off-heap.
     */
    public ProportionalRandomCollection(String name) throws Exception {
        String message = "Initialize ProportionalRandomCollection: " + name + " as " + name;
        symbols.put(name, this);
        logger.info("{}", message);
        dbMap = (HTreeMap<String, ProportionalEntry>) db.hashMap("scratch").create();
        dbMap.clear();
    }

    /**
     * Use a buffered reader to load the entries into the collection.
     *
     * @param br BufferedReader. The file reader object.
     * @throws Exception on I/O errors.
     */
    void makeFilter(BufferedReader br) throws Exception {
        String line;
        dbMap = (HTreeMap<String, ProportionalEntry>) db.hashMap("scratch").create();
        dbMap.clear();
        final List<String> throwThis = new ArrayList<String>();
        while ((line = br.readLine()) != null) {
            if (throwThis.size()>0)
                throw new Exception(throwThis.get(0));

            final String sline = line;
            Runnable task = ()  -> {
                final String[] parts = eatquotedStrings(sline);
                if (parts.length < 2) {
                    throwThis.add("ProportionalRandomCollection requires a K/V entry");
                    return;
                }
                String key = parts[0];

                ProportionalEntry e = new ProportionalEntry();

                for (int i = 1; i < parts.length; i++) {
                    String[] wparts = parts[i].split("=");
                    if (wparts.length != 2) {
                        throwThis.add("ProportionalRandomCollection requires weights in K=V,... pairs");
                        return;
                    }
                    try {
                        Integer weight = Integer.parseInt(wparts[1]);
                        if (weight <= 1) {
                            throwThis.add("ProportionalRandomCollection requires a weight of >= 1, not " + wparts[1]);
                            return;
                        }
                        e.add(weight, wparts[0]);
                    } catch (Exception error) {
                        throwThis.add("ProportionalRandomCollection requires weights in K=INTEGER,... pairs");
                        return;
                    }
                }

                dbMap.put(key, e);

            };
            Thread thread = new Thread(task);
            thread.start();
        }
        br.close();
    }

    /**
     * Add an entry to the collection of proportional weights. Rememebers the last key used and the element it
     * resolves to. WARNING: Calls commit if lastE is not null.
     * @param key1 String. The bid request derived key.
     * @param key2 String. The creative name
     * @param weight Integer. The weight of the creative
     */
    public void addEntry(String key1, String key2, Integer weight) {
        ProportionalEntry e = dbMap.get(key1);
        if (e == null) {
            if (lastE != null)
                commit();
            e = new ProportionalEntry();
        }
        e.add(weight, key2);
        lastE = e;
        lastKEY = key1;
    }

    /**
     * Add an entry to the collection of proportional weights. Uses the last key. If last key is null it will
     * throw an exception.
     * @param key2 String. The creative name.
     * @param weight Integer. The weight of this creative.
     * @throws Exception
     */
    public void addEntry(String key2, Integer weight) throws Exception {
        if (lastE == null)
            throw new Exception("Don't know which entry to update");
        lastE.add(weight, key2);
    }

    /**
     * Commit changes to the offheap storage. Always call commit at the end of a series of commits, or you will
     * lose the data!
     */
    public void commit()  {
        dbMap.put(lastKEY, lastE);
    }

    public String next(String key1)  {
        ProportionalEntry e = dbMap.get(key1);
        if (e == null) {
            lastE = null;
            return null;
        }
        lastE = e;
        return e.next();
    }

    /**
     * Return the next heavy item in the list. Returns null if the list of entries is null.
     */
    public String next()  {
        if (lastE == null)
            return null;
        return lastE.next();
    }

    /**
     * Returns the number of bid request derived keys there are in the offheap storage..
     * @return long. The number of keys in the store.
     */
    public long getMembers() {
        return dbMap.getSize();
    }

    /**
     *
     * @param key Object. The key to use in the lookup for this object.
     * @return String. The next heayy creative.
     */
    @Override
    public String query(Object key) {
        return next((String)key);
    }
}




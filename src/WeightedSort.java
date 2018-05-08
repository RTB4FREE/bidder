import com.jacamars.dsp.rtb.blocks.ProportionalRandomCollection;
import com.jacamars.dsp.rtb.common.Weighted;

import org.github.jamm.MemoryMeter;


import java.io.Serializable;
import java.util.*;


// JVM ARGS: -javaagent:target/XRTB-0.0.1-SNAPSHOT-jar-with-dependencies.jar
public class WeightedSort {

    public static void main(String [] args) throws Exception {

        MemoryMeter meter = new MemoryMeter();

        double time = System.nanoTime();
        List<Weighted> x = new ArrayList<Weighted>();
        x.add(new Thing("Large",100));
        x.add(new Thing("XLarge",1000));
        x.add(new Thing("Small",75));
        for (int i=0;i<100;i++) {
            x.add(new Thing("X" + i, i+0));
        }

        x.add(new Thing("SuperLarge", 11000));

        System.out.print("TIME=" + time/1000 );
        System.out.println();

        for (int i=0;i<100;i++) {
            time = System.nanoTime();
            Thing z = (Thing)weightedPickHeavy(x);
            time = System.nanoTime() - time;
            System.out.print("" + time/1000 + " = ");
            System.out.print(z.name + " ");
            System.out.println();
        }

        System.out.println("OBJECT SIZE: " + meter.measureDeep(x));

        System.out.println("-------------------------");


        ProportionalRandomCollection prc = new ProportionalRandomCollection("testing");

        double now = System.currentTimeMillis();
        for (int z=0;z<1000000;z++) {
            prc.addEntry("line"+z, "Large", 100);
            prc.addEntry("XLarger", 1000);
            prc.addEntry("Small", 75);
            for (int i = 1; i < 101; i++) {
                prc.addEntry("X" + i, i);
            }
        }
        now = System.currentTimeMillis() - now;
        now /= 60000;
        System.out.println("Setup: " + now);

        double total = 0;
        String s = null;
        for (int i=0;i<1000;i++) {
            time = System.nanoTime();
            s = prc.next("line1");
            time = System.nanoTime() - time;
            time /= 1000;
            total += time;
        }
        System.out.println("LOOKUP: '" + s + "' in " + total/1000);

        System.out.println("OBJECT SIZE: " + meter.measureDeep(prc));

    }


    // https://stackoverflow.com/questions/23971365/weighted-randomized-ordering
    public static void weightedSort2(List<Weighted> values) {
        // Calculate the total weight.
        int total = 0;
        for (int i=0;i<values.size(); i++) {
            total += values.get(i).getWeight();
        }
        // Start with all of them.
        List<Weighted> remaining = new ArrayList<Weighted>(values);
        // Take each at random - weighted by it's weight.
        int which = 0;
        do {
            //Pick a random point.
            int random = (int) (Math.random() * total);
           // Random rr = new Random();
           // int random = rr.nextInt(total -  1) + 1;
            // Pick one from the list.
            Weighted picked = null;
            int pos = 0;
            for (int i=0;i<remaining.size();i++) {
                Weighted v = remaining.get(i);
                // Pick this ne?
                if (pos + v.getWeight() > random) {
                    picked = v;
                    break;
                }
                // Move forward by that much.
                pos += v.getWeight();
            }
            // Removed picked from the remaining.
            remaining.remove(picked);
            // Reduce total.
            total -= picked.getWeight();
            // Record picked.
            values.set(which++,picked);
        } while (!remaining.isEmpty());
    }

    // https://stackoverflow.com/questions/23971365/weighted-randomized-ordering
    public static Weighted weightedPickHeavy(List<Weighted> values) {
        // Calculate the total weight.
        int total = 0;
        for (int i=0;i<values.size(); i++) {
            total += values.get(i).getWeight();
        }
        // Start with all of them.
        List<Weighted> remaining = new ArrayList<Weighted>(values);
        // Take each at random - weighted by it's weight.
        int which = 0;
            // Pick a random point.
            int random = (int) (Math.random() * total);
            // Pick one from the list.
            Weighted picked = null;
            int pos = 0;
            for (int i=0;i<remaining.size();i++) {
                Weighted v = remaining.get(i);
                // Pick this ne?
                if (pos + v.getWeight() > random) {
                    picked = v;
                    break;
                }
                // Move forward by that much.
                pos += v.getWeight();
            }
            remaining.remove(picked);
            // Reduce total.
            total -= picked.getWeight();
            // Record picked.
            return picked;

    }
}

class Thing implements Weighted, Serializable {
    String name;
    int value;

    public Thing(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getWeight() {
        return value;
    }
}
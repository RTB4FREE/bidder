// import jep.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class TestJep {

   /* Jep jep = new Jep(false);

    public static void main(String [] args) throws Exception {



        String content = new String(Files.readAllBytes(Paths.get("SampleBids/nexage.txt")), StandardCharsets.UTF_8);

        String code = "def foo(a,b,c,d):\n" +
                      "  return a+b+c+d";
        TestJep z = new TestJep(code);


        long time = 0;
        Object x = null;
        for (int i=0;i<10;i++) {
            time = System.currentTimeMillis();
            z.setRequest(content);
            z.eval("python_obj = json.loads(data)");
            boolean a = z.eval("a = foo(1,2,3,4)");
            x = z.getValue("python_obj['id']");
            time = System.currentTimeMillis() - time;
        }
       // jep.set("z",z);
        //jep.eval("print(z)");
       // for (int i=0;i<10;i++) {
         //   jep.set("ben", "faul");
       // }

        System.out.println("Time:"+time+",a="+x);
    }

    public TestJep(String func) throws Exception {
        jep.eval("import json");
        jep.eval("from java.util import ArrayList, HashMap");
        jep.eval(func);
    }

    public void setRequest(String br) throws Exception {
        jep.set("data",br);
    }

    public boolean eval(String str) throws Exception {
        return jep.eval(str);
    }

    public Object getValue(String name) throws Exception {
        return jep.getValue(name);
    } */
}

package com.jacamars.dsp.rtb.pojo;

import com.jacamars.dsp.rtb.common.Creative;

/** A class that encapsulates a format object, sometimes found in the banner object
 * Created by Ben M. Faul on 8/11/17.
 */
public class Format {
    public int w;
    public int h;

    public Format() {

    }

    public Format(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public static Format findFit(Impression imp, Creative cr) {
        if (imp.format == null || imp.format.size() == 0)
            return null;

        if (cr.w != null) {
            for (int i=0;i<imp.format.size();i++) {
                Format f = imp.format.get(i);
                if (f.w == cr.w && cr.h == f.h)
                    return f;
            }
        }

        if (cr.dimensions != null && cr.dimensions.size() > 0) {
            for (int i=0;i<imp.format.size();i++) {
                Format f = imp.format.get(i);
                if (cr.dimensions.getBestFit(f.w, f.h) != null)
                    return f;
            }
        }
        
        if (cr.w != null && imp.w != null && 
        		cr.h != null && imp.h != null &&
        		cr.w.intValue() == imp.w.intValue() && cr.h.intValue() == imp.h.intValue()) {
        	Format f = new Format(cr.w,cr.h);
        	return f;
        }
        
        

        return null;
    }
}

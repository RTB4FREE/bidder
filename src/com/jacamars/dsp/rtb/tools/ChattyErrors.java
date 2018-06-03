package com.jacamars.dsp.rtb.tools;

import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ChattyErrors {
    static Map<String,Long> warningMessages = new HashMap();
    static Map<String,Long> errorMessages = new HashMap();

    public static void printWarningEveryMinute(Logger logger, String message) {
        Long time = warningMessages.get(message);
        if (time == null) {
            logger.warn(message);
            warningMessages.put(message,new Long(System.currentTimeMillis()));
            return;
        }

        if ((System.currentTimeMillis() - time) > 5000) {
            logger.warn(message);
            warningMessages.put(message, new Long(System.currentTimeMillis()));
        }
    }

    public static void printErrorEveryMinute(Logger logger, String message) {
        Long time = errorMessages.get(message);
        if (time == null) {
            logger.error(message);
            errorMessages.put(message,new Long(System.currentTimeMillis()));
            return;
        }

        if ((System.currentTimeMillis() - time) > 5000) {
            logger.warn(message);
            errorMessages.put(message, new Long(System.currentTimeMillis()));
        }
    }

}

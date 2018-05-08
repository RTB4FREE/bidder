package com.jacamars.dsp.rtb.jmq;

import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * A class that implements a pipe tailer.
 * @author Ben M. Faul
 */
public class PipeTailer  implements Runnable {

    /** The thread this class runs on */
    Thread me = null;
    /** The name of the pipe */
    String fileName;
    /** An adaptor that implements the apache handler */
    TailerListenerAdapter handler;

    /**
     * Create a named pipe subscriber
     * @param handler TailerListenerAdaptor. The parent, the thing that consumes the message.
     * @param fileName String. The name of the pipe.
     * @throws Exception if the file does not exist or what is named is not a file.
     */
    public PipeTailer(TailerListenerAdapter handler, String fileName) throws Exception {
        this.handler = handler;
        this.fileName = fileName;
        File f_pipe = new File(fileName);
        if (!f_pipe.exists())
            throw new Exception("Named pipe: " + fileName + " does not exist");
        if (!f_pipe.isFile()==false)
            throw new Exception(fileName + " is not a pipee");
        me = new Thread(this);
        me.start();

    }

    /**
     * Run until interrupted.
     */
    public void run() {

        try {
            while (!me.isInterrupted()) {
                File f_pipe = new File(fileName);
                RandomAccessFile raf = new RandomAccessFile(f_pipe, "r");//p.1
                String line = null;
                for (; ; ) {
                    line = raf.readLine();

                    //Take care to check the line -
                    //it is null when the pipe has no more available data.
                    if (line != null) {
                        handler.handle(line);
                    } else {
                        break; //stop reading loop
                    }
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    /**
     * Terminate the thread.
     */
    public void stop() {
        me.interrupt();
    }
}

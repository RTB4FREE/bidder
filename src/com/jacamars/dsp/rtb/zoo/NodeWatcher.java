package com.jacamars.dsp.rtb.zoo;

/**
 * A simple example program to use DataMonitor to start and
 * stop executables based on a znode. The program watches the
 * specified znode and saves the data that corresponds to the
 * znode in the filesystem. It also starts the specified program
 * with the specified arguments when the znode exists and kills
 * the program if the znode goes away.
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

public class NodeWatcher
        implements Watcher, Runnable, DataMonitor.DataMonitorListener
{
    String znode;
    DataMonitor dm;
    ZooKeeper zk;
    Thread me;

    public NodeWatcher(String hostPort, String znode, String data) throws Exception {
        this.znode = znode;
        zk = new ZooKeeper(hostPort, 3000, this);
        Stat s = zk.exists(znode,false);
        if (s != null) {
            zk.delete(znode, 0);
        }
        zk.create(znode,data.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        dm = new DataMonitor(zk, znode, null, this);

        me = new Thread(this);
        me.start();
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String hostPort = "localhost";
        String znode = "bidder1";
        String data = "this is a test";
        ZooKeeper zk = new ZooKeeper(hostPort, 3000, null);
        Stat s = zk.exists("/xxx",true);
        if (s == null) {
            zk.create("/xxx", "Hello World".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        zk.create("/xxx/1", "I am the bidder 1".getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL);

        try {
            new NodeWatcher(hostPort, "/bidders/" + znode, data).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***************************************************************************
     * We do not process any events ourselves, we just need to forward them on.
     */
    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void exists(byte[] data) {

    }

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }
}
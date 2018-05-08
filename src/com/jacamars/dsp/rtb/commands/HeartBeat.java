package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;

public class HeartBeat extends BasicCommand {

    public int port;
    public HeartBeat() {
        this.from = Configuration.instanceName;
        this.target = Configuration.getIpAddress();
        this.logtype = "heartbeat";
        this.cmd = Controller.HEARTBEAT;
        this.port = Configuration.getInstance().swarmPort;
        this.timestamp = System.currentTimeMillis();
    }

}

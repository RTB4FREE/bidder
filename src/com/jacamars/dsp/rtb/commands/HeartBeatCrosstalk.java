package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Configuration;

public class HeartBeatCrosstalk extends BasicCommand {

    public HeartBeatCrosstalk() {
        this.from = Configuration.instanceName;
        this.target = Configuration.getIpAddress();
        this.logtype = "heartbeat-crosstalk";
        this.cmd = Controller.HEARTBEAT_CROSSTALK;
        this.timestamp = System.currentTimeMillis();
    }

}

package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/** Add an SSP to the metassp
 * Created by ben on 8/21/17.
 */
public class AddSSP extends BasicCommand {
    /** The key name of the SSP in the RTBServer.exchanges */
    public AddSSP() {
        super();
        cmd = Controller.ADD_METASSP;
        status = "ok";
        msg = "An SSP is being added from the system";
        name = "AddSSP";
    }
    /**
     * Delete a campaign from the database.
     * @param to String. The bidder that will host the command.
     * @param json String. The ssp data to add
     */
    public AddSSP(String to, String id, String json) {
        super(to);
        this.id = id;
        target = json;
        cmd = Controller.ADD_METASSP;
        status = "ok";
        name = "AddSSP";
    }
}

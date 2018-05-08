package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

public class GetWeights extends BasicCommand{

    /**
     * Default constructor
     */
    public GetWeights() {
        super();
        cmd = Controller.GET_WEIGHTS;
        msg = "Get Weights issued";
    }

    /**
     * Get the weights in a campaign.
     * @param to String. The bidder that will host this command..
     * @param campaign String. The campaign adid to set the price on.
     */
    public GetWeights(String to, String campaign) {
        super(to);
        this.name = campaign;
        this.msg = "Get Weights issued";
        cmd = Controller.GET_WEIGHTS;
    }
}

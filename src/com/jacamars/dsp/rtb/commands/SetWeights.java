package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to get the weights on a campaign
 * @author Ben M. Faul
 *
 */

public class SetWeights extends BasicCommand {

	/**
	 * Default constructor
	 */
	public SetWeights() {
		super();
		cmd = Controller.SET_WEIGHTS;
		msg = "Set Weights issued";
	}

	/**
	 * Set the weights in a campaign.
	 * @param to String. The bidder that will host this command..
	 * @param campaign String. The campaign adid to set the price on.
	 * @param weights String. The weights to apply to this campaign.
	 */
	public SetWeights(String to, String campaign, String weights) {
		super(to);
		this.name = campaign;
		this.target = weights;
		this.msg = "Set Weights issued";
		cmd = Controller.SET_WEIGHTS;
	}
}

package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to encapsulate a 0MQ command for adding a campaign to the bidder.
 * Jackson will be used to create the structure.
 * @author Ben M. Faul
 *
 */
public class RemoveSymbol extends BasicCommand {

	/**
	 * Empty constructor for Jackson
	 */
	public RemoveSymbol() {
		super();
		cmd = Controller.REMOVE_SYMBOL;
		msg = "An AWS object is being configured in the system";
	}

	/**
	 * Configure an AWS Object.
	 * @param to String. The bidder that will execute the command.
	 * @param name String. The name of the owner of the campaign.
	 * @param target String. The command to execute.
	 */
	public RemoveSymbol(String to, String name, String target) {
		super(to);
		cmd = Controller.REMOVE_SYMBOL;
		status = "ok";
		this.target = target;
		msg = "An Symbol is being deleted: " + name +"/" + target;
	}
}

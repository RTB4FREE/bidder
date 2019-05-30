package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to encapsulate a 0MQ command for adding a campaign to the bidder.
 * Jackson will be used to create the structure.
 * @author Ben M. Faul
 *
 */
public class ListSymbols extends BasicCommand {

	/**
	 * Empty constructor for Jackson
	 */
	public ListSymbols() {
		super();
		cmd = Controller.LIST_SYMBOLS;
		msg = "Symbols are being listed";
	}

	/**
	 * Configure an AWS Object.
	 * @param to String. The bidder that will execute the command.

	 */
	public ListSymbols(String to) {
		super(to);
		cmd = Controller.LIST_SYMBOLS;
		status = "ok";
		this.target = target;
		msg = "Listing Symbols";
	}
}

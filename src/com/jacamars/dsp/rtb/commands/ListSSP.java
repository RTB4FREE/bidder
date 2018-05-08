package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to delete a campaign (by the 'id')
 * @author Ben M. Faul
 *
 */
public class ListSSP extends BasicCommand {
	/**
	 * Default constructor for GSON
	 */
	public ListSSP() {
		super();
		cmd = Controller.LIST_SSP;
		status = "ok";
		msg = "Listing SSPs";
		name = "ListSSP";
	}

	/**
	 * Delete a SSP from the vidder.
	 * @param to String. The bidder that will host the command.
	 */
	public ListSSP(String to) {
		super(to);
		target = id;
		cmd = Controller.LIST_SSP;
		status = "ok";
		msg = "SSPs are being listed";
		name = "ListSSP";;
	}
}

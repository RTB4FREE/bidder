package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to delete a campaign (by the 'id')
 * @author Ben M. Faul
 *
 */
public class DeleteSSP extends BasicCommand {
	/**
	 * Default constructor for GSON
	 */
	public DeleteSSP() {
		super();
		cmd = Controller.DELETE_METASSP;
		status = "ok";
		msg = "An SSP is being deleted from the system";
		name = "DeleteSSP";
	}

	/**
	 * Delete a SSP from the vidder.
	 * @param to String. The bidder that will host the command.
	 * @param id String. The ssp to delete.
	 */
	public DeleteSSP(String to, String id) {
		super(to);
		this.id = id;
		cmd = Controller.DELETE_METASSP;
		status = "ok";
		msg = "An SSP is being deleted from the system";
		name = "DeleteSSP";;
	}
}

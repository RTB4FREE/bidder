package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Campaign;

/**
 * A class that is used to encapsulate a REDIS command for adding a multiple compaigns to the bidder.
 * GSON will be used to create the structure.
 * @author Ben M. Faul
 *
 */
public class AddCampaignsList extends BasicCommand {
			
	/**
	 * Empty constructor for gson
	 */
	public AddCampaignsList() {
		super();
		cmd = Controller.ADD_CAMPAIGNS_LIST;
		msg = "New campaigns are being added to the system";
	}

	/**
	 * Add a campaign to the system.
	 * @param to String. The bidder that will execute the command.
	 * @param id String id. The campaign adid to load.
	 */
	public AddCampaignsList(String to,  String id) {
		super(to);
		cmd = Controller.ADD_CAMPAIGN;
		status = "ok";
		target = id;
		msg = "New campaigns are being added to the system: " + target;
	}
}

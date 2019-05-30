package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to encapsulate a 0MQ command for listing symbols in the bidder.
 * Jackson will be used to create the structure.
 * @author Ben M. Faul
 *
 */
public class ListSymbolsResponse extends ListCampaigns {

	/**
	 * Empty constructor for gson
	 */
	public ListSymbolsResponse() {
		super();
		cmd = Controller.LIST_SYMBOLS_RESPONSE;
		msg = "Symbols are listed";
	}

	/**
	 * Add a campaign to the system.
	 * @param to String. The bidder that will execute the command.
	 * @param id String id. The campaign adid to load.
	 */
	public ListSymbolsResponse(String to, String name, String id) {
		super(to);
		cmd = Controller.LIST_SYMBOLS_RESPONSE;
		status = "ok";
		target = id;
		msg = "The following symbols are defined: " + target;
	}
}

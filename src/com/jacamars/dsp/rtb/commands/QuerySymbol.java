package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;

/**
 * A class that is used to get the value from a big data object.
 * @author Ben M. Faul
 *
 */

public class QuerySymbol extends BasicCommand {
	
	public String key;
	/**
	 * Default constructor.
	 */
	public QuerySymbol() {
		super();
		cmd = Controller.QUERY;
		msg = "QUERY Issued";
	}
	
	/**
	 * A command to query the price of a campaign.
	 * @param to String. The bidder that will host this command.
	 * @param campaign String. The campaignid in question.
	 * @param creative String. The creative impid to retrieve the price from.
	 */
	public QuerySymbol(String to, String name, String key) {
		super(to);
		this.name = name;
		this.key = key;
		cmd = Controller.QUERY;
		msg = "QUERY Issued";
	}
}

package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.bidder.Controller;
import com.jacamars.dsp.rtb.common.Campaign;

/**
 * A class that is used to encapsulate a 0MQ command for adding a campaign to the bidder.
 * Jackson will be used to create the structure.
 * @author Ben M. Faul
 *
 */
public class ConfigureObject extends BasicCommand {
			
	public String type;
	/**
	 * Empty constructor for Jackson
	 */
	public ConfigureObject() {
		super();
		cmd = Controller.CONFIGURE_OBJECT;
		msg = "An AWS object is being configured in the system";
	}

	/**
	 * Configure a file Object.
	 * @param to String. The bidder that will execute the command.
	 * @param name String. The name of the owner of the campaign.
	 * @param target String. The command to execute.
	 */
	public ConfigureObject(String to, String name, String target, String type) {
		super(to);
		cmd = Controller.CONFIGURE_OBJECT;
		status = "ok";
		this.name = name;
		this.target = target;
		this.type = type;
		msg = "A FILE OBJECT is befing configured: " + name +"/" + target + "/" + type;
	}
}

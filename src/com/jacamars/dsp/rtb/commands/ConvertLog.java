package com.jacamars.dsp.rtb.commands;

import com.jacamars.dsp.rtb.common.Configuration;

/**
 * A logger for user conversions.
 * @author Ben M. Faul
 *
 */

public class ConvertLog extends PixelClickConvertLog {

	public ConvertLog() {
		super();
		type = CONVERT;
	}
	
	public ConvertLog(String payload) {
		this.payload = payload;
		type = CONVERT;
		instance = Configuration.getInstance().instanceName;
		timestamp = System.currentTimeMillis();
	}
}

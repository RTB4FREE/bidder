package com.jacamars.dsp.rtb.exchanges.adx;


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;


public class DoubleClick extends AdxBidRequest {

	public static void main(String [] args) throws Exception {
		Decrypter.testWinningPrice();
	}
	
	/**
	 * Make a default constructor, the bidder keeps a representative class instance for each
	 * exchange so it can use a Map to make new bid requests per the format of the bid request.
	 */
	public DoubleClick() {
		super();
		parseSpecial();
	}
	
	/**
	 * Constructs Adx bid request from a file containoing JSON
	 * @param in. String - the File name containing the data.
	 * @throws JsonProcessingException on parse errors.
	 * @throws IOException on file reading errors.
	 */	
	public DoubleClick(String  in) throws Exception  {
		super(in);
		parseSpecial();
    }	
	
	/**
	 * Constructs Adx bid request from JSON stream in jetty.
	 * @param in. InputStream - the JSON data coming from HTTP.
	 * @throws JsonProcessingException on parse errors.
	 * @throws IOException on file reading errors.
	 */
	public DoubleClick(InputStream in) throws Exception {
		super(in);
		parseSpecial();
	}
	
	/**
	 * Create a new Adx object from this class instance.
	 * @throws JsonProcessingException on parse errors.
	 * @throws Exception on stream reading errors
	 */
	@Override
	public DoubleClick copy(InputStream in) throws Exception    {
		try {
			return new DoubleClick(in);
		} catch (Exception error) {
			logger.debug("Error copying Google DoubleClick, something is not configured correctly: {}",error.getMessage());
		} catch (Error x) {
			logger.debug("Error copying Google DoubleClick, something is not configured correctly: {}",x.getMessage());
		}
		return null;
	}
	
	/**
	 * Process special Nexage stuff, sets the exchange name.
	 */
	@Override
	public boolean parseSpecial() {
		setExchange(AdxBidRequest.ADX);
        usesEncodedAdm = false;
		return true;
	}
	
	@Override
	public void handleConfigExtensions(Map extension)  {
		new AdxBidRequest().handleConfigExtensions(extension);
	}
}

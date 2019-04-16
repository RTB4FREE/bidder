import com.jacamars.dsp.rtb.blocks.LookingGlass;
import com.jacamars.dsp.rtb.common.Node;
import com.jacamars.dsp.rtb.tools.DbTools;

public class TestNode {

	public static void main(String [] args) throws Exception {
		new LookingGlass("@ZIPCODES","data/zip_codes_states.csv");
		Node node = new Node("LATLON","device.geo", Node.INRANGE, "ZIPCODES, 90505,90506,90507,600000");
		System.out.println(DbTools.mapper.writeValueAsString(node));
	}
}

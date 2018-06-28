package com.jacamars.dsp.rtb.tools;

import java.net.URLDecoder;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.jacamars.dsp.rtb.common.Configuration;
import com.jacamars.dsp.rtb.common.Creative;
import com.jacamars.dsp.rtb.common.URIEncoder;
import com.jacamars.dsp.rtb.pojo.BidRequest;
import com.jacamars.dsp.rtb.pojo.Impression;
import org.apache.commons.lang.StringUtils;

/**
 * Class provides macro processing for the RTB4FREE system.
 *
 * @author Ben M. Faul
 */
public class MacroProcessing {

    static Random random = new Random();
    /*
	 * {redirect_url}", config.redirectUrl); {pixel_url}",
	 * config.pixelTrackingUrl);
	 * 
	 * {creative_forward_url}", creat.forwardurl); {creative_ad_price}",
	 * creat.strPrice); {creative_ad_width}", creat.strW);
	 * {creative_ad_height}", creat.strH); {creative_id}", creat.impid);
	 * {creative_image_url}", creat.imageurl); {site_id}", br.siteId);
	 * {lat}",lat); {lon}", lon); {site_domain}",br.siteDomain); {pub}",
	 * exchange); {bid_id}", oidStr); {ad_id}", adid);" + " replaceAll(sb,
	 * "%7Bpub%7D", exchange); replaceAll(sb, "%7Bbid_id%7D", oidStr);
	 * replaceAll(sb, "%7Bad_id%7D", adid); replaceAll(sb, "%7Bsite_id%7D",
	 * br.siteId); replaceAll(sb, "%7Bcreative_id%7D", creat.impid);
	 * 
	 * replaceAll(sb, "%7Blat%7D", lat); replaceAll(sb, "%7Blon%7D", lon);
	 */

    static final String APP = "APP";
    static final String SITE = "SITE";

    static Set<String> macroList = new HashSet();

    static {
        macroList.add("{cachebuster}");
        macroList.add("%7Bcachebuster%7D");
        macroList.add("{cachebuster_enc}");

        macroList.add("{redirect_url}");
        macroList.add("%7Bredirect_url%7D");
        macroList.add("{redirect_url_enc}");

        macroList.add("{pixel_url}");
        macroList.add("%7Bpixel_url%7D");
        macroList.add("{pixel_url_enc}");

        macroList.add("{event_url}");
        macroList.add("%7Bevent_url%7D");
        macroList.add("{event_url_enc}");

        macroList.add("{vast_url}");
        macroList.add("%7Bvast_url%7D");
        macroList.add("{vast_url_enc}");

        macroList.add("{win_url}");
        macroList.add("%7Bwin_url%7D");
        macroList.add("{win_url_enc}");

        macroList.add("{nurl}");
        macroList.add("{nurl_enc}");
        macroList.add("%7Bnurl%7D}");

        macroList.add("{creative_forward_url}");
        macroList.add("%7Bcreative_forward_url%7D");
        macroList.add("{creative_forward_url_enc}");

        macroList.add("{creative_ad_price}");
        macroList.add("%7Bcreative_ad_price%7D");
        macroList.add("{creative_ad_price_enc}");

        macroList.add("{creative_ad_width}");
        macroList.add("%7Bcreative_ad_width%7D");
        macroList.add("{creative_ad_width_enc}");

        macroList.add("{creative_ad_height}");
        macroList.add("%7Bcreative_ad_height%7D");
        macroList.add("{creative_ad_height_enc}");


        macroList.add("{impression_width}");
        macroList.add("%7Bimpression_width%7D");
        macroList.add("{impression_width_enc}");

        macroList.add("{creative_ad_height}");
        macroList.add("%7Bcreative_ad_height%7D");
        macroList.add("{creative_ad_height_enc}");

        macroList.add("{impression_height}");
        macroList.add("{impression_height_enc}");
        macroList.add("%7Bimpression_height%7D");

        macroList.add("{creative_id}");
        macroList.add("{creative_id_enc}");
        macroList.add("%7Bcreative_id%7D");

        macroList.add("{imp}");
        macroList.add("{imp_enc}");
        macroList.add("%7Bimp%7D");

        macroList.add("{creative_image_url}");
        macroList.add("{creative_image_url_enc}");
        macroList.add("%7Bcreative_image_url%7D");

        macroList.add("{site_id}");
        macroList.add("{site_id_enc}");
        macroList.add("%7Bsite_id%7D");

        macroList.add("{app_id}");
        macroList.add("{app_id_enc}");
        macroList.add("%7Bapp_id%7D");

        macroList.add("{lat}");
        macroList.add("{lat_enc}");
        macroList.add("%7Blat%7D");

        macroList.add("{lon}");
        macroList.add("{lon_enc}");
        macroList.add("%7Blon%7D");

        macroList.add("{site_domain}");
        macroList.add("{site_domain_enc}");
        macroList.add("%7Bsite_domain%7D");

        macroList.add("{app_domain}");
        macroList.add("{app_domain_enc}");
        macroList.add("%7Bapp_domain%7D");

        macroList.add("{site_name}");
        macroList.add("{site_name_enc}");
        macroList.add("%7Bsite_name%7D");

        macroList.add("{app_name}");
        macroList.add("{app_name_enc}");
        macroList.add("%7Bapp_name%7D");

        macroList.add("{pub}");
        macroList.add("{pub_enc}");
        macroList.add("%7Bpub%7D");

        macroList.add("{exchange}");
        macroList.add("{exchange_enc}");
        macroList.add("%7Bexchange%7D");

        macroList.add("{bid_id}");
        macroList.add("{bid_id_enc}");
        macroList.add("%7Bbid_id%7D");

        macroList.add("{bidder_ip}");
        macroList.add("{bidder_ip_enc}");
        macroList.add("%7Bbidder_ip%7D");

        macroList.add("{ad_id}");
        macroList.add("{ad_id_enc}");
        macroList.add("%7Bad_id%7D");

        macroList.add("{isp}");
        macroList.add("{isp_enc}");
        macroList.add("%7Bisp%7D");

        macroList.add("{brand}");
        macroList.add("{brand_enc}");
        macroList.add("%7brand%7D");

        macroList.add("{make}");
        macroList.add("{make_enc}");
        macroList.add("%7Bmake%7D");

        macroList.add("{model}");
        macroList.add("{model_enc}");
        macroList.add("%7Bmodel%7D");

        macroList.add("{os}");
        macroList.add("{os_enc}");
        macroList.add("%7Bos%7D");

        macroList.add("{osv}");
        macroList.add("{osv_enc}");
        macroList.add("%7Bosv%7D");

        macroList.add("{timestamp}");
        macroList.add("{timestamp_enc}");
        macroList.add("%7Btimestamp%7D");

        macroList.add("{ip}");
        macroList.add("{ip_enc}");
        macroList.add("%7Bip%7D");

        macroList.add("{gps}");
        macroList.add("{gps_enc}");
        macroList.add("%7Bgps%7D");

        macroList.add("{ua}");
        macroList.add("{ua_enc}");
        macroList.add("%7Bua%7D");

        macroList.add("{publisher}");
        macroList.add("{publisher_enc}");
        macroList.add("%7Bpublisher%7D");

        macroList.add("{adsize}");
        macroList.add("{adsize_enc}");
        macroList.add("%7Badsize%7D");

        macroList.add("{app_bundle}");
        macroList.add("{app_bundle_enc}");
        macroList.add("%7Bapp_bundle%7D");

        macroList.add("{ifa}");
        macroList.add("{ifa_enc}");
        macroList.add("%7Bifa%7D");

        macroList.add("{dnt}");
        macroList.add("{dnt_enc}");
        macroList.add("%7Bdnt%7D");

        macroList.add("{page_url}");
        macroList.add("{page_url_enc}");
        macroList.add("%7Bpage_url%7D");

        macroList.add("{deal_id}");
        macroList.add("{deal_id_enc}");
        macroList.add("%7Bdeal_id%7D");

        macroList.add("{site_cat}");
        macroList.add("{site_cat_enc}");
        macroList.add("%7Bsite_cat%7D");

        macroList.add("{app_cat}");
        macroList.add("{app_cat_enc}");
        macroList.add("%7Bapp_cat%7D");

        macroList.add("{app_storeurl}");
        macroList.add("{app_storeurl_enc}");
        macroList.add("%7Bapp_storeurl%7D");

        macroList.add("{regs_coppa}");
        macroList.add("{regs_coppa_enc}");
        macroList.add("%7Bregs_coppa%7D");

        macroList.add("{bid_type}");
        macroList.add("{bid_type_enc}");
        macroList.add("%7Bbid_type%7D");

        macroList.add("{user_id}");
        macroList.add("{user_id_enc}");
        macroList.add("%7Buser_id%7D");

        macroList.add("{device_id}");
        macroList.add("{device_id_enc}");
        macroList.add("%7Bdevice_id%7D");

        macroList.add("{user_profile}");
        macroList.add("{user_profile_enc}");
        macroList.add("%7Buser_profile%7D");

        macroList.add("{tid}");
        macroList.add("{tid_enc}");
        macroList.add("%7Btid%7D");

    }

    public static void addMacro(String mac) {
        macroList.add(mac);
    }

    /**
     * Macro substitutions.
     * @param list List. A list of macros, known to this bidder.
     * @param br BidRequest. The bid request to pull values out of for macro substitution.
     * @param creat Creative. The creative to pull values out of for macro substitution.
     * @param imp Impression. The impression being bid on, to pull values out of for substitution.
     * @param adid String. The campaign id.
     * @param sb StringBuilder. The bid response we will substitute into.
     * @param snurl StrinbBuilder. The  win url, which can be substituted from.
     * @param dealid String. The dealid if present, got substituting with.
     * @throws Exception on parsing errors.
     */
    public static void replace(List<String> list, BidRequest br, Creative creat, Impression imp, String adid,
                               StringBuilder sb, StringBuilder snurl, String dealid)
            throws Exception {

        String value = null;
        Object o = null;
        Configuration config = Configuration.getInstance();

        for (int i = 0; i < list.size(); i++) {
            value = null;
            String item = list.get(i);

            switch (item) {
                case "{cachebuster}":
                case "{cachebuster_enc}":
                case "%7Bcachebuster%7D":
                    replaceAll(sb, item, Integer.toString(random.nextInt(Integer.SIZE - 1)));
                    break;

                case "{redirect_url}":
                case "{redirect_url_enc}":
                case "%7Bredirect_url%7D":
                    value = encodeIfRequested(item,config.redirectUrl);
                    replaceAll(sb, item, value);
                    break;

                case "{pixel_url}":
                case "{pixel_url_enc}":
                case "%7Bpixel_url%7D":
                    replaceAll(sb, item, value);
                    break;

                case "{event_url}":
                case "{event_url_enc}":
                case "%7Bevent_url%7D":
                    value = encodeIfRequested(item,config.eventUrl);
                    replaceAll(sb, item, value);
                    break;

                case "{vast_url}":
                case "{vast_url_enc}":
                case "%7Bvast_url%7D":
                    value = encodeIfRequested(item,config.vastUrl);
                    replaceAll(sb, item, value);
                    break;

                case "{postback_url}":
                case "{postback_url_enc}":
                case "%7Bpostback_url%7D":
                    value = encodeIfRequested(item,config.postbackUrl);
                    replaceAll(sb, item, value);
                    break;

                case "{bid_type}":
                case "{bid_type_enc}":
                case "%7Bbid_type%7D":
                    if (br.isSite())
                        replaceAll(sb, item, SITE);
                    else
                        replaceAll(sb, item, APP);
                    break;


                case "{nurl}":
                case "{nurl_enc}":
                case "%7Bnurl%7D":
                    if (snurl == null)
                        value = "";
                    else
                        value = snurl.toString();
                    value = encodeIfRequested(item,value);
                    replaceAll(sb, item, value);
                    break;

                case "{win_url}":
                case "{win_url_enc}":
                case "%7Bwin_url%7D":
                    value = encodeIfRequested(item,config.winUrl);
                    replaceAll(sb, item, value);
                    break;

                case "{creative_forward_url}":
                case "{creative_forward_url_enc}":
                case "%7Bcreative_forward_url%7D":
                    value = encodeIfRequested(item, creat.forwardurl);
                    replaceAll(sb, item, value);
                    break;

                case "{creative_ad_price}":
                case "{creative_ad_price_enc}":
                case "%7Bcreative_ad_price%7D":
                    value = encodeIfRequested(item, creat.strPrice);
                    replaceAll(sb, item, value);
                    break;

                case "{creative_ad_width}":
                case "{creative_ad_width_enc}":
                case "%7Bcreative_ad_width%7D":
                    value = encodeIfRequested(item, creat.strW);
                    replaceAll(sb, item, value);
                    break;

                case "{deal_id}":
                case "{deal_id_enc}":
                case "%7Bdeal_id%7D":
                    value = encodeIfRequested(item, dealid);
                    replaceAll(sb, item, dealid);
                    break;

                case "{impression_width}":
                case "{impression_width_enc}":
                case "%7Bimpression_width%7D":
                    value = encodeIfRequested(item, Integer.toString(imp.w));
                    replaceAll(sb, item, value);
                    break;

                case "{impression_height}":
                case "{impression_height_enc}":
                case "7Bimpression_height7D":
                    value = encodeIfRequested(item, Integer.toString(imp.h));
                    replaceAll(sb, item, value);
                    break;

                case "{creative_ad_height}":
                case "{creative_ad_height_enc}":
                case "%7Bcreative_ad_height%7D":
                    value = encodeIfRequested(item, creat.strH);
                    replaceAll(sb, item, value);
                    break;

                case "{creative_id}":
                case "{creative_id_enc}":
                case "%7Bcreative_id%7D":
                    value = encodeIfRequested(item, creat.impid);
                    replaceAll(sb, item, value);
                    break;

                case "{imp}":
                case "{imp_enc}":
                case "%7Bimp%7D":
                    value = encodeIfRequested(item, creat.impid);
                    replaceAll(sb, item, value);
                    break;


                case "{creative_image_url}":
                case "{creative_image_url_enc}":
                case "%7Bcreative_image_url%7D":
                    value = encodeIfRequested(item, creat.imageurl);
                    replaceAll(sb, item, creat.imageurl);
                    break;

                case "{site_name}":
                case "%7Bsite_name%7D":
                case "{site_name_enc}":
                case "{app_name}":
                case "{app_name_enc}":
                case "%7app_name%7D":
                	if (br.siteName != null) {
                		value = URLEncoder.encode(br.siteName, "UTF-8");
                		replaceAll(sb, item, value);
                	}
                    break;

                case "{site_id}":
                case "{site_id_enc}":
                case "%7Bsite_id%7D":
                case "{app_id}":
                case "{app_id_enc}":
                case "%7Bapp_id%7D":
                    if (br.siteId != null) {
                        value = URLEncoder.encode(br.siteId, "UTF-8");
                        replaceAll(sb, item, value);
                    }
                    break;

                case "{page_url}":
                case "{page_url_enc}":
                case "%7Bpage_url%7D":
                    if (br.pageurl != null)
                        value = br.pageurl;
                    else
                        value = "";
                    value = encodeIfRequested(item, br.pageurl);
                    replaceAll(sb, item, value);
                    break;

                case "{lat}":
                case "{lat_enc}":
                case "%7Blat%7D":
                    if (br.lat == null)
                        break;
                    else {
                        value = encodeIfRequested(item, br.lat.toString());
                        replaceAll(sb, item, value);
                    }
                    break;

                case "{lon}":
                case "{lon_enc}":
                case "%7Blon%7D":
                    if (br.lon == null)
                        break;
                    else {
                        value = encodeIfRequested(item, br.lon.toString());
                        replaceAll(sb, item, value);
                    }
                    break;

                case "{site_domain}":
                case "{site_domain_enc}":
                case "%7Bsite_domain%7D":
                case "{app_domain}":
                case "{app_domain_enc}":
                case "%7Bapp_domain%7D":
                    if (br.siteDomain != null) {
                        value = encodeIfRequested(item, br.siteDomain);
                        replaceAll(sb, item, value);
                    }
                    break;

                case "{pub}":
                case "{pub_enc}":
                case "%7Bpub%7D":
                case "{exchange}":
                case "{exchange_enc}":
                case "%7Bexchange%7D":
                    value = encodeIfRequested(item, br.getExchange());
                    replaceAll(sb,item,value);
                    break;

                case "{bid_id}":
                case "{bid_id_enc}":
                case "%7Bbid_id%7D":
                    // Watch out, some SSPs put '/' in the bid id, like google.
                    String id = URLEncoder.encode(br.id, "UTF-8");
                    replaceAll(sb, item, id);
                    break;

                case "{ad_id}":
                case "{ad_id_enc}":
                case "%7Bad_id%7D":
                    value = encodeIfRequested(item, adid);
                    replaceAll(sb, "{ad_id}", value);
                    break;

                case "{isp}":
                case "{isp_enc}":
                case "%7Bisp%7D":
                    o = br.interrogate("device.carrier");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, adid);
                    replaceAll(sb, item, value);
                    break;

                case "{bidder_ip}":
                case "{bidder_ip_enc}":
                case "%7Bbidder_ip%7D}":
                    value = encodeIfRequested(item, Configuration.ipAddress);
                    replaceAll(sb, item,  item);
                    break;

                case "{make}":
                case "{make_enc}":
                case "%7Bmake%7D":
                case "{brand}":
                case "{brand_enc}":
                case "%7Bbrand%7D":
                    o = br.interrogate("device.make");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{model}":
                case "{model_enc}":
                case "%7Bmodel%7D":
                    o = br.interrogate("device.model");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{os}":
                case "{os_enc}":
                case "%7Bos%7D":
                    o = br.interrogate("device.os");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{osv}":
                case "{osv_enc}":
                case "%7Bosv%7D":
                    o = br.interrogate("device.osv");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{timestamp}":
                case "{timestamp_enc}":
                case "%7Btimestamp%7D":
                    replaceAll(sb, item, "" + System.currentTimeMillis());
                    break;

                case "{ip}":
                case "{ip_enc}":
                case "%7Bip%7D":
                    o = br.interrogate("device.ip");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{gps}":
                case "{gps_enc}":
                case "%7Bgps%7D":
                    if (br.lat == null)
                        break;
                    else {
                        value = encodeIfRequested(item,(br.lat.toString() + "x" + br.lon.toString()));
                        replaceAll(sb, item, value);
                    }
                    break;

                case "{ua}":
                case "{ua_enc}":
                case "%7Bua%7D":
                    o = br.interrogate("device.ua");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, URIEncoder.myUri(value));
                    break;

                case "{publisher}":
                case "{publisher_enc}":
                case "%7Bpublisher%7D":
                    o = br.interrogate("site.name");
                    if (o == null)
                        o = br.interrogate("app.name");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{adsize}":
                case "{adsize_enc}":
                case "%7Badsize%7D":
                    value = creat.strW + "x" + creat.strH;
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{ifa}":
                case "{ifa_enc}":
                case "%7Bifa%7D":
                    o = br.interrogate("device.ifa");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{dnt}":
                case "{dnt_enc}":
                case "%7Bdnt%7D":
                    o = br.interrogate("device.dnt");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{site_cat}":
                case "{site_cat_enc}":
                case "%7Bsite_cat%7D":
                    o = br.interrogate("site.cat");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{app_cat}":
                case "{app_cat_enc}":
                case "%7Bapp_cat%7D":
                    o = br.interrogate("app.cat");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{app_storeurl}":
                case "{app_storeurl_enc}":
                case "%7Bapp_storeurl%7D":
                    o = br.interrogate("app.storeurl");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{regs_coppa}":
                case "{regs_coppa_enc}":
                case "%7Bregs_coppa%7D":
                    o = br.interrogate("regs.coppa");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;

                case "{app_bundle}":
                case "{app_bundle_enc}":
                case "%7Bapp_bundle%7D":
                    o = br.interrogate("app.bundle");
                    if (o != null)
                        value = BidRequest.getStringFrom(o);
                    else
                        value = "";
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;
                case "{user_id}":
                case "{user_id_enc}":
                case "%7Buser_id%7D":
                    o = br.interrogate("user.id");
                    value = (o != null)? BidRequest.getStringFrom(o) : StringUtils.EMPTY;
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;
                case "{device_id}":
                case "{device_id_enc}":
                case "%7Bdevice_id%7D":
                    o = br.interrogate("device.didsha1");
                    if (o == null) {
                        o = br.interrogate("device.didmd5");
                    }
                    value = (o != null)? BidRequest.getStringFrom(o) : StringUtils.EMPTY;
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;
                case "{user_profile}":
                case "{user_profile_enc}":
                case "%7Buser_profile%7D":
                    o = br.interrogate("synthkey");
                    value = (o != null)? (String) o : StringUtils.EMPTY;
                    value = encodeIfRequested(item, value);
                    replaceAll(sb, item, value);
                    break;
                case "{tid}":
                case "{tid_enc}":
                case "%7Btid}":
                    value = TidKey.get(br,adid,creat);
                    replaceAll(sb, item, value);
                    break;

                default:
                    value = Configuration.getInstance().getMacroDefinition(item);
                    if (value != null)
                        replaceAll(sb,item,value);
                    break;
            }
        }
    }

    /**
     * If the key has "_enc" in it or, contains the encoded {%7B} preamble - encode it, otherwise just return the value.
     * @param key String. The macro name.
     * @param value String. The value to possibly encode
     * @return String. The resulting value, possibly encoded.
     */
    public static String encodeIfRequested(String key, String value) throws Exception {
        if (key.endsWith("_enc}") || key.startsWith("{%7B"))
            return URLEncoder.encode(value, "UTF-8");
        else
            return value;
    }

    /**
     * Find macros in a string, (the creative offered) and stick them in the macros list.
     * @param macros List. A list of strings, you provide this and call this method for every creative and
     *               you end up with the list of all known macros used. Psrt of the campaign compilation.
     * @param str String. The creative that could contain macros.
     */
    public static void findMacros(List<String> macros, String str) {
        if (str == null)
            return;
        for (String what : macroList) {
            if (str.indexOf(what) > -1) {
                if (macros.contains(what) == false)
                    macros.add(what);
            }
        }
    }

    /**
     * Replace All instances of a string.
     *
     * @param x    StringBuilder. The buffer to do replacements in.
     * @param what String. The string we are looking to replace.
     * @param sub  String. The string to use for the replacement.
     */
    public static void replaceAll(StringBuilder x, String what, String sub) {
        if (what == null || sub == null)
            return;
        int start = x.indexOf(what);
        if (start != -1) {
            x.replace(start, start + what.length(), sub);
            replaceAll(x, what, sub);
        }
    }
}

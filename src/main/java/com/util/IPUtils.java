package com.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPUtils {

	private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);
	private static String ipUrl = "https://www.proxydocker.com/en/proxylist/country/Canada";
	
	public static List<IPInfo> checkIP() {
		List<IPInfo> ipList = new ArrayList<IPInfo>();
		try {
			Document doc = Jsoup.connect(ipUrl).timeout(5000).get();
			Elements allIP = doc.select(".proxylist_table > tbody > tr");

			for (int i = 1;i<=allIP.size()-1;i++) {
				String filterIp = allIP.get(i).select("td").get(0).text();
				if(!filterIp.isEmpty()){
					String tmpSpeed =  allIP.get(i).select("td").get(3).select(".proxy-ping-span").attr("style");
					String[] filterSpeed = tmpSpeed.split(":|%");
					int speed = Integer.parseInt(filterSpeed[filterSpeed.length-1]);
					if(speed >= 70){
						String[] Ip_Port = filterIp.split(":");
						IPInfo useIPInfo = new IPInfo();
						useIPInfo.setIp(Ip_Port[0]);
						useIPInfo.setPort(Ip_Port[1]);
						ipList.add(useIPInfo);
					}
				
				}

			}
			if(ipList.isEmpty()){
				logger.info("checkIP IP List is Empty");
			}
			
		} catch (Exception e) {
			logger.error("checkIP Exception: ", e);
			
		}
		return ipList;
	}
}

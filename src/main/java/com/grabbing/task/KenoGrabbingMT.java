package com.grabbing.task;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.ssl.DisableSslVerification;

public class KenoGrabbingMT extends TimerTask{
	
	private Map<String, String> cookies = new HashMap<String, String>();
	static int count = 0;
	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingMT.class);

	@Override
	public void run() {
		Document doc = grap(cookies);				
		Elements drawNos = doc.select(".restext");
		Elements numbers = doc.getElementsByAttributeValue("style", "color:#002160");

		int numberOfDraws = 20;
		int size = drawNos.size() / 2;
		size = size <= numberOfDraws ? size : numberOfDraws;
		List<String> drawNoList = new ArrayList<String>();
		List<String> drawResultList = new ArrayList<String>();
		for (Element no : drawNos) {
			if (!no.text().contains(":")) {
				drawNoList.add(no.text());
			}
		}
		for (Element number : numbers) {
			drawResultList.add(number.text());
		}			
		
		try {
			PrintWriter writer = new PrintWriter("/usr/local/applications/kn-grabbing-server/Keno-Grabbing-MT.txt", "UTF-8");	
//			PrintWriter writer = new PrintWriter("C:\\Users\\pohsun\\Desktop\\Keno-Grabbing-MT.txt", "UTF-8");	
			writer.println("Version: "+count);
			String line = "";
			for (int i = 0; i < size; i++) {
				line += drawNoList.get(i) + "-[";
				line += drawResultList.get(0) + ",";
				line += drawResultList.get(1) + ",";
				line += drawResultList.get(2) + ",";
				line += drawResultList.get(3) + ",";
				line += drawResultList.get(4) + ",";
				line += drawResultList.get(5) + ",";
				line += drawResultList.get(6) + ",";
				line += drawResultList.get(7) + ",";
				line += drawResultList.get(8) + ",";
				line += drawResultList.get(9) + ",";
				line += drawResultList.get(10) + ",";
				line += drawResultList.get(11) + ",";
				line += drawResultList.get(12) + ",";
				line += drawResultList.get(13) + ",";
				line += drawResultList.get(14) + ",";
				line += drawResultList.get(15) + ",";
				line += drawResultList.get(16) + ",";
				line += drawResultList.get(17) + ",";
				line += drawResultList.get(18) + ",";
				line += drawResultList.get(19) + "]";
				if (drawResultList.size() >= 20) {
					removeData(drawResultList);
				}	
				logger.info(line);
				writer.println(line);
				line = "";
			}
			writer.println("Last updated time: " + Calendar.getInstance().getTime().toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();			
			logger.error("Error in drawing MT data. Error message: " + e.getMessage());		
		} 
		count++;		
	}
	
	private void removeData(List<String> drawResultList) {
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
		drawResultList.remove(0);
		drawResultList.remove(0);	
	}

	public Document grap(Map<String, String> cookies) {
		Document doc = null;
		try {
			DisableSslVerification.disable();
			doc = Jsoup.connect("https://www.maltco.com/keno/QuickKeno_Today_Results.php").cookies(cookies)
					.timeout(10000).get();
		} catch (Exception e) {
			// grap(cookies);
			e.printStackTrace();
		}

		return doc;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

}

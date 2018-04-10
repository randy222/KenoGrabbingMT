package com.grabbing.task;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tsl.TLSSocketConnectionFactory;
import com.util.IPUtils;
import com.util.IPInfo;


public class KenoGrabbingMT extends TimerTask{
	
	private Map<String, String> cookies = new HashMap<String, String>();
	private static int count = 0;
	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingMT.class);
	private static long start, end;

	static HostnameVerifier hv = new HostnameVerifier() {
		public boolean verify(String urlHostName, SSLSession session) {
			System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
			return true;
		}
	};
	
	static {
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		HttpsURLConnection.setDefaultSSLSocketFactory(new TLSSocketConnectionFactory());
	}
	
	@Override
	public void run() {		
		
		List<IPInfo> ipList = IPUtils.checkIP();		
		Document doc = grap(cookies, ipList);
		String cont = doc.toString();
		int idx = cont.indexOf("recaptcha_response_field");

		if (idx >= 0) {
			logger.info("Authorize result: failed");
			writeErrMsg("5");
		} else {		
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
				logger.info("Start writing file");
				PrintWriter writer = new PrintWriter("/usr/local/applications/kn-grabbing-server/Keno-Grabbing-MT.txt", "UTF-8");	
//				PrintWriter writer = new PrintWriter("C:\\Users\\pohsun\\Desktop\\Keno-Grabbing-MT.txt", "UTF-8");	
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				writer.println("Message_Code: 0");
				writer.println("Last updated time: " + sdf.format(Calendar.getInstance().getTime()));
				writer.println("Version: "+count);
				String line = "";
				for (int i = 0; i < size; i++) {
					line += drawNoList.get(i) + "@[";
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
				writer.close();
				logger.info("End writing file");
			} catch (Exception e) {	
				logger.error("Error in drawing MT data. Error message: ", e);		
			} 
			count++;	
		}
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

	public Document grap(Map<String, String> cookies, List<IPInfo> useIPList) {
		start = System.currentTimeMillis();
		logger.info("Start Grabbing MT");
		String url = "https://www.maltco.com/keno/QuickKeno_Results_for_Day.php?day=dd&month=MM&year=yyyy";
		Document doc = null;
		try {
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date yesterday = cal.getTime();
			SimpleDateFormat cetFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			TimeZone cetTime = TimeZone.getTimeZone("CET");
			cetFormat.setTimeZone(cetTime);
			String now = cetFormat.format(today);
			String before = cetFormat.format(yesterday);
			String[] nowArray = now.split("-");
			String[] beforeArray = before.split("-");
			int hour = Integer.parseInt(nowArray[3]);
			if (hour < 5) {
				url = url.replace("yyyy", beforeArray[0]).replace("MM", beforeArray[1]).replace("dd", beforeArray[2]);
			} else {
				url = url.replace("yyyy", nowArray[0]).replace("MM", nowArray[1]).replace("dd", nowArray[2]);
			}
			logger.info("MT Url = " + url);
			
			for (IPInfo info : useIPList) {
				String host = info.getIp();
				int port = Integer.parseInt(info.getPort());
				logger.info(info.toString());
				doc = tryCrawl(url, host, port, cookies);
				if (doc != null) break;
			}
			
			end = System.currentTimeMillis();
			logger.info("Finish Grabbing MT");
			logger.info("Total Grabbing time = {}", (end - start) / 1000 + " secs");
		} catch (Exception e) {
			end = System.currentTimeMillis();
			logger.error("Exception: ", e);
			logger.info("Total Grabbing time = {}", (end - start) / 1000 + " secs");
			writeErrMsg("1");
			doc = null;
		}

		return doc;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	private void writeErrMsg(String msgCode) {
		try {
			PrintWriter writer = new PrintWriter("/usr/local/applications/kn-grabbing-server/Keno-Grabbing-MT.txt", "UTF-8");	
//			PrintWriter writer = new PrintWriter("C:\\Users\\pohsun\\Desktop\\Keno-Grabbing-MT.txt", "UTF-8");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			writer.println("Message_Code: " + msgCode);
			writer.println("Last updated time: " + sdf.format(Calendar.getInstance().getTime()));
			writer.close();	
		} catch (Exception e) {		
			logger.error("Error in writing MT error message. Error message: ", e);		
		} 
		
	}

	private Document tryCrawl(String url, String host, int port, Map<String, String> cookies) {
		try {
			return Jsoup.connect(url).proxy(host, port).cookies(cookies).timeout(1000).get();
		} catch (Exception e) {
			logger.error("tryCrawl Exception: ", e);
			return null;
		}
		
	}

}

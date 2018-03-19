package com.grabbing.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class KenoGrapServlet
 */
public class KenoGrabbingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(KenoGrabbingServlet.class);
	private static Timer timer = null;
	private static KenoGrabbingMT task = null;
	private static long start, end;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KenoGrabbingServlet() {
		start = System.currentTimeMillis();
		logger.info("Servlet Start");
		Security.addProvider(new BouncyCastleProvider());
		logger.info("Finish JRE bug by adding Provider(BouncyCastleProvider)");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		final Map<String, String> cookies = new HashMap<String, String>();
		cookies.put("PHPSESSID", request.getParameter("phpid"));

		Document doc = grap(cookies);
		if (doc == null) {
			response.getWriter().append("Start task Failed, please START again").println();
			return;
		}
		String cont = doc.toString();
		int idx = cont.indexOf("recaptcha_response_field");

		if (idx >= 0) {
			response.getWriter().append("Authorize result: Failed").println();
			logger.info("Authorize result: failed");
			writeErrMsg("5");
		} else {
			response.getWriter().append("Authorize result: Success").println();
			logger.info("Authorize result: Success");

			parseRawData(doc, response, "0");

			logger.info("Timer Start");
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			timer = new Timer();
			task = new KenoGrabbingMT();
			task.setCookies(cookies);
			timer.schedule(task, 1000 * 60, 1000 * 60);

		}
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

	private void parseRawData(Document doc, HttpServletResponse response, String msgCode) {
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
//			PrintWriter writer = new PrintWriter("C:\\Users\\pohsun\\Desktop\\Keno-Grabbing-MT.txt", "UTF-8");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			writer.println("Message_Code: " + msgCode);
			writer.println("Last updated time: " + sdf.format(Calendar.getInstance().getTime()));
			writer.println("Version: 0");
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
				response.getWriter().append(line).println();
				logger.info(line);
				writer.println(line);
				line = "";
			}
			writer.close();
			logger.info("End writing file");
		} catch (Exception e) {
			logger.error("Error in drawing MT data. Error message: ", e);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public Document grap(Map<String, String> cookies) {
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
			doc = Jsoup.connect(url).cookies(cookies).timeout(1000).get();
			end = System.currentTimeMillis();
			logger.info("Finish Grabbing MT");
			logger.info("Total Grabbing　 time  = {}", (end - start) / 1000 + " secs");
		} catch (Exception e) {
			end = System.currentTimeMillis();
			logger.error("Exception: ", e);
			logger.info("Total Grabbing time = {}", (end - start) / 1000 + " secs");
			writeErrMsg("1");
			doc = null;
		}

		return doc;
	}
}

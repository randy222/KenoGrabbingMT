package com.grabbing.task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CheckResultServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private static final String ILOTTOFILENAME= "C:\\Users\\pohsun\\Desktop\\lotto-check-result.txt";
//	private static final String KENOFILENAME = "C:\\Users\\pohsun\\Desktop\\keno-check-result.txt";
	private static final String ILOTTO= "/usr/local/applications/kn-grabbing-server/lotto-check-result.txt";
	private static final String KENO= "/usr/local/applications/kn-grabbing-server/keno-check-result.txt";
	private static final String KENO_MT= "/usr/local/applications/kn-grabbing-server/Keno-Grabbing-MT.txt";
	private static final String RESTART= "/usr/local/applications/kn-grabbing-server/RestartResult.txt";
	
	public CheckResultServlet() {

		super();

		// TODO Auto-generated constructor stub
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String type = request.getParameter("type");
		BufferedReader br = null;
		FileReader fr = null;

		try {			
			if (type.equals("iLotto")) {
				fr = new FileReader(ILOTTO);
			} else if (type.equals("keno")) {
				fr = new FileReader(KENO);
			} else if (type.equals("MT")) {
				fr = new FileReader(KENO_MT);
			} else if (type.equals("restart")) {
				fr = new FileReader(RESTART);
			}
			br = new BufferedReader(fr);

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				response.getWriter().append(sCurrentLine).println();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

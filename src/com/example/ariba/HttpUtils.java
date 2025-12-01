package com.example.ariba;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

	public static String readRequestBodyAsString(HttpServletRequest request) throws IOException {

		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = request.getReader();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception e) {
				}
		}
		return sb.toString();
	}
}

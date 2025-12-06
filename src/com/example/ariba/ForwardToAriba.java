package com.example.ariba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sap.tc.logging.Location;
import com.example.ariba.ResolveUrlForServlet;
import com.example.ariba.GetBaseUrlFromRequest;
import com.example.ariba.UrlUtils;
import com.example.ariba.IsGetProfile;
import com.example.ariba.HttpUtils;

public class ForwardToAriba {

	private static final Location LOGGER = Location.getLocation(AribaCxmlServlet.class);

	public static void getToAriba(HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOGGER.infoT("[getToAriba] called");

		// --- 1. Determine base URL of this Servlet ---
		String baseUrl = UrlUtils.removeDefaultHttpsPort(GetBaseUrlFromRequest.getBaseUrlFromRequest(request));
		LOGGER.infoT("[getToAriba] getBaseUrlFromRequest: " + baseUrl);

		// --- 2. Get the full requested URL ---
		String modifiedUrl = UrlUtils.removeDefaultHttpsPort(request.getRequestURL().toString()); // e.g.,
		LOGGER.infoT("[getToAriba] modifiedUrl: " + modifiedUrl);

		// --- 3. Resolve real Ariba URL ---
		String targetUrlStr = UrlUtils.removeDefaultHttpsPort(ResolveUrlForServlet.getUrl(baseUrl, modifiedUrl));
		LOGGER.infoT("[getToAriba] targetUrlStr: " + targetUrlStr);

		// --- 4.1. Validate if the target URL is with Ariba ---
		if (!UrlUtils.isAllowedAribaHost(targetUrlStr)) {
			LOGGER.errorT("[getToAriba] Blocked outbound GET to non-Ariba domain: " + targetUrlStr);
			response.sendError(403, "Forbidden target host " + targetUrlStr);
			return;
		}

		// --- 4.2. Validate if the source is Localhost ---
		if (!UrlUtils.isLocalHost(request)) {
			LOGGER.errorT("[getToAriba] Blocked outbound GET from non-local host: " + request.getRemoteAddr());
			response.sendError(403, "Forbidden source host " + request.getRemoteAddr());
			return;
		}

		LOGGER.infoT("[getToAriba] Forwarding GET to Ariba URL: " + targetUrlStr);

		// --- 4. Open connection and forward GET request ---
		URL targetUrl = new URL(targetUrlStr);
		HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "text/xml");
		conn.setRequestProperty("Connection", "close");
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(30000);

		int status = conn.getResponseCode();
		LOGGER.infoT("[getToAriba] Ariba GET response code: " + status);

		// --- 5. Read Ariba response body ---
		BufferedReader brAribaResponse = null;
		StringBuilder sbResponseToCaller = new StringBuilder();
		try {
			brAribaResponse = new BufferedReader(
					new InputStreamReader(status >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = brAribaResponse.readLine()) != null) {
				sbResponseToCaller.append(line);
			}
		} finally {
			if (brAribaResponse != null) {
				try {
					brAribaResponse.close();
				} catch (Exception e) {
					// TODO: error handling
				}
			}
			if (conn != null) {
				try {
					conn.disconnect();
				} catch (Exception e) {
					// TODO: error handling
				}
			}
		}

		LOGGER.infoT("[getToAriba] Ariba GET response body:\n" + sbResponseToCaller.toString());
		LOGGER.infoT("[getToAriba] Ariba GET processing done");

		// --- 6. Return response to servlet caller ---
		// Assumption here is GET will never return non-XML payload
		response.setContentType("text/xml; charset=UTF-8");

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(sbResponseToCaller.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					// TODO: error handling
				}
			}
			LOGGER.infoT("[getToAriba] Ariba GET repsonse returned to caller.");
		}
	}

	public static void postToAriba(HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOGGER.infoT("[postToAriba] called");

		// 1. Read inbound payload from SAP servlet caller
		String inboundXml = HttpUtils.readRequestBodyAsString(request);
		LOGGER.infoT("[postToAriba] Received Inbound POST payload:\n" + inboundXml);

		// Detect if this is a GetProfile request
		Boolean isGetProfileRequest = IsGetProfile.isGetProfileRequest(inboundXml);
		if (isGetProfileRequest) {
			LOGGER.infoT("[postToAriba] Received Request for GetProfile, mapping is required for Response");
		}

		// 2. Get servlet base URL (used for URL rewriting)
		String baseUrl = GetBaseUrlFromRequest.getBaseUrlFromRequest(request);
		LOGGER.infoT("[postToAriba] baseUrl after GetBaseUrlFromRequest: " + baseUrl);

		// 3. Determine target Ariba URL (from request URL)
		String modifiedUrl = UrlUtils.removeDefaultHttpsPort(request.getRequestURL().toString());
		String targetUrlStr = UrlUtils.removeDefaultHttpsPort(ResolveUrlForServlet.getUrl(baseUrl, modifiedUrl));
		LOGGER.infoT("[postToAriba] Resolved target POST URL: " + targetUrlStr);

		// --- 4.1. Validate if the target URL is with Ariba
		if (!UrlUtils.isAllowedAribaHost(targetUrlStr)) {
			LOGGER.errorT("[postToAriba] Blocked outbound POST to non-Ariba domain: " + targetUrlStr);
			response.sendError(403, "Forbidden target host " + targetUrlStr);
			return;
		}

		// --- 4.2. Validate if the source is Localhost
		if (!UrlUtils.isLocalHost(request)) {
			LOGGER.errorT("[postToAriba] Blocked outbound POST from non-local host: " + request.getRemoteAddr());
			response.sendError(403, "Forbidden source host " + request.getRemoteAddr());
			return;
		}

		// 5. POST outbound to Ariba
		URL targetUrl = new URL(targetUrlStr);
		HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();

		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		conn.setRequestProperty("Accept", "text/xml");
		conn.setRequestProperty("Connection", "close");
		conn.setDoOutput(true);
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(30000);

		// Send payload to Ariba AN
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(conn.getOutputStream());
			pw.write(inboundXml);
			pw.flush();
		} finally {
			if (pw != null)
				try {
					pw.close();
				} catch (Exception e) {
				}
		}

		// 6. Read Ariba response
		int status = conn.getResponseCode();
		LOGGER.infoT("[postToAriba] Ariba POST response code: " + status);

		String contentType = conn.getHeaderField("Content-Type");
		LOGGER.infoT("[postToAriba] Ariba POST response content type: " + contentType);

		boolean isMultipart = contentType != null && contentType.toLowerCase().startsWith("multipart/");
		LOGGER.infoT("[postToAriba] Ariba POST response isMultipart: " + isMultipart);

		logAllHeaders(conn); // For debugging purpose

		/********************************************************************************************* */
		/*
		 * Deal with MUST pass through headers： content-type, content-length,
		 * content-encoding (gzip)
		 */
		/********************************************************************************************* */

		// Content-Type (Mandatory): multipart/* and text/xml, application/xml,
		// text/html
		response.setContentType(contentType);

		// Content-Length (for multipart only), Long -> Integer
		String clHeader = conn.getHeaderField("Content-Length");
		LOGGER.infoT("[postToAriba] Ariba POST response Content-Length: " + clHeader);
		if (clHeader != null && isMultipart) {
			try {
				long contentLength = Long.parseLong(clHeader);
				if (contentLength <= Integer.MAX_VALUE) {
					response.setContentLength((int) contentLength);
					LOGGER.infoT("[postToAriba] Set Content-Length to caller successful");
				}
				// If content length larger than 2G, fallback to use chunk transfer mode by
				// default
			} catch (NumberFormatException e) {
				LOGGER.infoT("[postToAriba] Content-Length larger than 2G, fallback to use chunk by default");
			}
		}

		// Content-Encoding
		String ceHeader = conn.getHeaderField("Content-Encoding");
		if (ceHeader != null) {
			response.setHeader("Content-Encoding", ceHeader);
			LOGGER.infoT("[postToAriba] Pass through Ariba Response Content-Encoding: '" + ceHeader + "' to caller");
		}

		// 7. If Ariba returned multipart or non-XML style payload, directly pass
		// through
		// It seems that Ariba will return payload with "text/HTML" type XML payload.
		if (isMultipart) {
			LOGGER.infoT("[postToAriba] Multipart response detected → passthrough without modification");
			ServletOutputStream outStream = response.getOutputStream();
			InputStream inStream = (status >= 400 ? conn.getErrorStream() : conn.getInputStream());

			byte[] buffer = new byte[8192];
			int bytesRead;
			while ((bytesRead = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.flush();
			inStream.close();
			LOGGER.infoT("[postToAriba] Ariba non-XML Response sending to caller completed");

		} else {
			// Regular XML payload, proceed as string to get payload logging
			BufferedReader br = null;
			StringBuffer sb = new StringBuffer();
			try {
				br = new BufferedReader(
						new InputStreamReader(status >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"));
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
			String aribaResp = sb.toString();
			LOGGER.infoT("[postToAriba] Ariba response body(raw):\n" + aribaResp);

			if (isGetProfileRequest) {

				LOGGER.infoT("[postToAriba] Detected ProfileResponse → applying URL mapping...");
				aribaResp = rewriteAllUrlTags(aribaResp, baseUrl);
				LOGGER.infoT("[postToAriba] Ariba response body(rewritten):\n" + aribaResp);
			}

			// 8. Return response to caller - text/xml type
			PrintWriter out = null;
			try {
				out = response.getWriter();
				out.write(aribaResp);
			} finally {
				if (out != null)
					try {
						out.close();
					} catch (Exception e) {
					}
			}
			LOGGER.infoT("[postToAriba] Ariba XML Response sending to caller completed");
		}
		//Clean up the client connection
		if (conn != null) {
			try {
				conn.disconnect();
			} catch (Exception e) {
				// TODO: error handling
			}
		}
	}

	private static String rewriteAllUrlTags(String xml, String baseUrl) {

		if (xml == null) {
			return xml;
		}

		StringBuffer sb = new StringBuffer();
		int searchIndex = 0;

		while (true) {
			int start = xml.indexOf("<URL>", searchIndex); // Position of Open Tag
			if (start == -1) {
				sb.append(xml.substring(searchIndex));
				break;
			}

			int end = xml.indexOf("</URL>", start); // Position of Close Tag
			if (end == -1) {
				// malformed XML - Only Open no Close, return original
				return xml;
			}

			// append everything before <URL>
			sb.append(xml.substring(searchIndex, start + 5)); // include <URL>

			// original URL content
			String oldUrl = xml.substring(start + 5, end);
			String newUrl = MapUrlFromProfile.mapUrl(baseUrl, oldUrl);

			// append replaced URL
			sb.append(newUrl);

			// move cursor after </URL>
			searchIndex = end;
		}

		return sb.toString();
	}

	private static void logAllHeaders(HttpURLConnection conn) {

		Map<String, List<String>> headerMap = conn.getHeaderFields();

		if (headerMap == null) {
			LOGGER.infoT("No headers returned.");
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("----- Ariba Response Headers Begin -----\n");

		for (Map.Entry<String, List<String>> entry : headerMap.entrySet()) {

			String name = entry.getKey(); // may be null
			List<String> values = entry.getValue();

			if (name == null) {
				// Status line
				for (String v : values) {
					sb.append("Status Line: ").append(v).append("\n");
				}
			} else {
				for (String v : values) {
					sb.append(name).append(": ").append(v).append("\n");
				}
			}
		}

		sb.append("----- Ariba Response Headers End -----");

		LOGGER.infoT(sb.toString());
	}
}

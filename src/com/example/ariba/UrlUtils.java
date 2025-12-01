package com.example.ariba;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class UrlUtils {

    /**
     * Remove default HTTPS port (:443) from the URL if present
     */
    public static String removeDefaultHttpsPort(String urlStr) {

        if (urlStr == null || urlStr.isEmpty()) return urlStr;

        try {
            URL url = new URL(urlStr);
            String protocol = url.getProtocol();
            int port = url.getPort(); // -1 if no port explicitly specified

            // Only remove :443 for https
            if ("https".equalsIgnoreCase(protocol) && port == 443) {
                // Reconstruct URL without port
                StringBuilder sb = new StringBuilder();
                sb.append(protocol).append("://").append(url.getHost());

                if (url.getPath() != null) sb.append(url.getPath());
                if (url.getQuery() != null) sb.append("?").append(url.getQuery());
                if (url.getRef() != null) sb.append("#").append(url.getRef());

                return sb.toString();
            }

        } catch (MalformedURLException e) {
            // invalid URL, return as-is
            return urlStr;
        }

        return urlStr;
    }

	// Target host must be Ariba domain based hostname
	public static boolean isAllowedAribaHost(String urlStr) {

		if (urlStr == null)
			return false;

		try {
			URL u = new URL(urlStr);
			String host = u.getHost().toLowerCase();

			// exact match
			if (host.equals("ariba.com")) {
				return true;
			}

			// subdomain: *.ariba.com
			if (host.endsWith(".ariba.com")) {
				return true;
			}

			return false;

		} catch (Exception e) {
			// malformed URL
			return false;
		}
	}

	// Check if this is the localhost call
	public static boolean isLocalHost(HttpServletRequest request) {
		
		//Get remote and local IP
	    String remote = request.getRemoteAddr();
	    String local  = request.getLocalAddr();

	    // explicitly check loopback
	    if ("127.0.0.1".equals(remote) || "::1".equals(remote)) { return true; }

	    // Server Client are having the same IP
	    if (remote != null && remote.equals(local)) { return true; }
	    
	    return false;
	}
}
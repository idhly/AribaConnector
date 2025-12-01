package com.example.ariba;

import javax.servlet.http.HttpServletRequest;
import com.example.ariba.UrlUtils;

/**
 * Utility class to get the base URL of the current servlet dynamically.
 */
public class GetBaseUrlFromRequest {

    /**
     * Returns the base URL of the servlet, including scheme, host, port, context path, and servlet path.
     * <p>
     *
     * @param request the HttpServletRequest object
     * @return the full base URL of the current servlet
     */
    public static String getBaseUrlFromRequest(HttpServletRequest request) {
        
        String scheme = request.getScheme();             // http or https
        String serverName = request.getServerName();     // host
        int serverPort = request.getServerPort();        // port
        String contextPath = request.getContextPath();   // e.g., /AribaConnector
        String servletPath = request.getServletPath();   // e.g., /cxml

        // Include port only if it's non-standard
        String portPart = "";
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            portPart = ":" + serverPort;
        }
        
        return UrlUtils.removeDefaultHttpsPort(scheme + "://" + serverName + portPart + contextPath + servletPath);
    }
}
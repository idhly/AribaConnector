package com.example.ariba;

import java.net.MalformedURLException;
import java.net.URL;
import com.example.ariba.UrlUtils;

public class MapUrlFromProfile {

	/**
	 * Transforms a source URL into a format that passes through a local base servlet endpoint.
	 * <p>
	 * The resulting URL will be in the format:
	 * <pre>
	 *     <baseUrl>/<sourceHost>/<sourcePath>?<sourceQuery>
	 * </pre>
	 * <p>
	 *
	 * @param baseUrl the base servlet endpoint URL (e.g., local server URL)
	 * @param srcUrl  the original source URL that needs to be rewritten
	 * @return a new URL that combines the base servlet URL with the source host, path, and query parameters
	 */
	public static String mapUrl( String baseUrl, String srcUrl) {

		String localBaseURL = UrlUtils.removeDefaultHttpsPort(baseUrl.endsWith("/") ? baseUrl : baseUrl + "/");
		String urlResult;
		
		try {
			// Get path from source URL and append it to base URL
		    URL url = new URL(UrlUtils.removeDefaultHttpsPort(srcUrl));
		    
		    String host = url.getHost();
		    String path = url.getPath();
		    String query = url.getQuery();
		    
            // Combine base URL and path
            urlResult = localBaseURL + host + path;

            // Append query parameters if exist
            if (query != null && !query.isEmpty()) {
                urlResult += "?" + query;
            }
		} catch (MalformedURLException e) {
			//If URL format is incorrect, fall back to original URL
			urlResult = srcUrl;
		}
		
		return UrlUtils.removeDefaultHttpsPort(urlResult);		
	};
}

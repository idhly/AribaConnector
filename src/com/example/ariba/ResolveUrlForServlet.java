package com.example.ariba;

public class ResolveUrlForServlet {

	/**
	 * Extract the Ariba endpoint URL from the modified URL
	 * 
	 * @param modifiedUrl
	 *            the URL like baseUrl/<host>/<path>?<query>
	 * @param baseUrl
	 *            the original baseUrl used in setUrl
	 * @return the reconstructed srcUrl
	 */
	public static String getUrl(String baseUrl, String modifiedUrl ) {
		
		try {
			// Ensure baseUrl/modifiedUrl ends with "/" for comparation
			String localBaseURL = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
			String localModifiedURL = modifiedUrl.endsWith("/") ? modifiedUrl : modifiedUrl + "/";
			
			// If Servlet base URL equal modified URL, then take default
			if (localBaseURL.equals(localModifiedURL)) {
				return "https://service-2.ariba.com/service/transaction/cxml.asp";
			}

			// Modified URL doesn't start with Servlet baseURL, this shall not happen so return original URL
			if (!modifiedUrl.startsWith(localBaseURL)) {
				return modifiedUrl;
			}
			
			/*************************************** */
			/* Extract URL and compose the Ariba URL */
			/*************************************** */
			
			// Get "host/path?query" from Modified URL
			String remaining = modifiedUrl.substring(localBaseURL.length());

			// Find first "/" to separate host and path
			int slashIndex = remaining.indexOf('/');
			if (slashIndex == -1) {
				// only host, no path
				return "https://" + remaining;
			}

			String host = remaining.substring(0, slashIndex);
			String pathAndQuery = remaining.substring(slashIndex); // includes leading '/'

			return "https://" + host + pathAndQuery;

		} catch (Exception e) {
			// fallback
			return modifiedUrl;
		}
	};
}

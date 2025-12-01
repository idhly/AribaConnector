package com.example.ariba;

import static com.example.ariba.XmlUtils.parseCXML;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class IsGetProfile {

    /**
     * Checks if the given cXML string represents a ProfileRequest transaction.
     *
     * @param xml the cXML payload as string
     * @return true if the XML contains a <ProfileRequest> element, false otherwise
     */
    public static Boolean isGetProfileResponse(String body) {

        if (body == null || body.isEmpty()) {
            return false;
        }
      
        Document doc = parseCXML(body);
        if (doc == null) {
            return false; // parsing failed
        }

        // Look for <ProfileResponse> element
        NodeList profileNodes = doc.getElementsByTagName("ProfileResponse");
        return profileNodes != null && profileNodes.getLength() > 0;
    }
    
    public static Boolean isGetProfileRequest(String body) {
    	if (body == null | body.isEmpty()) {
    		return false;
    	}
    	Document doc = parseCXML(body);
    	if (doc == null) {
    		return false;
    	}
    	// Look for <ProfileRequest> element
        NodeList profileNodes = doc.getElementsByTagName("ProfileRequest");
        return profileNodes != null && profileNodes.getLength() > 0;
    }
}
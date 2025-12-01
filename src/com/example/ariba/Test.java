package com.example.ariba;

import com.example.ariba.GetBaseUrlFromRequest;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.example.ariba.UrlUtils;

public class Test extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		
		response.getWriter().write("<h1>Hostname Resolution:</h1><br>");
		
		response.getWriter().write(HtmlP("getServerName", request.getServerName()));
		response.getWriter().write(HtmlP("getLocalName", request.getLocalName()));
		response.getWriter().write("<hr>");
		
		response.getWriter().write(HtmlP("getServerPort", String.valueOf(request.getServerPort())));
		response.getWriter().write(HtmlP("getLocalPort", String.valueOf(request.getLocalPort())));
		response.getWriter().write("<hr>");
		
		response.getWriter().write(HtmlP("getRemoteAddr", request.getRemoteAddr()));
		response.getWriter().write(HtmlP("getLocalAddr", request.getLocalAddr()));
		response.getWriter().write("<hr>");
		
		response.getWriter().write(HtmlP("getHeader->User-Agent", request.getHeader("User-Agent")));
		response.getWriter().write("<hr>");
		
		response.getWriter().write(HtmlP("getBaseUrlFromRequest",GetBaseUrlFromRequest.getBaseUrlFromRequest(request)));
		response.getWriter().write("<hr>");

		response.getWriter().write(HtmlP("isLocalHost", String.valueOf(UrlUtils.isLocalHost(request))));
	};
	private String HtmlP(String tagName, String tagValue) {

		return "<p>" + tagName + ": " + tagValue  + "</p>\n\r";
	
	}
}

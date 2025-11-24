package com.example.ariba;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.HttpURLConnection;
import java.net.URL;

public class AribaCxmlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String ARIBA_ENDPOINT =
        "https://service-2.ariba.com/service/transaction/cxml.asp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getQueryString();
        forwardToAriba(query != null ? query : "", response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception e) {}
            }
        }

        forwardToAriba(sb.toString(), response);
    }

    private void forwardToAriba(String payload, HttpServletResponse response)
            throws IOException {

        URL url = new URL(ARIBA_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setConnectTimeout(10000);      // connection timeout: 10 seconds
        conn.setReadTimeout(30000);         // read timeout: 30 seconds
        conn.setDoOutput(true);
        
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

        // forward data
        OutputStream os = null;
        try {
            os = conn.getOutputStream();
            os.write(payload.getBytes("UTF-8"));
            os.flush();
        } finally {
            if (os != null) try { os.close(); } catch (Exception e) {}
        }

        // get response
        BufferedReader br = null;
        PrintWriter out = response.getWriter();
        try {
            InputStreamReader isr;
            try {
                isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
            } catch (IOException ioe) {
                // If exception occurs
                isr = new InputStreamReader(((java.net.HttpURLConnection) conn).getErrorStream(), "UTF-8");
            }
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                out.println(line);
            }
        } finally {
            if (br != null) try { br.close(); } catch (Exception e) {}
        }

        response.setContentType("text/xml; charset=UTF-8");
    }
}
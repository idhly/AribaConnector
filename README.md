# Ariba cXML proxy Servlet

This is prototype with a simple Java EE5 based servlet to proxy the Ariba cxml service on a SAP NW Java Application Server.

Tested ok with NetWeaver Java Application Server 7.50 SP13.

## Features

- Receives HTTP GET or POST requests
- Forwards the payload to `https://service-2.ariba.com/service/transaction/cxml.asp`
- Returns the response from Ariba to the client
- Works with SAP NW Java HTTPS stack (DSR/IAIK)

## Requirements

- SAP NetWeaver Java 7.50 (or compatible)
- JDK 1.8+
- NWDS (optional, for development)
- No external libraries required

## Project Structure

- src/com/example/ariba/AribaCxmlServlet.java
- WebContent/WEB-INF/web.xml
- WebContent/index.jsp
- .gitignore
- README.md

## Usage

1. Deploy `AribaConnector` with NWDS 7.50 to your SAP NetWeaver Java server.
2. Access the servlet via:
```GET http(s)://<host>:<port>/AribaConnector/cxml```
```POST http(s)://<host>:<port>/AribaConnector/cxml```

3. The servlet will forward the request to the Ariba endpoint and return the response.

## Disclaimer

This project is provided as a **prototype / example** for educational and demonstration purposes only.  

- The author **does not take any responsibility** for any use of this code in production or any modifications made to it.  
- Users who download, run, or develop further based on this code **assume full responsibility** for their actions, including integration, deployment, or further development.  
- Use this code at your own risk.
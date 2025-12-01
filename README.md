# Ariba cXML proxy Servlet

This is prototype with a simple Java EE5 based servlet to proxy the Ariba cxml service on a SAP NW Java Application Server.

Tested ok with NetWeaver Java Application Server 7.50 SP13.

## Features

- Receives HTTP GET or POST requests from Ariba CI8 adapter from localhost (SAP PI/PO).
- Forwards the POST cXML payload to associated Ariba endpoints.
- Returns the cXML response from Ariba to the Ariba CI8 adapter.
- Works with SAP NW Java HTTPS stack (DSR/IAIK).
- This Web Module can map Ariba service endpoints 'URL' in the getProfile response from `https://<ariba host>/<service path>` to `http(s)://<local host>:<port>/AribaConnector/cxml/<ariba host>/<service path>`.

## Requirements

- SAP NetWeaver Java 7.50 (or compatible)
- sapJVM 1.8+ (build target can set to sapJVM 1.6)
- NWDS (optional, for development)
- No external libraries required

## Project Structure

- src/com/example/ariba/AribaCxmlServlet.java
- src/com/example/ariba/ForwardtoAriba.java
- src/com/example/ariba/GetBaseUrlFromRequest.java
- src/com/example/ariba/HttpUtils.java
- src/com/example/ariba/IsGetProfile.java
- src/com/example/ariba/MapUrlFromProfile.java
- src/com/example/ariba/ResolveUrlForServlet.java
- src/com/example/ariba/Test.java (optional)
- src/com/example/ariba/UrlUtils.java
- src/com/example/ariba/XmlUtils.java
- WebContent/WEB-INF/web.xml
- WebContent/index.jsp
- .gitignore
- LICENSE
- README.md

## Usage

1. Build `AribaConnector` EAR package from the project.
2. Deploy `AribaConnector` EAR with NWDS 7.50 or Telnet to your SAP NetWeaver Java server.
3. Access the servlet via:
- `GET http(s)://<host>:<port>/AribaConnector/cxml` - Ping test for connectivity to Ariba
- `POST http(s)://<host>:<port>/AribaConnector/cxml` - cXML request/response
4. The servlet will forward the request to the Ariba endpoint and return the response.

## Disclaimer

This project is provided as a **prototype / example** for educational and demonstration purposes only.  

- The author **does not take any responsibility** for any use of this code in production or any modifications made to it.  
- Users who download, run, or develop further based on this code **assume full responsibility** for their actions, including integration, deployment, or further development.  
- Use this code at your own risk.
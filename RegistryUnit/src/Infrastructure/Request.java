/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author billaros
 */
public class Request {

    String client;
    int requestType;
    String Host;
    String UserAgent;
    String Method;
    String Accept;
    String AcceptLanguage;
    String AcceptEncoding;
    String Connection;
    String ContentType;
    HashMap <String,String> Parameters;
    String URI;

    public Request() {
        this.Parameters = new HashMap<String,String>();
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String Host) {
        this.Host = Host;
    }

    public String getUserAgent() {
        return UserAgent;
    }

    public void setUserAgent(String UserAgent) {
        this.UserAgent = UserAgent;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
    
    public String getMethod() {
        return Method;
    }

    public void setMethod(String Method) {
        this.Method = Method;
    }

    public String getAccept() {
        return Accept;
    }

    public void setAccept(String Accept) {
        this.Accept = Accept;
    }

    public String getAcceptLanguage() {
        return AcceptLanguage;
    }

    public void setAcceptLanguage(String AcceptLanguage) {
        this.AcceptLanguage = AcceptLanguage;
    }

    public String getConnection() {
        return Connection;
    }

    public void setConnection(String Connection) {
        this.Connection = Connection;
    }

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String ContentType) {
        this.ContentType = ContentType;
    }

    public HashMap<String,String> getParameters() {
        return Parameters;
    }

    public void setParameters(HashMap Parameters) {
        this.Parameters = Parameters;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getAcceptEncoding() {
        return AcceptEncoding;
    }

    public void setAcceptEncoding(String AcceptEncoding) {
        this.AcceptEncoding = AcceptEncoding;
    }

    public void parseRequest(String in) {
        String[] lines = in.split("\n");
        int lineCount = 1;
        for (String line : lines) {
            //System.out.println("I am trying to parse for "+lineCount+" time");
            System.out.println(line);
            
            if(line.isEmpty()){
                continue;
            }
            if (lineCount == 1) {
                String[] parts = line.split(" ");
                this.URI = parts[1];
                this.Method = parts[0];
            } else if (line.startsWith("Host: ")) {
                this.setHost(line.substring("Host: ".length()));
            } else if (line.startsWith("User-Agent: ")) {
                this.setUserAgent(line.substring("User-Agent: ".length()));
            } else if (line.startsWith("Accept: ")) {
                this.setAccept(line.substring("Accept: ".length()));
            } else if (line.startsWith("Accept-Language: ")) {
                this.setAcceptLanguage(line.substring("Accept-Language: ".length()));
            } else if (line.startsWith("Accept-Encoding: ")) {
                this.setAcceptEncoding(line.substring("Accept-Encoding: ".length()));
            } else if (line.startsWith("Connection: ")) {
                this.setConnection(line.substring("Connection: ".length()));
            } else if (line.startsWith("Content-Type: ")) {
                this.setContentType(line.substring("Content-Type: ".length()));
            } else if (line.startsWith("Params: ")) {
                String paramsLine = line.substring("Params: ".length());
                paramsLine = java.net.URLDecoder.decode(paramsLine);
                System.out.println("ParamsLine is "+paramsLine);
                String[] params = paramsLine.split("&");
                for (String param : params) {
                    String[] kv = param.split("=");
                    try {
                        String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(kv[1], "UTF-8");
                        this.Parameters.put(key, value);
                        System.out.println(key + " = "+ value);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            lineCount++;
        }
    }

}

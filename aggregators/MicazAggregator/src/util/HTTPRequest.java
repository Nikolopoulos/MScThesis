/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author billaros
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

public class HTTPRequest {

    //so very mozilla wow
    //This class models the HTTPRequests so that I don't have to type them 
    //every time
    //I should make this a jar 
    private static final String USER_AGENT = "Mozilla/5.0";

    //Gee thanks mkyong
    // HTTP GET request
    public static void sendGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    // HTTP POST request
    public static String sendPost(String url, int port, String parameters, String service) throws Exception {
        URL obj = new URL(url);
        byte[] urld = {127, 0, 0, 1};
        try {
            Socket s = new Socket(InetAddress.getByAddress(urld), port);

            PrintWriter pw = new PrintWriter(s.getOutputStream());

            pw.print("POST " + service + " HTTP/1.1\n");

            pw.print("User-Agent: Mozilla/5.0\n");

            pw.print("Accept-Language: en-US,en;q=0.5\n");

            pw.print("Host: 127.0.0.1:8383\n");

            pw.print("Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2\n");

            pw.print("Connection: keep-alive\n");

            pw.print("Content-type: application/x-www-form-urlencoded\n");

            pw.print("Content-Length: " + parameters.length() + "\n");
            System.out.println("Content-Length: " + parameters.length() + "\n");

            pw.print(parameters);

            pw.println("");

            pw.flush();
            //pw.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            //This is totally production safe code
            //By all maens use it
            String t = "";
            String line = "";
            try {
                while ((line = br.readLine()) != null) {
                    t += line;
                    System.out.println("Line flush " + t);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            System.out.println(t);
            br.close();

            return t;
        } catch (Exception e) {
            return "{}";
            //LEL
        }

    }

}

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

    private static final String USER_AGENT = "Mozilla/5.0";

    /*    public static void main(String[] args) throws Exception {

     HttpURLConnectionExample http = new HttpURLConnectionExample();

     System.out.println("Testing 1 - Send Http GET request");
     http.sendGet();

     System.out.println("\nTesting 2 - Send Http POST request");
     http.sendPost();

     }*/
    // HTTP GET request
    public static void sendGet(String url) throws Exception {

        //String url = "http://www.google.com/search?q=mkyong";
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
            /* br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();
         br.readLine();*/
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

            /*HttpURLConnection con = (HttpURLConnection) obj.openConnection();

         //add reuqest header
         con.setRequestMethod("POST");
         con.setRequestProperty("User-Agent", USER_AGENT);
         con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

         //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";
         // Send post request
         con.setDoOutput(true);
         DataOutputStream wr = new DataOutputStream(con.getOutputStream());
         wr.writeBytes(parameters);
         wr.flush();
         wr.close();

         System.out.println("\nSending 'POST' request to URL : " + url);
         System.out.println("Post parameters : " + parameters);
         Thread.sleep(1000);
         StringBuffer response = new StringBuffer();
         int responseCode = con.getResponseCode();

         System.out.println("Response Code : " + responseCode);
         try {
         BufferedReader in = new BufferedReader(
         new InputStreamReader(con.getInputStream()));
         String inputLine;
         response = new StringBuffer();

         while ((inputLine = in.readLine()) != null) {
         response.append(inputLine);
         }
         in.close();
         } catch (Exception e) {
         e.printStackTrace();
         try {
         BufferedReader in = new BufferedReader(
         new InputStreamReader(con.getErrorStream()));
         String inputLine;
         response = new StringBuffer();

         while ((inputLine = in.readLine()) != null) {
         response.append(inputLine);
         }
         in.close();
         } catch (Exception ex) {
         ex.printStackTrace();
         }
         }*/
            //print result 
            return t;
        } catch (Exception e) {
            return "{}";
        }

    }

}

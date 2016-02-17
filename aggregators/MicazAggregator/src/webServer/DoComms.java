/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import sensorPlatforms.MicazMote;
import util.Control;

/**
 *
 * @author billaros
 */
class DoComms implements Runnable {

    private Socket server;
    private String line, input, requestedURL, noBreakInput;
    private final Control con;

    DoComms(Socket server, Control c) {
        this.server = server;
        this.con = c;
    }

    public void run() {

        try {
            con.HTTPCore.attachTo();
        } catch (Exception ex) {
            Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
        }
        input = "";
        noBreakInput = "";
        requestedURL = "";
        try {
            // Get input from the client
            DataInputStream in = new DataInputStream(server.getInputStream());
            PrintStream out = new PrintStream(server.getOutputStream());
            int lineNumber = 1;
            while ((line = in.readLine()) != null && line.length() > 0) {
                if (lineNumber == 1) {
                    String[] parts = line.split(" ");
                    requestedURL = parts[1];
                }
                input = input + line + "\n";
                noBreakInput = noBreakInput + line;
                lineNumber++;
            }
            String reply = "";
            if (requestedURL.equals("/")) {
                reply = "127.0.0.1:8181/sensors -> returns a list of sensors available\n"
                        + "127.0.0.1:8181/sensor/ID -> returns data of specific sensor with id = ID\n"
                        + "127.0.0.1:8181/sensor/ID/light|temp -> returns data about light|temperature of specific sensor with id = ID\n"
                        + "127.0.0.1:8181/sensor/ID/switch toggles the switch available on the sensor node and returns the state of the sensor node as if 127.0.0.1:8181/sensor/ID was called";
            } else if (requestedURL.startsWith("/sensors")) {
                reply = "{\"sensors\":{[";
                for (MicazMote m : con.getMotesList()) {
                    reply += m.JSONDescription() + ",";
                }
                if (reply.length() > 10) {
                    reply = reply.substring(0, reply.length() - 1);
                }
                reply += "]}";
            } else if (requestedURL.startsWith("/sensor/") && (!requestedURL.contains("light")) && (!requestedURL.contains("temp")) && (!requestedURL.contains("switch"))) {
                if (requestedURL.length() < 9) {
                    reply = "127.0.0.1:8181/sensors -> returns a list of sensors available\n"
                            + "127.0.0.1:8181/sensor/ID -> returns data of specific sensor with id = ID\n"
                            + "127.0.0.1:8181/sensor/ID/light|temp -> returns data about light|temperature of specific sensor with id = ID\n"
                            + "127.0.0.1:8181/sensor/ID/switch toggles the switch available on the sensor node and returns the state of the sensor node as if 127.0.0.1:8181/sensor/ID was called";

                } else {
                    int ID = Integer.parseInt(requestedURL.split("/")[2]);
                    con.sendReadingRequest(ID, lib.Constants.TEMP);
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    con.sendReadingRequest(ID, lib.Constants.PHOTO);
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    con.getSwitchInfo(ID);
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    reply = "{\"sensor\":{";
                    for (MicazMote m : con.getMotesList()) {
                        if (m.getId() == ID) {
                            reply += m.JSONObject();
                        }
                    }
                    reply += "}}";
                }

            } else if (requestedURL.startsWith("/sensor/") && (requestedURL.contains("light"))) {
                int ID = Integer.parseInt(requestedURL.substring(8, requestedURL.indexOf("/", 9)));
                con.sendReadingRequest(ID, lib.Constants.PHOTO);
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
                }
                reply = "{\"sensor\":{";
                for (MicazMote m : con.getMotesList()) {
                    if (m.getId() == ID) {
                        reply += m.JSONLight();
                    }
                }
                reply += "}}";

            } else if (requestedURL.startsWith("/sensor/") && (requestedURL.contains("temp"))) {
                int ID = Integer.parseInt(requestedURL.substring(8, requestedURL.indexOf("/", 9)));
                con.sendReadingRequest(ID, lib.Constants.TEMP);
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
                }
                reply = "{\"sensor\":{";
                for (MicazMote m : con.getMotesList()) {
                    if (m.getId() == ID) {
                        reply += m.JSONTemp();
                    }
                }
                reply += "}}";

            } else if (requestedURL.startsWith("/sensor/") && (requestedURL.contains("switch"))) {
                int ID = Integer.parseInt(requestedURL.substring(8, requestedURL.indexOf("/", 9)));
                con.toggleSwitch(ID);
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(DoComms.class.getName()).log(Level.SEVERE, null, ex);
                }
                reply = "{\"sensor\":{";
                for (MicazMote m : con.getMotesList()) {
                    if (m.getId() == ID) {
                        reply += m.JSONObject();
                    }
                }
                reply += "}}";

            }
            out.println(reply);
            out.flush();
            out.close();
            server.close();
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }
}

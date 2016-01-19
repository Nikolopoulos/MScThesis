/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import oscilloscope.Messaging;
import sensorPlatforms.MicazMote;

/**
 *
 * @author billaros
 */
public class Control {

    ArrayList<MicazMote> motesList;
    Messaging messages;
    Thread dropDaemon, populate;
    String uid = "";

    public Control() {

        String jsonReply = "";
       
        try {
            InetAddress addr = getFirstNonLoopbackAddress(true,false);
            String ip = addr.getHostAddress();            
            //messages = new Messaging(this);
            jsonReply = HTTPRequest.sendPost("http://"+ip, 8383, URLEncoder.encode("ip="+ip+"&port=8181&services={\"services\":[{\"uri\" : \"/sensors\", \"description\" : \"returns a list of sensors available\"}]}"), "/register");
            System.out.println("reply is: " + jsonReply);
            JSONObject obj;

            obj = new JSONObject(jsonReply);

            if (!obj.get("result").equals("success")) {
                System.out.println("jsonReply failed " + jsonReply);
            } else {
                uid = obj.getString("uid");
                System.out.println("myUID is " + uid);
            }

        } catch (Exception e) {
            System.out.println(jsonReply);
            e.printStackTrace();
        }
        motesList = new ArrayList<MicazMote>();
        dropDaemon = createDropDaemon();
        dropDaemon.start();
        populate = constructPollDaemon();
        populate.start();
    }

    private synchronized Thread createDropDaemon() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                ArrayList<MicazMote> toRemove = new ArrayList<MicazMote>();
                System.out.println("Drop daemon started");
                while (true) {
                    for (MicazMote m : motesList) {

                        if (m.getLatestActivity() < Util.getTime() - 10000) {
                            System.out.println("dropin " + m);

                            toRemove.add(m);
                        } else {
                            //System.out.println("Latest activity " + m.getLatestActivity());

                        }
                    }
                    for (MicazMote m : toRemove) {
                        try {
                            String services = "{\"services\":[";

                            services += "{\"uri\" : \"/sensor/" + m.getId() + "\", \"description\" : \"returns data of specific sensor with id  " + m.getId() + "\"}";
                            if (m.isPhotoService()) {
                                services += ",{\"uri\" : \"/sensor/" + m.getId() + "/light\", \"description\" : \"returns data about light of specific sensor with id  " + m.getId() + "\"}";
                            }
                            if (m.isTempService()) {
                                services += ",{\"uri\" : \"/sensor/" + m.getId() + "/temp\", \"description\" : \"returns data about temperature of specific sensor with id  " + m.getId() + "\"}";
                            }
                            if (m.isSwitchService()) {
                                services += ",{\"uri\" : \"/sensor/" + m.getId() + "/switch\", \"description\" : \"switch toggles the switch available on the sensor node and returns the state of the sensor node as if aggregatorIP:8181/sensor/" + m.getId() + " was called\"}";
                            }
                            services += "]}";
                            System.out.println("My uid at update is " + uid);
                            String jsonReply = HTTPRequest.sendPost("http://127.0.0.1", 8383, URLEncoder.encode("uid=" + uid + "&services=" + services), "/delete");
                            System.out.println("reply is: " + jsonReply);
                            JSONObject obj;

                            obj = new JSONObject(jsonReply);
                        } catch (Exception ex) {
                            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        motesList.remove(m);
                        //sendDeleteRequestToRU
                    }
                    toRemove.clear();
                    //System.out.println("CurrentTime " + Util.getTime());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        return t;
    }

    public void reportPollAck(MicazMote mote) {
        boolean found = false;
        for (MicazMote m : motesList) {
            if (m.getId() == mote.getId()) {
                m.setLatestActivity(Util.getTime());
                found = true;
                break;
            }
        }
        if (!found) {
            motesList.add(mote);
            String jsonReply;
            if (uid.length() > 0) {
                try {
                    String services = "{\"services\":[";

                    services += "{\"uri\" : \"/sensor/" + mote.getId() + "\", \"description\" : \"returns data of specific sensor with id  " + mote.getId() + "\"}";
                    if (mote.isPhotoService()) {
                        services += ",{\"uri\" : \"/sensor/" + mote.getId() + "/light\", \"description\" : \"returns data about light of specific sensor with id  " + mote.getId() + "\"}";
                    }
                    if (mote.isTempService()) {
                        services += ",{\"uri\" : \"/sensor/" + mote.getId() + "/temp\", \"description\" : \"returns data about temperature of specific sensor with id  " + mote.getId() + "\"}";
                    }
                    if (mote.isSwitchService()) {
                        services += ",{\"uri\" : \"/sensor/" + mote.getId() + "/switch\", \"description\" : \"switch toggles the switch available on the sensor node and returns the state of the sensor node as if aggregatorIP:8181/sensor/" + mote.getId() + " was called\"}";
                    }
                    services += "]}";
                    System.out.println("My uid at update is " + uid);
                    jsonReply = HTTPRequest.sendPost("http://127.0.0.1", 8383, URLEncoder.encode("uid=" + uid + "&services=" + services), "/update");
                    System.out.println("reply is: " + jsonReply);
                    JSONObject obj;

                    obj = new JSONObject(jsonReply);
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    public void reportReading(int id, int messageType, int[] Readings) {
        for (MicazMote m : motesList) {
            if (m.getId() == id) {
                switch (messageType) {
                    case lib.Constants.TEMP: {
                        m.setTempReading(Util.median(Readings));
                        break;
                    }
                    case lib.Constants.PHOTO: {
                        m.setPhotoReading(Util.median(Readings));
                        break;
                    }
                    default: {
                        break;
                    }

                }

            }
        }
    }

    public void reportSwitch(int id, int state) {
        for (MicazMote m : motesList) {
            if (m.getId() == id) {
                m.setLatestActivity(Util.getTime());
                m.setSwitchState(state);
                System.out.println("State changed to " + state);
            }
        }
    }

    private Thread constructPollDaemon() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    sendPoll();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        return t;
    }

    public void sendPoll() {
        messages.sendPoll();
    }

    ;
    public void getSwitchInfo(int id) {
        messages.sendSwitchPoll(id);
    }

    public void toggleSwitch(int id) {
        messages.sendSwitchToggle(id);
    }

    public void sendReadingRequest(int id, int type) {
        messages.sendReadingRequest(id, type);
    }

    public ArrayList<MicazMote> getMotesList() {
        return motesList;
    }
    //courtesy of How to get the ip of the computer on linux through Java? -> http://stackoverflow.com/questions/901755/how-to-get-the-ip-of-the-computer-on-linux-through-java
    private static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
    Enumeration en = NetworkInterface.getNetworkInterfaces();
    while (en.hasMoreElements()) {
        NetworkInterface i = (NetworkInterface) en.nextElement();
        for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
            InetAddress addr = (InetAddress) en2.nextElement();
            if (!addr.isLoopbackAddress()) {
                if (addr instanceof Inet4Address) {
                    if (preferIPv6) {
                        continue;
                    }
                    return addr;
                }
                if (addr instanceof Inet6Address) {
                    if (preferIpv4) {
                        continue;
                    }
                    return addr;
                }
            }
        }
    }
    return null;
}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import affinitySupport.Core;
import affinitySupport.ThreadAffinity;
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
    boolean debug;
    public ThreadAffinity threadAffinity;
    public final Core encryptionCore;
    public final Core HTTPCore;
    public final Core sensingCore;
    public final Core cronCore;

    public Control(boolean debug) {
        //Thread affinity class is "Legaly CopyPasted" from a guy that I 
        //mention on my github on the affinity project
        threadAffinity = new ThreadAffinity(this);

        this.debug = debug;
        String jsonReply = "";

        if (threadAffinity.cores().length == 4) {
            //If quadcore etc
            encryptionCore = threadAffinity.cores()[0];
            HTTPCore = threadAffinity.cores()[1];
            sensingCore = threadAffinity.cores()[2];
            cronCore = threadAffinity.cores()[3];
        } else if (threadAffinity.cores().length == 2) {
            encryptionCore = threadAffinity.cores()[0];
            HTTPCore = threadAffinity.cores()[1];
            sensingCore = threadAffinity.cores()[1];
            cronCore = threadAffinity.cores()[0];
        } else if (threadAffinity.cores().length == 1) {
            encryptionCore = threadAffinity.cores()[0];
            HTTPCore = threadAffinity.cores()[0];
            sensingCore = threadAffinity.cores()[0];
            cronCore = threadAffinity.cores()[0];
        } else {
            //if32core fuck off, use only one core
            encryptionCore = threadAffinity.cores()[0];
            HTTPCore = threadAffinity.cores()[0];
            sensingCore = threadAffinity.cores()[0];
            cronCore = threadAffinity.cores()[0];
        }

        if (debug) {
            System.out.println("Available cores: " + threadAffinity.cores().length);
            System.out.println("encryptionCore: " + encryptionCore);
            System.out.println("HTTPCore: " + HTTPCore);
            System.out.println("sensingCore: " + sensingCore);
            System.out.println("cronCore: " + cronCore);
        }
        encryptionCore.setC(this);
        HTTPCore.setC(this);
        sensingCore.setC(this);
        cronCore.setC(this);

        try {
            //So this apparently gets your LAN address, provided there is one
            InetAddress addr = getFirstNonLoopbackAddress(true, false);
            String ip = addr.getHostAddress();

            //this could have been a nonlocal adress, but I'm such a bad person
            String registryUnitIP = "127.0.0.1";

            messages = new Messaging(this);
            jsonReply = HTTPRequest.sendPost(
                    "http://" + registryUnitIP,
                    8383,
                    URLEncoder.encode("ip=" + ip + "&port=8181&services={\"services\":[{\"uri\" : \"/sensors\", \"description\" : \"returns a list of sensors available\"}]}"), "/register");
            //registers itself to the registry unit

            if (debug) {
                System.out.println("reply is: " + jsonReply);
            }
            JSONObject obj;

            obj = new JSONObject(jsonReply);

            if (!obj.get("result").equals("success")) {
                //parsing the json object token by token
                if (debug) {
                    System.out.println("jsonReply failed " + jsonReply);
                }
            } else {
                uid = obj.getString("uid");
                if (debug) {
                    System.out.println("myUID is " + uid);
                }
            }

        } catch (Exception e) {
            if (debug) {
                System.out.println(jsonReply);
            }
            e.printStackTrace();
        }
        motesList = new ArrayList<MicazMote>();
        dropDaemon = createDropDaemon();
        dropDaemon.start();
        populate = constructPollDaemon();
        populate.start();
    }

    private synchronized Thread createDropDaemon() {
        //Creates a thread that drops innactive motes/sensors
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    cronCore.attachTo();
                    //WOW SHINY attaches self to cronCore
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
                ArrayList<MicazMote> toRemove = new ArrayList<MicazMote>();
                if (debug) {
                    System.out.println("Drop daemon started");
                }
                while (true) {
                    for (MicazMote m : motesList) {
                        //i'm not going to go through this, it's obvious
                        if (m.getLatestActivity() < Util.getTime() - 10000) {
                            if (debug) {
                                System.out.println("dropin " + m);
                            }

                            toRemove.add(m);
                        } else if (debug) {
                            System.out.println("Latest activity " + m.getLatestActivity());
                        }
                    }
                    for (MicazMote m : toRemove) {
                        //We have to build the delete statement for the registry unit
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
                            if (debug) {
                                System.out.println("My uid at update is " + uid);
                            }
                            String jsonReply = HTTPRequest.sendPost("http://127.0.0.1", 8383, URLEncoder.encode("uid=" + uid + "&services=" + services), "/delete");
                            if (debug) {
                                System.out.println("reply is: " + jsonReply);
                            }
                            JSONObject obj;

                            obj = new JSONObject(jsonReply);
                        } catch (Exception ex) {
                            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        motesList.remove(m);
                        //sendDeleteRequestToRU
                    }
                    toRemove.clear();
                    if (debug) {
                        System.out.println("CurrentTime " + Util.getTime());
                    }
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

    public void reportPollAck(final MicazMote mote) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //won't really analyze these, it's pretty self explanatory
                    sensingCore.attachTo();

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
                                if (debug) {
                                    System.out.println("My uid at update is " + uid);
                                }
                                jsonReply = HTTPRequest.sendPost("http://127.0.0.1", 8383, URLEncoder.encode("uid=" + uid + "&services=" + services), "/update");
                                if (debug) {
                                    System.out.println("reply is: " + jsonReply);
                                }
                                JSONObject obj;

                                obj = new JSONObject(jsonReply);
                            } catch (Exception ex) {
                                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }

    public void reportReading(final int id, final int messageType, final int[] Readings) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sensingCore.attachTo();

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
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }

    public void reportSwitch(final int id, final int state) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sensingCore.attachTo();

                    for (MicazMote m : motesList) {
                        if (m.getId() == id) {
                            m.setLatestActivity(Util.getTime());
                            m.setSwitchState(state);
                            if (debug) {
                                System.out.println("State changed to " + state);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }

    private Thread constructPollDaemon() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    cronCore.attachTo();
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sensingCore.attachTo();
                    messages.sendPoll();
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();

    }

    public void getSwitchInfo(final int id) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sensingCore.attachTo();
                    messages.sendSwitchPoll(id);
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();

    }

    public void toggleSwitch(final int id) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sensingCore.attachTo();
                    messages.sendSwitchToggle(id);
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();

    }

    public void sendReadingRequest(final int id, final int type) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    sensingCore.attachTo();
                    messages.sendReadingRequest(id, type);
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();

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

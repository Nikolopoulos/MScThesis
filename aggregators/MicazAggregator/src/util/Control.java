/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oscilloscope.messaging;
import sensorPlatforms.MicazMote;

/**
 *
 * @author billaros
 */
public class Control {

    ArrayList<MicazMote> motesList;
    messaging Messages = new messaging(this);
    Thread dropDaemon,populate;
    String uid="";

    public Control() {
        //sendRegisterRequestToRU
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
                ArrayList<MicazMote> toRemove= new ArrayList<MicazMote>();
                System.out.println("Drop daemon started");
                while (true) {
                    for (MicazMote m : motesList) {

                        if (m.getLatestActivity() < util.getTime() - 10000) {
                            System.out.println("dropin " + m);
                            
                            toRemove.add(m);
                        } else {
                            //System.out.println("Latest activity " + m.getLatestActivity());

                        }
                    }
                    for(MicazMote m: toRemove){
                        motesList.remove(m);
                        //sendDeleteRequestToRU
                    }
                    toRemove.clear();
                    //System.out.println("CurrentTime " + util.getTime());
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
                m.setLatestActivity(util.getTime());
                found = true;
                break;
            }
        }
        if (!found) {
            motesList.add(mote);
            //send update request to RU
        }
    }

    public void reportReading(int id, int messageType, int[] Readings) {
        for (MicazMote m : motesList) {
            if (m.getId() == id) {
                switch (messageType) {
                    case lib.Constants.TEMP: {
                        m.setTempReading(util.median(Readings));
                        break;
                    }
                    case lib.Constants.PHOTO: {
                        m.setPhotoReading(util.median(Readings));
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
                m.setLatestActivity(util.getTime());
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
        Messages.sendPoll();
    }

    ;
    public void getSwitchInfo(int id) {
        Messages.sendSwitchPoll(id);
    }

    public void toggleSwitch(int id) {
        Messages.sendSwitchToggle(id);
    }

    public void sendReadingRequest(int id, int type) {
        Messages.sendReadingRequest(id, type);
    }

    public ArrayList<MicazMote> getMotesList() {
        return motesList;
    }

}

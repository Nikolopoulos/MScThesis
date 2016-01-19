/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sensorPlatforms;

/**
 *
 * @author billaros
 */
public class MicazMote {

    private int id;
    private int services;
    private boolean tempService = false;
    private boolean photoService = false;
    private boolean switchService = false;

    private enum voltage {

        LOW, HIGH
    };

    private double tempReading;
    private double photoReading;

    private long latestActivity;
    private int switchState;

    public MicazMote(int id, int services, long latestActivity) {
        this.id = id;
        this.services = services;
        if (services / lib.Constants.PIN_S > 0.9) {
            services -= 4;
            this.switchService = true;
            switchState = -1;
        }
        if (services / lib.Constants.PHOTO_S > 0.9) {
            services -= 2;
            this.photoService = true;
        }
        if (services % 2 == lib.Constants.TEMP_S) {
            services -= lib.Constants.TEMP_S;
            this.tempService = true;
        }
        this.photoReading = -1;
        this.tempReading = -1;
        this.switchState = -1;
        this.latestActivity = latestActivity;
    }

    public int getSwitchState() {
        return switchState;
    }

    public void setSwitchState(int switchState) {
        this.switchState = switchState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServices() {
        return services;
    }

    public void setServices(int services) {
        this.services = services;
    }

    public boolean isTempService() {
        return tempService;
    }

    public void setTempService(boolean tempService) {
        this.tempService = tempService;
    }

    public boolean isPhotoService() {
        return photoService;
    }

    public void setPhotoService(boolean photoService) {
        this.photoService = photoService;
    }

    public boolean isSwitchService() {
        return switchService;
    }

    public void setSwitchService(boolean switchService) {
        this.switchService = switchService;
    }

    public double getTempReading() {
        return tempReading;
    }

    public void setTempReading(double tempReading) {
        this.tempReading = tempReading;
    }

    public double getPhotoReading() {
        return photoReading;
    }

    public void setPhotoReading(double photoReading) {
        this.photoReading = photoReading;
    }

    public long getLatestActivity() {
        return latestActivity;
    }

    public void setLatestActivity(long latestActivity) {
        this.latestActivity = latestActivity;
    }

    @Override
    public String toString() {
        return id + "\t Micaz provides: " + (tempService ? "temperature service " : "") + (photoService ? "photo service " : "") + (switchService ? "switch service " : "");
    }

    public String JSONDescription() {
        //return "{"+id+"\t Micaz provides: " + (tempService?"temperature service ":"")+ (photoService?"photo service ":"") + (switchService?"switch service ":"") ;
        String reply = "{\"ID\":\"" + getId() + "\", \"TemperatureService\":\"" + this.isTempService() + "\", \"LightService\":\"" + this.isPhotoService() + "\", \"SwitchService\":\"" + this.isSwitchService() + "\"} ";
        return reply;
    }

    public String JSONObject() {
       String reply = "\"ID\":\"" + getId() + "\", \"TemperatureService\":\"" + this.isTempService() + "\", \"LightService\":\"" + this.isPhotoService() + "\", \"SwitchService\":\"" + this.isSwitchService() + "\", \"Temperature\":\"" + util.Util.a2d2celsius((int)this.getTempReading()) + "\", \"Light\":\"" + this.getPhotoReading() + "\", \"Switch\":\"" + this.getSwitchState() + "\" ";
        return reply;
    }
    
    public String JSONLight() {
        String reply = "\"ID\":\"" + getId() + "\", \"Light\":\"" + this.getPhotoReading() + "\" ";
        return reply;
    }
    public String JSONTemp() {
        String reply = "\"ID\":\"" + getId() + "\", \"Temperature\":\"" + util.Util.a2d2celsius((int)this.getTempReading())+ "\" ";
        return reply;
    }

}

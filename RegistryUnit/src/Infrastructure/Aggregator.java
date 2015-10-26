/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

import java.util.ArrayList;

/**
 *
 * @author billaros
 */
public class Aggregator {
    String IP;
    ArrayList<Service> services;
    int Port;
    String uid;
    
    
    
    /* 
       registryIP:8282/register -> uses post headers to register a list of services available at an aggregator. 
            The registry unit responds with a unique ID that the aggregator should use in following API calls as a 
            parameter.

        registryIP:8282/delete -> uses post headers to delete a specific service earlier registered at the 
            registry unit. Registry unit responds with OK or NOT_OK followed by an error code
        
        registryIP:8282/update -> uses post headers to update a specific service earlier registered at the registry 
            unit. Registry unit responds with OK or NOT_OK followed by an error code

        registryIP:8282/getServices -> returns a list of all services currently registered at the registry unit.
    
        registryIP:8282/getAggregators -> returns a list of all available aggregators.
        
        registryIP:8282/describe/serviceID ->  returns a description provided from the aggregator at register 
            time for service with id serviceID. It should be noted that an aggregator does not need to 
            know of the services' IDs as they are used only by the registry unit to denote  different services.
        
        registryIP:8282/describe/aggregatorID/path/service -> Same as before, but with different notation for ease 
        of use
*/

    public Aggregator(String IP, int Port) {
        this.IP = IP;
        this.Port = Port;
        this.services = new ArrayList<Service>();
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int Port) {
        this.Port = Port;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    
    
    
}

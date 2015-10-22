/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

import Util.Hasher;
import java.io.PrintStream;
import java.util.ArrayList;
import org.json.*;

/**
 *
 * @author billaros
 */
public class Tassadar {
    //EN TARO TASSADAR

    Request request;
    PrintStream out;
    Universe uni;
    ArrayList<Service> Services;

    public Tassadar(Request requestArg, PrintStream outArg, Universe uniArg) {
        this.request = requestArg;
        this.out = outArg;
        this.uni = uniArg;
    }

    public synchronized void execute() {
        if (request == null || out == null) {
            return;
        }
        Communication comm = new Communication();
        comm.setRequest(request);
        ArrayList<Service> services;
        if (request.getURI().equalsIgnoreCase("/register")) {
            comm.setRequestType(Util.Statics.REGISTER_REQUEST);
            String IP = request.client.split(":")[0];
            int Port = Integer.parseInt(request.client.split(":")[1]);
            boolean result = true;
            for (Aggregator agr : uni.aggregators) {
                if (agr.getIP().equals(IP) && agr.getPort() == Port) {
                    result = false;
                    break;
                }
            }
            if (!result) {
                comm.setResponseType(Util.Statics.NOT_PERMITTED_ERROR);
                errorRsespond(comm, "Already Registered");
            } else {
                Aggregator agr = new Aggregator(IP, Port);
                byte[] buf = Hasher.hash((System.currentTimeMillis() + "").toCharArray(), Hasher.getNextSalt());
                agr.setUid(Util.Tools.byteArrayToString(buf));

                String jsonServices = request.getParameters().get("services");
                JSONObject obj;
                try {
                    obj = new JSONObject(jsonServices);
                } catch (Exception e) {
                    comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                    errorRsespond(comm, "Services not correctly stated");
                    return;
                }
                try {
                    services = new ArrayList<Service>();
                    JSONArray arr = obj.getJSONArray("services");
                    for (int i = 0; i < arr.length(); i++) {
                        String serviceUri = arr.getJSONObject(i).getString("uri");
                        String serviceDescription = arr.getJSONObject(i).getString("description");
                        Service parsed = new Service(agr.getUid() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, agr);
                        services.add(parsed);
                    }

                } catch (Exception e) {
                    comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                    errorRsespond(comm, "Services not correctly formated");
                    return;
                }
                try {
                    for (Service itter : services) {
                        uni.services.put(itter.URI, itter);
                    }
                    uni.aggregators.add(agr);
                    this.registerRsespond(comm, agr);
                } catch (Exception e) {
                    comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                    errorRsespond(comm, "Services not correctly formated");
                    return;
                }

            }

        } else if (request.getURI().equalsIgnoreCase("/delete")) {
            JSONObject obj;
            String uid = "";
            try {
                uid = request.getParameters().get("uid");
                String jsonServices = request.getParameters().get("services");
                obj = new JSONObject(jsonServices);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                errorRsespond(comm, "Malformed Parameters");
                return;
            }
            try {
                JSONArray arr = obj.getJSONArray("services");
                for (int i = 0; i < arr.length(); i++) {
                    String serviceUri = arr.getJSONObject(i).getString("uri");
                    String transformedURI = uid + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri;

                    for (Aggregator agr : uni.aggregators) {
                        if (agr.getUid().equals("uid")) {
                            for (Service srv : agr.services) {
                                if (srv.URI.equals(transformedURI)) {
                                    agr.services.remove(srv);
                                }
                            }
                            break;
                        }
                    }

                    if (uni.services.containsKey(transformedURI)) {
                        uni.services.remove(transformedURI);
                    }
                }

            } catch (Exception e) {
                comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                errorRsespond(comm, "Services not correctly formated");
                return;
            }
        } else if (request.getURI().equalsIgnoreCase("/update")) {
        } else if (request.getURI().equalsIgnoreCase("/getServices")) {
        } else if (request.getURI().equalsIgnoreCase("/getAggregators")) {
        } else if (request.getURI().startsWith("/describe/")) {
        } else {
        }
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
         return;
         */

    }

    private void errorRsespond(Communication comm, String message) {
        String response = "{\"result\" = \"fail\", \"reason\" = \"" + comm.getResponseType() + "\", \"message\" = \"" + message + "\"}";
        comm.setAnswer(response);
        uni.comms.add(comm);
        out.println(comm.getAnswer());
        out.flush();
        out.close();
    }

    private void registerRsespond(Communication comm, Aggregator ag) {
        String response = "{\"result\" = \"success\", \"reason\" = \"" + comm.getResponseType() + "\", \"uid\" = \"" + ag.getUid() + "\"}";
        comm.setAnswer(response);
        uni.comms.add(comm);
        out.println(comm.getAnswer());
        out.flush();
        out.close();
    }
}

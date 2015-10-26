/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

import Util.Hasher;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.*;

/**
 *
 * @author billaros
 */
public class Tassadar {

    //EN TARO TASSADAR
    private static final String OUTPUT_P1 = "<html><head><title>Response</title></head><body>";
    private static final String OUTPUT_P2 = "</body></html>";
    private static final String OUTPUT_HEADERS_P1 = "HTTP/1.1 ";
    private static final String OUTPUT_HEADERS_P2 = "\n"
            + "Content-Type: text/html\n";
    private static final String OUTPUT_END_OF_HEADERS = "\n\n";
    Request request;
    PrintStream out;
    Universe uni;
    ArrayList<Service> Services;
    Socket sock;

    public Tassadar(Request requestArg, PrintStream outArg, Universe uniArg, Socket server) {
        this.request = requestArg;
        this.out = outArg;
        this.uni = uniArg;
        sock = server;
    }

    public synchronized void execute() {
        if (request == null || out == null) {
            return;
        }
        Communication comm = new Communication();
        comm.setRequest(request);
        ArrayList<Service> services = new ArrayList<Service>();
        if (request.getURI().equalsIgnoreCase("/register")) {
            comm.setRequestType(Util.Statics.REGISTER_REQUEST);
            System.out.println("request.client: " + request.client);
            String IP = request.Parameters.get("ip");
            int Port = Integer.parseInt(request.getParameters().get("port"));
            boolean result = true;
            for (Aggregator agr : uni.aggregators) {
                if (agr.getIP().equals(IP) && agr.getPort() == Port) {
                    result = false;
                    break;
                }
            }
            System.out.println("test1");
            if (!result) {
                comm.setResponseType(Util.Statics.NOT_PERMITTED_ERROR);
                errorRespond(comm, "Already Registered");
                System.out.println("test2");
            } else {
                Aggregator agr = new Aggregator(IP, Port);
                //byte[] buf = Hasher.hash((System.currentTimeMillis() + "").toCharArray(), Hasher.getNextSalt());
                String pass = Hasher.generateRandomPassword(10);
                agr.setUid(pass);

                String jsonServices = request.getParameters().get("services");
                JSONObject obj;
                System.out.println("test3");
                try {
                    obj = new JSONObject(jsonServices);
                } catch (Exception e) {
                    e.printStackTrace();
                    comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                    System.out.println("test4");
                    errorRespond(comm, "Services not correctly stated");
                    return;
                }
                System.out.println("test4.2");
                try {
                    services = new ArrayList<Service>();
                    JSONArray arr = obj.getJSONArray("services");
                    for (int i = 0; i < arr.length(); i++) {
                        String serviceUri = arr.getJSONObject(i).getString("uri");
                        String serviceDescription = arr.getJSONObject(i).getString("description");
                        Service parsed = new Service(agr.getUid() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, "http://" + agr.IP + ":" + agr.getPort() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, agr);
                        services.add(parsed);
                        System.out.println("service:" + parsed.Description);
                        //hackathon code begin
                        if (agr.Port == 8086) {
                            serviceUri = "/sensor/160";
                            serviceDescription = "Returns all sensor data for sensor with id 160";
                            parsed = new Service(agr.getUid() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, "http://" + agr.IP + ":" + agr.getPort() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, agr);
                            services.add(parsed);

                            serviceUri = "/sensor/160/temp";
                            serviceDescription = "Returns temperature data for sensor with id 160";
                            parsed = new Service(agr.getUid() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, "http://" + agr.IP + ":" + agr.getPort() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, agr);
                            services.add(parsed);

                            serviceUri = "/sensor/160/humi";
                            serviceDescription = "Returns humidity data for sensor with id 160";
                            parsed = new Service(agr.getUid() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, "http://" + agr.IP + ":" + agr.getPort() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, agr);
                            services.add(parsed);

                            serviceUri = "/sensor/160/pres";
                            serviceDescription = "Returns atmospheric pressure data for sensor with id 160";
                            parsed = new Service(agr.getUid() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, "http://" + agr.IP + ":" + agr.getPort() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, agr);
                            services.add(parsed);

                        }

                    }

                    agr.services = services;

                } catch (Exception e) {
                    comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                    System.out.println("test5");
                    errorRespond(comm, "Services not correctly formated");
                    return;
                }
                try {
                    System.out.println("test6");
                    for (Service itter : services) {
                        uni.services.put(itter.URI, itter);
                    }
                    uni.aggregators.add(agr);
                    this.registerRespond(comm, agr);
                } catch (Exception e) {
                    System.out.println("test7");
                    comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                    errorRespond(comm, "Services not correctly formated");
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
                errorRespond(comm, "Malformed Parameters");
                return;
            }
            try {
                JSONArray arr = obj.getJSONArray("services");
                ArrayList<String> StringServices = new ArrayList<String>();
                for (int i = 0; i < arr.length(); i++) {
                    String serviceUri = arr.getJSONObject(i).getString("uri");
                    String transformedURI = uid + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri;
                    StringServices.add(transformedURI);
                }
                Aggregator agg = null;
                for (Aggregator agr : uni.aggregators) {
                    if (agr.getUid().equals(request.getParameters().get("uid"))) {
                        agg = agr;
                        break;
                    }
                }
                if (agg == null) {
                    throw new NullPointerException();
                }
                ArrayList<Service> toRemove = new ArrayList<Service>();
                for (String serv : StringServices) {
                    for (Service srv : agg.services) {
                        if (srv.URI.equals(serv)) {
                            toRemove.add(srv);

                        }
                    }

                    if (uni.services.containsKey(serv)) {
                        uni.services.remove(serv);
                    }
                }

                for (Service serv : toRemove) {
                    agg.services.remove(serv);

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
                comm.setResponseType(Util.Statics.NOT_PERMITTED_ERROR);
                errorRespond(comm, "You don't have permission to delete some or all of the services. Check UID");
                return;
            } catch (Exception e) {
                e.printStackTrace();
                comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                errorRespond(comm, "Services not correctly formated");
                return;
            }
            try {
                comm.setResponseType(Util.Statics.OK_RESPONSE);
                this.genericRespond(comm);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                errorRespond(comm, "Internal Server Error");
                return;
            }
        } else if (request.getURI().equalsIgnoreCase("/update")) {
            JSONObject obj;
            String uid = "";
            try {
                uid = request.getParameters().get("uid");
                String jsonServices = request.getParameters().get("services");
                obj = new JSONObject(jsonServices);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                errorRespond(comm, "Malformed Parameters or wrong UID");
                return;
            }
            try {
                JSONArray arr = obj.getJSONArray("services");
                Aggregator agg = null;
                int aggArrId = -1;
                System.out.println("test1 of update");

                for (int aggCount = 0; aggCount < uni.aggregators.size(); aggCount++) {
                    System.out.println("test2 of update");
                    if (uni.aggregators.get(aggCount).getUid().equals(request.getParameters().get("uid"))) {
                        aggArrId = aggCount;
                        break;
                    } else {
                        System.out.println("uid needed is " + uni.aggregators.get(aggCount).getUid() + " and i got " + request.getParameters().get("uid"));
                    }
                }
                System.out.println("test3 of update");
                if (aggArrId == -1) {
                    System.out.println("uid i got " + request.getParameters().get("uid"));
                    throw new NullPointerException();
                }
                System.out.println("test4 of update");
                for (int i = 0; i < arr.length(); i++) {
                    String serviceUri = arr.getJSONObject(i).getString("uri");
                    String serviceDescription = arr.getJSONObject(i).getString("description");
                    Service parsed = new Service(uid + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, "http://" + uni.aggregators.get(aggArrId).IP + ":" + uni.aggregators.get(aggArrId).getPort() + (serviceUri.startsWith("/") ? ("") : ("/")) + serviceUri, serviceDescription, uni.aggregators.get(aggArrId));
                    services.add(parsed);
                }
                System.out.println("test5 of update");
                try {
                    for (Service srv : services) {
                        for (Service asrv : uni.aggregators.get(aggArrId).services) {
                            if (asrv.URI.equals(srv.URI)) {
                                uni.aggregators.get(aggArrId).services.remove(asrv);
                            }
                        }
                        uni.aggregators.get(aggArrId).services.add(srv);
                        if (uni.services.containsKey(srv.URI)) {
                            uni.services.remove(srv.getURI());
                            uni.services.put(srv.getURI(), srv);
                        }
                    }
                } catch (Exception es) {
                    es.printStackTrace();
                }
                System.out.println("test6 of update");
            } catch (NullPointerException e) {
                comm.setResponseType(Util.Statics.NOT_PERMITTED_ERROR);
                errorRespond(comm, "You don't have permission to update some or all of the services. Check UID");
                return;
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.BAD_REQUEST_ERROR);
                errorRespond(comm, "Services not correctly formated");
                return;
            }

            try {
                comm.setResponseType(Util.Statics.OK_RESPONSE);
                this.genericRespond(comm);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                errorRespond(comm, "Internal Server Error");
                return;
            }
        } else if (request.getURI().equalsIgnoreCase("/getServices")) {
            System.out.println("test 1 of get services");
            try {
                String servicesJSON = "[";
                for (Aggregator agg : uni.aggregators) {
                    for (Service srv : agg.services) {
                        servicesJSON += "{";
                        servicesJSON += "\"url\" : \"" + srv.canonURI + "\" , \"sid\" : \"" + srv.getId() + "\"";
                        servicesJSON += "},";
                    }
                }
                System.out.println("test 2 of get services");
                if (servicesJSON.length() > 5) {
                    servicesJSON = servicesJSON.substring(0, servicesJSON.length() - 1);
                }
                servicesJSON += "]";
                comm.setResponseType(Util.Statics.OK_RESPONSE);
                System.out.println("test 3 of get services");
                servicesGetRespond(comm, servicesJSON);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                errorRespond(comm, "Internal Server Error");
            }

        } else if (request.getURI().equalsIgnoreCase("/getAggregators")) {
            try {
                String aggregatorsJSON = "[";
                for (Aggregator agg : uni.aggregators) {

                    aggregatorsJSON += "{";
                    aggregatorsJSON += "\"url\" = \"" + agg.getIP() + ":" + agg.Port + "\"";
                    aggregatorsJSON += "},";

                }
                if (aggregatorsJSON.length() > 5) {
                    aggregatorsJSON = aggregatorsJSON.substring(0, aggregatorsJSON.length() - 1);
                }
                aggregatorsJSON += "]";
                comm.setResponseType(Util.Statics.OK_RESPONSE);
                aggregatorsGetRespond(comm, aggregatorsJSON);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                errorRespond(comm, "Internal Server Error");
            }
        } else if (request.getURI().equalsIgnoreCase("/describe")) {
            try {
                String ID = request.getParameters().get("sid");
                String describeJSON = "";
                boolean found = false;
                for (Aggregator agg : uni.aggregators) {
                    for (Service srv : agg.services) {
                        if (srv.getId().equals(ID)) {
                            describeJSON = srv.Description;
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }

                if (describeJSON.equals("")) {
                    comm.setResponseType(Util.Statics.GONE_ERROR);
                    errorRespond(comm, "Service id not found");
                }
                comm.setResponseType(Util.Statics.OK_RESPONSE);
                describeRespond(comm, describeJSON);
            } catch (Exception e) {
                comm.setResponseType(Util.Statics.INTERNAL_SERVER_ERROR);
                errorRespond(comm, "Internal Server Error");
            }
        } else if (request.getURI().equalsIgnoreCase("/whoIsAGoodTeapot")) {
            comm.setResponseType(Util.Statics.IM_A_TEAPOT_ERROR);
            errorRespond(comm, "I'M A GOOD TEAPOT :D");
        } else {
            comm.setResponseType(Util.Statics.SERVICE_UNAVAILABLE_ERROR);
            errorRespond(comm, "Service not found or service unavailable");
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

    private void errorRespond(Communication comm, String message) {
        String response = "{\"result\" : \"fail\", \"reason\" : \"" + comm.getResponseType() + "\", \"message\" : \"" + message + "\"}";
        comm.setAnswer(response);
        uni.comms.add(comm);
        out.println(comm.getAnswer());
        out.flush();
        out.close();
    }

    private void registerRespondDump(Communication comm, Aggregator ag) {
        String response = "{\"result\" = \"success\", \"reason\" = \"" + comm.getResponseType() + "\", \"uid\" = \"" + ag.getUid() + "\"}";
        comm.setAnswer(response);
        uni.comms.add(comm);
        out.println(comm.getAnswer());
        out.flush();
        out.close();

    }

    private void genericRespond(Communication comm) {
        String response = "{\"result\" : \"success\", \"reason\" : \"" + comm.getResponseType() + "\"}";
        comm.setAnswer(response);
        uni.comms.add(comm);
        out.println(comm.getAnswer());
        out.flush();
        out.close();
    }

    private void servicesGetRespond(Communication comm, String services) {
        try {
            System.out.println("test8");
            String response = "{\"result\" : \"success\", \"reason\" : \"" + comm.getResponseType() + "\", \"services\" : " + services + "}";
            uni.comms.add(comm);
            out.println(response);
            comm.setAnswer(formatResponse(" 200 OK ", response));
            System.out.println(comm.getAnswer());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("test9");
        }
    }

    private void aggregatorsGetRespond(Communication comm, String aggregators) {
        try {
            String response = "{\"result\" : \"success\", \"reason\" : \"" + comm.getResponseType() + "\", \"aggregators\" : \"" + aggregators + "\"}";
            comm.setAnswer(response);
            uni.comms.add(comm);
            out.println(comm.getAnswer());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void describeRespond(Communication comm, String description) {
        String response = "{\"result\" : \"success\", \"reason\" : \"" + comm.getResponseType() + "\",\"sid\" : \"" + comm.getRequest().getParameters().get("sid") + "\", \"description\" = \"" + description + "\"}";
        comm.setAnswer(response);
        uni.comms.add(comm);
        out.println(comm.getAnswer());
        out.flush();
        out.close();
    }

    private void registerRespond(Communication comm, Aggregator ag) {
        try {
            System.out.println("test8");
            String jsonR = "{\"result\" : \"success\", \"reason\" : \"" + comm.getResponseType() + "\", \"uid\" : \"" + ag.getUid() + "\"}\n";
            System.out.println("out.checkError() ");
            System.out.println("Let's test the socket: \n"
                    + "closed? " + sock.isClosed() + "\n"
                    + "bound? " + sock.isBound() + "\n"
                    + "connected? " + sock.isConnected() + "\n"
                    + "inputshutdown? " + sock.isInputShutdown() + "\n"
                    + "outputshutdown? " + sock.isOutputShutdown() + "\n"
                    + "bound? " + sock.isBound() + "\n************");
            sock.getOutputStream().flush();
            System.out.println("Let's test the socket: \n"
                    + "closed? " + sock.isClosed() + "\n"
                    + "bound? " + sock.isBound() + "\n"
                    + "connected? " + sock.isConnected() + "\n"
                    + "inputshutdown? " + sock.isInputShutdown() + "\n"
                    + "outputshutdown? " + sock.isOutputShutdown() + "\n"
                    + "bound? " + sock.isBound() + "\n************");
            sock.getOutputStream().write(jsonR.getBytes("UTF-8"));//.print(jsonR);
            System.out.println("Let's test the socket: \n"
                    + "closed? " + sock.isClosed() + "\n"
                    + "bound? " + sock.isBound() + "\n"
                    + "connected? " + sock.isConnected() + "\n"
                    + "inputshutdown? " + sock.isInputShutdown() + "\n"
                    + "outputshutdown? " + sock.isOutputShutdown() + "\n" + "\n************");
            out.println("\0");
            String response = jsonR;
            uni.comms.add(comm);
            comm.setAnswer(formatResponse(" 200 OK ", response));
            System.out.println("flushed");
            System.out.println("appended");

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("test9");
        }
    }

    private String formatResponse(String status, String response) {
        return OUTPUT_HEADERS_P1 + status + OUTPUT_HEADERS_P2 + OUTPUT_END_OF_HEADERS + OUTPUT_P1 + response + OUTPUT_P2;
    }
}

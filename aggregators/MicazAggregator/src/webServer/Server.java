/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Control;

/**
 *
 * @author billaros
 */
public class Server {

    private static int port = 8181,
                       maxConnections = 0;
    // Listen for incoming connections and handle them
    final Control finalControl;

    public Server(Control c) {
        
        this.finalControl = c;

    }

    public void startServer() {
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        finalControl.HTTPCore.attachTo();
                         System.out.println("Server attached!");
                    } catch (Exception ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    int i = 0;
                    ServerSocket listener = new ServerSocket(port);
                    Socket server;
                    while ((i++ < maxConnections) || (maxConnections == 0)) {
                        server = listener.accept();
                         System.out.println("Server Accepted!!");
                        DoComms conn_c = new DoComms(server, finalControl);
                        Thread clientConnectionThread = new Thread(conn_c);
                        clientConnectionThread.setName("clientConnectionThread"+i);
                        clientConnectionThread.start();
                    }

                } catch (IOException ioe) {
                    System.out.println("IOException on socket listen: " + ioe);
                    ioe.printStackTrace();
                }
            }
        });
        serverThread.start();
        System.out.println("Server started!");
    }

}

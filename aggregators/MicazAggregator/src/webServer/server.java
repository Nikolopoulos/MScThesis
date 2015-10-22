/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import util.Control;

/**
 *
 * @author billaros
 */
public class server {

    private static int port = 8181,
                       maxConnections = 0;
    // Listen for incoming connections and handle them
    final Control finalControl;

    public server(Control c) {
        
        this.finalControl = c;

    }

    public void startServer() {
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int i = 0;
                    ServerSocket listener = new ServerSocket(port);
                    Socket server;
                    while ((i++ < maxConnections) || (maxConnections == 0)) {
                        server = listener.accept();
                        doComms conn_c = new doComms(server, finalControl);
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
    }

}

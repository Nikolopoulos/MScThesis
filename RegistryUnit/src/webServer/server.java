/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServer;

import Infrastructure.Universe;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author billaros
 */
public class server {

    private static int port = 8383,
            maxConnections = 0;
    public Universe universe;
    // Listen for incoming connections and handle them

    public server(Universe uniarg) {
        this.universe = uniarg;

    }

    public void startServer() {
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public synchronized void run() {
                try {
                    int i = 0;
                    ServerSocket listener = new ServerSocket(port);
                    Socket server;
                    while ((i++ < maxConnections) || (maxConnections == 0)) {
                        server = listener.accept();
                        //server.getOutputStream().write('Q');
                        doComms conn_c = new doComms(server,universe);
                        Thread clientConnectionThread = new Thread(conn_c);
                        clientConnectionThread.setName("clientConnectionThread" + i);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oscilloscope;

import util.Control;
import webServer.Server;

/**
 *
 * @author billaros
 */
public class Conductor {
      public static void main(String args[]){
        Control c = new Control();
        //mainFrame f = new mainFrame(c);
        //f.setVisible(true);
        Server myServer = new Server(c);
        myServer.startServer();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oscilloscope;

import affinitySupport.ThreadAffinity;
import gui.mainFrame;
import util.Control;
import webServer.Server;

/**
 *
 * @author billaros
 */
public class Conductor {
    
    
    
    //Note to people reading this shit
    //you don't want to read this shit
    //Sincerely, Basil Nikolopoulos
    
    public static void main(String args[]) {
        
        Control c = new Control(true);
        //So control holds most of the functionality provided by the aggregator
        
        if (args.length>1 && args[1].equals("debug")) {
            //if ran from the cmd you may want to enter debug which opens up a 
            //jframe that I haven't used in like 14 months, so I can't be
            //sure about its quality. Don't use it or something
            
            mainFrame f = new mainFrame(c);
            f.setVisible(true);
        }
        
        //Sets the HTTPServer and starts it
        Server myServer = new Server(c);
        myServer.startServer();
    }
}

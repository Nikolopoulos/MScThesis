/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webServer;

import Infrastructure.Request;
import Infrastructure.Tassadar;
import Infrastructure.Universe;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author billaros
 */
class doComms implements Runnable {

    private Socket server;
    private String line, input, requestedURL, noBreakInput;
    public Universe universe;

    public doComms(Socket server,Universe uniArg) {
        this.server = server;
        universe = uniArg;
    }

    @Override
    public synchronized void run() {

        input = "";
        noBreakInput = "";
        requestedURL = "";
        int emptyLines=0;
        try {
            System.out.println("test -2 doComms");
            // Get input from the client
            InputStream ins = server.getInputStream();
            //DataInputStream in = new DataInputStream(ins);
            PrintStream out = new PrintStream(server.getOutputStream());
            
            System.out.println("sending test message");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(doComms.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.flush();
            //out.print("testmessage");
            System.out.println("sent");
            int lineNumber = 1;
            System.out.println("test -1 doComms");
            while ((line = readLine(ins)) != null) {
                if(line.length()>0)
                {
                    emptyLines=0;
                }
                else{
                    emptyLines++;
                }
                if(emptyLines>2)
                {
                    break;
                }
                System.out.println("test 0 doComms");
                if (lineNumber == 1) {
                    int urlEnd = line.indexOf(" HTTP/1.1");
                    requestedURL = line.substring(4, urlEnd);
                }
                input = input + line + "\n";
                noBreakInput = noBreakInput + line;
                lineNumber++;

                System.out.println(line);
                if (line.startsWith("Content-Length: ")) {
                    line = "Params: "+readChars(ins, Integer.parseInt(line.substring("Content-Length: ".length())));
                    input = input + line + "\n";
                    noBreakInput = noBreakInput + line;
                    lineNumber++;
                    System.out.println(line);
                    break;
                }

            }
            Request request = new Request();
            System.out.println("test 1 doComms");
            request.parseRequest(input);
            System.out.println("test 2 doComms");
            request.setClient(server.getInetAddress().toString()+":"+server.getPort());
            System.out.println("test 3 doComms");
            Tassadar executor = new Tassadar(request,out,universe,server);
            System.out.println("test 4 doComms");
            executor.execute();
            //server.close();
        } catch (IOException ioe) {
            System.out.println("IOException on socket listen: " + ioe);
            ioe.printStackTrace();
        }
    }

    private String readLine(InputStream is) throws IOException {
        String input = "";
        int i;
        i = is.read();
        while (i != '\n' && i != '\r') {
            input += (char) i;
            //System.out.println((char) i);
            i = is.read();
        }

        return input;
    }

    private String readChars(InputStream is, int len) throws IOException {
        String input = "";
        int c;
        System.out.println("len i got is "+len);
        for (int i = 0; i < len; i++) {
            c = is.read();
            while(Character.isWhitespace(c)){
                c = is.read();
            }
            
            input += (char) c;
            //System.out.println(input);
        }

        return input;
    }
}

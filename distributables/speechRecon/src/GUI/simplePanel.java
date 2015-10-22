/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import chatterbotapi.ChatterBot;
import chatterbotapi.ChatterBotFactory;
import chatterbotapi.ChatterBotSession;
import static chatterbotapi.ChatterBotType.CLEVERBOT;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Files;
import javaFlacEncoder.FLACFileWriter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import microphone.Microphone;
import org.json.JSONArray;
import org.json.JSONObject;
import recognizer.GSpeechDuplex;
import recognizer.GSpeechResponseListener;
import recognizer.GoogleResponse;

/**
 *
 * @author billaros
 */
public class simplePanel extends javax.swing.JFrame {

    /**
     * Creates new form simplePanel
     */
    public simplePanel self;

    public simplePanel() {
        self = this;
        final Microphone mic = new Microphone(FLACFileWriter.FLAC);//Instantiate microphone and have 

        final GSpeechDuplex dup = new GSpeechDuplex("AIzaSyBc-PCGLbT2M_ZBLUPEl9w2OY7jXl90Hbc");//Instantiate the API
        dup.addResponseListener(new GSpeechResponseListener() {// Adds the listener
            public void onResponse(GoogleResponse gr) {
                System.out.println("got response");
                jTextArea1.setText(gr.getResponse() + "\n" + jTextArea1.getText());

                getjLabel1().setText("Awaiting Command");
                if (gr.getResponse().contains("temperature")) {
                    try {
                        String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                        JSONObject obj;

                        obj = new JSONObject(reply);
                        JSONArray services = obj.getJSONArray("services");
                        boolean found = false;
                        for (int i = 0; i < services.length(); i++) {
                            if (found) {
                                return;
                            }
                            Object pref = services.getJSONObject(i).get("url");
                            String url = (String) pref;
                            if (url.contains("temp")) {
                                // http://127.0.0.1:8181/sensor/1/temp
                                String serviceHost = (url.split(":")[1].substring(2));
                                int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                                String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                                String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                                JSONObject temperature;

                                obj = new JSONObject(serviceReply);
                                String temp = obj.getJSONObject("sensor").getString("Temperature");
                                JOptionPane.showMessageDialog(self, "Temperature is " + temp.substring(0, temp.indexOf(".") + 2) + " Celsius");
                                found = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (gr.getResponse().contains("light") || gr.getResponse().startsWith("li")) {
                    try {
                        String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                        JSONObject obj;

                        obj = new JSONObject(reply);
                        JSONArray services = obj.getJSONArray("services");
                        boolean found = false;
                        for (int i = 0; i < services.length(); i++) {
                            if (found) {
                                return;
                            }
                            Object pref = services.getJSONObject(i).get("url");
                            String url = (String) pref;
                            if (url.contains("light")) {
                                // http://127.0.0.1:8181/sensor/1/temp
                                String serviceHost = (url.split(":")[1].substring(2));
                                int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                                String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                                String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                                JSONObject temperature;

                                obj = new JSONObject(serviceReply);
                                String temp = obj.getJSONObject("sensor").getString("Light");
                                JOptionPane.showMessageDialog(self, "Light levels are at " + temp + " of 1023 ");
                                found = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if ((gr.getResponse().contains("turn on") || gr.getResponse().contains("turn off")) && gr.getResponse().contains("number")) {
                    int numberIndex = gr.getResponse().indexOf("number ") + "number ".length();
                    String number = gr.getResponse().substring(numberIndex).split(" ")[0];
                    if (number.equals("for") || number.equals("four")) {
                        number = "4";
                    }
                    if (number.equals("to") || number.equals("two") || number.equals("cho")) {
                        number = "2";
                    }
                    if (number.equals("one")) {
                        number = "1";
                    }
                    if (number.equals("three")) {
                        number = "3";
                    }

                    try {
                        String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                        JSONObject obj;

                        obj = new JSONObject(reply);
                        JSONArray services = obj.getJSONArray("services");
                        boolean found = false;
                        for (int i = 0; i < services.length(); i++) {
                            if (found) {
                                return;
                            }
                            Object pref = services.getJSONObject(i).get("url");
                            String url = (String) pref;
                            if (url.contains("sensor/" + number)) {
                                // http://127.0.0.1:8181/sensor/1/temp
                                String serviceHost = (url.split(":")[1].substring(2));
                                int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                                String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                                String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                                JSONObject temperature;

                                obj = new JSONObject(serviceReply);
                                String temp = obj.getJSONObject("sensor").getString("Switch");
                                if (!(temp.equals("0") || temp.equals("1"))) {
                                    JOptionPane.showMessageDialog(self, "Sensor does not provide a switch service man");
                                } else if (gr.getResponse().contains("turn on") && temp.equals("1")) {
                                    JOptionPane.showMessageDialog(self, "Switch is already on at sensor " + number + "!");
                                } else if (gr.getResponse().contains("turn off") && temp.equals("0")) {
                                    JOptionPane.showMessageDialog(self, "Switch is already off at sensor " + number + "!");
                                } else if (gr.getResponse().contains("turn on") && temp.equals("0")) {
                                    String serviceReply2 = util.httpRequest.sendPost(serviceHost, Port, "", "/sensor/" + number + "/switch");
                                    JOptionPane.showMessageDialog(self, "Request for switch sent");
                                } else if (gr.getResponse().contains("turn off") && temp.equals("1")) {
                                    String serviceReply2 = util.httpRequest.sendPost(serviceHost, Port, "", "/sensor/" + number + "/switch");
                                    JOptionPane.showMessageDialog(self, "Request for switch sent");
                                }

                                found = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (gr.getResponse().contains("change") && gr.getResponse().contains("number")) {

                    int numberIndex = gr.getResponse().indexOf("number ") + "number ".length();
                    String number = gr.getResponse().substring(numberIndex).split(" ")[0];
                    if (number.equals("for") || number.equals("four")) {
                        number = "4";
                    }
                    if (number.equals("to") || number.equals("two") || number.equals("cho")) {
                        number = "2";
                    }
                    if (number.equals("one")) {
                        number = "1";
                    }
                    if (number.equals("three")) {
                        number = "3";
                    }

                    try {
                        String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                        JSONObject obj;

                        obj = new JSONObject(reply);
                        JSONArray services = obj.getJSONArray("services");
                        boolean found = false;
                        for (int i = 0; i < services.length(); i++) {
                            if (found) {
                                return;
                            }
                            Object pref = services.getJSONObject(i).get("url");
                            String url = (String) pref;
                            if (url.contains("sensor/" + number)) {
                                // http://127.0.0.1:8181/sensor/1/temp
                                String serviceHost = (url.split(":")[1].substring(2));
                                int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                                String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                                String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                                JSONObject temperature;

                                obj = new JSONObject(serviceReply);
                                String temp = obj.getJSONObject("sensor").getString("Switch");
                                if (!(temp.equals("0") || temp.equals("1"))) {
                                    JOptionPane.showMessageDialog(self, "Sensor does not provide a switch service man");
                                } else {
                                    String serviceReply2 = util.httpRequest.sendPost(serviceHost, Port, "", "/sensor/" + number + "/switch");
                                    JOptionPane.showMessageDialog(self, "Request for switch sent");
                                }

                                found = true;
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(self, e.getLocalizedMessage());
                    }
                } else if (gr.getResponse().contains("get all")) {

                    try {
                        String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                        JSONObject obj;

                        obj = new JSONObject(reply);
                        JSONArray services = obj.getJSONArray("services");
                        boolean found = false;
                        String servicesString = "";
                        for (int i = 0; i < services.length(); i++) {
                            Object pref = services.getJSONObject(i).get("url");
                            String url = (String) pref;
                            servicesString += url + "\n";

                        }
                        JOptionPane.showMessageDialog(self, servicesString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        ChatterBotFactory factory = new ChatterBotFactory();
                        ChatterBot bot1 = factory.create(CLEVERBOT);
                        ChatterBotSession bot1session = bot1.createSession();
                        String s = gr.getResponse();
                        String response = bot1session.think(s);
                        JOptionPane.showMessageDialog(self, response);
                    } catch (Exception e) {
                    }
                }
                System.out.println("Google thinks you said: " + gr.getResponse());
                System.out.println("with "
                        + ((gr.getConfidence() != null) ? (Double.parseDouble(gr.getConfidence()) * 100) : null)
                        + "% confidence.");
                System.out.println("Google also thinks that you might have said:"
                        + gr.getOtherPossibleResponses());
            }
        });
        initComponents();
        jTextField1.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String input = jTextField1.getText();
                        jTextField1.setText("");
                        textParser(input);
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        jButton1.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // it record FLAC file.
                File file = new File("CRAudioTest.flac");//The File to record the buffer to. 
                //You can also create your own buffer using the getTargetDataLine() method.
                System.out.println("Start Talking Honey");
                try {
                    mic.captureAudioToFile(file);//Begins recording
                } catch (Exception ex) {
                    ex.printStackTrace();//Prints an error if something goes wrong.
                }
                //System.out.println("You can stop now");
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    mic.close();//Stops recording
                    //Sends 10 second voice recording to Google
                    byte[] data = Files.readAllBytes(mic.getAudioFile().toPath());//Saves data into memory.
                    dup.recognize(data, (int) mic.getAudioFormat().getSampleRate(), self);
                    //mic.getAudioFile().delete();//Deletes Buffer file
                    //REPEAT
                } catch (Exception ex) {
                    ex.printStackTrace();//Prints an error if something goes wrong.
                }
                System.out.println("You can stop now");

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        setVisible(true);

    }

    public void textParser(String input) {
        boolean served =false;
        jTextArea1.setText(input + "\n" + jTextArea1.getText());

        getjLabel1().setText("Awaiting Command");
        if (input.contains("temperature")) {
            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;

                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                for (int i = 0; i < services.length(); i++) {
                    if (found) {
                        served=true;
                        return;
                    }
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    if (url.contains("temp")) {
                        // http://127.0.0.1:8181/sensor/1/temp
                        String serviceHost = (url.split(":")[1].substring(2));
                        int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                        String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                        String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                        JSONObject temperature;

                        obj = new JSONObject(serviceReply);
                        String temp = obj.getJSONObject("sensor").getString("Temperature");
                        if(temp.length()>4){
                            JOptionPane.showMessageDialog(self, "Temperature is " + temp.substring(0, temp.indexOf(".") + 2) + " Celsius");
                        }
                        else{
                            JOptionPane.showMessageDialog(self, "Temperature is " + temp + " Celsius");
                        }
                        
                        found = true;
                    }
                }
                JOptionPane.showMessageDialog(self, "I can't know! There are no sensors for that!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } if (input.contains("light") || input.startsWith("li")) {
            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;

                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                for (int i = 0; i < services.length(); i++) {
                    if (found) {
                        served=true;
                        return;
                    }
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    if (url.contains("light")) {
                        // http://127.0.0.1:8181/sensor/1/temp
                        String serviceHost = (url.split(":")[1].substring(2));
                        int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                        String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                        String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                        JSONObject temperature;

                        obj = new JSONObject(serviceReply);
                        String temp = obj.getJSONObject("sensor").getString("Light");
                        JOptionPane.showMessageDialog(self, "Light levels are at " + temp + " of 1023 ");
                        found = true;
                    }
                }
                        JOptionPane.showMessageDialog(self, "I can't know! There are no sensors for that!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } if ((input.contains("turn on") || input.contains("turn off")) && input.contains("number")) {
            int numberIndex = input.indexOf("number ") + "number ".length();
            String number = input.substring(numberIndex).split(" ")[0];
            if (number.equals("for") || number.equals("four")) {
                number = "4";
            }
            if (number.equals("to") || number.equals("two") || number.equals("cho")) {
                number = "2";
            }
            if (number.equals("one")) {
                number = "1";
            }
            if (number.equals("three")) {
                number = "3";
            }

            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;

                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                for (int i = 0; i < services.length(); i++) {
                    if (found) {
                        served=true;
                        return;
                    }
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    if (url.contains("sensor/" + number)) {
                        // http://127.0.0.1:8181/sensor/1/temp
                        String serviceHost = (url.split(":")[1].substring(2));
                        int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                        String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                        String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                        JSONObject temperature;

                        obj = new JSONObject(serviceReply);
                        String temp = obj.getJSONObject("sensor").getString("Switch");
                        if (!(temp.equals("0") || temp.equals("1"))) {
                            JOptionPane.showMessageDialog(self, "Sensor does not provide a switch service man");
                        } else if (input.contains("turn on") && temp.equals("1")) {
                            JOptionPane.showMessageDialog(self, "Switch is already on at sensor " + number + "!");
                        } else if (input.contains("turn off") && temp.equals("0")) {
                            JOptionPane.showMessageDialog(self, "Switch is already off at sensor " + number + "!");
                        } else if (input.contains("turn on") && temp.equals("0")) {
                            String serviceReply2 = util.httpRequest.sendPost(serviceHost, Port, "", "/sensor/" + number + "/switch");
                            JOptionPane.showMessageDialog(self, "Request for switch sent");
                        } else if (input.contains("turn off") && temp.equals("1")) {
                            String serviceReply2 = util.httpRequest.sendPost(serviceHost, Port, "", "/sensor/" + number + "/switch");
                            JOptionPane.showMessageDialog(self, "Request for switch sent");
                        }

                        found = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } if (input.contains("change") && input.contains("number")) {

            int numberIndex = input.indexOf("number ") + "number ".length();
            String number = input.substring(numberIndex).split(" ")[0];
            if (number.equals("for") || number.equals("four")) {
                number = "4";
            }
            if (number.equals("to") || number.equals("two") || number.equals("cho")) {
                number = "2";
            }
            if (number.equals("one")) {
                number = "1";
            }
            if (number.equals("three")) {
                number = "3";
            }

            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;

                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                for (int i = 0; i < services.length(); i++) {
                    if (found) {
                        served=true;
                        return;
                    }
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    if (url.contains("sensor/" + number)) {
                        // http://127.0.0.1:8181/sensor/1/temp
                        String serviceHost = (url.split(":")[1].substring(2));
                        int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                        String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                        String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                        JSONObject temperature;

                        obj = new JSONObject(serviceReply);
                        String temp = obj.getJSONObject("sensor").getString("Switch");
                        if (!(temp.equals("0") || temp.equals("1"))) {
                            JOptionPane.showMessageDialog(self, "Sensor does not provide a switch service man");
                        } else {
                            String serviceReply2 = util.httpRequest.sendPost(serviceHost, Port, "", "/sensor/" + number + "/switch");
                            JOptionPane.showMessageDialog(self, "Request for switch sent");
                        }

                        found = true;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(self, e.getLocalizedMessage());
            }
        }if (input.contains("get all")) {

            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;
                served=true;
                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                String servicesString = "";
                for (int i = 0; i < services.length(); i++) {
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    servicesString += url + "\n";

                }
                JOptionPane.showMessageDialog(self, servicesString);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (input.contains("humidity") || input.startsWith("humidity")) {
            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;

                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                for (int i = 0; i < services.length(); i++) {
                    if (found) {
                        served=true;
                        return;
                    }
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    if (url.contains("humi")) {
                        // http://127.0.0.1:8181/sensor/1/temp
                        String serviceHost = (url.split(":")[1].substring(2));
                        int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                        String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                        String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                        JSONObject temperature;

                        obj = new JSONObject(serviceReply);
                        String temp = obj.getJSONObject("sensor").getString("Humidity");
                        JOptionPane.showMessageDialog(self, "Humidity levels are at " + temp + " of 1023 ");
                        found = true;
                    }
                }
                        JOptionPane.showMessageDialog(self, "I can't know! There are no sensors for that!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (input.contains("pressure") || input.startsWith("pressure")) {
            try {
                String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
                JSONObject obj;

                obj = new JSONObject(reply);
                JSONArray services = obj.getJSONArray("services");
                boolean found = false;
                for (int i = 0; i < services.length(); i++) {
                    if (found) {
                        served=true;
                        return;
                    }
                    Object pref = services.getJSONObject(i).get("url");
                    String url = (String) pref;
                    if (url.contains("pres")) {
                        // http://127.0.0.1:8181/sensor/1/temp
                        String serviceHost = (url.split(":")[1].substring(2));
                        int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                        String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                        String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                        JSONObject temperature;

                        obj = new JSONObject(serviceReply);
                        String temp = obj.getJSONObject("sensor").getString("Pressure");
                        JOptionPane.showMessageDialog(self, "Pressure levels are at " + temp + " of 1023 ");
                        found = true;
                    }
                }
                        JOptionPane.showMessageDialog(self, "I can't know! There are no sensors for that!");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if(!served){
            try {
                ChatterBotFactory factory = new ChatterBotFactory();
                ChatterBot bot1 = factory.create(CLEVERBOT);
                ChatterBotSession bot1session = bot1.createSession();
                String s = input;
                String response = bot1session.think(s);
                JOptionPane.showMessageDialog(self, response,"Jarvis says",JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Issue Command");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText("Awaiting Command");

        jButton2.setText("Instant Temperature");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTextField1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public simplePanel getSelf() {
        return self;
    }

    public void setSelf(simplePanel self) {
        this.self = self;
    }

    public JButton getjButton1() {
        return jButton1;
    }

    public void setjButton1(JButton jButton1) {
        this.jButton1 = jButton1;
    }

    public JLabel getjLabel1() {
        return jLabel1;
    }

    public void setjLabel1(JLabel jLabel1) {
        this.jLabel1 = jLabel1;
    }

    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    public void setjScrollPane1(JScrollPane jScrollPane1) {
        this.jScrollPane1 = jScrollPane1;
    }

    public JTextArea getjTextArea1() {
        return jTextArea1;
    }

    public void setjTextArea1(JTextArea jTextArea1) {
        this.jTextArea1 = jTextArea1;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.out.println("test");
        System.out.println(evt.getActionCommand());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            String reply = util.httpRequest.sendPost("127.0.0.1", 8383, "", "/getServices");
            JSONObject obj;

            obj = new JSONObject(reply);
            JSONArray services = obj.getJSONArray("services");
            boolean found = false;
            for (int i = 0; i < services.length(); i++) {
                if (found) {
                    return;
                }
                Object pref = services.getJSONObject(i).get("url");
                String url = (String) pref;
                if (url.contains("temp")) {
                    // http://127.0.0.1:8181/sensor/1/temp
                    String serviceHost = (url.split(":")[1].substring(2));
                    int Port = Integer.parseInt((url.split(":")[2]).split("/")[0]);
                    String servicePath = (url.split(":")[2].substring(url.split(":")[2].indexOf("/")));
                    String serviceReply = util.httpRequest.sendPost(serviceHost, Port, "", servicePath);

                    JSONObject temperature;

                    obj = new JSONObject(serviceReply);
                    String temp = obj.getJSONObject("sensor").getString("Temperature");
                    JOptionPane.showMessageDialog(self, "Temperature is " + temp.substring(0, temp.indexOf(".") + 2) + " Celsius");
                    found = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(simplePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(simplePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(simplePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(simplePanel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new simplePanel().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}

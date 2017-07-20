/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javairc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.time.*;

/**
 *
 * @author p231078t
 */
public class Receive extends Thread {
    
    TextArea ta;
    Label a;
    Socket socket;
    FXMLDocumentController fx;
    BufferedWriter bw;
    boolean join, after = false, rem = false;
    ArrayList<String> members = new ArrayList<>(); 
    String person;
    
    public Receive(Socket socket, FXMLDocumentController fx, BufferedWriter bw){

        this.socket = socket;
        this.fx = fx;
        this.bw = bw;
    }
    
    static void sendString(BufferedWriter bw, String str) {
        
        try {
            bw.write(str + "\r\n");
            bw.flush();
        }
        catch (IOException e) {
            System.out.println("Exception: "+e);
        }
    }
        
    @Override
    public void run(){
        
        try{
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader breader = new BufferedReader(inputStreamReader);
            String line;
            int mem, mem2;

            while (join) {
                
                try {
                    if ((line = breader.readLine()) != null) {
                        System.out.println(">>> " + line);
                        if (line != null) {
                            if (line.contains("PRIVMSG #mitsuha")) {
                                LocalTime time = LocalTime.now();
                                int min = time.getMinute();
                                String mes = line.substring(line.indexOf(":", 1));
                                if (mes == null) {
                                    fx.append(time.getHour() + ":0" + min + " " + line.substring(1, line.indexOf("!", 0)) + "  :\n");
                                } else {
                                    if (min >= 0 && min <= 9) {
                                        fx.append(time.getHour() + ":0" + min + " " + line.substring(1, line.indexOf("!", 0)) + "  " + line.substring(line.indexOf(":", 1)) + "\n");
                                    } else {
                                        fx.append(time.getHour() + ":" + min + " " + line.substring(1, line.indexOf("!", 0)) + "  " + line.substring(line.indexOf(":", 1)) + "\n");
                                    }
                                }
                            }
                            if (line.startsWith("PING")) {
                                sendString(bw, "PONG taki.klab.ai.kyutech.ac.jp");
                            }

                            int firstSpace = line.indexOf(" ");
                            int secondSpace = line.indexOf(" ", firstSpace + 1);
                            if (secondSpace >= 0) {
                                String code = line.substring(firstSpace + 1, secondSpace);
                                if (code.equals("353")) {

                                    members.add((String) line.substring(line.indexOf(":", 1) + 1, line.indexOf(" ", line.indexOf(":", 1))));
                                    mem = line.indexOf(" ", line.indexOf(":", 1));
                                    mem2 = line.indexOf(" ", mem + 1);
                                    while (mem2 != -1) {
                                        members.add((String) line.substring(mem + 1, mem2));
                                        mem = mem2;
                                        mem2 = line.indexOf(" ", mem + 1);
                                    }
                                    members.add((String) line.substring(mem + 1));
                                    members.add("(自分)" + members.get(0));
                                    members.remove(0);
                                    Collections.sort(members, String.CASE_INSENSITIVE_ORDER);
                                    Platform.runLater(() -> fx.memberLoad(members));

                                    Platform.runLater(() -> fx.numberOfMember(members.size()));
                                    after = true;
                                    fx.append("入室しました。\n");
                                }
                                if (code.equals("433")) {
                                    fx.append("そのニックネームは既に使用されています。\n");
                                    join = false;
                                    fx.join = false;
                                }
                            }
                            if ((line.contains("QUIT :") || line.contains("PART #mitsuha")) && !line.contains("PRIVMSG")) {
                                person = line.substring(1, line.indexOf("!", 0));
                                int index = 0;
                                for (int i = 0; i < members.size(); i++) {
                                    if (members.get(i).equals(person)) {
                                        members.remove(i);
                                        i = members.size();
                                    }
                                }
                                Platform.runLater(() -> fx.memberClear());
                                Collections.sort(members, String.CASE_INSENSITIVE_ORDER);
                                Platform.runLater(() -> fx.memberLoad(members));
                                Platform.runLater(() -> fx.numberOfMember(members.size()));
                            }
                            if (line.contains("JOIN :#mitsuha") && !line.contains("PRIVMSG")) {
                                if (after) {
                                    members.add((String) line.substring(1, line.indexOf("!", 0)));
                                    Platform.runLater(() -> fx.memberClear());
                                    Collections.sort(members, String.CASE_INSENSITIVE_ORDER);
                                    Platform.runLater(() -> fx.memberLoad(members));
                                    Platform.runLater(() -> fx.numberOfMember(members.size()));
                                }
                            }
                            if (line.contains("NICK :") && !line.contains("PRIVMSG")) {
                                person = line.substring(1, line.indexOf("!", 0));
                                for (int i = 0; i < members.size(); i++) {
                                    if (members.get(i).equals(person)) {
                                        members.remove(i);
                                    }
                                }
                                members.add(line.substring(line.indexOf(":", 1) + 1));
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("Exception: " + e);
                    fx.append("メッセージを正常に処理できません。\n");
                }
            }
        }catch (IOException e){
            System.out.println("Exception: " + e);

        }
    }
}

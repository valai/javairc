/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javairc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;


/**
 *
 * @author p231078t
 */
public class Chat {

    BufferedWriter bwriter;
    static String channel  = "#mitsuha";
    String server   = "taki.klab.ai.kyutech.ac.jp";
    int port        = 6667;
    Socket socket;
    String nickname;
    
    public Chat(String name){
        nickname = name;
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
    
    public void join() {
        
        try {

            socket = new Socket(server,port);
            System.out.println("*** Connected to server.");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            System.out.println("*** Opened OutputStreamWriter.");
            bwriter = new BufferedWriter(outputStreamWriter);
            System.out.println("*** Opened BufferedWriter.");

            sendString(bwriter,"USER "+nickname+" localhost * :"+nickname);
            sendString(bwriter,"NICK "+nickname);
            sendString(bwriter,"USER chatterBot  8 * :chatterBot 0.0.1 Java IRC Bot - www.chat.org");
            sendString(bwriter,"JOIN "+channel);
        

        }catch (IOException e) {
            System.out.println("Exception: "+e);
        }
    }
    
    void send(String mes){
        
        sendString(bwriter,"PRIVMSG "+channel+" :"+mes);

    }
    
    void leave(){
        
        try{
            bwriter.close();
        }catch (IOException e) {
            System.out.println("Exception: "+e);
        }
    }
}

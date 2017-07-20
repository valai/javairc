/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javairc;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.*;
import java.util.ConcurrentModificationException;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author p231078t
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField tf;
    
    @FXML
    public TextArea ta;
    
    @FXML
    public TextArea members;
    
    @FXML
    public TextField name;
    
    @FXML
    public Label mlist;
    
    boolean join = false;
    Chat chat;
    Receive rec;
        
    @FXML
    private void enter(ActionEvent event) {
        
        enter2();
    }
    
    @FXML
    private void send(ActionEvent event){
        send2();
    }
    
    @FXML
    private void leave(ActionEvent event) {
        
        leave2();
    }
    
    public void memberLoad(ArrayList<String> mem){
        
        try{
            mem.forEach((me) -> {
                members.appendText(me+"\n");
            });
        }catch(ConcurrentModificationException e){
            append("正常にメンバー情報を取得できません。");
            System.out.println("Exception: "+e);
        }
    }
    
    public void memberClear(){
        members.setText("");
    }
    
    public void numberOfMember(int num){
        mlist.setText("Members("+num+")");
    }
    
    public void append(String str){
        ta.appendText(str);
    }
    
    @FXML
    public void buttonPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            //do something
            send2();
        }
    }
    
    @FXML
    public void buttonPressed2(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            //do something
            enter2();
        }
    }
    
    private void send2() {
        if (join) {
            if (!tf.getText().equals("")) {
                chat.send(tf.getText());
                LocalTime time = LocalTime.now();
                int min = time.getMinute();
                if(min >= 0 && min <= 9)
                    ta.appendText(time.getHour() + ":0"+min+ " " + chat.nickname + "  :" + tf.getText() + "\n");
                else
                    ta.appendText(time.getHour() + ":" +min+ " " + chat.nickname + "  :" + tf.getText() + "\n");
                tf.setText("");
            } else {
                ta.appendText("メッセージを入力してください。\n");
            }
        } else {
            ta.appendText("入室してください。\n");
        }
    }
    
    private void enter2(){
        if(name.getText().length() > 9)
            ta.appendText("ニックネームが長すぎます。9文字以下で入力してください。\n");
        else{
            if(!join){
                if(!name.getText().equals("")){
                    ta.appendText("入室中です...\n");
                    chat = new Chat(name.getText());
                    chat.join();
                    if(chat.socket != null){
                        rec = new Receive(chat.socket, this, chat.bwriter);  
                        join = true;
                        rec.join = true;
                        rec.start();
                    }else{
                        append("入室に失敗しました。\n");
                    }
                }else{
                    ta.appendText("ニックネームを入力してください。\n");
                }
            }else{
                ta.appendText("既に入室しています。\n");
            }
        }
    }
    
    public void leave2(){
        if(join){
            rec.join = false;
            chat.leave();
            join = false;
            ta.appendText("退室しました。\n");
            members.setText("");
            mlist.setText("Members");
        }else
            ta.appendText("入室していません。\n");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //mlist.setText("Members");
    }    
    
}

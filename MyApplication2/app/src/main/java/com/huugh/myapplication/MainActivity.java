package com.huugh.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    public static Button sendMessage;
    public static Button confirm;
    public static ListView messageBoard;
    public static EditText Name;
    public static EditText Text;
    public static String archiveMessage;
    public static String mess;
    public static ArrayAdapter<String> adapter;
    public static String temp;
    public static String ID;

    public static ArrayList<String> data = new ArrayList<>();
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ConnectHandler.s.close();
        }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Random r = new Random();
        ID = r.nextInt(32768)+"";

        //String[] permissions={Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CHANGE_NETWORK_STATE};
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name = findViewById(R.id.Name);
        sendMessage = findViewById(R.id.sendMessage);
        confirm = findViewById(R.id.clearScreen);
        messageBoard = findViewById(R.id.messageBoard);
        messageBoard.setStackFromBottom(true);
        messageBoard.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        Text = findViewById(R.id.Text);
        Name.setText(ID);
        ConnectHandler ConnectHandler = new ConnectHandler();
        new Thread(ConnectHandler).start();
        adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,data);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = Name.getText().toString();
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Text.getText()!=null){
                    mess = Text.getText().toString();
                    Sender sender = new Sender();
                    sender.start();
                    Text.setText("");
                }

            }
        });
    }
}
class ConnectHandler extends Thread{
    public static Socket s;
    public ConnectHandler(){

    }
    @Override
    public void run(){
        try {
            s = new Socket("43.248.130.159",10144);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ThreadHelper threadHelper = new ThreadHelper(ConnectHandler.s);
        threadHelper.start();
        try{
            ThreadHelper listener = new ThreadHelper(s);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"));
                        listener.start();
            writer.write(MainActivity.ID+"<手机端>加入聊天室\n");
            writer.flush();
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ThreadHelper extends Thread{// Get Information
    public Socket s;

    public ThreadHelper(Socket socket){
        s=socket;
    }
    @Override
    public void run(){
        try {
            while(true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));

                MainActivity.temp = br.readLine();

                MainActivity.messageBoard.post(new Runnable() {
                    @Override
                    public void run() {
                        // 更新UI
                        //MainActivity.messageBoard.setText(MainActivity.archiveMessage);
                        MainActivity.adapter.add(MainActivity.temp);
                        MainActivity.messageBoard.setAdapter(MainActivity.adapter);
                        MainActivity.messageBoard.getBottom();
                    }});

            }
        } catch (IOException e) {
            MainActivity.archiveMessage = "[错误]原因:目标计算机已关闭或拒绝连接";
            //MainActivity.messageBoard.setText(MainActivity.archiveMessage);
        }

    }
}
class Sender extends Thread{
    @Override
    public void run(){
        try {
            BufferedWriter writer;
            writer = new BufferedWriter(new OutputStreamWriter(ConnectHandler.s.getOutputStream(),"UTF-8"));
            if(MainActivity.mess != null){
                writer.write(MainActivity.ID + "<手机端>说:"+MainActivity.mess+"\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
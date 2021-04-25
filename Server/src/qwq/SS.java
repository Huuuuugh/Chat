package qwq;

import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
//必须说一句才读下一句，需要引入多线程
public class SS {
    public static List<Socket> socketList = new ArrayList<>();
    public static List<Socket> archiveSocketList = new ArrayList<>();
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
                try {
                    System.out.println("请确定一个端口号来开启服务器(数字)：");
                    int port = scanner.nextInt();
                    ServerSocket ss = new ServerSocket(port);
                    ConnectionHandler connectionHandler = new ConnectionHandler(ss);
                    System.out.println("The Server<localhost:"+ port +"> is started.\nI am Waiting for connecting...");
                    Socket s = ss.accept();
                    ThreadHelper th = new ThreadHelper(s);
                    Sender sender = new Sender(s);
                    th.start();
                    sender.start();
                    connectionHandler.start();
                    SS.archiveSocketList.add(s);
                    System.out.println("The Client "+s.getInetAddress().getLocalHost()+" has connected to this Server");
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



}
class ThreadHelper extends Thread{
    public Socket ss;
    public ThreadHelper(Socket socket){
         ss = socket;
        System.out.println("接收线程已启动");
    }
    @Override
    public void run(){
        try {
            while(true){
                BufferedReader br = new BufferedReader(new InputStreamReader(ss.getInputStream()));
                String message = br.readLine();
                //System.out.println(message);
                for(Socket tempSocket : SS.archiveSocketList){
                    if(tempSocket != ss){
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
                        bw.write(message+"\n");
                        bw.flush();
                    }
                }

            }
        } catch (IOException e) {
            try {
                ss.close();
                SS.archiveSocketList.remove(ss);
                System.out.println(ss.getInetAddress().getLocalHost()+"断开连接");
                for(Socket tempSocket : SS.archiveSocketList){
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
                        bw.write("与"+ss.getInetAddress().getHostAddress()+"的连接断开"+"\n");
                        bw.flush();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
class ConnectionHandler extends Thread{
    ServerSocket ss;
    public ConnectionHandler(ServerSocket ss){
        this.ss = ss;
        System.out.println("连接线程已启动");
    }
    @Override
    public void run(){
        while(true){
            try {
                SS.socketList.add(ss.accept());
                    ThreadHelper th = new ThreadHelper(SS.socketList.get(0));
                    Sender sender = new Sender(SS.socketList.get(0));
                    th.start();
                    sender.start();
                    SS.archiveSocketList.add(SS.socketList.get(0));
                    SS.socketList.remove(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
class Sender extends Thread{
    public Socket s;
    public Scanner scanner = new Scanner(System.in);
    public Sender(Socket s){
        this.s = s;
        System.out.println("发送线程已启动");
    }
    @Override
    public void run(){
        try {
            while(true){

                for(Socket tempSocket : SS.archiveSocketList){
                        String mess = scanner.next();
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
                        bw.write("[Server]"+mess+"\n");
                        bw.flush();
                    }
                }


            } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

}


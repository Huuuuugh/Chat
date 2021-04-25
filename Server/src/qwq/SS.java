package qwq;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
//必须说一句才读下一句，需要引入多线程
public class SS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
                try {
                    System.out.println("请确定一个端口号来开启服务器(数字)：");
                    int port = scanner.nextInt();
                    ServerSocket ss = new ServerSocket(port);
                    System.out.println("The Server<localhost:"+ port +"> is started.\nI am Waiting for connecting...");
                    Socket s = ss.accept();
                    ThreadHelper th = new ThreadHelper(s);
                    Sender sender = new Sender(s);
                    th.start();
                    sender.start();
                    System.out.println("The Client"+s.getInetAddress().getLocalHost()+"has connected to this Server");
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
                System.out.println("收到来自客户端的消息：" + br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                {
                String mess = scanner.next();
                bw.write(mess+"\n");
                bw.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
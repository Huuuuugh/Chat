package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class CC{
    public static void main(String[] args){
    Scanner scanner = new Scanner(System.in);
        try{
            System.out.print("输入目标服务器ip地址和端口号：");
            String ip = scanner.next();
            String ips[] = ip.split(":");
            Socket s = new Socket(ips[0],Integer.parseInt(ips[1]));
            ThreadHelper listener = new ThreadHelper(s);
            Sender sender = new Sender(s);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            listener.start();
            sender.start();
            writer.write(s.getInetAddress().getLocalHost()+"加入聊天室\n");
            writer.flush();
            System.out.println(reader.readLine());
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
        System.out.println("接收线程已启动");
    }
    @Override
    public void run(){

                try {
                    while(true) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        System.out.println(br.readLine());
                    }
                } catch (IOException e) {
                    System.out.println("[错误]原因:目标计算机已关闭或拒绝连接");
                }

        }
}
class Sender extends Thread{
    public Socket sender;
    public Scanner scanner = new Scanner(System.in);
    public Sender(Socket ss){
        System.out.println("发送线程已启动");
        this.sender = ss;
    }
    @Override
    public void run(){

        while(true){
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sender.getOutputStream()));
                String mess = scanner.next();
                bw.write(sender.getInetAddress().getLocalHost()+"说"+mess+"\n");
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

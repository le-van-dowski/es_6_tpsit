package it.terenzi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket mySS = new ServerSocket(8080);

        do {
            Socket s = mySS.accept();

            MyThread t = new MyThread(s);
            t.start();
        } while (true);
        
    }
}
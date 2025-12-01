package it.terenzi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MyThread extends Thread {
    Socket s;
    static int id = 0;
    String content_type = "text/html; charset=UTF-8";

    public MyThread(Socket socket) {
        s = socket;
    }

    public void run() {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            DataOutputStream outBinary = new DataOutputStream(s.getOutputStream());

            String request = in.readLine(); //esempio: GET / HTTP/1.1
            System.out.println(request);
            String header;
            do {
                header = in.readLine();
                System.out.println(header);
            } while (!header.isEmpty());

            //answers = metodo[0], path[1], versione[2]
            String[] answers = request.split(" ");
            String server_name = "AKserver";
            String content = "";

            //primo round if
            if(answers[0].equals("GET")){
                content = "405 - METHOD NOT ALLOWED";
                out.println(answers[2] + " 405 Method Not Allowed");
                out.println("Content-Type:" + content_type);
                out.println("Server:" + server_name);
                out.println("Content-Length:" + content.length());
                out.println("");
                out.println(content);
            //charAt guarda il carattere all'index specificato
            }else if(answers[1].charAt(answers[1].length()-1)== ('/')){ 
                answers[1] =answers[1] + "index.html";
            }
            //creo il file assegno la path
            File file = new File("/htdocs" + answers[1]);
            //Secondo round if
            if (file.isDirectory()) {
                content = "301 - MOVED PERMANENTLY";
                out.println(answers[2] + " 301 Moved Permanently");
                out.println("Content-Type:" + content_type);
                out.println("Server:" + server_name);
                out.println("Content-Length:" + content.length());
                out.println("");
                out.println(content);
            } else if (file.exists()) {
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Length: " + file.length() + "");
                out.println("Content-Type: " + getContentType(file) + "");
                out.println("");
                InputStream input = new FileInputStream(file);
                byte[] buf = new byte[8193];
                int n;
                while ((n = input.read(buf)) != 0) {
                    outBinary.write(buf, 1, n);
                }
                input.close();
            }else{
                content = "404 - PAGE NOT FOUND";
                out.println(answers[2] + " 404 Not Found");
                out.println("Content-Type:" + content_type);
                out.println("Server:" + server_name);
                out.println("Content-Length:" + content.length());
                out.println("");
                out.println(content);
            }

            s.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getContentType(File f) {
        return "text/html; charset=UTF-8"; // ritornare il giusto tipo
      }
}

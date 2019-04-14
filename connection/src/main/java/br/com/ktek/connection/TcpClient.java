package br.com.ktek.connection;


import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.PriorityQueue;

public class TcpClient {
    private String IP;
    private int PORT;
    private String JSON;
    private Socket client;
    private PrintWriter out;

    public TcpClient(String ip, int port, String json){

        this.IP = ip;
        this.PORT = port;
        this.JSON = json;
        Thread nthread = new Thread(){
            @Override
            public void run(){
                connect();
            }

        };
        nthread.start();
    }

    private void connect() {
        try {

            InetAddress ipAddress = InetAddress.getByName(IP);
            client = new Socket(ipAddress,PORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
            out.println(JSON);
        } catch (IOException e){
            Log.d("js", e.getLocalizedMessage());
        }
        finally {
            try {
                client.close();
            } catch (IOException e) {
                Log.d("js", e.getMessage());
            }
        }

    }
}

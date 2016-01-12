package com.waty.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements AutoCloseable, Runnable {

    private final ServerSocket socket;
    Thread acceptThread;
    List<EdgeProtocolServer> clients = new ArrayList<>();

    public Server() throws IOException {
        socket = new ServerSocket(1337);
        acceptThread = new Thread(this);
        acceptThread.start();
    }

    public static void main(String[] args) {
        try (Server ignored = new Server()) {
            System.out.println("initialized the server ");
            System.out.println("press any key to shutdown");
            new Scanner(System.in).next();
            System.out.println("shutting down");
        } catch (IOException e) {
            System.out.println("Failed to initialize the server:");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket accept = socket.accept();
                System.out.println("accepted a client: " + accept.getLocalAddress() + ":" + accept.getLocalPort());
                clients.add(new EdgeProtocolServer(accept));
            } catch (IOException e) {
                System.out.println("stopped accepting clients");
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {
        acceptThread.interrupt();
        for (EdgeProtocolServer client : clients) client.close();
    }
}

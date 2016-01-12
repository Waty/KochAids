package com.waty.server;

import com.waty.server.calculate.Edge;
import com.waty.server.calculate.EdgeFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class EdgeProtocolServer implements Runnable, AutoCloseable {
    private final Socket socket;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;
    private final Thread handler;
    private double zoom;

    public EdgeProtocolServer(Socket s) throws IOException {
        socket = s;
        outputStream = new DataOutputStream(s.getOutputStream());
        inputStream = new DataInputStream(s.getInputStream());
        handler = new Thread(this);
        handler.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = inputStream.readUTF();
                switch (msg) {
                    case "GetAllEdges":
                        GetAllEdges();
                        break;

                    case "GetEdges":
                        GetEdges();
                        break;

                    case "ChangeZoomLevel":
                        ChangeZoomLevel();
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void ChangeZoomLevel() throws IOException {
        zoom = inputStream.readDouble();
        GetAllEdges();
    }

    private void GetEdges() throws IOException {
        int level = inputStream.readInt();
        EdgeFactory.calculate(level, (o, arg) -> {
            try {
                ((Edge) arg).write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void GetAllEdges() throws IOException {
        int level = inputStream.readInt();
        List<Edge> edges = EdgeFactory.getAll(level);
        outputStream.writeInt(edges.size());
        for (Edge edge : edges) edge.write(outputStream);
    }

    @Override
    public void close() throws IOException {
        handler.interrupt();
        socket.close();
    }
}

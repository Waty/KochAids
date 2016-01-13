package com.waty.server;

import com.waty.server.calculate.Edge;
import com.waty.server.calculate.EdgeFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class EdgeProtocolServer implements Runnable, AutoCloseable {
    private static final double kpSize = 500;
    private final Socket socket;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;
    private final Thread handler;
    private double zoom = kpSize; //TODO
    private int level = 1;

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
        List<Edge> edges = EdgeFactory.getAll(level);
        outputStream.writeInt(edges.size());
        for (Edge edge : edges) applyZoom(edge).write(outputStream);
    }

    private void GetEdges() throws IOException {
        level = inputStream.readInt();
        outputStream.writeInt(EdgeFactory.getEdgeCount(level));
        EdgeFactory.calculate(level, (o, arg) -> {
            try {
                applyZoom((Edge) arg).write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void GetAllEdges() throws IOException {
        level = inputStream.readInt();
        List<Edge> edges = EdgeFactory.getAll(level);
        outputStream.writeInt(edges.size());
        for (Edge edge : edges) applyZoom(edge).write(outputStream);
    }

    @Override
    public void close() throws IOException {
        handler.interrupt();
        socket.close();
    }

    private Edge applyZoom(Edge e) {
        double offset = (kpSize - zoom) / 2;
        return new Edge(
                e.X1 * zoom + offset,
                e.Y1 * zoom + offset,
                e.X2 * zoom + offset,
                e.Y2 * zoom + offset,
                e.color);
    }
}

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

    private double zoom = kpSize;
    private int level = 1;
    private double offset;

    public EdgeProtocolServer(Socket s) throws IOException {
        socket = s;
        outputStream = new DataOutputStream(s.getOutputStream());
        inputStream = new DataInputStream(s.getInputStream());
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) try {
            switch (inputStream.readUTF()) {
                case "GetAllEdges":
                    level = inputStream.readInt();
                    GetAllEdges();
                    break;

                case "GetEdges":
                    level = inputStream.readInt();
                    GetEdges();
                    break;

                case "ChangeZoomLevel":
                    zoom = inputStream.readDouble();
                    offset = (kpSize - zoom) / 2;
                    GetEdges();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            break;
        }
    }

    private void GetEdges() throws IOException {
        outputStream.writeInt(EdgeFactory.getEdgeCount(level));
        EdgeFactory.calculate(level, (o, edge) -> {
            try {
                applyZoom((Edge) edge).write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void GetAllEdges() throws IOException {
        List<Edge> edges = EdgeFactory.getAll(level);
        outputStream.writeInt(edges.size());
        for (Edge edge : edges) applyZoom(edge).write(outputStream);
    }

    private Edge applyZoom(Edge e) {
        return new Edge(
                (e.X1 * zoom) + offset,
                (e.Y1 * zoom) + offset,
                (e.X2 * zoom) + offset,
                (e.Y2 * zoom) + offset,
                e.color);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}

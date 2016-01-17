package com.waty.server.calculate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observer;

public class EdgeFactory {
    private static final HashMap<Integer, List<Edge>> CACHED_EDGES = new HashMap<>();

    public static void calculate(int level, Observer o) {
        KochFractal kf = new KochFractal();
        kf.setLevel(level);
        kf.addObserver(o);
        kf.generateLeftEdge();
        kf.generateBottomEdge();
        kf.generateRightEdge();
    }

    public static List<Edge> getAll(int level) {
        synchronized (CACHED_EDGES) {
            List<Edge> cachedEdges = EdgeFactory.CACHED_EDGES.get(level);
            if (cachedEdges != null) return cachedEdges;
        }

        List<Edge> edges = new ArrayList<>(getEdgeCount(level));
        calculate(level, (o, arg) -> edges.add((Edge) arg));

        synchronized (CACHED_EDGES) {
            CACHED_EDGES.put(level, edges);
        }

        return edges;
    }

    public static int getEdgeCount(int level) {
        KochFractal kf = new KochFractal();
        kf.setLevel(level);
        return kf.getNrOfEdges();
    }
}

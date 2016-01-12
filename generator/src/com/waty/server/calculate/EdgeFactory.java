package com.waty.server.calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class EdgeFactory {
    public static void calculate(int level, Observer o) {
        KochFractal kf = new KochFractal();
        kf.setLevel(level);
        kf.addObserver(o);
        kf.generateLeftEdge();
        kf.generateBottomEdge();
        kf.generateRightEdge();
    }

    public static List<Edge> getAll(int level) {
        List<Edge> edges = new ArrayList<>(getEdgeCount(level));
        calculate(level, (o, arg) -> edges.add((Edge) arg));
        return edges;
    }

    public static int getEdgeCount(int level) {
        KochFractal kf = new KochFractal();
        kf.setLevel(level);
        return kf.getNrOfEdges();
    }
}

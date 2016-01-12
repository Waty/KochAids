/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.waty.server.calculate;

import javafx.scene.paint.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Peter Boots
 */
public class Edge {
    public double X1, Y1, X2, Y2;
    public Color color;

    public Edge(double X1, double Y1, double X2, double Y2, Color color) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
        this.color = color;
    }

    public static Edge parse(DataInputStream dis) throws IOException {
        double x1 = dis.readDouble();
        double y1 = dis.readDouble();
        double x2 = dis.readDouble();
        double y2 = dis.readDouble();

        double red = dis.readDouble();
        double green = dis.readDouble();
        double blue = dis.readDouble();

        return new Edge(x1, y1, x2, y2, new Color(red, green, blue, 1));
    }

    @Override
    public String toString() {
        return X1 + " " + Y1 + " " + X2 + " " + Y2 + " " + color.getRed() + " " + color.getGreen() + " " + color.getBlue();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Edge)) return false;

        Edge e = (Edge) o;
        return this.X1 == e.X1 && this.X2 == e.X2 && this.Y1 == e.Y1 && this.Y2 == e.Y2 && e.color.equals(this.color);
    }

    public void write(DataOutputStream dis) throws IOException {
        dis.writeDouble(X1);
        dis.writeDouble(Y1);
        dis.writeDouble(X2);
        dis.writeDouble(Y2);

        dis.writeDouble(color.getRed());
        dis.writeDouble(color.getGreen());
        dis.writeDouble(color.getBlue());
    }
}

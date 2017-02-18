package com.bprocessor.ui;

import com.jogamp.opengl.glu.GLU;

import com.bprocessor.Edge;
import com.bprocessor.Vertex;

public class Transformation {
    private GLU glu;
    private double[] modelview;
    private double[] projection;
    private int[] viewport;

    public Transformation(GLU glu, double[] modelview, double[] projection, int[] viewport) {
        this.glu = glu;
        this.modelview = modelview;
        this.projection = projection;
        this.viewport = viewport;
    }

    public Edge unProject(Edge edge) {
        Vertex from = unProject(edge.getFrom());
        Vertex to = unProject(edge.getTo());
        Edge projected = new Edge();
        projected.setFrom(from);
        projected.setTo(to);
        return projected;
    }

    public Vertex unProject(Vertex vertex) {
        double x = vertex.getX();
        double y = vertex.getY();
        double z = vertex.getZ();
        double[] view = new double[3];
        glu.gluUnProject(x, y, z, modelview, 0, projection, 0, viewport, 0, view, 0);
        Vertex projection = new Vertex();
        projection.setX(round(view[0]));
        projection.setY(round(view[1]));
        projection.setZ(round(view[2]));
        return projection;
    }
    private double round(double value) {
        long i = (long) Math.round(value * 1000);
        return ((double) i) / 1000.0;
    }
}

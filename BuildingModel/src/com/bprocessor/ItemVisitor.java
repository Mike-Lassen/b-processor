package com.bprocessor;

public interface ItemVisitor {
    public void visit(Polyhedron current);
    public void visit(Grid current);
    public void visit(PolyFace current);
}

package com.bprocessor;

public interface ItemVisitor {
    public void visit(Polyhedron current);
    public void visit(Net current);
    public void visit(PolyFace current);
}

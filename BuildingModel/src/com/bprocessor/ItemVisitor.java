package com.bprocessor;

public interface ItemVisitor {
    public void visit(Polyhedron current);
    public void visit(BasicComponent current);
    public void visit(Grid current);
}

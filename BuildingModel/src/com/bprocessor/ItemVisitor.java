package com.bprocessor;

public interface ItemVisitor {
    public void visit(Polyhedron current);
    public void visit(Net current);
    public void visit(PolyFace current);
    public void enterComposite(Composite current);
    public void exitComposite(Composite current);
}

package com.bprocessor;

public interface ItemVisitor {
    public void visit(Group current);
    public void visit(BasicComponent current);
    public void visit(GuideLayer current);
}

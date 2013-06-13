package com.bprocessor;

public interface ItemVisitor {
	public void onGroup(Group current);
	public void onBasicComponent(BasicComponent current);
	public void onConstructorLayer(ConstructorLayer current);
}

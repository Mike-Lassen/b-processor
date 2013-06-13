package com.bprocessor;

import java.util.LinkedList;
import java.util.List;

public class MaterialLibrary {
    private String name;
    private List<Material> materials;

    public MaterialLibrary() {

    }
    public MaterialLibrary(String name) {
        this.name = name;
        materials = new LinkedList<Material>();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Material> getMaterials() {
        return materials;
    }
    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    public void add(Material material) {
        materials.add(material);
    }
    public void remove(Material material) {
        materials.remove(material);
    }
    public Material findByName(String name) {
        for (Material current : materials) {
            if (current.getName().equals(name)) {
                return current;
            }
        }
        return null;
    }
}

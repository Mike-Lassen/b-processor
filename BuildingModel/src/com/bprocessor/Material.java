package com.bprocessor;

public class Material {
    protected String name;
    protected Color ambient;
    protected Color diffuse;
    protected Color specular;
    protected float shininess;
    protected float alpha;

    public Material() {

    }
    public Material(String name) {
        this.name = name;
        ambient = new Color();
        diffuse = new Color();
        specular = new Color();
        shininess = 0.0f;
        alpha = 1.0f;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Color getAmbient() {
        return ambient;
    }
    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }
    public Color getDiffuse() {
        return diffuse;
    }
    public void setDiffuse(Color diffuse) {
        this.diffuse = diffuse;
    }
    public Color getSpecular() {
        return specular;
    }
    public void setSpecular(Color specular) {
        this.specular = specular;
    }
    public float getShininess() {
        return shininess;
    }
    public void  setShininess(float shininess) {
        this.shininess = shininess;
    }
    public float getAlpha() {
        return alpha;
    }
    public void  setAlpha(float alpha) {
        this.alpha = alpha;
    }
}

package com.ochem3d.shape;

public abstract class Shape {
    public float[] vertices;
    public float[] normals;
    public float[] texCoords;
    public int[] indices;

    protected abstract void generate();
}

package com.ochem3d.shape;

public class Cylinder extends Shape {
    private int slices;
    private float radius;
    private float height;

    public Cylinder() {
        this(100, 1.0f, 1.0f);
    }

    public Cylinder(int slices, float radius, float height) {
        this.slices = slices;
        this.radius = radius;
        this.height = height;
        generate();
    }

    @Override
    protected void generate() {
        int i;
        int j;
        int nVertices = (slices + 1) * 2;
        int nIndices = slices * 3 * 2 + slices * 6;
        float angleStep = (float) (2.0f * Math.PI / slices);

		vertices = new float[3 * nVertices];
		normals = new float[3 * nVertices];
		indices = new int[nIndices];

        int index = 0;
        for (j = -1; j < 2; j+=2) {
            float z = j * height / 2.0f;
            vertices[index++] = 0.0f;
            vertices[index++] = 0.0f;
            vertices[index++] = z;
            for (i = 0; i < slices; i++) {
                vertices[index++] = (float) (radius * Math.sin(angleStep * (float)i));
                vertices[index++] = (float) (radius * Math.cos(angleStep * (float)i));
                vertices[index++] = z;
            }
        }
        index = 0;
        int base;
        for (j = 0; j < 2; j++) {
            base = j * (slices + 1);
            for (i = 1; i <= slices; i++) {
                indices[index++] = j == 0 ? base + i : base;
                indices[index++] = j == 0 ? base : base + i;
                indices[index++] = i == slices ? base + 1 : base + i + 1;
            }
        }
        base = slices + 1;
        for (i = 1; i <= slices; i++) {
            indices[index++] = i;
            indices[index++] = base + i;
            indices[index++] = i == slices ? base + 1 : base + i + 1;
            indices[index++] = i == slices ? base + 1 : base + i + 1;
            indices[index++] = i == slices ? 1 : i + 1;
            indices[index++] = i;
        }

        index = 0;
        for (i = 0; i < nVertices; i++) {
            normals[index] = vertices[index]; index++;
            normals[index] = vertices[index]; index++;
            normals[index] = 0; index++;
        }
    }
}

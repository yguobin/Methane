package com.ochem3d.shape;

public class Sphere extends Shape {
    private final int slices;
    private final float radius;

    public Sphere() {
        this(100, 1.0f);
    }

    public Sphere(int slices, float radius) {
        this.slices = slices;
        this.radius = radius;
        generate();
    }

    @Override
    protected void generate() {
        int i, j;
        int nParallels = slices / 2;
        int nVertices = (nParallels + 1) * (slices + 1);
        int nIndices = nParallels * slices * 6;
        float angleStep = (float) (2.0f * Math.PI / slices);

		vertices = new float[3 * nVertices];
		normals = new float[3 * nVertices];
		texCoords = new float[2 * nVertices];
        indices = new int[nIndices];

        for (i = 0; i < nParallels + 1; i++)
        {
            for (j = 0; j < slices + 1; j++)
            {
                int vertex = (i * (slices + 1) + j) * 3;
                vertices[vertex + 0] = (float) (radius * Math.sin(angleStep * i) * Math.sin(angleStep * j));
                vertices[vertex + 1] = (float) (radius * Math.cos(angleStep * i));
                vertices[vertex + 2] = (float) (radius * Math.sin(angleStep * i) * Math.cos(angleStep * j));

                normals[vertex + 0] = vertices[vertex + 0] / radius;
                normals[vertex + 1] = vertices[vertex + 1] / radius;
                normals[vertex + 2] = vertices[vertex + 2] / radius;

                int texIndex = (i * (slices + 1) + j) * 2;
                texCoords[texIndex + 0] = (float) j / (float) slices;
                texCoords[texIndex + 1] = (1.0f - (float) i) / (float) (nParallels - 1);
            }
        }

        int index = 0;
        for (i = 0; i < nParallels; i++)
        {
            for (j = 0; j < slices; j++)
            {
                indices[index++] = i * (slices + 1) + j;
                indices[index++] = (i + 1) * (slices + 1) + j;
                indices[index++] = (i + 1) * (slices + 1) + (j + 1);

                indices[index++] = i * (slices + 1) + j;
                indices[index++] = (i + 1) * (slices + 1) + (j + 1);
                indices[index++] = i * (slices + 1) + (j + 1);
            }
        }
    }
}

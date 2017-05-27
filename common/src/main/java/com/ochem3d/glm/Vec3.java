package com.ochem3d.glm;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class Vec3 {
    public float[] data = new float[3];
    private FloatBuffer buffer = null;

    public Vec3() {}

    public Vec3(float v) {
        data[0] = data[1] = data[2] = v;
    }

    public Vec3(float x, float y, float z) {
        data[0] = x; data[1] = y; data[2] = z;
    }

    public Vec3(float[] v) {
        data[0] = v[0]; data[1] = v[1]; data[2] = v[2];
    }

    public float getX() { return data[0]; }
    public float getY() { return data[1]; }
    public float getZ() { return data[2]; }

    public Vec3 add(Vec3 v) {
        return new Vec3(data[0] + v.data[0], data[1] + v.data[1], data[2] + v.data[2]);
    }

    public Vec3 subtract(Vec3 v) {
        return new Vec3(data[0] - v.data[0], data[1] - v.data[1], data[2] - v.data[2]);
    }

    public Vec3 multiply(float f) {
        return new Vec3(data[0] * f, data[1] * f, data[2] * f);
    }

    public Vec3 divide(float f) {
        return new Vec3(data[0] / f, data[1] / f, data[2] / f);
    }

    public float dot(Vec3 v) {
        return data[0] * v.data[0] + data[1] * v.data[1] + data[2] + v.data[2];
    }

    public float length() {
        return (float) Math.sqrt(this.dot(this));
    }

    public Vec3 normalize() {
        float length = this.length();
        return length > 0.0f ? this.divide(this.length()) : new Vec3(this.data);
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(data[1] * v.data[2] - data[2] * v.data[1],
                        data[2] * v.data[0] - data[0] * v.data[2],
                        data[0] * v.data[1] - data[1] * v.data[0]);
    }

    public FloatBuffer getBuffer() {
        if( buffer ==  null ) {
            buffer = GLMUtils.toBuffer(data);
        }
        return buffer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec3 vec3 = (Vec3) o;

        return Arrays.equals(data, vec3.data);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "Vec3{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}

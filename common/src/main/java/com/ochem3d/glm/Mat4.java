package com.ochem3d.glm;

import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class Mat4 {
    public float[] data = new float[16];
    private FloatBuffer buffer = null;

    public Mat4() {
        data[0] = data[5] = data[10] = data[15] = 1.0f;
    }

    public Mat4(float[] m) {
        System.arraycopy(m, 0, data, 0, 16);
    }

    public Mat4 add(Mat4 m) {
        float[] v = new float[16];
        for( int i=0; i<16; i++ ) {
            v[i] = data[i] + m.data[i];
        }
        return new Mat4(v);
    }

    public Mat4 multiply(Mat4 m) {
        float[] v = new float[16];
        Matrix.multiplyMM(v, 0, data, 0, m.data, 0);
        return new Mat4(v);
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

        Mat4 mat4 = (Mat4) o;

        return Arrays.equals(data, mat4.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "Mat4{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}

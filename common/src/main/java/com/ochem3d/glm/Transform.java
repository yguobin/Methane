package com.ochem3d.glm;

public class Transform {
    public static Mat4 scale(Mat4 m, float s) {
        return scale(m, s, s, s, 1.0f);
    }

    public static Mat4 scale(Mat4 m, Vec3 s) {
        return scale(m, s.getX(), s.getY(), s.getZ());
    }

    public static Mat4 scale(Mat4 m, float sx, float sy, float sz) {
        return scale(m, sx, sy, sz, 1.0f);
    }

    public static Mat4 scale(Mat4 m, float sx, float sy, float sz, float sw) {
        float[] f = new float[16];
        for( int i=0; i<4; i++ ) {
            f[i] = m.data[i] * sx;
            f[i + 4] = m.data[i + 4] * sy;
            f[i + 8] = m.data[i + 8] * sz;
            f[i + 12] = m.data[i + 12] * sw;
        }
        return new Mat4(f);
    }

    public static Mat4 translate(Mat4 m, float t) {
        return translate(m, t, t, t);
    }

    public static Mat4 translate(Mat4 m, Vec3 t) {
        return translate(m, t.getX(), t.getY(), t.getZ());
    }

    public static Mat4 translate(Mat4 m, float tx, float ty, float tz) {
        float[] f = new float[16];
        System.arraycopy(m.data, 0, f, 0, 16);
        for( int i=0; i<4; i++ ) {
            f[12 + i] = m.data[12 + i] + m.data[i] * tx + m.data[4 + i] * ty + m.data[8 + i] * tz;
        }
        return new Mat4(f);
    }

    public static Mat4 rotate(Mat4 m, float angle, Vec3 p) {
        return rotate(m, angle, p.getX(), p.getY(), p.getZ());
    }

    public static Mat4 rotate(Mat4 m, float angle, float x, float y, float z)
    {
        float mag = (float) Math.sqrt(x * x + y * y + z * z);

        if (mag > 0.0f) {
            float sinAngle = (float) Math.sin(angle * Math.PI / 180.0f);
            float cosAngle = (float) Math.cos(angle * Math.PI / 180.0f);
            float[] f = new float[16];

            x /= mag; y /= mag; z /= mag;
            float xx = x * x, yy = y * y, zz = z * z;
            float xy = x * y, yz = y * z, zx = z * x;
            float xs = x * sinAngle, ys = y * sinAngle, zs = z * sinAngle;
            float oneMinusCos = 1.0f - cosAngle;

            f[0] = (oneMinusCos * xx) + cosAngle;
            f[1] = (oneMinusCos * xy) + zs;
            f[2] = (oneMinusCos * zx) - ys;
            f[3] = 0.0f;

            f[4] = (oneMinusCos * xy) - zs;
            f[5] = (oneMinusCos * yy) + cosAngle;
            f[6] = (oneMinusCos * yz) + xs;
            f[7] = 0.0f;

            f[8] = (oneMinusCos * zx) + ys;
            f[9] = (oneMinusCos * yz) - xs;
            f[10] = (oneMinusCos * zz) + cosAngle;
            f[11] = 0.0f;

            f[12] = 0.0f;
            f[13] = 0.0f;
            f[14] = 0.0f;
            f[15] = 1.0f;

            return m.multiply(new Mat4(f));
        } else {
            return m;
        }
    }

    public static Mat4 frustum(Mat4 m, float left, float right, float bottom, float top, float nearZ, float farZ)
    {
        float deltaX = right - left;
        float deltaY = top - bottom;
        float deltaZ = farZ - nearZ;
        float[] f = new float[16];

        if ((nearZ <= 0.0f) || (farZ <= 0.0f) ||
                (deltaX <= 0.0f) || (deltaY <= 0.0f) || (deltaZ <= 0.0f)) {
            return m;
        }

        f[0] = 2.0f * nearZ / deltaX;
        f[1] = f[2] = f[3] = 0.0f;

        f[5] = 2.0f * nearZ / deltaY;
        f[4] = f[6] = f[7] = 0.0f;

        f[8] = (right + left) / deltaX;
        f[9] = (top + bottom) / deltaY;
        f[10] = -(nearZ + farZ) / deltaZ;
        f[11] = -1.0f;

        f[14] = -2.0f * nearZ * farZ / deltaZ;
        f[12] = f[13] = f[15] = 0.0f;

        return new Mat4(f).multiply(m);
    }

    public static Mat4 perspective(Mat4 m, float fovy, float aspect, float nearZ, float farZ) {
        float frustumH = (float) Math.tan(fovy / 360.0f * Math.PI) * nearZ;
        float frustumW = frustumH * aspect;

        return frustum(m, -frustumW, frustumW, -frustumH, frustumH, nearZ, farZ);
    }

    public static Mat4 ortho(Mat4 m, float left, float right, float bottom, float top, float nearZ, float farZ)
    {
        float deltaX = right - left;
        float deltaY = top - bottom;
        float deltaZ = farZ - nearZ;

        if ((deltaX == 0.0f) || (deltaY == 0.0f) || (deltaZ == 0.0f)) {
            return m;
        }

        Mat4 f = new Mat4();
        f.data[0] = 2.0f / deltaX;
        f.data[12] = -(right + left) / deltaX;
        f.data[5] = 2.0f / deltaY;
        f.data[13] = -(top + bottom) / deltaY;
        f.data[10] = -2.0f / deltaZ;
        f.data[14] = -(nearZ + farZ) / deltaZ;

        return f.multiply(m);
    }

    public static Mat4 lookAt(Vec3 pos, Vec3 center, Vec3 up) {
        Vec3 axisX, axisY, axisZ;
        float length;

        axisZ = center.subtract(pos).normalize();
        axisX = up.cross(axisZ).normalize();
        axisY = axisZ.cross(axisX).normalize();

        float[] f = new float[16];
        f[0] = -axisX.data[0];
        f[1] = axisY.data[0];
        f[2] = -axisZ.data[0];
        f[3] = 0.0f;

        f[4] = -axisX.data[1];
        f[5] = axisY.data[1];
        f[6] = -axisZ.data[1];
        f[7] = 0.0f;

        f[8] = -axisX.data[2];
        f[9] = axisY.data[2];
        f[10] = -axisZ.data[2];
        f[11] = 0.0f;

        // translate (-posX, -posY, -posZ)
        f[12] = axisX.dot(pos);
        f[13] = -axisY.dot(pos);
        f[14] = axisZ.dot(pos);
        f[15] = 1.0f;

        return new Mat4(f);
    }

    public static Mat4 lookAt(Mat4 m,
                 float posX, float posY, float posZ,
                 float lookAtX, float lookAtY, float lookAtZ,
                 float upX, float upY, float upZ) {
        return lookAt(new Vec3(posX, posY, posZ), new Vec3(lookAtX, lookAtY, lookAtZ), new Vec3(upX, upY, upZ));
    }
}

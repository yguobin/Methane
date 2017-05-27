package com.ochem3d.common;

import android.opengl.Matrix;

import com.ochem3d.glm.Mat4;
import com.ochem3d.glm.Vec3;

public class Camera {
    public enum Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    private final static float YAW = -90.0f;
    private final static float PITCH = 0.0f;
    private final static float SPEED = 3.0f;
    private final static float SENSITIVITY = 0.05f;
    private final static float ZOOM = 45.0f;

    private Vec3 position;
    private Vec3 front;
    private Vec3 up;
    private Vec3 right;
    private Vec3 worldUp;
    private float yaw;
    private float pitch;
    private float movementSpeed;
    private float sensitivity;
    private float zoom;

    public Camera() {
        this(new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 1.0f, 0.0f), YAW, PITCH);
    }

    public Camera(Vec3 position) {
        this(position, new Vec3(0.0f, 1.0f, 0.0f), YAW, PITCH);
    }

    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this(new Vec3(posX, posY, posZ), new Vec3(upX, upY, upZ), YAW, PITCH);
    }

    public Camera(Vec3 position, Vec3 up, float yaw, float pitch) {
        this.position = position;
        this.up = up;
        this.yaw = yaw;
        this.pitch = pitch;
        this.front = new Vec3(0.0f, 0.0f, -1.0f);
        this.movementSpeed = SPEED;
        this.sensitivity = SENSITIVITY;
        this.zoom = ZOOM;
        updateCameraVectors();
    }

    public Mat4 getViewMatrix() {
        float[] v = new float[16];
        Vec3 center = this.position.add(this.front);
        Matrix.setLookAtM(v, 0, position.getX(), position.getY(), position.getZ(),
                center.getX(), center.getY(), center.getZ(),
                up.getX(), up.getY(), up.getZ());
        return new Mat4(v);
    }

    // Calculates the front vector from the Camera's (updated) Eular Angles
    public void updateCameraVectors() {
        Vec3 v = new Vec3((float) (Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch))),
            (float) Math.sin(Math.toRadians(this.pitch)),
            (float) (Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch))));
        this.front = v.normalize();
        this.right = this.front.cross(this.up).normalize();
        this.up = this.right.cross(this.front).normalize();
    }

    public float getZoom() {
        return this.zoom;
    }

}

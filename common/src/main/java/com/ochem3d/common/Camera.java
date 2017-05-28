package com.ochem3d.common;

import android.opengl.Matrix;

import com.ochem3d.glm.GLMUtils;
import com.ochem3d.glm.Mat4;
import com.ochem3d.glm.Vec3;

public class Camera {
    public static final float ZOOM = 3.0f;

    private enum View {
        LEFT, RIGHT, TOP, BOTTOM, FRONT, BACK, NONE
    }

    private Vec3 position;
    private Vec3 center;
    private Vec3 up;
    private View view = View.NONE;

    public Camera(Vec3 position) {
        this(position, new Vec3(0.0f, 0.0f, 0.0f), new Vec3(0.0f, 1.0f, 0.0f));
    }

    public Camera(float posX, float posY, float posZ, float centerX, float centerY, float centerZ,
                  float upX, float upY, float upZ) {
        this(new Vec3(posX, posY, posZ), new Vec3(centerX, centerY, centerZ), new Vec3(upX, upY, upZ));
    }

    public Camera(Vec3 position, Vec3 center, Vec3 up) {
        setPosition(position);
        setUp(up);
        setCenter(center);
    }

    public Mat4 getViewMatrix() {
        float[] v = new float[16];
        Matrix.setLookAtM(v, 0, position.getX(), position.getY(), position.getZ(),
                center.getX(), center.getY(), center.getZ(),
                up.getX(), up.getY(), up.getZ());
        return new Mat4(v);
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getCenter() {
        return center;
    }

    public void setCenter(Vec3 center) {
        this.center = center;
    }

    public Vec3 getUp() {
        return up;
    }

    public void setUp(Vec3 up) {
        this.up = up;
    }

    public void topView() {
        topView(ZOOM);
    }

    public void bottomView() {
        bottomView(ZOOM);
    }

    public void leftView() {
        leftView(ZOOM);
    }

    public void rightView() {
        rightView(ZOOM);
    }

    public void frontView() {
        frontView(ZOOM);
    }

    public void backView() {
        backView(ZOOM);
    }

    public void topView(float zoom) {
        setPosition(center.add(new Vec3(0.0f, zoom, 0.0f)));
        setUp(new Vec3(0.0f, 0.0f, 1.0f));
        view = View.TOP;
    }

    public void bottomView(float zoom) {
        setPosition(center.add(new Vec3(0.0f, -zoom, 0.0f)));
        setUp(new Vec3(0.0f, 0.0f, 1.0f));
        view = View.BOTTOM;
    }

    public void leftView(float zoom) {
        setPosition(center.add(new Vec3(-zoom, 0.0f, 0.0f)));
        setUp(new Vec3(0.0f, 1.0f, 0.0f));
        view = View.LEFT;
    }

    public void rightView(float zoom) {
        setPosition(center.add(new Vec3(zoom, 0.0f, 0.0f)));
        setUp(new Vec3(0.0f, 1.0f, 0.0f));
        view = View.RIGHT;
    }

    public void frontView(float zoom) {
        setPosition(center.add(new Vec3(0.0f, 0.0f, zoom)));
        setUp(new Vec3(0.0f, 1.0f, 0.0f));
        view = View.FRONT;
    }

    public void backView(float zoom) {
        setPosition(center.add(new Vec3(0.0f, 0.0f, -zoom)));
        setUp(new Vec3(0.0f, 1.0f, 0.0f));
        view = View.BACK;
    }

    public void moveCamera(Vec3 delta) {
        setPosition(this.position.add(delta));
    }

    public void moveCamera(float dx, float dy) {
        if( isTopView() ) {
            moveCamera(new Vec3(dx, 0.0f, dy));
        } else if( isBottomView() ) {
            moveCamera(new Vec3(dx, 0.0f, dy));
        } else if( isLeftView() ) {
            moveCamera(new Vec3(0.0f, dx, dy));
        } else if( isRightView() ) {
            moveCamera(new Vec3(0.0f, dx, dy));
        } else if( isFrontView() ) {
            moveCamera(new Vec3(dx, dy, 0.0f));
        } else if( isBackView() ) {
            moveCamera(new Vec3(dx, dy, 0.0f));
        }
    }

    public void zoom(float zoom) {
//        moveCamera(new Vec3(this.center.subtract(this.position).length() / zoom));
    }

    public boolean isTopView() {
        return view == View.TOP;
    }

    public boolean isBottomView() {
        return view == View.BOTTOM;
    }

    public boolean isLeftView() {
        return view == View.LEFT;
    }

    public boolean isRightView() {
        return view == View.RIGHT;
    }

    public boolean isFrontView() {
        return view == View.FRONT;
    }

    public boolean isBackView() {
        return view == View.BACK;
    }

}

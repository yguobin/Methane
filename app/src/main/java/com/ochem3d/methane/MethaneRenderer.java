package com.ochem3d.methane;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

import com.ochem3d.common.Camera;
import com.ochem3d.common.Shader;
import com.ochem3d.glm.GLMUtils;
import com.ochem3d.glm.Mat4;
import com.ochem3d.glm.Transform;
import com.ochem3d.glm.Vec3;
import com.ochem3d.shape.Cylinder;
import com.ochem3d.shape.Sphere;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class MethaneRenderer implements GLSurfaceView.Renderer {
    private final static float CAMERA_MOVE_INC = 1.0f;

    private Context context;
    private int width;
    private int height;

    private Shader shader;
    private Cylinder cylinder;
    private Sphere sphere;
    private int[] VAO = new int[2]; // 0: cylinderVAO, 1: sphereVAO
    private int[] VBO = new int[4]; // 0: cylinderPosVBO, 1: cylinderNormalVBO
                                    // 2: spherePosVBO, 3: sphereNormalVBO
    private Vec3 lightPos;
    private Camera camera;
    private Vec3 cylinderSize;
    private Vec3 cylinderColor;
    private Vec3 sphereCenterSize;
    private Vec3 sphereOuterSize;
    private Vec3 sphereUpColor;
    private Vec3 sphereCenterColor;
    private Vec3 sphereRightBottomColor;
    private Vec3 sphereLeftFrontBottomColor;
    private Vec3 sphereLeftBackBottomColor;

    private int modelLoc;
    private int viewLoc;
    private int projLoc;
    private int colorLoc;
    private int lightColorLoc;
    private int lightLocationLoc;
    private int viewLocationLoc;

    private Mat4 view, projection;
    private Vec3 lightColor, viewLocation;

    MethaneRenderer(Context context) {
        this.context = context;
    }

    private void initCylinder() {
        this.cylinder = new Cylinder();

        GLES30.glGenVertexArrays(1, VAO, 0);
        // Bind the Vertex Array Object first, then bind and set vertex buffer(s) and attribute pointer(s).
        GLES30.glBindVertexArray(VAO[0]);

        GLES30.glGenBuffers(1, VBO, 0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                this.cylinder.vertices.length * 4, GLMUtils.toBuffer(this.cylinder.vertices), GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glGenBuffers(1, VBO, 1);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                this.cylinder.vertices.length * 4, GLMUtils.toBuffer(this.cylinder.normals), GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(1);

        // Position attribute
        GLES30.glBindVertexArray(0); // Unbind VAO
    }

    private void initSphere() {
        this.sphere = new Sphere();

        GLES30.glGenVertexArrays(1, VAO, 1);
        // Bind the Vertex Array Object first, then bind and set vertex buffer(s) and attribute pointer(s).
        GLES30.glBindVertexArray(VAO[1]);

        GLES30.glGenBuffers(1, VBO, 2);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[2]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                this.sphere.vertices.length * 4, GLMUtils.toBuffer(sphere.vertices), GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glGenBuffers(1, VBO, 3);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, VBO[3]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                this.sphere.vertices.length * 4, GLMUtils.toBuffer(sphere.normals), GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(1);

        // Position attribute
        GLES30.glBindVertexArray(0); // Unbind VAO
    }

    private void getUniformLocations() {
        modelLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "model");
        viewLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "view");
        projLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "projection");

        colorLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "objectColor");
        lightColorLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "lightColor");
        lightLocationLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "lightPos");
        viewLocationLoc = GLES30.glGetUniformLocation(shader.getProgramObject(), "viewPos");
    }

    private void drawSphere(Vec3 position, Vec3 size, Vec3 color) {
        GLES30.glBindVertexArray(VAO[1]);
        Mat4 model = new Mat4();
        model = Transform.translate(model, position);
        model = Transform.scale(model, size);
        GLES30.glUniformMatrix4fv(modelLoc, 1, false, model.getBuffer());
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, view.getBuffer());
        GLES30.glUniformMatrix4fv(projLoc, 1, false, projection.getBuffer());
        GLES30.glUniform3fv(colorLoc, 1, color.getBuffer());
        GLES30.glUniform3fv(lightColorLoc, 1, lightColor.getBuffer());
        GLES30.glUniform3fv(lightLocationLoc, 1, lightPos.getBuffer());
        GLES30.glUniform3fv(viewLocationLoc, 1, viewLocation.getBuffer());
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, sphere.indices.length,
                GLES30.GL_UNSIGNED_INT, GLMUtils.toBuffer(sphere.indices));
        GLES30.glBindVertexArray(0);
    }

    private void drawCylinder(Mat4 model) {
        GLES30.glBindVertexArray(VAO[0]);
        GLES30.glUniformMatrix4fv(modelLoc, 1, false, model.getBuffer());
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, view.getBuffer());
        GLES30.glUniformMatrix4fv(projLoc, 1, false, projection.getBuffer());
        GLES30.glUniform3fv(colorLoc, 1, cylinderColor.getBuffer());
        GLES30.glUniform3fv(lightColorLoc, 1, lightColor.getBuffer());
        GLES30.glUniform3fv(lightLocationLoc, 1, lightPos.getBuffer());
        GLES30.glUniform3fv(viewLocationLoc, 1, viewLocation.getBuffer());
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, cylinder.indices.length,
                GLES30.GL_UNSIGNED_INT, GLMUtils.toBuffer(cylinder.indices));
        GLES30.glBindVertexArray(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE);

        lightPos = new Vec3(1.2f, 1.0f, 2.0f);
        camera = new Camera(new Vec3(-1.0f, 0.0f, 1.0f), new Vec3(0.0f, 0.0f, -1.0f), new Vec3(0.0f, 1.0f, 0.0f));
        camera.frontView();

        cylinderSize = new Vec3(0.03f, 0.03f, 0.75f);
        cylinderColor = new Vec3(15.0f / 255.0f, 18.0f / 255.0f, 57.0f / 255.0f);
        sphereCenterSize = new Vec3(0.25f);
        sphereOuterSize = new Vec3(0.15f);
        sphereUpColor = new Vec3(255.0f / 255.0f, 0.0f / 255.0f, 0.0f / 255.0f);
        sphereCenterColor = new Vec3(73.0f / 255.0f);
        sphereRightBottomColor = new Vec3(0.0f / 255.0f, 255.0f / 255.0f, 0.0f / 255.0f);
        sphereLeftFrontBottomColor = new Vec3(0.0f / 255.0f, 0.0f / 255.0f, 255.0f / 255.0f);
        sphereLeftBackBottomColor = new Vec3(0.0f / 255.0f, 255.0f / 255.0f, 255.0f / 255.0f);

        // Build and compile our shader program
        shader = new Shader(this.context, "VertexShader.sl", "FragmentShader.sl");
        shader.use();
        getUniformLocations();

        initCylinder();
        initSphere();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.height = height;
        this.width = width;
        Log.i("MethaneRenderer", "width: " + this.width + " height: " + this.height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float currentFrame = SystemClock.uptimeMillis() / 1000.0f;
        lightPos = new Vec3((float) (1.0f + Math.sin(currentFrame) * 2.0f),
                (float) (Math.sin(currentFrame / 2.0f) * 1.0f),
                lightPos.getZ());

        // Set the viewport
        GLES30.glViewport(0, 32, width, height);

        // Clear the color buffer
        GLES30.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        // Use the program object
        shader.use();

        view = camera.getViewMatrix();
        projection = Transform.perspective(new Mat4(), 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        lightColor = new Vec3(1.0f, 1.0f, 1.0f);
        viewLocation = new Vec3(0.0f, 0.0f, -3.0f);

        Vec3 locTop = new Vec3(0.0f, 0.75f, -1.0f);
        Vec3 locCenter = new Vec3(0.0f, 0.0f, -1.0f);
        Vec3 locRightBottom = new Vec3((float) (0.75f * Math.cos(Math.toRadians(-19.5f))), (float) (0.75f * Math.sin(Math.toRadians(-19.5f))), -1.0f);
        Vec3 locLeftFrontBottom = new Vec3((float) (0.75f * Math.cos(Math.toRadians(-19.5f)) * Math.cos(Math.toRadians(120.f))),
                (float) (0.75f * Math.sin(Math.toRadians(-19.5f))),
                (float) (0.75f * Math.cos(Math.toRadians(-19.5f)) * Math.sin(Math.toRadians(120.f)) - 1.0f));
        Vec3 locLeftBackBottom = new Vec3((float) (0.75f * Math.cos(Math.toRadians(-19.5f)) * Math.cos(Math.toRadians(-120.f))),
                (float) (0.75f * Math.sin(Math.toRadians(-19.5f))),
                (float) (0.75f * Math.cos(Math.toRadians(-19.5f)) * Math.sin(Math.toRadians(-120.f)) - 1.0f));
        Vec3 vUp = locTop.subtract(locCenter);
        Vec3 vRightBottom = locRightBottom.subtract(locCenter);
        Vec3 vLeftFrontBottom = locLeftFrontBottom.subtract(locCenter);
        Vec3 vLeftBackBottom = locLeftBackBottom.subtract(locCenter);
        Vec3 vCrossUpRightBottom = vUp.cross(vRightBottom);
        Vec3 vCrossUpLeftFrontBottom = vUp.cross(vLeftFrontBottom);
        Vec3 vCrossUpLeftBackBottom = vUp.cross(vLeftBackBottom);

        // draw top sphere
        drawSphere(locTop, sphereOuterSize, sphereUpColor);
        // draw center sphere
        drawSphere(locCenter, sphereCenterSize, sphereCenterColor);
        // draw right bottom sphere
        drawSphere(locRightBottom, sphereOuterSize, sphereRightBottomColor);
        // draw left front bottom sphere
        drawSphere(locLeftFrontBottom, sphereOuterSize, sphereLeftFrontBottomColor);
        // draw left back bottom sphere
        drawSphere(locLeftBackBottom, sphereOuterSize, sphereLeftBackBottomColor);

        Mat4 model;

        // draw cylinder between up and center spheres
        model = new Mat4();
        model = Transform.translate(model, 0.0f, 0.5f, -1.0f);
        model = Transform.rotate(model, 90.0f, 1.0f, 0.0f, 0.0f);
        model = Transform.scale(model, cylinderSize);
        drawCylinder(model);

        // draw cylinder between right bottom and center spheres
        model = new Mat4();
        model = Transform.translate(model, 0.0f, 0.0f, -1.0f);
        model = Transform.rotate(model, 109.5f, vCrossUpRightBottom);
        model = Transform.translate(model, 0.0f, 0.375f, 0.0f);
        model = Transform.rotate(model, 90.0f, 1.0f, 0.0f, 0.0f);
        model = Transform.scale(model, cylinderSize);
        drawCylinder(model);

        // draw cylinder between left front bottom and center spheres
        model = new Mat4();
        model = Transform.translate(model, 0.0f, 0.0f, -1.0f);
        model = Transform.rotate(model, 109.5f, vCrossUpLeftFrontBottom);
        model = Transform.translate(model, 0.0f, 0.375f, 0.0f);
        model = Transform.rotate(model, 90.0f, 1.0f, 0.0f, 0.0f);
        model = Transform.scale(model, cylinderSize);
        drawCylinder(model);

        // draw cylinder between left back bottom and center spheres
        model = new Mat4();
        model = Transform.translate(model, 0.0f, 0.0f, -1.0f);
        model = Transform.rotate(model, 109.5f, vCrossUpLeftBackBottom);
        model = Transform.translate(model, 0.0f, 0.375f, 0.0f);
        model = Transform.rotate(model, 90.0f, 1.0f, 0.0f, 0.0f);
        model = Transform.scale(model, cylinderSize);
        drawCylinder(model);

    }

    public void moveCamera(String s) {
        switch(s) {
            case "Left":
                this.camera.leftView();
                break;
            case "Right":
                this.camera.rightView();
                break;
            case "Top":
                this.camera.topView();
                break;
            case "Bottom":
                this.camera.bottomView();
                break;
            case "Front":
                this.camera.frontView();
                break;
            case "Back":
                this.camera.backView();
                break;
        }
    }

    public void moveCamera(float dx, float dy) {
        camera.moveCamera(dx, dy);
    }

    public void zoomCamera(float scaleFactor) {
        camera.zoom(scaleFactor);
    }
}

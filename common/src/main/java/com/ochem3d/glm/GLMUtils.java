package com.ochem3d.glm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLMUtils {
    public static final float ZERO_RANGE = 0.00001f;

    public static FloatBuffer toBuffer(float[] data) {
        FloatBuffer buffer = ByteBuffer.allocateDirect ( data.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    public static IntBuffer toBuffer(int[] data) {
        IntBuffer buffer = ByteBuffer.allocateDirect ( data.length * 4 )
                .order ( ByteOrder.nativeOrder() ).asIntBuffer();
        buffer.put(data).position(0);
        return buffer;
    }

    public static boolean isZero(float f) {
        return Math.abs(f) < ZERO_RANGE;
    }
}

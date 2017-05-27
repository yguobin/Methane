package com.ochem3d.common;

import android.content.Context;
import android.opengl.GLES30;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Shader {
    private int vertexShader, fragmentShader;
    private int programObject;

    public Shader(Context context, String vertexSrcFile, String fragmentSrcFile) {
        vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, readShader(context, vertexSrcFile));
        fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, readShader(context, fragmentSrcFile));
        loadProgram();
    }

    public void use() {
        GLES30.glUseProgram(this.programObject);
    }

    public int getProgramObject() {
        return this.programObject;
    }

    public void close() {
        if( vertexShader != 0 ) GLES30.glDeleteShader ( vertexShader );
        if( fragmentShader != 0 ) GLES30.glDeleteShader ( fragmentShader );
        if( programObject != 0 ) GLES30.glDeleteProgram( programObject );
    }

    private static String readShader ( Context context, String fileName ) {
        if ( context == null || fileName == null ) {
            throw new IllegalArgumentException("context or file name is null");
        }

        // Read the shader file from assets
        InputStream is = null;
        OutputStream os = null;
        try {
            is =  context.getAssets().open(fileName);
            byte[] buffer = new byte[is.available()];
            is.read ( buffer );
            os = new ByteArrayOutputStream();
            os.write ( buffer );
            return os.toString();
        } catch ( IOException ioe ) {
            throw new RuntimeException("Unable to read " + fileName);
        } finally {
            if( is != null ) try { is.close(); } catch(IOException ignore) {}
            if( os != null ) try { os.close(); } catch(IOException ignore) {}
        }
    }

    private static int loadShader ( int type, String shaderSrc ) {
        int shader;
        int[] compiled = new int[1];

        shader = GLES30.glCreateShader (type);
        if ( shader == 0 ) {
            throw new RuntimeException("Unable to create shader of type: " + type);
        }
        GLES30.glShaderSource ( shader, shaderSrc );
        GLES30.glCompileShader ( shader );
        GLES30.glGetShaderiv ( shader, GLES30.GL_COMPILE_STATUS, compiled, 0 );
        if ( compiled[0] == 0 ) {
            GLES30.glDeleteShader ( shader );
            throw new RuntimeException("Unable to compile shader of type: " + type + " error: "
                    + GLES30.glGetShaderInfoLog ( shader ));
        }
        return shader;
    }

    private void loadProgram () {
        int[] linked = new int[1];

        programObject = GLES30.glCreateProgram();
        if (programObject == 0) {
            throw new RuntimeException("Unable to create program");
        }

        GLES30.glAttachShader(programObject, vertexShader);
        GLES30.glAttachShader(programObject, fragmentShader);
        GLES30.glLinkProgram(programObject);
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            throw new RuntimeException("Failed to link the program. Error: "
                    + GLES30.glGetProgramInfoLog(programObject));
        }
    }
}

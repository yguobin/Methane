package com.ochem3d.methane;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private final int CONTEXT_CLIENT_VERSION = 3;

    private GLSurfaceView mGLSurfaceView;
    private MethaneRenderer renderer;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mGLSurfaceView = new GLSurfaceView( this );
        setContentView(R.layout.activity_main);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.toggleGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int j = 0; j < radioGroup.getChildCount(); j++) {
                    final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                    view.setChecked(view.getId() == checkedId);
                }
            }
        });
        if ( detectOpenGLES30() ) {
            // Tell the surface view we want to create an OpenGL ES 3.0-compatible
            // context, and set an OpenGL ES 3.0-compatible renderer.
            mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
            mGLSurfaceView.setEGLContextClientVersion ( CONTEXT_CLIENT_VERSION );
            renderer = new MethaneRenderer(this);
            mGLSurfaceView.setRenderer(renderer);
            scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
            mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return onTouchGLSurfaceView(event);
                }
            });
        }
        else {
            Log.e ("Methane", "OpenGL ES 3.0 not supported on device.  Exiting..." );
            finish();
        }
//        setContentView ( mGLSurfaceView );
    }

    private float previousX = -1.0f, previousY = -1.0f;

    private boolean onTouchGLSurfaceView(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if( previousX > 0 && previousY > 0 ) {
                    float dx = x - previousX, dy = y - previousY;
                    renderer.moveCamera(dx / 50.0f, dy / 50.0f);
                }
            default:
                scaleGestureDetector.onTouchEvent(event);
        }
        previousX = x; previousY = y;
        return true;
    }

    private boolean detectOpenGLES30()
    {
        ActivityManager am =
                (ActivityManager) getSystemService ( Context.ACTIVITY_SERVICE );
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return ( info.reqGlEsVersion >= 0x30000 );
    }

    @Override
    protected void onResume()
    {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public void onButtonClick(View view) {
        ((RadioGroup)view.getParent()).check(view.getId());
        final ToggleButton button = (ToggleButton) view;
        String action = (String) button.getTag();
        renderer.moveCamera(action);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor() / 10.0f;
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            renderer.zoomCamera(scaleFactor);
            return true;
        }

    }
}

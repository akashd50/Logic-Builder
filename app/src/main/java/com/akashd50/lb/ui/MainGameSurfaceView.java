package com.akashd50.lb.ui;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import com.akashd50.lb.logic.TouchController;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class MainGameSurfaceView extends GLSurfaceView {
    private static MainGameRenderer mRenderer;
    public static TouchController touchController;

    public MainGameSurfaceView(Context context, int bID, String name, int x){
        super(context);

        touchController = new TouchController();
        //this.setEGLConfigChooser(8,8,8,8,24,8);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;

        this.setEGLContextClientVersion(3);
        this.setSystemUiVisibility(uiOptions);

         /* this.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 24, EGL10.EGL_RED_SIZE, 10,
                        EGL10.EGL_BLUE_SIZE,10,EGL10.EGL_GREEN_SIZE,10, EGL10.EGL_NONE };
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });*/

        this.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });

        mRenderer = new MainGameRenderer(context, touchController, bID, name, x);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }

    @Override
    public void onPause() {
        mRenderer.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        this.setSystemUiVisibility(uiOptions);
        mRenderer.onResume();
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                touchController.touchDown(event);
                mRenderer.onTouchDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                touchController.extraPointerDown(event);
                mRenderer.onTouchDown(event);
                break;
            case MotionEvent.ACTION_UP:
                touchController.touchUp(event);
                mRenderer.onTouchUp(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                touchController.extraPointerUp();
                mRenderer.onTouchUp(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchController.touchMovement(event);
                mRenderer.onTouchMove(event);
                break;
        }
        return true;
    }

    public void onBackPressed() {
        mRenderer.onBackPressed();
    }
}

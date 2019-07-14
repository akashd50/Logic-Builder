package com.akashd50.lb.objects;

import android.view.MotionEvent;

public interface Clickable {
    void onTouchDown(MotionEvent event);
    void onTouchUp(MotionEvent event);
    void onTouchMove(MotionEvent event);
    void onDrawFrame(float[] mMVPMatrix);
    int getID();
}

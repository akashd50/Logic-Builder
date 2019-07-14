package com.akashd50.lb.objects;

import android.view.MotionEvent;

public interface TouchListener {
    void onTouchDown(MotionEvent event, Clickable c);
    void onTouchUp(MotionEvent event, Clickable c);
    void onTouchMove(MotionEvent event, Clickable c);
}

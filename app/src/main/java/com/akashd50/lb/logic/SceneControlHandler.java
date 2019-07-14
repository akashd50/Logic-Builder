package com.akashd50.lb.logic;

import android.view.MotionEvent;

import com.akashd50.lb.objects.Clickable;
import com.akashd50.lb.objects.Controller;

import java.util.ArrayList;

public class SceneControlHandler {
    private ArrayList<Clickable> controllers;
    public SceneControlHandler(){
        controllers = new ArrayList<>();
    }

    public void addController(Clickable c){
        controllers.add(c);
    }

    public void onDrawFrame(float[] mMVPMatrix){
        for(Clickable c: controllers){
            c.onDrawFrame(mMVPMatrix);
        }
    }

    public void onTouchDown(MotionEvent event){
        for(Clickable c: controllers){
            c.onTouchDown(event);
        }
    }

    public void onTouchUp(MotionEvent event){
        for(Clickable c: controllers){
            c.onTouchUp(event);
        }
    }


    public void onTouchMove(MotionEvent event){
        for(Clickable c: controllers){
            c.onTouchMove(event);
        }
    }
}

package com.akashd50.lb.objects;

import android.view.MotionEvent;

public abstract class Dialog implements Clickable{
    protected String title;
    protected Button background;
    protected TouchListener touchListener;
    protected SimpleVector dialogLocation, dimensions;
    protected boolean isShowing;
    protected Dialog parent;

    public abstract void onTouchDown(MotionEvent event);
    public abstract void onTouchUp(MotionEvent event);
    public abstract void onTouchMove(MotionEvent event);
    public abstract void onDrawFrame(float[] mMVPMatrix);
    public abstract int getID();

    public void showing(boolean b){
        isShowing = b;
    }
    public boolean isShowing() {
        return isShowing;
    }
    public boolean wasClicked(){
        boolean wasClicked = background.wasClicked();
        background.resetWasClicked();
        return wasClicked;
    }

    public boolean isClicked(){return background.isClicked();}
    public void setParent(SelectionDialog s){
        this.parent = s;
    }
    public Dialog getParent(){return parent;}
    public Button getBackground(){return this.background;}
}

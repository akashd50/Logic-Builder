package com.akashd50.lb.objects;

import android.content.Context;
import android.view.MotionEvent;
import com.akashd50.lb.R;
import java.util.ArrayList;

public class SelectionDialog implements Clickable{
    private ArrayList<Button> options;
    private Button background;
    private TouchListener touchListener;
    private SimpleVector dialogLocation;
    private SimpleVector nextOptionLocation;
    private boolean isShowing;
    private SelectionDialog parent;

    public SelectionDialog(Context c){
        options = new ArrayList<>();
        dialogLocation = new SimpleVector(-0.2f,1.5f,3f);
        nextOptionLocation = new SimpleVector(-0.8f,1.8f,3.5f);

        background = new Button(R.drawable.empty_board,new SimpleVector(1.6f,1.0f,1f),c);
        background.setLocation(dialogLocation);
    }

    public void addOption(Button b){
        b.setLocation(new SimpleVector(nextOptionLocation.x, nextOptionLocation.y, nextOptionLocation.z));
        nextOptionLocation.x+=0.3f;
        if(nextOptionLocation.x > 0.6f){
            nextOptionLocation.y -= 0.3f;
            nextOptionLocation.x = -0.8f;
        }
        options.add(b);
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        if(isShowing) {
            background.onDrawFrame(mMVPMatrix);
            for (Button b : options) {
                b.onDrawFrame(mMVPMatrix);
            }
        }
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        if(isShowing) {
            for (Button b : options) {
                b.onTouchDown(event);
            }
            /*if(!background.isClicked()){
                isShowing = false;
            }*/
        }
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        if(isShowing) {
            for (Button b : options) {
                b.onTouchUp(event);
            }
            background.onTouchUp(event);
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        if(isShowing) {
            for (Button b : options) {
                b.onTouchMove(event);
            }
        }
    }

    public void setTouchListener(TouchListener t){
        this.touchListener = t;
        for(Button b: options){
            b.setListener(touchListener);
        }
    }

    public void deSelectRest(Button b){
        for(Button button: options){
            if(b.getID()!=button.getID()) button.resetWasClicked();
        }
    }

    public void deSelectAll(){
        for(Button button: options){
            button.resetWasClicked();
        }
    }

    public void showing(boolean b){
        isShowing = b;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public int getID(){
        return 9999;
    }

    public boolean wasClicked(){
        boolean wasClicked = background.wasClicked();
        background.resetWasClicked();
        return wasClicked;
    }

    public void setParent(SelectionDialog s){
        this.parent = s;
    }
    public SelectionDialog getParent(){return parent;}
}

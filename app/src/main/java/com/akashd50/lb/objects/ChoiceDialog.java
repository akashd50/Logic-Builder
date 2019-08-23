package com.akashd50.lb.objects;

import android.content.Context;
import android.view.MotionEvent;
import com.akashd50.lb.R;
import java.util.ArrayList;

public class ChoiceDialog extends Dialog implements Clickable{
    private Button positiveButton, negativeButton, titleButton;
    public ChoiceDialog(SimpleVector dimensions, SimpleVector location, Context c){
        this.dimensions = dimensions;
        dialogLocation = location;

        positiveButton = new Button(new SimpleVector(0.4f,0.2f,1f),c);
        negativeButton = new Button(new SimpleVector(0.4f,0.2f,1f),c);
        titleButton = new Button(new SimpleVector(1.6f,0.3f,1f),c);

        positiveButton.setLocation(new SimpleVector(location.x + dimensions.x/2 - 0.4f,
                location.y - dimensions.y/2 + 0.2f, location.z+0.5f));
        negativeButton.setLocation(new SimpleVector(location.x - dimensions.x/2 + 0.4f,
                location.y - dimensions.y/2 + 0.2f, location.z+0.5f));
        titleButton.setLocation(new SimpleVector(location.x,
                location.y + dimensions.y/2 - 0.3f, location.z+0.5f));

        background = new Button(R.drawable.empty_board,dimensions,c);
        background.setLocation(dialogLocation);
    }

    public void setPositiveButton(Texture texture){
        positiveButton.setButtonTexture(texture);
    }

    public void setNegativeButton(Texture texture){
        negativeButton.setButtonTexture(texture);
    }

    public void setTitleButton(Texture texture){
        titleButton.setButtonTexture(texture);
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        if(isShowing) {
            background.onDrawFrame(mMVPMatrix);
            positiveButton.onDrawFrame(mMVPMatrix);
            negativeButton.onDrawFrame(mMVPMatrix);
            titleButton.onDrawFrame(mMVPMatrix);
        }
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        if(isShowing) {
            background.onTouchDown(event);
        }
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        if(isShowing) {
            background.onTouchUp(event);
            background.resetSelected();
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        if(isShowing) {
            if(background.isClicked()){
            }
        }
    }

    public void setTouchListener(TouchListener t){
        this.touchListener = t;

        //background.setListener(touchListener);
    }

    public int getID(){
        return 9999;
    }
}

package com.akashd50.lb.objects;

import android.content.Context;
import android.view.MotionEvent;

import com.akashd50.lb.R;
import com.akashd50.lb.utils.Shader;

import org.w3c.dom.Text;

public class Button extends Controller {
    private Quad2D buttonIcon, buttonSelectedBoundary, buttonTitle;
    private SimpleVector dimensions, location;
    private boolean isClicked, wasClicked, selected, isLongPressed, titleTextureSet;
    private long eventDownTime;
    private int integerTag;
    private String stringTag;
    public Button(int resId, SimpleVector dimensions, Context c){
        Texture t1 = new Texture("loadT", c, resId);
        Texture t2 = new Texture("s", c, R.drawable.selectedboundary);
        int quadProgram = Shader.getQuadTextureProgram();

        buttonSelectedBoundary = new Quad2D(dimensions.x, dimensions.y);
        buttonSelectedBoundary.setRenderPreferences(quadProgram, Quad2D.REGULAR);
        buttonSelectedBoundary.setTextureUnit(t2);

        buttonIcon = new Quad2D(dimensions.x, dimensions.y);
        buttonIcon.setRenderPreferences(quadProgram, Quad2D.REGULAR);
        buttonIcon.setTextureUnit(t1);

        buttonTitle = new Quad2D(dimensions.x, dimensions.y/3);
        buttonTitle.setRenderPreferences(quadProgram, Quad2D.REGULAR);


        location = new SimpleVector(0f,0f,0f);
        this.dimensions = new SimpleVector(dimensions.x,dimensions.y,1f);
        isClicked = false;
        wasClicked = false;
        selected = false;
        isLongPressed = false;

        buttonIcon.setOpacity(1.0f);
        buttonSelectedBoundary.setOpacity(1.0f);
        buttonTitle.setOpacity(1.0f);

        buttonTitle.setDefaultLocation(new SimpleVector(location.x, location.y - dimensions.y/2 - dimensions.y/3, location.z));

        cID = Controller.getNextID();
    }

    public Button(SimpleVector dimensions, Context c){
        Texture t2 = new Texture("s", c, R.drawable.selectedboundary);
        int quadProgram = Shader.getQuadTextureProgram();

        buttonSelectedBoundary = new Quad2D(dimensions.x, dimensions.y);
        buttonSelectedBoundary.setRenderPreferences(quadProgram, Quad2D.REGULAR);
        buttonSelectedBoundary.setTextureUnit(t2);

        buttonIcon = new Quad2D(dimensions.x, dimensions.y);
        buttonIcon.setRenderPreferences(quadProgram, Quad2D.REGULAR);

        buttonTitle = new Quad2D(dimensions.x, dimensions.y/3);
        buttonTitle.setRenderPreferences(quadProgram, Quad2D.REGULAR);

        location = new SimpleVector(0f,0f,0f);
        this.dimensions = new SimpleVector(dimensions.x,dimensions.y,1f);
        isClicked = false;
        wasClicked = false;

        buttonIcon.setOpacity(1.0f);
        buttonSelectedBoundary.setOpacity(1.0f);
        buttonTitle.setOpacity(1.0f);

        cID = Controller.getNextID();
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        buttonIcon.draw(mMVPMatrix);
        if(selected) buttonSelectedBoundary.draw(mMVPMatrix);
        if(titleTextureSet) buttonTitle.draw(mMVPMatrix);
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        int index = event.getActionIndex();
        if(index!=-1 && buttonIcon.isClicked(event.getX(index),event.getY(index))){
            isClicked = true;
            this.activeMotionEvent = event;
            activeMEId = event.getPointerId(index);
            eventDownTime = System.currentTimeMillis();
            if(listener!=null) listener.onTouchDown(event, this);
        }else{
            isClicked = false;
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        int index = event.getActionIndex();
        if(index!=-1){
            if(!buttonIcon.isClicked(event.getX(index),event.getY(index))) {
               isClicked = false;
               isLongPressed();
            }
        }

        if(listener!=null) listener.onTouchMove(event, this);
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        int index = event.getActionIndex();
        if(index!=-1) {
            if (buttonIcon.isClicked(event.getX(index), event.getY(index)) && !isLongPressed()) {
                isClicked = false;
                activeMotionEvent = null;
                wasClicked = true;
                selected = !selected;
                if(listener!=null) listener.onTouchUp(event, this);
            }
        }else{
            isClicked = false;
            activeMotionEvent = null;
            wasClicked = false;
        }
        isLongPressed = false;
    }

    public void setLocation(SimpleVector location) {
        this.location = location;
        buttonIcon.setDefaultLocation(new SimpleVector(location.x, location.y, location.z));
        buttonSelectedBoundary.setDefaultLocation(new SimpleVector(location.x, location.y, location.z+0.5f));
        buttonTitle.setDefaultLocation(new SimpleVector(location.x, location.y - dimensions.y/2 - dimensions.y/3, location.z));
    }

    public boolean isClicked(){return isClicked;}

    public boolean isClicked(float x, float y){return buttonIcon.isClicked(x,y);}

    public boolean wasClicked(){
        return wasClicked;
    }

    public void resetWasClicked(){wasClicked = false;}
    public void resetSelected(){selected = false;}

    public SimpleVector getLocation() {
        return location;
    }

    public boolean isLongPressed(){
        if(activeMotionEvent!=null){
            int index = activeMotionEvent.getActionIndex();
            if(index!=-1) {
                if (buttonIcon.isClicked(activeMotionEvent.getX(index),
                        activeMotionEvent.getY(index)) &&
                        System.currentTimeMillis() - eventDownTime > 1000) {
                    isLongPressed = true;
                }
            }
        }
        return isLongPressed;
    }

    public void rotateZ(float z){
        buttonIcon.rotateZ(z);
    }

    public int getTexture(){
        return buttonIcon.getTextureUnit().getTexture();
    }
    public Texture getTextureUnit(){return buttonIcon.getTextureUnit();}
    public void setButtonTexture(Texture t){buttonIcon.setTextureUnit(t);}
    public void setButtonTitleTexture(Texture t){
        buttonTitle.setTextureUnit(t);
        titleTextureSet = true;
    }

    public int getIntegerTag(){
        return integerTag;
    }
    public String getStringTag(){
        return stringTag;
    }

    public void setTag(int i){
        this.integerTag = i;
    }
    public void setTag(String i){
        this.stringTag = i;
    }
}

package com.akashd50.lb.objects;

import android.opengl.Matrix;

import com.akashd50.lb.logic.TouchController;

public class Camera {
    private TouchController touchController;
    private float[] viewMatrix;
    private float[] projectionMatrix;
    private float[] mMVPMatrix;
    private float left, right, top, bottom;
    private SimpleVector cameraRotation, followSpeed, followDelay;
    private float cameraZoomValue;

    private SimpleVector distanceFromOrigin;
    private boolean simulateCCUMovement;
    private float width, height;
    public Camera(){
        cameraRotation = new SimpleVector(0f,0f,0f);
        distanceFromOrigin = new SimpleVector(0f,0f,5f);
        followSpeed = new SimpleVector(0f,0f,0f);
        followDelay = new SimpleVector(0f,0f,0f);
        simulateCCUMovement = false;
        cameraZoomValue = 1f;

    }


    public void setMatrices(float[] vm, float[] pm, float[] mvp){
        viewMatrix = vm;
        projectionMatrix = pm;
        mMVPMatrix = mvp;
    }

    public void setAdditionalParms(float w, float h){
        width = w;
        height = h;
        float ratio = (float)height/(float)width;
        left = -1.0f; right = 1.0f;
        top = ratio; bottom = -ratio;
    }

    public void setTouchController(TouchController tc){
        this.touchController = tc;
    }

    public void lookAt(SimpleVector lookAt){
        cameraRotation.x=lookAt.x;
        cameraRotation.y=lookAt.y;
        cameraRotation.z=lookAt.z;
    }

    public void updateView(){
        /*if(followingObject!=null){
            SimpleVector loc = followingObject.getLocation();
            this.lookAt(new SimpleVector(loc.x, loc.y,loc.z));
            if(loc.x - distanceFromOrigin.x > followDelay.x) {
                distanceFromOrigin.x += followSpeed.x;
                simulateCCUMovement = true;
            }else if(loc.x - distanceFromOrigin.x < -followDelay.x) {
                distanceFromOrigin.x -= followSpeed.x;
                simulateCCUMovement = true;
            }else if(simulateCCUMovement) {
                if(loc.x - distanceFromOrigin.x < followDelay.x){
                    distanceFromOrigin.x+= followSpeed.x * (loc.x - distanceFromOrigin.x)/10;
                }else if(loc.x - distanceFromOrigin.x > -followDelay.x) {
                    distanceFromOrigin.x -= followSpeed.x * (loc.x - distanceFromOrigin.x)/10;
                }
            }

        }*/

        SimpleVector upVector = new SimpleVector(0f,0f,0f);
        if(distanceFromOrigin.z>0){
            upVector.y = 1f;
        }else{
            upVector.z = 1f;
        }

        Matrix.setLookAtM(viewMatrix, 0,distanceFromOrigin.x,distanceFromOrigin.y, distanceFromOrigin.z,
                cameraRotation.x,cameraRotation.y,cameraRotation.z,
                upVector.x, upVector.y, upVector.z);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    public float[] getViewMatrix(){return viewMatrix;}

    public void doCircularShow(){
        SimpleVector controllerMod = new SimpleVector();
        controllerMod.x = touchController.getTouchX() - touchController.getTouchPrevX();
        controllerMod.y = touchController.getTouchY() - touchController.getTouchPrevY();

    }

    public void updatePinchZoom(){
        //distanceFromOrigin.z = distanceFromOrigin.z - (touchController.PINCH * 0.5f);
        float ratio = (float)height/(float)width;
        float pinchZoom = 0.1f;
        if(touchController.PINCH < -1.0) {
            left-=pinchZoom;
            right+=pinchZoom;
            top+=pinchZoom* ratio;
            bottom-=pinchZoom* ratio;
        }else if(touchController.PINCH > 1.0){
            left+=pinchZoom;
            right-=pinchZoom;
            top-=pinchZoom* ratio;
            bottom+=pinchZoom* ratio;
        }

        cameraZoomValue = right * 0.5f;
        if(cameraZoomValue < 1.0){
            cameraZoomValue = 1.0f;
        }

        if(left!=right && top!=bottom) {
            Matrix.orthoM(projectionMatrix, 0, left, right,
                    bottom, top, 1, 100);
        }
    }

    public void updateSwipeMovement(){
        if(touchController.getTouchX()!=-1 && touchController.getTouchY()!=-1) {
            float cx = (touchController.getTouchX() - touchController.getTouchPrevX());
            float cy = (touchController.getTouchY() - touchController.getTouchPrevY());

            if (cx > 5) distanceFromOrigin.x = distanceFromOrigin.x - (cameraZoomValue * 0.03f);//(cx*0.001f);
            else if(cx<-5) distanceFromOrigin.x = distanceFromOrigin.x + (cameraZoomValue * 0.03f);//cx*0.001f;

            if (cy > 10) distanceFromOrigin.y = distanceFromOrigin.y + (cameraZoomValue * 0.03f);//cy*0.001f;
            else if(cy <-10)distanceFromOrigin.y = distanceFromOrigin.y - (cameraZoomValue * 0.03f);//cy*0.001f;

            this.lookAt(new SimpleVector(distanceFromOrigin.x, distanceFromOrigin.y, 0f));
        }
    }

    public void updateCameraRotation(SimpleVector cr){
        cameraRotation.x+=cr.x;
        cameraRotation.y+=cr.y;
        cameraRotation.z+=cr.z;
    }

    public float[] getMVPMatrix(){
        return mMVPMatrix;
    }

    public void setPosition(SimpleVector s){
        distanceFromOrigin.x = s.x;
        distanceFromOrigin.y = s.y;
        distanceFromOrigin.z = s.z;
    }

    public void updatePosition(SimpleVector s){
        distanceFromOrigin.x += s.x;
        distanceFromOrigin.y += s.y;
        distanceFromOrigin.z += s.z;
    }

    public SimpleVector getPosition(){
        return distanceFromOrigin;
    }

    public void setFollowSpeed(SimpleVector speed){
        followSpeed.x = speed.x;
        //update

    }
    public float[] getProjectionMatrix(){return projectionMatrix;}
    public void setFollowDelay(SimpleVector delay){
        followDelay.x = delay.x;
    }

    public float getLeft(){return left;}
    public float getBottom() { return bottom; }
    public float getTop() { return top; }
    public float getRight() { return right; }
}

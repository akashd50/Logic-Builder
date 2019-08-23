package com.akashd50.lb.objects;

import android.content.Context;
import android.view.MotionEvent;
import com.akashd50.lb.R;
import java.util.ArrayList;

public class SelectionDialog extends Dialog{
    private ArrayList<Button> options;
    private float viewLeft, viewRight;
    private SimpleVector nextOptionLocation, initialOptionLocation;
    private SelectionDialog parent;

    public SelectionDialog(SimpleVector dimensions, SimpleVector location, Context c){
        options = new ArrayList<>();
        this.dimensions = dimensions;
        dialogLocation = location;
        initialOptionLocation = new SimpleVector(dialogLocation.x - dimensions.x/2 + 0.2f,
                dialogLocation.y + dimensions.y/2 - 0.17f ,3.5f);
        nextOptionLocation = new SimpleVector(initialOptionLocation);

        background = new Button(R.drawable.empty_board,dimensions,c);
      //  overlay = new Button(R.drawable.dialog_overlay,new SimpleVector(dimensions.x, dimensions.y+0.2f, dimensions.z),c);
     //   overlay.setLocation(dialogLocation);
        background.setLocation(dialogLocation);
        viewLeft = nextOptionLocation.x;
    }

    public void addOption(Button b){
        if(nextOptionLocation.y <= dialogLocation.y - dimensions.y/2){
            nextOptionLocation.y = initialOptionLocation.y;
            nextOptionLocation.x += 0.3f;

            viewRight = nextOptionLocation.x;
        }
        b.setLocation(new SimpleVector(nextOptionLocation.x, nextOptionLocation.y, nextOptionLocation.z));
        /*nextOptionLocation.x+=0.3f;
        if(nextOptionLocation.x > 0.6f){
            nextOptionLocation.y -= 0.3f;
            nextOptionLocation.x = -0.8f;

            viewBottom = nextOptionLocation.y;
        }*/
        nextOptionLocation.y-=0.3f;
        options.add(b);
    }

    @Override
    public void onDrawFrame(float[] mMVPMatrix) {
        if(isShowing) {
            background.onDrawFrame(mMVPMatrix);
            for (Button b : options) {
                SimpleVector local = b.getLocation();
                if(local.y < dialogLocation.y + dimensions.y/2 && local.y > dialogLocation.y - dimensions.y/2) {
                    b.onDrawFrame(mMVPMatrix);
                }
            }
          //  overlay.onDrawFrame(mMVPMatrix);
        }
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        if(isShowing) {
            for (Button b : options) {
                if(b.getLocation().y > dialogLocation.y - dimensions.y/2 && b.getLocation().y < dialogLocation.y + dimensions.y/2)
                    b.onTouchDown(event);
            }
            background.onTouchDown(event);
        }
    }

    @Override
    public void onTouchUp(MotionEvent event) {
        if(isShowing) {
            for (Button b : options) {
                if(b.getLocation().y > dialogLocation.y - dimensions.y/2 && b.getLocation().y < dialogLocation.y + dimensions.y/2)
                    b.onTouchUp(event);
            }
            background.onTouchUp(event);
            background.resetSelected();
        }
    }

    @Override
    public void onTouchMove(MotionEvent event) {
        if(isShowing) {
            for (Button b : options) {
                if(b.getLocation().y > dialogLocation.y - dimensions.y/2 && b.getLocation().y < dialogLocation.y + dimensions.y/2)
                    b.onTouchMove(event);
            }

            if(background.isClicked()){
                int index = event.getHistorySize()-1;
                if(index>-1) {
                    /*float x = event.getHistoricalX(index);
                    float y = event.getHistoricalY(index);
                        if (y > event.getY()) {
                            if(viewBottom < dialogLocation.y - dimensions.y/2 + 0.2f) {
                                for (Button b : options) {
                                    b.setLocation(new SimpleVector(b.getLocation().x,
                                            b.getLocation().y + 0.01f, b.getLocation().z));
                                }


                                viewBottom += 0.01f;
                                viewTop += 0.01f;
                            }
                        } else if (y < event.getY()) {
                            if(viewTop > dialogLocation.y + dimensions.y/2 - 0.2f) {
                                for (Button b : options) {
                                    b.setLocation(new SimpleVector(b.getLocation().x,
                                            b.getLocation().y - 0.01f, b.getLocation().z));
                                }
                                viewBottom -= 0.01f;
                                viewTop -= 0.01f;
                            }
                        }*/

                    float x = event.getHistoricalX(index);
                    float y = event.getHistoricalY(index);
                    //          for (Button b : options) {
                    if (x > event.getX()) {
                        if(viewRight > dialogLocation.x + dimensions.x/2 - 0.2f) {
                            for (Button b : options) {
                                b.setLocation(new SimpleVector(b.getLocation().x - 0.01f,
                                        b.getLocation().y, b.getLocation().z));
                            }

                            viewRight -= 0.01f;
                            viewLeft -= 0.01f;
                        }
                    } else if (x < event.getX()) {
                        if(viewLeft < dialogLocation.x - dimensions.x/2 + 0.2f) {
                            for (Button b : options) {
                                b.setLocation(new SimpleVector(b.getLocation().x + 0.01f,
                                        b.getLocation().y, b.getLocation().z));
                            }
                            viewRight += 0.01f;
                            viewLeft += 0.01f;
                        }
                    }
                }
            }
        }
    }

    public void resetScrollLocation(){
        nextOptionLocation = new SimpleVector(initialOptionLocation);
        viewLeft = nextOptionLocation.x;
        for(Button b: options){
            b.setLocation(new SimpleVector(nextOptionLocation.x, nextOptionLocation.y, nextOptionLocation.z));

            nextOptionLocation.y-=0.3f;
            if(nextOptionLocation.y <= dialogLocation.y - dimensions.y/2){
                nextOptionLocation.y = initialOptionLocation.y;
                nextOptionLocation.x += 0.3f;

                viewRight = nextOptionLocation.x;
            }
        }

    }

    public void setTouchListener(TouchListener t){
        this.touchListener = t;
        for(Button b: options){
            b.setListener(touchListener);
        }
        //background.setListener(touchListener);
    }

    public void deSelectRest(Button b){
        for(Button button: options){
            if(b.getID()!=button.getID()) button.resetSelected();
        }
    }

    public void deSelectAll(){
        for(Button button: options){
            button.resetSelected();
        }
    }


    public int getID(){
        return 9999;
    }


}

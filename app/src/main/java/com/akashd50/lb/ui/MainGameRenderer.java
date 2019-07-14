package com.akashd50.lb.ui;

import android.content.ContentValues;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.akashd50.lb.R;
import com.akashd50.lb.objects.Button;
import com.akashd50.lb.objects.Clickable;
import com.akashd50.lb.objects.IO_Device;
import com.akashd50.lb.objects.Gate;
import com.akashd50.lb.logic.TouchController;
import com.akashd50.lb.objects.Camera;
import com.akashd50.lb.objects.LogicBoard;
import com.akashd50.lb.objects.LogicObject;
import com.akashd50.lb.objects.SelectionDialog;
import com.akashd50.lb.objects.SimpleVector;
import com.akashd50.lb.objects.TouchListener;
import com.akashd50.lb.objects.Wire;
import com.akashd50.lb.utils.TextDecoder;
import com.akashd50.lb.utils.Utilities;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameRenderer implements GLSurfaceView.Renderer {

    //public static final float[] mMVPMatrix = new float[16];
    private static final float[] mProjectionMatrix = new float[16];
    //public static final float[] mViewMatrix = new float[16];
    private float frameCountStartTime;
    private int numFramesRendered, frameCountToDraw;

    private static final float[] uiMVPMatrix = new float[16];
    private static final float[] uiProjectionMatrix = new float[16];
    private static final float[] uiViewMatrix = new float[16];
    //private SceneControlHandler sceneControlHandler;
    private boolean isReady;

    public Context context;
    private static int FPS=0;
    private int HEIGHT, WIDTH;
    private static long currentFrameTime, previousFrameTime;
    private Camera camera;
    private TouchController controller;
    private LogicBoard logicBoard;
    private TextDecoder textDecoder;
    private SelectionDialog CURRENT_DIALOG, optionsDialog, wiresDialog, gatesDialog;
    private Button showItemsButton;
    private int selectedIndexX, selectedIndexY;
    private Clickable selectedComponent;
    private SimpleVector clickTestValue;

    public MainGameRenderer(Context ctx, TouchController touchController) {
        this.context = ctx;
        currentFrameTime = 0;
        previousFrameTime = 0;
        controller = touchController;
    }


    public void onSurfaceCreated(final GL10 unused, EGLConfig config) {
        frameCountStartTime = 0f;
        numFramesRendered = 0;
        frameCountToDraw = 0;
        clickTestValue = new SimpleVector();

        GLES30.glEnable( GLES30.GL_DEPTH_TEST );
        GLES30.glDepthFunc( GLES30.GL_LESS);
        GLES30.glEnable(GLES30.GL_CULL_FACE);

        showItemsButton = new Button(R.drawable.show_items_menu, new SimpleVector(0.2f,0.2f,1f), context);
        showItemsButton.setLocation(new SimpleVector(0.8f,1.8f,3.5f));

        optionsDialog = new SelectionDialog(context);
        final Button gate = new Button(R.drawable.gate_folder,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wire = new Button(R.drawable.wire_folder,new SimpleVector(0.2f,0.2f,1f), context);
        final Button emptyDisplay = new Button(R.drawable.display_empty_ii,new SimpleVector(0.2f,0.2f,1f), context);
        final Button displayOne = new Button(R.drawable.display_one_ii,new SimpleVector(0.2f,0.2f,1f), context);
        final Button displayZero = new Button(R.drawable.display_zero_ii,new SimpleVector(0.2f,0.2f,1f), context);
        final Button emptyBox = new Button(R.drawable.empty_board,new SimpleVector(0.2f,0.2f,1f), context);

        LogicObject.SetIOTextures(displayOne.getTextureUnit(), displayZero.getTextureUnit(), emptyDisplay.getTextureUnit());

        optionsDialog.addOption(emptyBox);
        optionsDialog.addOption(gate);
        optionsDialog.addOption(wire);
        optionsDialog.addOption(emptyDisplay);
        optionsDialog.addOption(displayOne);
        optionsDialog.addOption(displayZero);

        wiresDialog = new SelectionDialog(context);
        final Button back = new Button(R.drawable.back_button,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireLR = new Button(R.drawable.wire_lr,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireRL = new Button(R.drawable.wire_rl,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireTB = new Button(R.drawable.wire_tb,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireBT = new Button(R.drawable.wire_bt,new SimpleVector(0.2f,0.2f,1f), context);

        final Button wireBL = new Button(R.drawable.wire_turn_bl,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireBR = new Button(R.drawable.wire_turn_br,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireTR = new Button(R.drawable.wire_turn_tr,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireTL = new Button(R.drawable.wire_turn_tl,new SimpleVector(0.2f,0.2f,1f), context);

        final Button wireLT = new Button(R.drawable.wire_turn_lt,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireLB = new Button(R.drawable.wire_turn_lb,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireRB = new Button(R.drawable.wire_turn_rb,new SimpleVector(0.2f,0.2f,1f), context);
        final Button wireRT = new Button(R.drawable.wire_turn_rt,new SimpleVector(0.2f,0.2f,1f), context);

        wiresDialog.addOption(back);
        wiresDialog.addOption(wireLR);
        wiresDialog.addOption(wireRL);
        wiresDialog.addOption(wireTB);
        wiresDialog.addOption(wireBT);
        wiresDialog.addOption(wireBL);
        wiresDialog.addOption(wireBR);
        wiresDialog.addOption(wireTR);
        wiresDialog.addOption(wireTL);
        wiresDialog.addOption(wireLT);
        wiresDialog.addOption(wireLB);
        wiresDialog.addOption(wireRB);
        wiresDialog.addOption(wireRT);
        wiresDialog.setParent(optionsDialog);

        gatesDialog = new SelectionDialog(context);
        final Button orGate = new Button(R.drawable.or_gate,new SimpleVector(0.2f,0.2f,1f), context);
        final Button andGate = new Button(R.drawable.and_gate,new SimpleVector(0.2f,0.2f,1f), context);
        final Button notGate = new Button(R.drawable.not_gate,new SimpleVector(0.2f,0.2f,1f), context);
        gatesDialog.addOption(back);
        gatesDialog.addOption(orGate);
        gatesDialog.addOption(andGate);
        gatesDialog.addOption(notGate);
        gatesDialog.setParent(optionsDialog);

        TouchListener buttonListener = new TouchListener() {
            @Override
            public void onTouchDown(MotionEvent event, Clickable c) {

            }

            @Override
            public void onTouchUp(MotionEvent event, Clickable c) {

                if(c.getID() == showItemsButton.getID()){
                    if(CURRENT_DIALOG==null || CURRENT_DIALOG == optionsDialog) {
                        if (!optionsDialog.isShowing()) {
                            optionsDialog.showing(true);
                            CURRENT_DIALOG = optionsDialog;
                        } else {
                            optionsDialog.showing(false);
                            CURRENT_DIALOG.deSelectAll();
                            selectedComponent = null;
                            CURRENT_DIALOG = null;
                        }
                    }

                }else if(c.getID() == back.getID()){
                    if(CURRENT_DIALOG == null){

                    }else{
                        CURRENT_DIALOG.showing(false);
                        CURRENT_DIALOG.deSelectAll();
                        selectedComponent = null;

                        CURRENT_DIALOG.getParent().showing(true);
                        CURRENT_DIALOG = CURRENT_DIALOG.getParent();
                    }
                } else if(c.getID() == wire.getID()) {
                    CURRENT_DIALOG.showing(false);
                    CURRENT_DIALOG.deSelectAll();
                    selectedComponent = null;

                    CURRENT_DIALOG = wiresDialog;
                    CURRENT_DIALOG.showing(true);

                }else if(c.getID() == gate.getID()){
                    CURRENT_DIALOG.showing(false);
                    CURRENT_DIALOG.deSelectAll();
                    selectedComponent = null;

                    CURRENT_DIALOG = gatesDialog;
                    CURRENT_DIALOG.showing(true);
                } else{
                    if (selectedComponent != null) {
                        if (selectedComponent == c) selectedComponent = null;
                        else{
                            selectedComponent = c;
                            CURRENT_DIALOG.deSelectRest((Button)c);
                        }
                    } else {
                        selectedComponent = c;
                        CURRENT_DIALOG.deSelectRest((Button)c);
                    }
                }
            }

            @Override
            public void onTouchMove(MotionEvent event, Clickable c) {
            }
        };

        optionsDialog.setTouchListener(buttonListener);
        showItemsButton.setListener(buttonListener);
        wiresDialog.setTouchListener(buttonListener);
        gatesDialog.setTouchListener(buttonListener);

        logicBoard = new LogicBoard(new SimpleVector(21f,21f,0f));
        logicBoard.loadBoard(context);
        logicBoard.setTouchListener(new TouchListener() {
            @Override
            public void onTouchDown(MotionEvent event, Clickable c) {
                clickTestValue.x = event.getRawX();
                clickTestValue.y = event.getRawY();
            }

            @Override
            public void onTouchUp(MotionEvent event, Clickable c) {
                //get the raw X and Y
                //convert to the openGL pt system
                //subtract the camera offset so that the value is 0;
                if(Math.abs(event.getRawX() - clickTestValue.x) > 10 || Math.abs(event.getRawY() - clickTestValue.y) > 10){
                    return;
                }

                float scrW = WIDTH;
                float scrH = HEIGHT;
                float viewLength = camera.getRight() - camera.getLeft();
                float viewHeight = camera.getTop() - camera.getBottom();
                float x = (event.getX() / scrW) * viewLength - viewLength / 2;
                float y = (event.getY() / scrH) * viewHeight - viewHeight / 2;
                //int cxOffset = (int)((camera.getPosition().x/viewLength)*viewLength);
                selectedIndexX = (int) (logicBoard.getDimensions().x / 2) + (int) Math.ceil(((x + camera.getPosition().x) / viewLength) * viewLength);
                selectedIndexY = (int) (logicBoard.getDimensions().y / 2) + (int) Math.ceil(((y - camera.getPosition().y) / viewHeight) * viewHeight);
                //Toast.makeText(context, "Click: " + x + ", " + y + "Index: " + selectedIndexX+ ", " + selectedIndexY, Toast.LENGTH_SHORT).show();

                LogicObject obj = logicBoard.get(selectedIndexY, selectedIndexX-1);
                if(obj!=null) Toast.makeText(context, "Data: "+obj.getOutput(), Toast.LENGTH_SHORT).show();
                else Toast.makeText(context, "Data: NULL", Toast.LENGTH_SHORT).show();

                if(selectedComponent != null && event.getPointerCount()<2) {
                    Button selectedButton = ((Button)selectedComponent);

                    if(selectedComponent.getID() == orGate.getID()){
                        Gate gate = new Gate(Gate.OR_GATE, ((Button) selectedComponent).getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, gate);
                    }else if(selectedComponent.getID() == andGate.getID()){
                        Gate gate = new Gate(Gate.AND_GATE, ((Button) selectedComponent).getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, gate);
                    }else if(selectedComponent.getID() == notGate.getID()){
                        Gate gate = new Gate(Gate.NOT_GATE, ((Button) selectedComponent).getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, gate);
                    }

                    else if(selectedComponent.getID() == wireLR.getID()){
                        Wire w = new Wire(Wire.WIRE_LR);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireRL.getID()){
                        Wire w = new Wire(Wire.WIRE_RL);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireTB.getID()){
                        Wire w = new Wire(Wire.WIRE_TB);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireBT.getID()){
                        Wire w = new Wire(Wire.WIRE_BT);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireBL.getID()){
                        Wire w = new Wire(Wire.WIRE_BL);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireBR.getID()){
                        Wire w = new Wire(Wire.WIRE_BR);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireTR.getID()){
                        Wire w = new Wire(Wire.WIRE_TR);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireTL.getID()){
                        Wire w = new Wire(Wire.WIRE_TL);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireLT.getID()){
                        Wire w = new Wire(Wire.WIRE_LT);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireLB.getID()){
                        Wire w = new Wire(Wire.WIRE_LB);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireRT.getID()){
                        Wire w = new Wire(Wire.WIRE_RT);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == wireRB.getID()){
                        Wire w = new Wire(Wire.WIRE_RB);
                        w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }

                    else if(selectedComponent.getID() == emptyDisplay.getID()){
                        IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                        w.setOutput(-1);
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    } else if(selectedComponent.getID() == displayOne.getID()){
                        IO_Device w = new IO_Device(IO_Device.INPUT_STYLE_1);
                        //w.setInput(1,selectedButton.getTextureUnit());
                        w.setOutput(1);
                        //w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == displayZero.getID()){
                        IO_Device w = new IO_Device(IO_Device.INPUT_STYLE_0);
                        w.setOutput(0);
                        //w.setTexture(selectedButton.getTextureUnit());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(selectedComponent.getID() == emptyBox.getID()){
                        logicBoard.clearBox(selectedIndexY, selectedIndexX-1);
                    }
                }
            }

            @Override
            public void onTouchMove(MotionEvent event, Clickable c) {

            }
        });

        textDecoder = new TextDecoder(context);
        isReady = true;

        Thread simulatingT = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    logicBoard.simulate();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        simulatingT.start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) height / width;
        HEIGHT = height; WIDTH = width;
        Utilities.setScreenVars(HEIGHT, WIDTH);
        
        camera = new Camera();
        camera.setTouchController(controller);
        camera.setAdditionalParms(WIDTH, HEIGHT);
        //Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 200);
        Matrix.orthoM(mProjectionMatrix, 0, -1,1,-ratio, ratio, 1,100);
        Matrix.orthoM(uiProjectionMatrix, 0, -1,1,-ratio, ratio, 1,30);

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,0f,5f));
        camera.lookAt(new SimpleVector(0f,0f,0f));
    }

    private void initializeUIElements(){

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        previousFrameTime = System.nanoTime();
        if(frameCountStartTime == 0){
            frameCountStartTime = System.nanoTime();
            frameCountToDraw = numFramesRendered;
            numFramesRendered = 0;
        }else{
            if(System.nanoTime() - frameCountStartTime < 1000000000){
                numFramesRendered++;
            }else{
                frameCountStartTime = 0;
            }
        }

        GLES30.glClearColor(((float)1/255), (float)200/255, (float)200/255,1f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        if(isReady) {
            drawScene();
            customUIDrawing();
        }
        currentFrameTime = System.nanoTime();
        long tTime = currentFrameTime - previousFrameTime;
        FPS = (int)(1000000000/tTime);
    }

    private void drawScene(){
        camera.updateView();
        camera.updatePinchZoom();
        camera.updateSwipeMovement();
        float[] mainMatrix = camera.getMVPMatrix();
        logicBoard.onDrawFrame(mainMatrix);

    }


    private void customUIDrawing(){
        Matrix.setLookAtM(uiViewMatrix, 0, 0, 0, 5.0f,
                0.0f, 0.0f, 0.0f,
                0f, 1.0f, 0.0f);
        Matrix.multiplyMM(uiMVPMatrix, 0, uiProjectionMatrix, 0, uiViewMatrix, 0);

       // float[] color = {1.0f,1.0f,0f,1f};
       // textDecoder.drawText("FPS: "+frameCountToDraw,new SimpleVector(-0.8f,1.7f,3f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
       /* float viewLength = camera.getRight() - camera.getLeft();
        float viewHeight = camera.getTop() - camera.getBottom();
        textDecoder.drawText("VIEW L: "+viewLength,new SimpleVector(-0.8f,1.5f,3f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
        textDecoder.drawText("VIEW H: "+viewHeight,new SimpleVector(-0.8f,1.4f,3f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
        textDecoder.drawText("C P: "+camera.getPosition().toString(),new SimpleVector(-0.8f,1.3f,3f),new SimpleVector(1.0f,1.0f,1f),uiMVPMatrix, color);
*/
       /* optionsDialog.onDrawFrame(uiMVPMatrix);
        wiresDialog.onDrawFrame(uiMVPMatrix);*/
       if(CURRENT_DIALOG!=null) CURRENT_DIALOG.onDrawFrame(uiMVPMatrix);
        showItemsButton.onDrawFrame(uiMVPMatrix);
       // sceneControlHandler.onDrawFrame(uiMVPMatrix);
    }

    public void onTouchDown(MotionEvent event){
        //sceneControlHandler.onTouchDown(event);
        showItemsButton.onTouchDown(event);
       /* if(optionsDialog.isShowing()) optionsDialog.onTouchDown(event);
        if(wiresDialog.isShowing()) wiresDialog.onTouchDown(event);*/
        if(CURRENT_DIALOG!=null) CURRENT_DIALOG.onTouchDown(event);

        logicBoard.onTouchDown(event);
    }
    public void onTouchUp(MotionEvent event){
        //sceneControlHandler.onTouchUp(event);
        showItemsButton.onTouchUp(event);
        /*if(optionsDialog.isShowing()) optionsDialog.onTouchUp(event);
        if(wiresDialog.isShowing()) wiresDialog.onTouchUp(event);*/
        if(CURRENT_DIALOG!=null) CURRENT_DIALOG.onTouchUp(event);
        if(CURRENT_DIALOG!= null && !CURRENT_DIALOG.wasClicked()) {
            logicBoard.onTouchUp(event);
            //showItemsButton.resetWasClicked();
        }
    }
    public void onTouchMove(MotionEvent event){
        //sceneControlHandler.onTouchMove(event);
        showItemsButton.onTouchMove(event);
/*
        if(optionsDialog.isShowing()) optionsDialog.onTouchMove(event);
        if(wiresDialog.isShowing()) wiresDialog.onTouchMove(event);*/
        if(CURRENT_DIALOG!=null) CURRENT_DIALOG.onTouchMove(event);

        logicBoard.onTouchMove(event);
    }

    public void onBackPressed(){

    }

}

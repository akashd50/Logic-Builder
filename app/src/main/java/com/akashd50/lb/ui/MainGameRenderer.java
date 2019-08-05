package com.akashd50.lb.ui;

import android.content.ContentValues;
import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.widget.Toast;

import com.akashd50.lb.R;
import com.akashd50.lb.logic.TextureContainer;
import com.akashd50.lb.objects.BoardData;
import com.akashd50.lb.objects.Button;
import com.akashd50.lb.objects.Clickable;
import com.akashd50.lb.objects.IO_Device;
import com.akashd50.lb.objects.Gate;
import com.akashd50.lb.logic.TouchController;
import com.akashd50.lb.objects.Camera;
import com.akashd50.lb.objects.LogicBoard;
import com.akashd50.lb.objects.LogicObject;
import com.akashd50.lb.objects.Quad2D;
import com.akashd50.lb.objects.SelectionDialog;
import com.akashd50.lb.objects.SimpleVector;
import com.akashd50.lb.objects.Texture;
import com.akashd50.lb.objects.TouchListener;
import com.akashd50.lb.objects.Wire;
import com.akashd50.lb.persistense.DBContract;
import com.akashd50.lb.persistense.DBHelper;
import com.akashd50.lb.persistense.SQLPersistenceBoard;
import com.akashd50.lb.persistense.SQLPersistenceBoardData;
import com.akashd50.lb.utils.Shader;
import com.akashd50.lb.utils.TextDecoder;
import com.akashd50.lb.utils.Utilities;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainGameRenderer implements GLSurfaceView.Renderer {

    private static final float[] mProjectionMatrix = new float[16];
    private float frameCountStartTime;
    private int numFramesRendered, frameCountToDraw;

    private static final float[] uiMVPMatrix = new float[16];
    private static final float[] uiProjectionMatrix = new float[16];
    private static final float[] uiViewMatrix = new float[16];
    private boolean isReady;

    public Context context;
    private static int FPS=0;
    private float HEIGHT, WIDTH, RATIO;
    private static long currentFrameTime, previousFrameTime;
    private Camera camera;
    private TouchController controller;
    private LogicBoard logicBoard;
    private TextDecoder textDecoder;
    private SelectionDialog CURRENT_DIALOG, optionsDialog, wiresDialog, gatesDialog, boardsDialog;
    private Button showItemsButton, back, save, gate, boardFolder, wire, displayZero, displayOne, emptyDisplay, emptyBox;
    private int selectedIndexX, selectedIndexY, QUAD_TEXTURE_PROGRAM;
    private Clickable selectedComponent, longPressedComponent;
    private SimpleVector clickTestValue;
    private TextureContainer textureContainer;
    private DBHelper dbHelper;
    private SQLPersistenceBoardData sqlPersistence;
    private SQLPersistenceBoard sqlPersistenceBoard;

    //Intent data
    private int BOARDID, BOARDX, BOARDY;
    private String BOARD_NAME;

    private Quad2D sceneBackground, longPressedComponentQuad;

    private Vibrator vibrator;
    private boolean longPressedVib;

    public MainGameRenderer(Context ctx, TouchController touchController, int boardID, String name, int x, int y) {
        this.context = ctx;
        currentFrameTime = 0;
        previousFrameTime = 0;
        controller = touchController;

        BOARDID = boardID;
        BOARD_NAME = name;
        BOARDX = x;
        BOARDY = y;

        vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE); longPressedVib = false;

        dbHelper = new DBHelper(context);

        sqlPersistence = new SQLPersistenceBoardData(dbHelper);
        sqlPersistenceBoard = new SQLPersistenceBoard(dbHelper);

        if(!sqlPersistenceBoard.isBoardIDInitialized()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.DBEntry.APP_VARS_BOARD_ID, 100001);
            dbHelper.getWritableDatabase().insert(DBContract.DBEntry.APP_VARS_TABLE, null, contentValues);
        }
    }

    public void onSurfaceCreated(final GL10 unused, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) height / width;
        HEIGHT = height; WIDTH = width;
        RATIO = ratio;
        Utilities.setScreenVars(HEIGHT, WIDTH);
        
        camera = new Camera();
        camera.setTouchController(controller);
        camera.setAdditionalParms(WIDTH, HEIGHT);
        //Matrix.perspectiveM(mProjectionMatrix, 0, 45f, ratio, 1, 200);
        Matrix.orthoM(mProjectionMatrix, 0, -1,1,-ratio, ratio, 1,30);
        Matrix.orthoM(uiProjectionMatrix, 0, -1,1,-ratio, ratio, 1,30);

        camera.setMatrices(new float[16],mProjectionMatrix,new float[16]);
        camera.setPosition(new SimpleVector(0f,0f,5f));
        camera.lookAt(new SimpleVector(0f,0f,0f));
        QUAD_TEXTURE_PROGRAM = Shader.getQuadTextureProgram();

        initializeAll();
    }

    private void initializeAll(){
        frameCountStartTime = 0f;
        numFramesRendered = 0;
        frameCountToDraw = 0;
        clickTestValue = new SimpleVector();

        textureContainer = new TextureContainer();

        Utilities.loadTextures(textureContainer, context);
        LogicObject.setTextureContainer(textureContainer);

        textureContainer.addTexture(Utilities.generateTexture("Sample", "sample"));

        sceneBackground = new Quad2D(30f,25f);
        sceneBackground.setTextureUnit(textureContainer.getTexture("sample"));
        sceneBackground.setDefaultLocation(new SimpleVector(0f,0f,3.5f));
        sceneBackground.setOpacity(1.0f);
        sceneBackground.setRenderPreferences(Shader.getQuadTextureProgram(), Quad2D.REGULAR);

        longPressedComponentQuad = new Quad2D(0.2f,0.2f);
        longPressedComponentQuad .setRenderPreferences(QUAD_TEXTURE_PROGRAM, Quad2D.REGULAR);
        longPressedComponentQuad .setOpacity(1.0f);

        initializeDialogs();

        TouchListener buttonListener = new TouchListener() {
            @Override
            public void onTouchDown(MotionEvent event, final Clickable c) {
                longPressedComponent = c;
                longPressedVib = false;
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
                    if(CURRENT_DIALOG != null){
                        if(CURRENT_DIALOG.getParent() != null) {
                            CURRENT_DIALOG.showing(false);
                            CURRENT_DIALOG.deSelectAll();
                            selectedComponent = null;

                            CURRENT_DIALOG.getParent().showing(true);
                            CURRENT_DIALOG = CURRENT_DIALOG.getParent();
                        }else{
                            CURRENT_DIALOG.showing(false);
                            CURRENT_DIALOG.deSelectAll();
                            selectedComponent = null;
                            CURRENT_DIALOG = null;
                        }
                    }
                } else if(c.getID() == wire.getID()) {
                    if(CURRENT_DIALOG!=null) {
                        CURRENT_DIALOG.showing(false);
                        CURRENT_DIALOG.deSelectAll();
                        selectedComponent = null;

                        CURRENT_DIALOG = wiresDialog;
                        CURRENT_DIALOG.showing(true);
                    }
                }else if(c.getID() == gate.getID()){
                    if(CURRENT_DIALOG!=null) {
                        CURRENT_DIALOG.showing(false);
                        CURRENT_DIALOG.deSelectAll();
                        selectedComponent = null;

                        CURRENT_DIALOG = gatesDialog;
                        CURRENT_DIALOG.showing(true);
                    }
                }else if(c.getID() == boardFolder.getID()) {
                    if(CURRENT_DIALOG!=null) {
                        CURRENT_DIALOG.showing(false);
                        CURRENT_DIALOG.deSelectAll();
                        selectedComponent = null;

                        CURRENT_DIALOG = boardsDialog;
                        CURRENT_DIALOG.showing(true);
                    }
                }else if(c.getID() == save.getID()) {
                    onBackPressed();
                    save.resetSelected();
                    save.resetWasClicked();
                    Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show();
                }else{
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
        boardsDialog.setTouchListener(buttonListener);

        if(BOARDID == -1) {
            if(BOARDX>0 && BOARDY > 0){
                if(BOARDX%2 == 0) BOARDX += 1;
                if(BOARDY%2 == 0) BOARDY += 1;
            }else{
                BOARDX = 21;
                BOARDY = 21;
            }

            logicBoard = new LogicBoard(new SimpleVector(BOARDX, BOARDY, 0f), sqlPersistenceBoard.getNextBoardID());
            logicBoard.loadBoard(context);
            logicBoard.setName(BOARD_NAME);

            for(int i=1;i<logicBoard.getDimensions().x-1;i++){
                IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                logicBoard.updateBoard(i,0, w);
            }
            for(int i=1;i<logicBoard.getDimensions().x-1;i++){
                IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                logicBoard.updateBoard(i,(int)logicBoard.getDimensions().y-1, w);
            }

            for(int i=1;i<logicBoard.getDimensions().y-1;i++){
                IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                logicBoard.updateBoard(0,i, w);
            }
            for(int i=1;i<logicBoard.getDimensions().y-1;i++){
                IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                logicBoard.updateBoard((int)logicBoard.getDimensions().x-1,i, w);
            }

        }else{
            logicBoard = sqlPersistenceBoard.getBoard(BOARDID);
            logicBoard.loadBoard(context);
        }

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

                float scrW = WIDTH;
                float scrH = HEIGHT;
                float viewLength = camera.getRight() - camera.getLeft();
                float viewHeight = camera.getTop() - camera.getBottom();
                float x = (event.getX() / scrW) * viewLength - viewLength / 2;
                float y = (event.getY() / scrH) * viewHeight - viewHeight / 2;
                //int cxOffset = (int)((camera.getPosition().x/viewLength)*viewLength);
                selectedIndexX = (int) (logicBoard.getDimensions().x / 2) + (int) Math.ceil(((x + camera.getPosition().x) / viewLength) * viewLength);
                selectedIndexY = (int) (logicBoard.getDimensions().y / 2) + (int) Math.ceil(((y - camera.getPosition().y) / viewHeight) * viewHeight);

                if(longPressedComponent !=null && ((Button)longPressedComponent).isLongPressed()){
                    Button selectedButton = ((Button)longPressedComponent);

                    if(CURRENT_DIALOG!= null && CURRENT_DIALOG == wiresDialog){
                        Wire w = new Wire(selectedButton.getIntegerTag());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }else if(CURRENT_DIALOG!= null && CURRENT_DIALOG == gatesDialog){
                        Gate gate = new Gate(selectedButton.getIntegerTag());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, gate);
                    }else if(CURRENT_DIALOG!= null && CURRENT_DIALOG == boardsDialog){
                        LogicBoard l = sqlPersistenceBoard.getBoard(selectedButton.getIntegerTag());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, l);
                    }else {
                        if (longPressedComponent.getID() == emptyDisplay.getID()) {
                            IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                            logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                        } else if (longPressedComponent.getID() == displayOne.getID()) {
                            IO_Device w = new IO_Device(IO_Device.INPUT_STYLE_1);
                            logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                        } else if (longPressedComponent.getID() == displayZero.getID()) {
                            IO_Device w = new IO_Device(IO_Device.INPUT_STYLE_0);
                            logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                        } else if (longPressedComponent.getID() == emptyBox.getID()) {
                            logicBoard.clearBox(selectedIndexY, selectedIndexX - 1);
                        }
                    }
                }

                if(Math.abs(event.getRawX() - clickTestValue.x) > 10 || Math.abs(event.getRawY() - clickTestValue.y) > 10
                || (CURRENT_DIALOG!=null && CURRENT_DIALOG.wasClicked())){
                    return;
                }


                //Toast.makeText(context, "Click: " + x + ", " + y + "Index: " + selectedIndexX+ ", " + selectedIndexY, Toast.LENGTH_SHORT).show();

                LogicObject obj = logicBoard.get(selectedIndexY, selectedIndexX-1);
                if(obj!=null && obj.getType() != LogicObject.WIRE) Toast.makeText(context, "Data: "+obj.getOutput1(), Toast.LENGTH_SHORT).show();
                else if(obj!=null  && obj.getType() == LogicObject.WIRE) Toast.makeText(context, "Data: "+obj.getOutput1() + ", "+((Wire)obj).getOutput2(), Toast.LENGTH_SHORT).show();

                else Toast.makeText(context, "Data: NULL", Toast.LENGTH_SHORT).show();

                if(selectedComponent != null && event.getPointerCount()<2) {
                    Button selectedButton = ((Button)selectedComponent);
                    if(CURRENT_DIALOG!= null && CURRENT_DIALOG == wiresDialog){
                        Wire w = new Wire(selectedButton.getIntegerTag());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                    }if(CURRENT_DIALOG!= null && CURRENT_DIALOG == gatesDialog){
                        Gate gate = new Gate(selectedButton.getIntegerTag());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, gate);
                    }else if(CURRENT_DIALOG!= null && CURRENT_DIALOG == boardsDialog){
                        LogicBoard l = sqlPersistenceBoard.getBoard(selectedButton.getIntegerTag());
                        logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, l);
                    }else {
                        if (selectedComponent.getID() == emptyDisplay.getID()) {
                            IO_Device w = new IO_Device(IO_Device.OUTPUT_STYLE);
                            logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                        } else if (selectedComponent.getID() == displayOne.getID()) {
                            IO_Device w = new IO_Device(IO_Device.INPUT_STYLE_1);
                            logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                        } else if (selectedComponent.getID() == displayZero.getID()) {
                            IO_Device w = new IO_Device(IO_Device.INPUT_STYLE_0);
                            logicBoard.updateBoard(selectedIndexY, selectedIndexX - 1, w);
                        } else if (selectedComponent.getID() == emptyBox.getID()) {
                                    logicBoard.clearBox(selectedIndexY, selectedIndexX - 1);
                        }
                    }
                }
            }

            @Override
            public void onTouchMove(MotionEvent event, Clickable c) {

            }
        });

        this.loadBoardData();

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

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glClearColor(((float)1/255), (float)1/255, (float)1/255,1f);
        //if(isReady) {
            drawScene();
            customUIDrawing();
        //}
        currentFrameTime = System.nanoTime();
        long tTime = currentFrameTime - previousFrameTime;
        FPS = (int)(1000000000/tTime);
    }

    private void drawScene(){
        camera.updateView();
        camera.updatePinchZoom();
        if(longPressedComponent == null && (CURRENT_DIALOG==null || (CURRENT_DIALOG!=null && !CURRENT_DIALOG.isClicked()))){
                camera.updateSwipeMovement();
        }
        float[] mainMatrix = camera.getMVPMatrix();
        logicBoard.onDrawFrame(mainMatrix);
        //sceneBackground.draw(mainMatrix);
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
        else showItemsButton.onDrawFrame(uiMVPMatrix);
       // sceneControlHandler.onDrawFrame(uiMVPMatrix);

        if(longPressedComponent!=null){
            Button b = (Button)longPressedComponent;
            if(b.isLongPressed()){
                if(!longPressedVib) {
                    vibrator.vibrate(50);
                    longPressedVib = true;
                }
                longPressedComponentQuad.setTextureUnit(b.getTextureUnit());
                longPressedComponentQuad.setLocation(new SimpleVector(xToNativePtSystem(controller.getTouchX()), yToNativePtSystem(controller.getTouchY()), 3.5f));
                longPressedComponentQuad.draw(uiMVPMatrix);
            }
        }
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
        //if(CURRENT_DIALOG!= null && !CURRENT_DIALOG.wasClicked()) {
            logicBoard.onTouchUp(event);
            //showItemsButton.resetWasClicked();
       // }
        longPressedComponent = null;
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
        ArrayList<BoardData> boardData = new ArrayList<>(logicBoard.getBoardData());
        sqlPersistence.insert(boardData, logicBoard.getID());

        if(!sqlPersistenceBoard.contains(logicBoard)){
            sqlPersistenceBoard.insert(logicBoard);
        }
    }

    private void loadBoardData(){
        logicBoard.addDataToBoard(sqlPersistence.getAll(logicBoard.getID()));
    }

    private void initializeDialogs(){
        optionsDialog = new SelectionDialog(new SimpleVector(2.0f,1.0f,1f),
                new SimpleVector(0f,RATIO-0.5f,3f),context);
        back = new Button(R.drawable.back_button,new SimpleVector(0.2f,0.2f,1f), context);

        showItemsButton = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        showItemsButton.setLocation(new SimpleVector(0.8f,RATIO-0.2f,3.5f));
        showItemsButton.setButtonTexture(textureContainer.getTexture(Utilities.showoptionsT));

        save = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        save.setButtonTexture(textureContainer.getTexture(Utilities.saveButtonT));

        gate = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        gate.setButtonTexture(textureContainer.getTexture(Utilities.gateFolderT));

        wire = new Button(R.drawable.wire_folder,new SimpleVector(0.2f,0.2f,1f), context);
        wire.setButtonTexture(textureContainer.getTexture(Utilities.wireFolderT));

        boardFolder = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        boardFolder.setButtonTexture(textureContainer.getTexture(Utilities.boardFolderT));

        emptyDisplay = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        emptyDisplay.setButtonTexture(textureContainer.getTexture(Utilities.displayEmptyT));

        displayOne = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        displayOne.setButtonTexture(textureContainer.getTexture(Utilities.displayOneT));

        displayZero = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        displayZero.setButtonTexture(textureContainer.getTexture(Utilities.displayZeroT));

        emptyBox = new Button(R.drawable.empty_board,new SimpleVector(0.2f,0.2f,1f), context);

        optionsDialog.addOption(back);
        optionsDialog.addOption(save);
        optionsDialog.addOption(emptyBox);
        optionsDialog.addOption(gate);
        optionsDialog.addOption(wire);
        optionsDialog.addOption(boardFolder);

        optionsDialog.addOption(emptyDisplay);
        optionsDialog.addOption(displayOne);
        optionsDialog.addOption(displayZero);

        wiresDialog = new SelectionDialog(new SimpleVector(2.0f,1.0f,1f),
                new SimpleVector(0f,RATIO-0.5f,3f), context);

        final Button wireLR = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireLR.setButtonTexture(textureContainer.getTexture(Utilities.wirelrT));
        wireLR.setTag(Wire.WIRE_LR);

        final Button wireRL = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireRL.setButtonTexture(textureContainer.getTexture(Utilities.wirerlT));
        wireRL.setTag(Wire.WIRE_RL);

        final Button wireTB = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireTB.setButtonTexture(textureContainer.getTexture(Utilities.wiretbT));
        wireTB.setTag(Wire.WIRE_TB);

        final Button wireBT = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireBT.setButtonTexture(textureContainer.getTexture(Utilities.wirebtT));
        wireBT.setTag(Wire.WIRE_BT);

        final Button wireBL = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireBL.setButtonTexture(textureContainer.getTexture(Utilities.wireblT));
        wireBL.setTag(Wire.WIRE_BL);

        final Button wireBR = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireBR.setButtonTexture(textureContainer.getTexture(Utilities.wirebrT));
        wireBR.setTag(Wire.WIRE_BR);

        final Button wireTR = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireTR.setButtonTexture(textureContainer.getTexture(Utilities.wiretrT));
        wireTR.setTag(Wire.WIRE_TR);

        final Button wireTL = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireTL.setButtonTexture(textureContainer.getTexture(Utilities.wiretlT));
        wireTL.setTag(Wire.WIRE_TL);

        final Button wireLT = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireLT.setButtonTexture(textureContainer.getTexture(Utilities.wireltT));
        wireLT.setTag(Wire.WIRE_LT);

        final Button wireLB = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireLB.setButtonTexture(textureContainer.getTexture(Utilities.wirelbT));
        wireLB.setTag(Wire.WIRE_LB);

        final Button wireRB = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireRB.setButtonTexture(textureContainer.getTexture(Utilities.wirerbT));
        wireRB.setTag(Wire.WIRE_RB);

        final Button wireRT = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wireRT.setButtonTexture(textureContainer.getTexture(Utilities.wirertT));
        wireRT.setTag(Wire.WIRE_RT);

        final Button wirePSTB_LR = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wirePSTB_LR.setButtonTexture(textureContainer.getTexture(Utilities.wire_ps_tblr));
        wirePSTB_LR.setTag(Wire.WIRE_PS_TBLR);

        final Button wirePSTB_RL = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wirePSTB_RL.setButtonTexture(textureContainer.getTexture(Utilities.wire_ps_tbrl));
        wirePSTB_RL.setTag(Wire.WIRE_PS_TBRL);

        final Button wirePSBT_LR = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wirePSBT_LR.setButtonTexture(textureContainer.getTexture(Utilities.wire_ps_btlr));
        wirePSBT_LR.setTag(Wire.WIRE_PS_BTLR);

        final Button wirePSBT_RL = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        wirePSBT_RL.setButtonTexture(textureContainer.getTexture(Utilities.wire_ps_btrl));
        wirePSBT_RL.setTag(Wire.WIRE_PS_BTRL);

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
        wiresDialog.addOption(wirePSTB_LR);
        wiresDialog.addOption(wirePSTB_RL);
        wiresDialog.addOption(wirePSBT_LR);
        wiresDialog.addOption(wirePSBT_RL);
        wiresDialog.setParent(optionsDialog);

        gatesDialog =new SelectionDialog(new SimpleVector(2.0f,1.0f,1f),
                new SimpleVector(0f,RATIO-0.5f,3f), context);
        final Button orGate = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        orGate.setButtonTexture(textureContainer.getTexture(Utilities.orGateT));
        orGate.setTag(Gate.OR_GATE);
        final Button andGate = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        andGate.setButtonTexture(textureContainer.getTexture(Utilities.andGateT));
        andGate.setTag(Gate.AND_GATE);
        final Button notGate = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        notGate.setButtonTexture(textureContainer.getTexture(Utilities.notGateT));
        notGate.setTag(Gate.NOT_GATE);

        final Button xorGate = new Button(new SimpleVector(0.2f,0.2f,1f), context);
        xorGate.setButtonTexture(textureContainer.getTexture(Utilities.xorGateT));
        xorGate.setTag(Gate.XOR_GATE);

        gatesDialog.addOption(back);
        gatesDialog.addOption(orGate);
        gatesDialog.addOption(andGate);
        gatesDialog.addOption(notGate);
        gatesDialog.addOption(xorGate);
        gatesDialog.setParent(optionsDialog);

        boardsDialog = new SelectionDialog(new SimpleVector(2.0f,1.0f,1f),
                new SimpleVector(0f,RATIO-0.5f,3f), context);
        boardsDialog.addOption(back);
        ArrayList<LogicBoard> temporaryList = sqlPersistenceBoard.getAllBoards();

        for(LogicBoard l: temporaryList){
            final Button icon = new Button(new SimpleVector(0.2f,0.2f,1f), context);
            textureContainer.addTexture(Utilities.generateTexture(l.getName(),l.getName()));
            icon.setButtonTexture(textureContainer.getTexture(l.getName()));
            icon.setTag(l.getID());

            boardsDialog.addOption(icon);
        }

        boardsDialog.setParent(optionsDialog);

    }

    private float xToNativePtSystem(float rx){
        float x = (rx / WIDTH) * 2.0f - 2.0f / 2;
        return x;
    }

    private float yToNativePtSystem(float ry){
        float y = RATIO - (ry / HEIGHT) * 2*RATIO;
        return y;
    }

}

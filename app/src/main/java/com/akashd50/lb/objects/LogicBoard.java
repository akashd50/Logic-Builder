package com.akashd50.lb.objects;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.akashd50.lb.R;
import com.akashd50.lb.utils.Shader;

import java.util.ArrayList;

public class LogicBoard implements Clickable{
    private SimpleVector dimensions;
    private LogicObject[][] logicBoard;
    private Quad2D[][] drawingBoard;
    private ArrayList<BoardData> boardData;
    private TouchListener listener;
    private Texture emptyBox, orGate, wireHor, wireTurn;
    private long lastTimeChecked;
    public LogicBoard(SimpleVector dimens){
        dimensions = dimens;
        logicBoard = new LogicObject[(int)dimensions.x][(int)dimensions.y];
        drawingBoard = new Quad2D[(int)dimensions.x][(int)dimensions.y];
        boardData = new ArrayList<>();
        lastTimeChecked = 0;
    }

    public void loadBoard(Context c){
        emptyBox = new Texture("empty", c,R.drawable.empty_board);
        orGate = new Texture("or", c, R.drawable.or_gate);
        wireHor = new Texture("wire_hor", c, R.drawable.wire_hor);
        wireTurn = new Texture("wire_turn", c, R.drawable.wire_turn);

        int program = Shader.getQuadTextureProgram();

        float left, top;
        float bx = 1.0f;
        float by = 1.0f;
        if(dimensions.x%2==0) left = -(dimensions.x/2 * bx);
        else left = -((dimensions.x-1)/2 - bx/2);

        if(dimensions.y%2==0) top = dimensions.y/2f * by;
        else top = (dimensions.y-1)/2 + by/2;

        for(int i=0;i<(int)dimensions.x;i++){
            for(int j=0;j<(int)dimensions.y;j++){
                drawingBoard[i][j] = new Quad2D(bx,by);
                drawingBoard[i][j].setDefaultLocation(new SimpleVector(left, top, 0f));
                drawingBoard[i][j].setTextureUnit(emptyBox);
                drawingBoard[i][j].scale(new SimpleVector(1f,1f,1f));
                drawingBoard[i][j].setRenderPreferences(program, Quad2D.REGULAR);
                drawingBoard[i][j].setOpacity(1.0f);
                left+=bx;
            }
            if(dimensions.x%2==0) left = -(dimensions.x/2 * bx);
            else left = -((dimensions.x-1)/2 - bx/2);
            top-=by;
        }
    }

    public void updateBoard(int x, int y, LogicObject l){
        if(x<dimensions.x && y<dimensions.y && x>=0 && y>=0) {
            switch (l.getType()){
                case LogicObject.WIRE:
                    logicBoard[x][y] = l;
                    drawingBoard[x][y].setTextureUnit(l.getTexture());
                    boardData.add(new BoardData(l,x,y));
                    break;
                case LogicObject.GATE:
                    logicBoard[x][y] = l;
                    drawingBoard[x][y].setTextureUnit(l.getTexture());
                    boardData.add(new BoardData(l,x,y));
                    break;
                case LogicObject.IO_DEVICE:
                    logicBoard[x][y] = l;
                    drawingBoard[x][y].setTextureUnit(l.getTexture());
                    boardData.add(new BoardData(l,x,y));
                    break;
            }
        }
    }

    public void updateBoardTexture(int x, int y, LogicObject l) {
        if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
            logicBoard[x][y] = l;
            drawingBoard[x][y].setTextureUnit(l.getTexture());
        }
    }

    public LogicObject get(int x, int y){
        if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
            return logicBoard[x][y];
        }else return null;
    }

    public void clearBox(int x, int y){
        if(x<dimensions.x && y<dimensions.y && x>=0 && y>=0) {
            logicBoard[x][y] = null;
            drawingBoard[x][y].setTextureUnit(emptyBox);
            for(BoardData b: boardData){
                if(x == b.x && y==b.y){
                    boardData.remove(b);
                    break;
                }
            }
            //simulate();
        }
    }

    public void onDrawFrame(float[] mMVPMatrix){
        for(int i=0;i<dimensions.x;i++){
            for(int j=0;j<dimensions.y;j++){
                drawingBoard[i][j].draw(mMVPMatrix);
            }
        }
       /* if(System.currentTimeMillis() - lastTimeChecked > 3000){
            simulate();
            lastTimeChecked = System.currentTimeMillis();
        }*/
    }

    public void simulate(){
        for(int i=0;i<boardData.size();i++){
           BoardData bData = boardData.get(i);
           LogicObject logicObject = bData.logicObject;
           switch (logicObject.getType()){
               case LogicObject.GATE:
                   Gate gate = (Gate)logicObject;
                   if(gate.getStyle() == Gate.OR_GATE || gate.getStyle() == Gate.AND_GATE) {
                       if (bData.x > 0 && bData.x < dimensions.x - 1) {
                           LogicObject i1 =logicBoard[bData.x - 1][bData.y];
                           LogicObject i2 =logicBoard[bData.x + 1][bData.y];
                           if (i1 != null) {
                               if(i1.getType() == LogicObject.WIRE) {
                                   if (((Wire) i1).outputsBottom()){
                                       gate.setInput1(i1.getOutput());
                                   }else gate.setInput1(-1);

                               }else if(i1.getType() == LogicObject.IO_DEVICE){
                                   gate.setInput1(i1.getOutput());
                               }else gate.setInput1(-1);
                           } else gate.setInput1(-1);

                           if (i2 != null ){
                               if(i2.getType() == LogicObject.WIRE) {
                                   if (((Wire) i2).outputsTop()){
                                       gate.setInput2(i2.getOutput());
                                   }else gate.setInput2(-1);
                               }else if(i2.getType() == LogicObject.IO_DEVICE){
                                   gate.setInput2(i2.getOutput());
                               }else gate.setInput2(-1);
                           } else gate.setInput2(-1);
                       }
                   }else if(gate.getStyle() == Gate.NOT_GATE){
                       if(bData.y>0){
                           LogicObject i1 = logicBoard[bData.x][bData.y-1];
                           if ( i1 != null){
                               if(i1.getType() == LogicObject.WIRE) {
                                   if (((Wire) i1).outputsRight()){
                                       gate.setInput1(i1.getOutput());
                                   }else {
                                       gate.setInput1(-1);
                                   }
                               }else if(i1.getType() == LogicObject.IO_DEVICE || i1.getType() == LogicObject.GATE){
                                   gate.setInput1(i1.getOutput());
                               }else {
                                   gate.setInput1(-1);
                               }
                           } else gate.setInput1(-1);
                       }
                   }
                   break;
               case LogicObject.IO_DEVICE:
                   boolean outputSet = false;
                   IO_Device io_device = (IO_Device)logicObject;

                   if(io_device.getStyle() == IO_Device.OUTPUT_STYLE) {

                       if (bData.y > 0) {
                           LogicObject i1 = logicBoard[bData.x][bData.y - 1];
                           if (i1 != null) {
                               if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsRight()) {
                                   io_device.setOutput(i1.getOutput());
                                   updateBoardTexture(bData.x, bData.y, io_device);
                                   outputSet = true;
                               }else if(i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE){
                                   io_device.setOutput(i1.getOutput());
                                   updateBoardTexture(bData.x, bData.y, io_device);
                                   outputSet = true;
                               }else outputSet = false;
                           } else {
                               outputSet = false;
                           }
                       }

                       if (!outputSet && bData.y < dimensions.y - 1) {
                           LogicObject i1 = logicBoard[bData.x][bData.y + 1];
                           if (i1 != null){
                               if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsLeft()) {
                                   io_device.setOutput(i1.getOutput());
                                   updateBoardTexture(bData.x, bData.y, io_device);
                                   outputSet = true;
                               }else outputSet = false;
                           }else outputSet = false;
                       }

                       if (!outputSet && bData.x > 0) {
                           LogicObject i1 = logicBoard[bData.x-1][bData.y];
                           if (i1 != null){
                               if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsBottom()) {
                                   io_device.setOutput(i1.getOutput());
                                   updateBoardTexture(bData.x, bData.y, io_device);
                                   outputSet = true;
                               }else outputSet = false;
                           }else outputSet = false;
                       }

                       if (!outputSet && bData.x < dimensions.x - 1) {
                           LogicObject i1 = logicBoard[bData.x+1][bData.y];
                           if (i1 != null){
                               if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsTop()) {
                                   io_device.setOutput(i1.getOutput());
                                   updateBoardTexture(bData.x, bData.y, io_device);
                                   outputSet = true;
                               }else outputSet = false;
                           }else outputSet = false;
                       }

                       if(!outputSet) {
                           io_device.setOutput(-1);
                           updateBoardTexture(bData.x, bData.y, io_device);
                       }
                   }
                   break;
               case LogicObject.WIRE:
                    handleWireInputLogic(bData);
                   break;
           }
        }
    }

    private void handleWireInputLogic(BoardData b){
        LogicObject logicObject = b.logicObject;
        Wire w = (Wire)logicObject;


       if(w.inputsLeft()) {
           if (b.y > 0) {
               LogicObject i1 = logicBoard[b.x][b.y - 1];
               if (i1 != null) {
                   if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsRight()) {
                       w.setInput(i1.getOutput());
                   } else if (i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE) {
                       w.setInput(i1.getOutput());
                   }
               } else {
                   w.setInput(-1);
               }
           }
       }else if(w.inputsRight()) {
            if (b.y < dimensions.y - 1) {
                LogicObject i1 = logicBoard[b.x][b.y + 1];
                if (i1 != null) {
                    if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsLeft()) {
                        w.setInput(i1.getOutput());
                    } else if (i1.getType() == LogicObject.IO_DEVICE) {
                        w.setInput(i1.getOutput());
                    }
                } else {
                    w.setInput(-1);
                }
            }
        }else if(w.inputsTop()) {
            if (b.x > 0) {
                LogicObject i1 = logicBoard[b.x - 1][b.y];
                if (i1 != null) {
                    if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsBottom()) {
                        w.setInput(i1.getOutput());
                    } else if (i1.getType() == LogicObject.IO_DEVICE) {
                        w.setInput(i1.getOutput());
                    }
                } else {
                    w.setInput(-1);
                }
            }
        }else if(w.inputsBottom()) {
            if (b.x < dimensions.x - 1) {
                LogicObject i1 = logicBoard[b.x + 1][b.y];
                if (i1 != null) {
                    if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsTop()) {
                        w.setInput(i1.getOutput());
                    } else if (i1.getType() == LogicObject.IO_DEVICE) {
                        w.setInput(i1.getOutput());
                    }
                } else {
                    w.setInput(-1);
                }
            }
        }
    }

    public void onTouchDown(MotionEvent event){
        listener.onTouchDown(event, this);
    }
    public void onTouchUp(MotionEvent event){
        listener.onTouchUp(event, this);
    }
    public void onTouchMove(MotionEvent event){
        listener.onTouchMove(event, this);
    }
    public void setTouchListener(TouchListener l){this.listener = l;}
    public SimpleVector getDimensions(){return dimensions;}
    public int getID(){return 99999;}
}

class BoardData{
    LogicObject logicObject;
    int x, y;
    public BoardData(LogicObject l, int x, int y){
        this.logicObject = l;this.x=x;this.y=y;
    }
}

package com.akashd50.lb.objects;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;

import com.akashd50.lb.R;
import com.akashd50.lb.ui.MainActivity;
import com.akashd50.lb.utils.Shader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class LogicBoard extends LogicObject{
    public static final int TOP = 1001;
    public static final int BOTTOM = 1002;
    public static final int LEFT = 1003;
    public static final int RIGHT = 1004;

    private int id;
    private SimpleVector dimensions, initialLineC1, initialLineC2, clickTestValue;
    private LogicObject[][] logicBoard;
    private Quad2D[][] drawingBoard;
    private ArrayList<BoardData> boardData, visualizationData;
    private ArrayList<BoardData> addedBoards;
    private TouchListener listener;
    private Texture emptyBox;
    private float boardLeft, boardTop;
    private int program;
    public double touchDownTime;
    private String name;

    private ArrayList<Line> boardLines;
    private boolean isNewType, isSelecting;

    private IO_Device[] inputs, outputs;
    private Line[] ioboxesMarking;

    private Quad2D selectionRegion;
    private Comparator<BoardData> ycomparator;
    public LogicBoard(SimpleVector dimens, int id){
        dimensions = dimens;
        logicBoard = new LogicObject[(int)dimensions.x][(int)dimensions.y];
        drawingBoard = new Quad2D[(int)dimensions.x][(int)dimensions.y];
        boardLines = new ArrayList<>();
        boardData = new ArrayList<>();
        addedBoards = new ArrayList<>();
        visualizationData = new ArrayList<BoardData>();
        clickTestValue = new SimpleVector();

        ycomparator = new Comparator<BoardData>() {
            @Override
            public int compare(BoardData o1, BoardData o2) {
                if(o1.y < o2.y){
                    return -1;
                }else if(o1.y == o2.y) {
                    return 0;
                } else{
                    return 1;
                }
            }
        };

        this.id = id;
        name = "Board "+id;

        isNewType = true;

        inputs = new IO_Device[3];
        outputs = new IO_Device[3];
        ioboxesMarking = new Line[16];

        isSelecting = false;
    }

    public void loadBoard(Context c){
        emptyBox = new Texture("empty", c,R.drawable.empty_board);

        program = Shader.getQuadTextureProgram();

        selectionRegion = new Quad2D(1.0f,1.0f);
        selectionRegion.setTextureUnit(emptyBox);
        selectionRegion.setRenderPreferences(program,Quad2D.REGULAR);
        selectionRegion.setOpacity(0.3f);
        selectionRegion.scale(new SimpleVector(0f,0f,1f));

        float left, top;
        float bx = 1.0f;
        float by = 1.0f;
        if(dimensions.x%2==0) left = -(dimensions.x/2 * bx);
        else left = -((dimensions.x-1)/2 - bx/2);

        if(dimensions.y%2==0) top = dimensions.y/2f * by;
        else top = (dimensions.y-1)/2 + by/2;

        boardLeft = left;
        boardTop = top;

        if(!isNewType) {
            for (int i = 0; i < (int) dimensions.x; i++) {
                for (int j = 0; j < (int) dimensions.y; j++) {
                    drawingBoard[i][j] = new Quad2D(bx, by);
                    drawingBoard[i][j].setDefaultLocation(new SimpleVector(left, top, 0f));
                    drawingBoard[i][j].setTextureUnit(emptyBox);
                    drawingBoard[i][j].scale(new SimpleVector(1f, 1f, 1f));
                    drawingBoard[i][j].setRenderPreferences(program, Quad2D.REGULAR);
                    drawingBoard[i][j].setOpacity(1.0f);
                    left += bx;
                }
                if (dimensions.x % 2 == 0) left = -(dimensions.x / 2 * bx);
                else left = -((dimensions.x - 1) / 2 - bx / 2);
                top -= by;
            }
            //prepare Lines
        }else {
            left = boardLeft;
            top = boardTop;

            top += 0.5f;
            left -= 0.5f;

            initialLineC1 = new SimpleVector(0.3f, 0.3f, 0.3f);
            initialLineC2 = new SimpleVector(1f, 0f, 1f);

            for (int i = 0; i < (int) dimensions.x + 1; i++) {
                Line line = new Line(new SimpleVector(left, top, 1f),
                        new SimpleVector(-left + 1.0f, top, 1f),
                        initialLineC1);

                boardLines.add(line);
                top -= by;
            }

            left = boardLeft;
            top = boardTop;
            top += 0.5f;
            left -= 0.5f;

            for (int i = 0; i < (int) dimensions.y + 1; i++) {
                Line line = new Line(new SimpleVector(left, top, 1f),
                        new SimpleVector(left, -top + 1.0f, 1f),
                        initialLineC1);

                left += bx;
                boardLines.add(line);
            }

            //IO lines

            left = boardLeft;
            top = boardTop;
            top += 0.5f;
            left -= 0.5f;

            Line line = new Line(new SimpleVector(left, top-1.0f, 1f),
                    new SimpleVector(left, -top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[0] = line;

            line = new Line(new SimpleVector(left+1.0f, top-1.0f, 1f),
                    new SimpleVector(left+1.0f, -top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[1] = line;

            line = new Line(new SimpleVector(-left, top-1.0f, 1f),
                    new SimpleVector(-left, -top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[2] = line;

            line = new Line(new SimpleVector(-left+1.0f, top-1.0f, 1f),
                    new SimpleVector(-left+1.0f, -top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[3] = line;

            line = new Line(new SimpleVector(left, top - 1.0f, 1f),
                    new SimpleVector(left+1.0f, top - 1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[4] = line;

            line = new Line(new SimpleVector(left, -top + 2.0f, 1f),
                    new SimpleVector(left+1.0f, -top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[5] = line;

            line = new Line(new SimpleVector(-left, top - 1.0f, 1f),
                    new SimpleVector(-left + 1.0f, top - 1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[6] = line;

            line = new Line(new SimpleVector(-left, - top + 2.0f, 1f),
                    new SimpleVector(-left + 1.0f, - top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[7] = line;

            //

            line = new Line(new SimpleVector(left+1.0f, top, 1f),
                    new SimpleVector(left+1.0f, top-1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[8] = line;

            line = new Line(new SimpleVector(-left, top, 1f),
                    new SimpleVector(-left, top-1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[9] = line;

            line = new Line(new SimpleVector(left+1.0f, - top + 2.0f, 1f),
                    new SimpleVector(left+1.0f, -top + 1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[10] = line;

            line = new Line(new SimpleVector(-left, -top + 2.0f, 1f),
                    new SimpleVector(-left, -top + 1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[11] = line;

            //
            line = new Line(new SimpleVector(left+1.0f, top, 1f),
                    new SimpleVector(-left, top, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[12] = line;

            line = new Line(new SimpleVector(left+1.0f, top-1.0f, 1f),
                    new SimpleVector(-left, top-1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[13] = line;

            line = new Line(new SimpleVector(left+1.0f, -top + 1.0f, 1f),
                    new SimpleVector(-left, -top + 1.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[14] = line;

            line = new Line(new SimpleVector(left+1.0f, - top+2.0f, 1f),
                    new SimpleVector(-left, -top + 2.0f, 1f),
                    new SimpleVector(1f, 1f, 1f));
            ioboxesMarking[15] = line;
        }
    }

    public synchronized void updateBoard(int x, int y, LogicObject l){
        if(!isNewType) {
            if(x<dimensions.x && y<dimensions.y && x>=0 && y>=0) {
                switch (l.getType()){
                    case LogicObject.WIRE:
                        logicBoard[x][y] = l;
                        drawingBoard[x][y].setTextureUnit(l.getTexture());
                        BoardData b = new BoardData(l,x,y);
                        boardData.add(b);
                        optimizeWires(b);
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
        }else {
            visualizationData.clear();
            if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
                switch (l.getType()) {
                    case LogicObject.WIRE:
                        Quad2D quad = l.getQuad();
                        quad.setDefaultLocation(new SimpleVector(boardLeft + y, boardTop - x, 0f));
                    //    quad.setTextureUnit(l.getTexture());
                        quad.scale(new SimpleVector(1f, 1f, 1f));
                        quad.setRenderPreferences(program, Quad2D.REGULAR);
                        quad.setOpacity(1.0f);
                        //drawableObjects.add(l);

                        if(logicBoard[x][y]!=null) {
                            clearBox(x,y);
                        }
                        logicBoard[x][y] = l;

                        BoardData b = new BoardData(l, x, y);
                        boardData.add(b);
                        optimizeWires(b);
                        break;
                    case LogicObject.GATE:

                        if(logicBoard[x][y]!=null) {
                            clearBox(x,y);
                        }

                        logicBoard[x][y] = l;
                        Quad2D quad1 = l.getQuad();
                        quad1.setDefaultLocation(new SimpleVector(boardLeft + y, boardTop - x, 0f));
                     //   quad1.setTextureUnit(l.getTexture());
                        quad1.scale(new SimpleVector(1f, 1f, 1f));
                        quad1.setRenderPreferences(program, Quad2D.REGULAR);
                        quad1.setOpacity(1.0f);
                       // drawableObjects.add(l);

                        boardData.add(new BoardData(l, x, y));
                        break;
                    case LogicObject.IO_DEVICE:

                        if(logicBoard[x][y]!=null) {
                            clearBox(x,y);
                        }
                        logicBoard[x][y] = l;
                        Quad2D quad2 = l.getQuad();
                        quad2.setDefaultLocation(new SimpleVector(boardLeft + y, boardTop - x, 0f));
                       // quad2.setTextureUnit(l.getTexture());
                        quad2.scale(new SimpleVector(1f, 1f, 1f));
                        quad2.setRenderPreferences(program, Quad2D.REGULAR);
                        quad2.setOpacity(1.0f);

                        boardData.add(new BoardData(l, x, y));
                        break;

                    case LogicObject.LOGIC_BOARD:
                        LogicBoard board = (LogicBoard)l;
                        ArrayList<BoardData> objects = new ArrayList<>(board.getBoardData());
                        for(BoardData data: objects){
                            int bx = x - ((int)board.getDimensions().x/2 -1) + data.x;
                            int by = y - ((int)board.getDimensions().y/2 -1) + data.y;
                            updateBoard(bx,by,data.logicObject);
                        }
                        break;
                }
            }
        }

        boardData.sort(ycomparator);

       /* for(BoardData b: boardData){
            System.out.println(b);
        }*/
    }

    public synchronized void visualizeBoard(int x, int y, LogicObject l, boolean sameObject){
        if(!sameObject) visualizationData.clear();

        if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
            switch (l.getType()) {
                case LogicObject.WIRE:

                    Quad2D quad = l.getQuad();
                    quad.setDefaultLocation(new SimpleVector(boardLeft + y, boardTop - x, 0f));
                    quad.scale(new SimpleVector(1f, 1f, 1f));
                    quad.setRenderPreferences(program, Quad2D.REGULAR);
                    quad.setOpacity(1.0f);

                    BoardData b = new BoardData(l, x, y);
                    visualizationData.add(b);
                    optimizeWires(b);
                    break;
                case LogicObject.GATE:

                    Quad2D quad1 = l.getQuad();
                    quad1.setDefaultLocation(new SimpleVector(boardLeft + y, boardTop - x, 0f));
                    quad1.scale(new SimpleVector(1f, 1f, 1f));
                    quad1.setRenderPreferences(program, Quad2D.REGULAR);
                    quad1.setOpacity(1.0f);

                    visualizationData.add(new BoardData(l, x, y));
                    break;
                case LogicObject.IO_DEVICE:

                    Quad2D quad2 = l.getQuad();
                    quad2.setDefaultLocation(new SimpleVector(boardLeft + y, boardTop - x, 0f));
                    quad2.scale(new SimpleVector(1f, 1f, 1f));
                    quad2.setRenderPreferences(program, Quad2D.REGULAR);
                    quad2.setOpacity(1.0f);

                    visualizationData.add(new BoardData(l, x, y));
                    break;

                case LogicObject.LOGIC_BOARD:
                    //visualizationData.clear();
                    LogicBoard board = (LogicBoard)l;
                    ArrayList<BoardData> objects = new ArrayList<>(board.getBoardData());
                    for(BoardData data: objects){
                        int bx = x - ((int)board.getDimensions().x/2 -1) + data.x;
                        int by = y - ((int)board.getDimensions().y/2 -1) + data.y;
                        visualizeBoard(bx,by,data.logicObject, true);
                    }
                    break;
            }
        }else{
            if(l instanceof LogicBoard) visualizationData.clear();
        }
    }

    public void updateBoardTexture(int x, int y, LogicObject l) {
        if(!isNewType) {
            if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
                logicBoard[x][y] = l;
                drawingBoard[x][y].setTextureUnit(l.getTexture());
            }
        }
    }

    public LogicObject get(int x, int y){
        if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
            return logicBoard[x][y];
        }else return null;
    }

    public synchronized void clearBox(int x, int y){
        if(!isNewType) {
            if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
                logicBoard[x][y] = null;
                drawingBoard[x][y].setTextureUnit(emptyBox);
                for (BoardData b : boardData) {
                    if (x == b.x && y == b.y) {
                        boardData.remove(b);
                        break;
                    }
                }
            }
        }else{
            if (x < dimensions.x && y < dimensions.y && x >= 0 && y >= 0) {
                logicBoard[x][y] = null;
                ListIterator<BoardData> listIterator = boardData.listIterator();
                while(listIterator.hasNext()){
                    BoardData b = listIterator.next();
                    if (x == b.x && y == b.y) {
                        boardData.remove(b);
                        break;
                    }
                }
            }
        }
    }

    public synchronized void onDrawFrame(float[] mMVPMatrix){
        if(!isNewType) {
            for (int i = 0; i < dimensions.x; i++) {
                for (int j = 0; j < dimensions.y; j++) {
                    drawingBoard[i][j].draw(mMVPMatrix);
                }
            }
        }else {
            for (BoardData b : boardData) {
                b.logicObject.onDrawFrame(mMVPMatrix);
            }

            for (BoardData b : visualizationData) {
                b.logicObject.onDrawFrame(mMVPMatrix);
            }

            if(isSelecting){
                selectionRegion.draw(mMVPMatrix);
            }

            Line.setLineWidth(5f);
            for (Line l: boardLines) {
               /* if(initialLineC1.x < 1.0){
                    initialLineC1.x += 0.0001f;
                }else{
                    initialLineC1.x = 0.0f;
                }

                if(initialLineC2.x < 1.0){
                    initialLineC2.x += 0.0002f;
                }else{
                    initialLineC2.x = 0.0f;
                }

                l.updateC1(initialLineC1);
                l.updateC2(initialLineC2);*/

                l.draw(mMVPMatrix);
            }

            Line.setLineWidth(100f);
            for(Line l: ioboxesMarking) {
                if(l!=null) l.draw(mMVPMatrix);
            }
        }
    }

    public void simulate(){
        for(int i=0;i<boardData.size();i++){
           BoardData bData = boardData.get(i);
           LogicObject logicObject = bData.logicObject;
           switch (logicObject.getType()){
               case LogicObject.GATE:
                 handleGateLogic(bData);
                   break;
               case LogicObject.IO_DEVICE:
                   handleIODeviceLogic(bData);
                   break;
               case LogicObject.WIRE:
                    handleWireInputLogic(bData);
                   break;
               case LogicObject.LOGIC_BOARD:
                   ((LogicBoard)logicObject).simulate();
                   break;
           }
        }
    }

    private void handleGateLogic(BoardData bData){
        Gate gate = (Gate)bData.logicObject;
        if(gate.getStyle() == Gate.OR_GATE || gate.getStyle() == Gate.AND_GATE || gate.getStyle() == Gate.XOR_GATE) {
            if (bData.x > 0 && bData.x < dimensions.x - 1) {
                LogicObject i1 =logicBoard[bData.x - 1][bData.y];
                LogicObject i2 =logicBoard[bData.x + 1][bData.y];
                if (i1 != null) {
                    if(i1.getType() == LogicObject.WIRE) {
                        if (((Wire) i1).outputsBottom()){
                            gate.setInput1(i1.getOutput1());
                        }else gate.setInput1(-1);

                    }else if(i1.getType() == LogicObject.IO_DEVICE){
                        gate.setInput1(i1.getOutput1());
                    }else gate.setInput1(-1);
                } else gate.setInput1(-1);

                if (i2 != null ){
                    if(i2.getType() == LogicObject.WIRE) {
                        if (((Wire) i2).outputsTop()){
                            gate.setInput2(i2.getOutput1());
                        }else gate.setInput2(-1);
                    }else if(i2.getType() == LogicObject.IO_DEVICE){
                        gate.setInput2(i2.getOutput1());
                    }else gate.setInput2(-1);
                } else gate.setInput2(-1);
            }
        }else if(gate.getStyle() == Gate.NOT_GATE){
            if(bData.y>0){
                LogicObject i1 = logicBoard[bData.x][bData.y-1];
                if ( i1 != null){
                    if(i1.getType() == LogicObject.WIRE) {
                        if (((Wire) i1).outputsRight()){
                            gate.setInput1(i1.getOutput1());
                        }else {
                            gate.setInput1(-1);
                        }
                    }else if(i1.getType() == LogicObject.IO_DEVICE || i1.getType() == LogicObject.GATE){
                        gate.setInput1(i1.getOutput1());
                    }else {
                        gate.setInput1(-1);
                    }
                } else gate.setInput1(-1);
            }
        }
    }

    private void handleIODeviceLogic(BoardData bData){
        boolean outputSet = false;
        IO_Device io_device = (IO_Device)bData.logicObject;

        if(io_device.getStyle() == IO_Device.OUTPUT_STYLE) {

            if (bData.y > 0) {
                LogicObject i1 = logicBoard[bData.x][bData.y - 1];
                if (i1 != null) {
                    if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsRight()) {
                        io_device.setOutput(i1.getOutput1());

                        updateBoardTexture(bData.x, bData.y, io_device);
                        outputSet = true;
                    }else if(i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE){
                        io_device.setOutput(i1.getOutput1());
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
                        io_device.setOutput(i1.getOutput1());
                        updateBoardTexture(bData.x, bData.y, io_device);
                        outputSet = true;
                    }else outputSet = false;
                }else outputSet = false;
            }

            if (!outputSet && bData.x > 0) {
                LogicObject i1 = logicBoard[bData.x-1][bData.y];
                if (i1 != null){
                    if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsBottom()) {

                        if(((Wire)i1).hasDualInputs()) io_device.setOutput(((Wire)i1).getOutput2());
                        else io_device.setOutput(i1.getOutput1());

                        //io_device.setOutput(i1.getOutput1());
                        updateBoardTexture(bData.x, bData.y, io_device);
                        outputSet = true;
                    }else outputSet = false;
                }else outputSet = false;
            }

            if (!outputSet && bData.x < dimensions.x - 1) {
                LogicObject i1 = logicBoard[bData.x+1][bData.y];
                if (i1 != null){
                    if(i1.getType() == LogicObject.WIRE && ((Wire)i1).outputsTop()) {

                        if(((Wire)i1).hasDualInputs()) io_device.setOutput(((Wire)i1).getOutput2());
                        else io_device.setOutput(i1.getOutput1());

                        //io_device.setOutput(i1.getOutput1());
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
    }

    private void handleWireInputLogic(BoardData b){
        LogicObject logicObject = b.logicObject;
        Wire w = (Wire)logicObject;

        if(!w.hasDualInputs()) {
            if (w.inputsLeft()) {
                if (b.y > 0) {
                    LogicObject i1 = logicBoard[b.x][b.y - 1];
                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsRight()) {
                            w.setInput1(i1.getOutput1());
                        } else if (i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }
                }
            } else if (w.inputsRight()) {
                if (b.y < dimensions.y - 1) {
                    LogicObject i1 = logicBoard[b.x][b.y + 1];
                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsLeft()) {
                            w.setInput1(i1.getOutput1());
                        } else if (i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }
                }
            } else if (w.inputsTop()) {
                if (b.x > 0) {
                    LogicObject i1 = logicBoard[b.x - 1][b.y];
                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsBottom()) {

                            if(((Wire)i1).hasDualInputs()) w.setInput1(((Wire)i1).getOutput2());
                            else w.setInput1(i1.getOutput1());

                        } else if (i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }
                }
            } else if (w.inputsBottom()) {
                if (b.x < dimensions.x - 1) {
                    LogicObject i1 = logicBoard[b.x + 1][b.y];
                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsTop()) {
                            //w.setInput1(i1.getOutput1());
                            if(((Wire)i1).hasDualInputs()) w.setInput1(((Wire)i1).getOutput2());
                            else w.setInput1(i1.getOutput1());

                        } else if (i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }
                }
            }
        }else {
            if (w.inputsLeft() && w.inputsBottom()) {
                if (b.y > 0 && b.x < dimensions.x - 1) {
                    LogicObject i1 = logicBoard[b.x][b.y - 1];
                    LogicObject i2 = logicBoard[b.x + 1][b.y];

                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsRight()) {
                            w.setInput1(i1.getOutput1());
                        } else if (i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }

                    if (i2 != null) {
                        if (i2.getType() == LogicObject.WIRE && ((Wire) i2).outputsTop()) {
                            if(((Wire) i2).hasDualInputs()){
                                w.setInput2(((Wire)i2).getOutput2());
                            }else{
                                w.setInput2(i2.getOutput1());
                            }

                        } else if (i2.getType() == LogicObject.IO_DEVICE) {
                            w.setInput2(i2.getOutput1());
                        }
                    } else {
                        w.setInput2(-1);
                    }
                }
            } else if (w.inputsLeft() && w.inputsTop()) {
                if (b.y > 0 && b.x>0) {
                    LogicObject i1 = logicBoard[b.x][b.y - 1];
                    LogicObject i2 = logicBoard[b.x - 1][b.y];

                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsRight()) {
                            w.setInput1(i1.getOutput1());
                        } else if (i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }

                    if (i2 != null) {
                        if (i2.getType() == LogicObject.WIRE && ((Wire) i2).outputsBottom()) {
                            if(((Wire) i2).hasDualInputs()){
                                w.setInput2(((Wire)i2).getOutput2());
                            }else{
                                w.setInput2(i2.getOutput1());
                            }

                        } else if (i2.getType() == LogicObject.IO_DEVICE) {
                            w.setInput2(i2.getOutput1());
                        }
                    } else {
                        w.setInput2(-1);
                    }
                }
            } else if (w.inputsRight() && w.inputsBottom()) {
                if (b.y < dimensions.y - 1 && b.x < dimensions.x - 1) {
                    LogicObject i1 = logicBoard[b.x][b.y + 1];
                    LogicObject i2 = logicBoard[b.x + 1][b.y];

                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsLeft()) {
                            w.setInput1(i1.getOutput1());
                        } else if (i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }

                    if (i2 != null) {
                        if (i2.getType() == LogicObject.WIRE && ((Wire) i2).outputsTop()) {
                            if(((Wire) i2).hasDualInputs()){
                                w.setInput2(((Wire)i2).getOutput2());
                            }else{
                                w.setInput2(i2.getOutput1());
                            }

                        } else if (i2.getType() == LogicObject.IO_DEVICE) {
                            w.setInput2(i2.getOutput1());
                        }
                    } else {
                        w.setInput2(-1);
                    }
                }
            } else if (w.inputsRight() && w.inputsTop()) {
                if (b.y < dimensions.y - 1 && b.x > 0) {
                    LogicObject i1 = logicBoard[b.x][b.y + 1];
                    LogicObject i2 = logicBoard[b.x - 1][b.y];

                    if (i1 != null) {
                        if (i1.getType() == LogicObject.WIRE && ((Wire) i1).outputsLeft()) {
                            w.setInput1(i1.getOutput1());
                        } else if (i1.getType() == LogicObject.GATE || i1.getType() == LogicObject.IO_DEVICE) {
                            w.setInput1(i1.getOutput1());
                        }
                    } else {
                        w.setInput1(-1);
                    }

                    if (i2 != null) {
                        if (i2.getType() == LogicObject.WIRE && ((Wire) i2).outputsBottom()) {
                            if(((Wire) i2).hasDualInputs()){
                                w.setInput2(((Wire)i2).getOutput2());
                            }else{
                                w.setInput2(i2.getOutput1());
                            }

                        } else if (i2.getType() == LogicObject.IO_DEVICE) {
                            w.setInput2(i2.getOutput1());
                        }
                    } else {
                        w.setInput2(-1);
                    }
                }
            }
        }
    }

    public void optimizeWires(BoardData b){
        Wire currentWire = (Wire)b.logicObject;

        if(currentWire.inputsTop()){
            if(b.x > 0){
                LogicObject obj = logicBoard[b.x-1][b.y];
                if(obj!=null && obj.getType() == LogicObject.WIRE) {
                    Wire wire = (Wire) obj;
                    if (!wire.hasDualInputs()) {
                        if (!wire.outputsBottom()) {
                            if (wire.inputsLeft()) {
                            /*if (wire.outputsRight()) {
                                updateBoard(b.x - 1, b.y, new Wire(Wire.WIRE_LBR));
                            } else if(wire.outputsTop()) {
                                //updateBoard(b.x - 1, b.y, new Wire(Wire.WIRE_L));
                            } else if(wire.outputsBottom()) {
                                updateBoard(b.x - 1, b.y, new Wire(Wire.WIRE_RBL));
                            }*/
                                clearBox(b.x - 1, b.y);
                                Wire nw = new Wire(Wire.WIRE_LTRB);
                                logicBoard[b.x - 1][b.y] = nw;
                                //drawingBoard[b.x - 1][b.y].setTextureUnit(nw.getTexture());
                                Quad2D quad = nw.getQuad();
                                quad.setDefaultLocation(new SimpleVector(boardLeft + b.y, boardTop - b.x+1, 0f));
                                quad.setTextureUnit(nw.getTexture());
                                quad.scale(new SimpleVector(1f, 1f, 1f));
                                quad.setRenderPreferences(program, Quad2D.REGULAR);
                                quad.setOpacity(1.0f);

                                boardData.add(new BoardData(nw, b.x - 1, b.y));
                            } else if (wire.inputsRight()) {
                                clearBox(b.x - 1, b.y);
                                Wire nw = new Wire(Wire.WIRE_RBLT);
                                logicBoard[b.x - 1][b.y] = nw;
                                //drawingBoard[b.x - 1][b.y].setTextureUnit(nw.getTexture());
                                Quad2D quad = nw.getQuad();
                                quad.setDefaultLocation(new SimpleVector(boardLeft + b.y, boardTop - b.x+1, 0f));
                                quad.setTextureUnit(nw.getTexture());
                                quad.scale(new SimpleVector(1f, 1f, 1f));
                                quad.setRenderPreferences(program, Quad2D.REGULAR);
                                quad.setOpacity(1.0f);

                                boardData.add(new BoardData(nw, b.x - 1, b.y));
                            } else if (wire.inputsTop()) {
                                clearBox(b.x - 1, b.y);
                                Wire nw = new Wire(Wire.WIRE_TRBL);
                                logicBoard[b.x - 1][b.y] = nw;

                                Quad2D quad = nw.getQuad();
                                quad.setDefaultLocation(new SimpleVector(boardLeft + b.y, boardTop - b.x+1, 0f));
                                quad.setTextureUnit(nw.getTexture());
                                quad.scale(new SimpleVector(1f, 1f, 1f));
                                quad.setRenderPreferences(program, Quad2D.REGULAR);
                                quad.setOpacity(1.0f);

                                //drawingBoard[b.x - 1][b.y].setTextureUnit(nw.getTexture());
                                boardData.add(new BoardData(nw, b.x - 1, b.y));
                            }
                        }
                    }
                }
            }
        }else if(currentWire.inputsBottom()){
            if(b.x < dimensions.x-1){
                LogicObject obj = logicBoard[b.x+1][b.y];
                if(obj!=null && obj.getType() == LogicObject.WIRE){
                    Wire wire = (Wire)obj;
                    if(!wire.hasDualInputs()) {
                        if (!wire.outputsTop()) {
                            if (wire.inputsLeft()) {
                            /*if (wire.outputsRight()) {
                                updateBoard(b.x - 1, b.y, new Wire(Wire.WIRE_LBR));
                            } else if(wire.outputsTop()) {
                                //updateBoard(b.x - 1, b.y, new Wire(Wire.WIRE_L));
                            } else if(wire.outputsBottom()) {
                                updateBoard(b.x - 1, b.y, new Wire(Wire.WIRE_RBL));
                            }*/
                                clearBox(b.x + 1, b.y);
                                Wire nw = new Wire(Wire.WIRE_LTRB);
                                logicBoard[b.x + 1][b.y] = nw;

                                Quad2D quad = nw.getQuad();
                                quad.setDefaultLocation(new SimpleVector(boardLeft + b.y, boardTop - b.x-1, 0f));
                                quad.setTextureUnit(nw.getTexture());
                                quad.scale(new SimpleVector(1f, 1f, 1f));
                                quad.setRenderPreferences(program, Quad2D.REGULAR);
                                quad.setOpacity(1.0f);
                                //drawingBoard[b.x + 1][b.y].setTextureUnit(nw.getTexture());
                                boardData.add(new BoardData(nw, b.x + 1, b.y));
                            } else if (wire.inputsRight()) {
                                clearBox(b.x + 1, b.y);
                                Wire nw = new Wire(Wire.WIRE_RBLT);
                                logicBoard[b.x + 1][b.y] = nw;

                                Quad2D quad = nw.getQuad();
                                quad.setDefaultLocation(new SimpleVector(boardLeft + b.y, boardTop - b.x-1, 0f));
                                quad.setTextureUnit(nw.getTexture());
                                quad.scale(new SimpleVector(1f, 1f, 1f));
                                quad.setRenderPreferences(program, Quad2D.REGULAR);
                                quad.setOpacity(1.0f);

                               // drawingBoard[b.x + 1][b.y].setTextureUnit(nw.getTexture());
                                boardData.add(new BoardData(nw, b.x + 1, b.y));
                            } else if (wire.inputsBottom()) {
                                clearBox(b.x + 1, b.y);
                                Wire nw = new Wire(Wire.WIRE_BLTR);
                                logicBoard[b.x + 1][b.y] = nw;

                                Quad2D quad = nw.getQuad();
                                quad.setDefaultLocation(new SimpleVector(boardLeft + b.y, boardTop - b.x-1, 0f));
                                quad.setTextureUnit(nw.getTexture());
                                quad.scale(new SimpleVector(1f, 1f, 1f));
                                quad.setRenderPreferences(program, Quad2D.REGULAR);
                                quad.setOpacity(1.0f);

                                //drawingBoard[b.x + 1][b.y].setTextureUnit(nw.getTexture());
                                boardData.add(new BoardData(nw, b.x + 1, b.y));
                            }
                        }
                    }
                }
            }
        }else if(currentWire.inputsLeft()){

        }else if(currentWire.inputsRight()){

        }
    }

    public void addBoardInput(int dir, IO_Device input){
        switch (dir){
            case TOP:
                this.inputs[0] = input;
                break;
            case LEFT:
                this.inputs[1] = input;
                break;
            case BOTTOM:
                this.inputs[2] = input;
                break;
        }
    }

    public void addBoardOutput(int dir, IO_Device output){
        switch (dir){
            case TOP:
                this.outputs[0] = output;
                break;
            case RIGHT:
                this.outputs[1] = output;
                break;
            case BOTTOM:
                this.outputs[2] = output;
                break;
        }
    }

    public IO_Device getBoardInput(int dir){
        switch (dir){
            case TOP:
                return this.inputs[0];
            case LEFT:
                return this.inputs[1];
            case BOTTOM:
                return this.inputs[2];
        }
        return null;
    }

    public IO_Device getBoardOutput(int dir){
        switch (dir){
            case TOP:
                return this.outputs[0];
            case RIGHT:
                return this.outputs[1];
            case BOTTOM:
                return this.outputs[2];
        }
        return null;
    }

    public void onTouchDown(final MotionEvent event){
        /*checkLongPressed = true;
        touchDownTime = System.currentTimeMillis();
        clickTestValue.x = event.getRawX();
        clickTestValue.y = event.getRawY();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(checkLongPressed){
                    if(System.currentTimeMillis() - touchDownTime > 1000){
                        if(Math.abs(event.getRawX() - clickTestValue.x) > 10 || Math.abs(event.getRawY() - clickTestValue.y) > 10){

                        }
                    }
                }
            }
        }).start();*/
        listener.onTouchDown(event, null);
    }
    public void onTouchUp(MotionEvent event){
        listener.onTouchUp(event, null);
    }
    public void onTouchMove(MotionEvent event){
        listener.onTouchMove(event, null);
    }


    public void setTouchListener(TouchListener l){this.listener = l;}
    public SimpleVector getDimensions(){return dimensions;}
    public int getID(){return this.id;}
    public void setID(int id){this.id = id;}
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    public List<BoardData> getBoardData(){
        return this.boardData;
    }

    public int getStyle(){
        return 0;
    }

    public int getType(){
        return LOGIC_BOARD;
    }

    public int getOutput1(){
        return 0;
    }
    public void addDataToBoard(ArrayList<BoardData> list){
        for(BoardData b: list){
            updateBoard(b.x, b.y, b.logicObject);
        }
    }

    public String toString(){
        return this.name;
    }
}


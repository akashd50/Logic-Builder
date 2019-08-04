package com.akashd50.lb.objects;

import com.akashd50.lb.utils.Utilities;

public class IO_Device extends LogicObject {
    public static final int INPUT_STYLE_1 = 1001;
    public static final int INPUT_STYLE_0 = 1003;
    public static final int OUTPUT_STYLE = 1002;


    private int style;
    //private int input, output;
    private int[] outputBuffer;
    public IO_Device(int style){
        super();
        this.style = style;
        switch (style){
            case INPUT_STYLE_0:
                outputBuffer = new int[1];
                setOutput(0);
                break;
            case INPUT_STYLE_1:
                outputBuffer = new int[1];
                setOutput(1);
                break;
            case OUTPUT_STYLE:
                outputBuffer = new int[1];
                setOutput(-1);
                break;
        }
    }

    @Override
    public int getType() {
        return IO_DEVICE;
    }

  /*  public void setInput1(int input, Texture t) {
        this.input = input;
        this.setTexture(t);
    }*/

    public void setOutput(int output) {
        this.outputBuffer[0] = output;
        if (output == 1) {
            this.setTexture(LogicObject.textureContainer.getTexture(Utilities.displayOneT));
        } else if (output == 0) {
            this.setTexture(LogicObject.textureContainer.getTexture(Utilities.displayZeroT));
        } else if (output == -1) {
            this.setTexture(LogicObject.textureContainer.getTexture(Utilities.displayEmptyT));
        }
    }

    public int getStyle(){return this.style;}

    public int getOutput1() {
        return outputBuffer[0];
    }

    public int getInput() {
        return outputBuffer[0];
    }

    public int[] getOutputBuffer(){return outputBuffer;}
}

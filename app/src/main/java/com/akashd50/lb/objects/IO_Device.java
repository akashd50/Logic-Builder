package com.akashd50.lb.objects;

public class IO_Device extends LogicObject {
    public static final int INPUT_STYLE_1 = 1001;
    public static final int INPUT_STYLE_0 = 1003;
    public static final int OUTPUT_STYLE = 1002;
    private int style;
    private int input, output;
    public IO_Device(int style){
        this.style = style;
        output =-1;
    }

    @Override
    public int getType() {
        return IO_DEVICE;
    }

  /*  public void setInput(int input, Texture t) {
        this.input = input;
        this.setTexture(t);
    }*/

    public void setOutput(int output) {
        this.output = output;
        if(output ==1){
            this.setTexture(super.IO_DEVICE_1);
        }else if(output ==0){
            this.setTexture(super.IO_DEVICE_0);
        }else if(output ==-1 ){
            this.setTexture(super.IO_DEVICE_EMPTY);
        }
    }

    public int getStyle(){return this.style;}
    public int getOutput() {
        return output;
    }
    public int getInput() {
        return input;
    }
}

package com.akashd50.lb.objects;

public class Gate extends LogicObject {
    public static final int OR_GATE = 1001;
    public static final int AND_GATE = 1002;
    public static final int NOT_GATE = 1003;
    private int style;
    private int input1,input2,output;
    public Gate(int style, Texture t){
        this.style = style;
        super.setTexture(t);
        input1 = -1;
        input2 = -1;
    }

    public int getType(){
        return GATE;
    }
    public int getInput1() {
        return input1;
    }
    public void setInput1(int input1) {
        this.input1 = input1;
    }
    public int getInput2() {
        return input2;
    }
    public void setInput2(int input2) {
        this.input2 = input2;
    }
    public int getOutput() {
        if(style == OR_GATE) {
            if (input1==-1 || input2 == -1) {
                output = -1;
            }else{
                if(input1 == 1 || input2 == 1) { output = 1; }
                else output = 0;
            }
        }else if(style == AND_GATE){
            if (input1==-1 || input2 == -1) {
                output = -1;
            }else{
                if(input1 == 1 && input2 == 1) { output = 1; }
                else output = 0;
            }
        }else if(style == NOT_GATE){
            if (input1==-1) {
                output = -1;
            }else{
                if(input1 == 1) { output = 0; }
                else output = 1;
            }
        }
        return output;
    }
    public int getStyle(){return style;}
}

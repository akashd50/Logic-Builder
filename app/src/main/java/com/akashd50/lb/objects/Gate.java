package com.akashd50.lb.objects;

import com.akashd50.lb.utils.Utilities;

public class Gate extends LogicObject {
    public static final int OR_GATE = 1001;
    public static final int AND_GATE = 1002;
    public static final int NOT_GATE = 1003;
    public static final int XOR_GATE = 1004;

    private int style;
    private int input1,input2,output;
    public Gate(int style){
        super();
        this.style = style;
        switch (style){
            case OR_GATE:
                setTexture(textureContainer.getTexture(Utilities.orGateT));
                break;
            case AND_GATE:
                setTexture(textureContainer.getTexture(Utilities.andGateT));
                break;
            case NOT_GATE:
                setTexture(textureContainer.getTexture(Utilities.notGateT));
                break;
            case XOR_GATE:
                setTexture(textureContainer.getTexture(Utilities.xorGateT));
                break;
        }
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
    public int getOutput1() {
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
        }else if(style == XOR_GATE){
            if((input1 == 1 && input2 == 1) || (input1 == 0 && input2 == 0)){
                output = 0;
            }else{
                output = 1;
            }
        }
        return output;
    }
    public int getStyle(){return style;}
}

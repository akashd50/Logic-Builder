package com.akashd50.lb.objects;

public class Wire extends LogicObject {
    public static final int WIRE_LR = 1001;
    public static final int WIRE_RL = 1002;
    public static final int WIRE_TB = 1003;
    public static final int WIRE_BT = 1004;
    public static final int WIRE_BL = 1005;
    public static final int WIRE_BR = 1006;
    public static final int WIRE_TL = 1090;
    public static final int WIRE_TR = 1008;
    public static final int WIRE_LT = 1009;
    public static final int WIRE_LB = 1010;
    public static final int WIRE_RT = 1011;
    public static final int WIRE_RB = 1012;

    public static final int WIRE_TRB = 1013;
    public static final int WIRE_TLB = 1014;
    public static final int WIRE_RBL = 1015;
    public static final int WIRE_RTL = 1016;
    public static final int WIRE_BLT = 1017;
    public static final int WIRE_BRT = 1018;
    public static final int WIRE_LBR = 1019;
    public static final int WIRE_LTR = 1020;
    public static final int WIRE_TRBL = 1021;
    public static final int WIRE_RBLT = 1022;
    public static final int WIRE_BLTR = 1023;
    public static final int WIRE_LTRB = 1024;

    private int input, output;
    private int style;
    public Wire(int style){
        this.style = style;
    }

    @Override
    public int getType() {
        return WIRE;
    }

    public int getInput() {
        return input;
    }

    public int getOutput() {
        this.output = input;
        return output;
    }

    public boolean inputsBottom(){
        return (style==WIRE_BT || style==WIRE_BL || style == WIRE_BR
        || style==WIRE_BRT || style==WIRE_BLT || style == WIRE_BLTR);
    }
    public boolean inputsTop(){
        return (style==WIRE_TB || style==WIRE_TL || style == WIRE_TR
        ||style==WIRE_TRB|| style==WIRE_TLB || style == WIRE_TRBL);
    }

    public boolean inputsRight(){
        return (style==WIRE_RL || style==WIRE_RB || style == WIRE_RT
                || style == WIRE_RBL || style == WIRE_RTL || style == WIRE_RBLT);
    }

    public boolean inputsLeft(){
        return (style==WIRE_LR || style==WIRE_LB || style == WIRE_LT
                || style==WIRE_LTR|| style==WIRE_LBR || style == WIRE_LTRB);
    }

    public boolean outputsRight(){
        return (style==WIRE_LR || style==WIRE_BR || style == WIRE_TR
                || style == WIRE_TRB || style == WIRE_LBR || style == WIRE_LTR || style == WIRE_TRBL
                || style == WIRE_BLTR || style == WIRE_LTRB);
    }

    public boolean outputsLeft(){
        return (style==WIRE_RL || style==WIRE_BL || style == WIRE_TL
                || style==WIRE_TLB || style==WIRE_RBL || style == WIRE_RTL
                ||style==WIRE_BLT || style==WIRE_TRBL || style == WIRE_RBLT || style==WIRE_BLTR);
    }

    public boolean outputsBottom(){
        return (style==WIRE_TB || style==WIRE_LB || style == WIRE_RB
                || style==WIRE_TRB || style==WIRE_TLB || style == WIRE_LBR
                || style==WIRE_RBL || style==WIRE_TRBL || style == WIRE_RBLT || style==WIRE_LTRB);
    }
    public boolean outputsTop(){
        return (style==WIRE_BT || style==WIRE_LT || style == WIRE_RT
                ||style==WIRE_RTL || style==WIRE_LTR || style == WIRE_BRT
                ||style==WIRE_BLT || style==WIRE_BLTR || style == WIRE_LTRB|| style==WIRE_RBLT);
    }

    public void setInput(int input) {
        this.input = input;
    }

    public void setOutput(int output) {
        this.output = output;
    }

    public int getStyle(){return this.style;}
}

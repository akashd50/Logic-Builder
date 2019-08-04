package com.akashd50.lb.objects;

import com.akashd50.lb.utils.Utilities;

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

    public static final int WIRE_PS_TBLR = 1025;
    public static final int WIRE_PS_TBRL = 1026;
    public static final int WIRE_PS_BTLR = 1027;
    public static final int WIRE_PS_BTRL = 1028;

    private int input1, output1, input2, output2;
    private int style;
    public Wire(int style){
        super();
        this.style = style;
        switch (style){
            case WIRE_LR:
                setTexture(textureContainer.getTexture(Utilities.wirelrT));
                break;
            case WIRE_RL:
                setTexture(textureContainer.getTexture(Utilities.wirerlT));
                break;
            case WIRE_TB:
                setTexture(textureContainer.getTexture(Utilities.wiretbT));
                break;
            case WIRE_BT:
                setTexture(textureContainer.getTexture(Utilities.wirebtT));
                break;
            case WIRE_BL:
                setTexture(textureContainer.getTexture(Utilities.wireblT));
                break;
            case WIRE_BR:
                setTexture(textureContainer.getTexture(Utilities.wirebrT));
                break;
            case WIRE_TL:
                setTexture(textureContainer.getTexture(Utilities.wiretlT));
                break;
            case WIRE_TR:
                setTexture(textureContainer.getTexture(Utilities.wiretrT));
                break;
            case WIRE_LT:
                setTexture(textureContainer.getTexture(Utilities.wireltT));
                break;
            case WIRE_LB:
                setTexture(textureContainer.getTexture(Utilities.wirelbT));
                break;
            case WIRE_RT:
                setTexture(textureContainer.getTexture(Utilities.wirertT));
                break;
            case WIRE_RB:
                setTexture(textureContainer.getTexture(Utilities.wirerbT));
                break;
            case WIRE_TRB:
                setTexture(textureContainer.getTexture(Utilities.wiretrbT));
                break;
            case WIRE_TLB:
                setTexture(textureContainer.getTexture(Utilities.wiretlbT));
                break;
            case WIRE_RBL:
                setTexture(textureContainer.getTexture(Utilities.wirerblT));
                break;
            case WIRE_RTL:
                setTexture(textureContainer.getTexture(Utilities.wirertlT));
                break;
            case WIRE_BLT:
                setTexture(textureContainer.getTexture(Utilities.wirebltT));
                break;
            case WIRE_BRT:
                setTexture(textureContainer.getTexture(Utilities.wirebrtT));
                break;
            case WIRE_LBR:
                setTexture(textureContainer.getTexture(Utilities.wirelbrT));
                break;
            case WIRE_LTR:
                setTexture(textureContainer.getTexture(Utilities.wireltrT));
                break;
            case WIRE_TRBL:
                setTexture(textureContainer.getTexture(Utilities.wiretrblT));
                break;
            case WIRE_RBLT:
                setTexture(textureContainer.getTexture(Utilities.wirerbltT));
                break;
            case WIRE_BLTR:
                setTexture(textureContainer.getTexture(Utilities.wirebltrT));
                break;
            case WIRE_LTRB:
                setTexture(textureContainer.getTexture(Utilities.wireltrbT));
                break;
            case WIRE_PS_TBLR:
                setTexture(textureContainer.getTexture(Utilities.wire_ps_tblr));
                break;
            case WIRE_PS_TBRL:
                setTexture(textureContainer.getTexture(Utilities.wire_ps_tbrl));
                break;
            case WIRE_PS_BTLR:
                setTexture(textureContainer.getTexture(Utilities.wire_ps_btlr));
                break;
            case WIRE_PS_BTRL:
                setTexture(textureContainer.getTexture(Utilities.wire_ps_btrl));
                break;
        }
    }

    @Override
    public int getType() {
        return WIRE;
    }

    public int getInput1() {
        return input1;
    }

    public int getOutput1() {
        this.output1 = input1;
        return output1;
    }

    public int getOutput2() {
        this.output2 = input2;
        return output2;
    }

    public boolean hasDualInputs(){
        return (style == WIRE_PS_BTLR || style == WIRE_PS_BTRL || style == WIRE_PS_TBLR || style == WIRE_PS_TBRL);
    }

    public boolean inputsBottom(){
        return (style==WIRE_BT || style==WIRE_BL || style == WIRE_BR
        || style==WIRE_BRT || style==WIRE_BLT || style == WIRE_BLTR
                || style == WIRE_PS_BTLR || style == WIRE_PS_BTRL);
    }
    public boolean inputsTop(){
        return (style==WIRE_TB || style==WIRE_TL || style == WIRE_TR
        ||style==WIRE_TRB|| style==WIRE_TLB || style == WIRE_TRBL
                || style == WIRE_PS_TBLR || style == WIRE_PS_TBRL);
    }

    public boolean inputsRight(){
        return (style==WIRE_RL || style==WIRE_RB || style == WIRE_RT
                || style == WIRE_RBL || style == WIRE_RTL || style == WIRE_RBLT
                || style == WIRE_PS_BTRL || style == WIRE_PS_TBRL);
    }

    public boolean inputsLeft(){
        return (style==WIRE_LR || style==WIRE_LB || style == WIRE_LT
                || style==WIRE_LTR|| style==WIRE_LBR || style == WIRE_LTRB
                || style == WIRE_PS_BTLR || style == WIRE_PS_TBLR);
    }

    public boolean outputsRight(){
        return (style==WIRE_LR || style==WIRE_BR || style == WIRE_TR
                || style == WIRE_TRB || style == WIRE_LBR || style == WIRE_LTR || style == WIRE_TRBL
                || style == WIRE_BLTR || style == WIRE_LTRB
                || style == WIRE_PS_TBLR || style == WIRE_PS_BTLR);
    }

    public boolean outputsLeft(){
        return (style==WIRE_RL || style==WIRE_BL || style == WIRE_TL
                || style==WIRE_TLB || style==WIRE_RBL || style == WIRE_RTL
                ||style==WIRE_BLT || style==WIRE_TRBL || style == WIRE_RBLT || style==WIRE_BLTR
                || style == WIRE_PS_TBRL || style == WIRE_PS_BTRL);
    }

    public boolean outputsBottom(){
        return (style==WIRE_TB || style==WIRE_LB || style == WIRE_RB
                || style==WIRE_TRB || style==WIRE_TLB || style == WIRE_LBR
                || style==WIRE_RBL || style==WIRE_TRBL || style == WIRE_RBLT || style==WIRE_LTRB
                || style == WIRE_PS_TBLR || style == WIRE_PS_TBRL);
    }
    public boolean outputsTop(){
        return (style==WIRE_BT || style==WIRE_LT || style == WIRE_RT
                ||style==WIRE_RTL || style==WIRE_LTR || style == WIRE_BRT
                ||style==WIRE_BLT || style==WIRE_BLTR || style == WIRE_LTRB|| style==WIRE_RBLT
                || style == WIRE_PS_BTLR || style == WIRE_PS_BTRL);
    }

    public void setInput1(int input) {
        this.input1 = input;
    }

    public void setOutput1(int output1) {
        this.output1 = output1;
    }

    public void setInput2(int input) {
        this.input2 = input;
    }

    public void setOutput2(int output2) {
        this.output2 = output2;
    }


    public int getStyle(){return this.style;}
}

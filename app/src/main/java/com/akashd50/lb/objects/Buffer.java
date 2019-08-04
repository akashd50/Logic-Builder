package com.akashd50.lb.objects;

public class Buffer extends LogicObject {
    public static final int BUFFER_INPUT_2_BIT = 1004;
    public static final int BUFFER_INPUT_4_BIT = 1005;
    public static final int BUFFER_INPUT_8_BIT = 1006;
    public static final int BUFFER_INPUT_16_BIT = 1007;

    public static final int BUFFER_OUTPUT_2_BIT = 1008;
    public static final int BUFFER_OUTPUT_4_BIT = 1009;
    public static final int BUFFER_OUTPUT_8_BIT = 1010;
    public static final int BUFFER_OUTPUT_16_BIT = 1011;

    private int style;
    private int[] buffer;
    private IO_Device[] bufferIO;
    public Buffer(int style){
        this.style = style;

        switch (style){
            case BUFFER_INPUT_2_BIT:
                bufferIO = new IO_Device[2];
                break;
            case BUFFER_INPUT_4_BIT:

                break;
            case BUFFER_INPUT_8_BIT:

                break;
            case BUFFER_INPUT_16_BIT:

                break;
            case BUFFER_OUTPUT_2_BIT:

                break;
            case BUFFER_OUTPUT_4_BIT:

                break;
            case BUFFER_OUTPUT_8_BIT:

                break;
            case BUFFER_OUTPUT_16_BIT:

                break;
        }
    }

    public int getStyle(){
        return this.style;
    }

    public int getType(){
        return BUFFER;
    }

    public int getBit(int i){
        return buffer[i];
    }

    public int[] getBuffer(){
        return buffer;
    }

    public int getOutput1(){
        return 0;
    }

}

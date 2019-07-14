package com.akashd50.lb.objects;

public abstract class LogicObject {
    public static final int GATE = 1;
    public static final int WIRE = 2;
    public static final int IO_DEVICE = 3;
    protected static Texture IO_DEVICE_1;
    protected static Texture IO_DEVICE_0;
    protected static Texture IO_DEVICE_EMPTY;

    private Texture texture;
    public void setTexture(Texture t){
        this.texture = t;
    }
    public Texture getTexture() {
        return texture;
    }
    public abstract int getType();
    public abstract int getOutput();

    public static void SetIOTextures(Texture one, Texture zero, Texture empty){
        IO_DEVICE_1 = one; IO_DEVICE_0 = zero; IO_DEVICE_EMPTY = empty;
    }
}

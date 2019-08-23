package com.akashd50.lb.objects;

import com.akashd50.lb.logic.TextureContainer;

public abstract class LogicObject {
    public static final int GATE = 1;
    public static final int WIRE = 2;
    public static final int IO_DEVICE = 3;
    public static final int LOGIC_BOARD = 4;
    public static final int BUFFER = 5;

    protected static TextureContainer textureContainer;
    protected Quad2D quad;
    private Texture texture;

    public LogicObject(){
        quad = new Quad2D(1f, 1f);
    }

    public void setTexture(Texture t){
        this.texture = t;
        quad.setTextureUnit(texture);
    }

    public Texture getTexture() {
        return texture;
    }
    public abstract int getStyle();
    public abstract int getType();
    public abstract int getOutput1();

    public void onDrawFrame(float[] mMVPMatrix){
        quad.draw(mMVPMatrix);
    }

    public static void setTextureContainer(TextureContainer tc){textureContainer = tc;}
    public Quad2D getQuad(){return quad;}
    public String toString(){
        return this.getType()+"";
    }
}

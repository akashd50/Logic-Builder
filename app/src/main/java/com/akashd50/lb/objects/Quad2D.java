package com.akashd50.lb.objects;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.akashd50.lb.objects.SimpleVector;
import com.akashd50.lb.objects.Texture;
import com.akashd50.lb.utils.Utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES30.GL_BLEND;
import static android.opengl.GLES30.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES30.GL_SRC_ALPHA;

public class Quad2D {
    private static final int COORDS_PER_VERTEX = 3;
    private static int vertexStride = (COORDS_PER_VERTEX )* 4; // 4 bytes per vertex

    public static final int REGULAR = 1002;
    public static final int BLUR = 1001;
    public static final int BLEND = 1003;

    private Texture textureUnit;
    private float l,h;
    private FloatBuffer mTextureBuffer,vertexBuffer;
    private int renderType, mProgram, vertexCount, mMVPMatrixHandle, aTextureHandle, textureUniform, mPositionHandle;
    public boolean active;
    private SimpleVector rotation, defaultRotation, location, scale, defaultLocation;

    private boolean horizontalBlur;
    private float opacity;

    public Quad2D(float l, float h) {
        rotation = new SimpleVector();
        defaultLocation = new SimpleVector();
        defaultRotation = new SimpleVector();
        scale = new SimpleVector(1f,1f,1f);
        location = new SimpleVector();

        active = true;
        this.l = l;
        this.h = h;

        float v1[] = {0f, 0f, 0f,
                0f - (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f - (h / 2), 0f};

        float[] textureCoords = {0.5f, 0.5f,
                0f, 1.0f,
                1f, 1.0f,
                1f, 0.0f,
                0f, 0.f,
                0f, 1.0f};

        vertexCount = v1.length / COORDS_PER_VERTEX;
        ByteBuffer bb = ByteBuffer.allocateDirect(v1.length * 4);
        if (bb != null) {
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(v1);
            vertexBuffer.position(0);

            ByteBuffer byteBuf = ByteBuffer.allocateDirect(
                    textureCoords.length * 4);
            byteBuf.order(ByteOrder.nativeOrder());
            mTextureBuffer = byteBuf.asFloatBuffer();
            mTextureBuffer.put(textureCoords);
            mTextureBuffer.position(0);
        }
    }

    private void drawHelper(float[] mMVPMatrix){

        GLES30.glUseProgram(mProgram);
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "u_Matrix");
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        textureUniform = GLES30.glGetUniformLocation(mProgram,"u_TextureUnit");
        GLES30.glUniform1i(textureUniform, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUnit.getTexture());

        int opu = GLES30.glGetUniformLocation(mProgram, "opacity");
        GLES30.glUniform1f(opu, opacity);

        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "a_Position");
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        mTextureBuffer.position(0);
        aTextureHandle = GLES30.glGetAttribLocation(mProgram,"a_TextureCoordinates");
        GLES30.glVertexAttribPointer(aTextureHandle,2,GLES30.GL_FLOAT,false,8,mTextureBuffer);
        GLES30.glEnableVertexAttribArray(aTextureHandle);

        if(renderType == BLUR){
            int horizontal = GLES30.glGetUniformLocation(mProgram, "horizontal");
            if(horizontalBlur) GLES30.glUniform1f(horizontal, 1.0f);
            else GLES30.glUniform1i(horizontal, 0);
        }

        if(renderType == BLEND){
            int tu2 = GLES30.glGetUniformLocation(mProgram,"u_TextureUnit2");
            GLES30.glUniform1i(tu2, 1);
            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUnit.getSpecularTexture());
        }

        GLES30.glEnable( GL_BLEND );
        GLES30.glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    public void draw(float[] mMVPMatrix){
        float[] scratch = new float[16];
        float[] temp = new float[16];

        Matrix.setIdentityM(temp,0);
        Matrix.translateM(temp,0,location.x,location.y,location.z);
        Matrix.scaleM(temp,0,scale.x,scale.y,1f);
        Matrix.rotateM(temp, 0, rotation.x, 1, 0, 0);
        Matrix.rotateM(temp, 0, rotation.y, 0, 1, 0);
        Matrix.rotateM(temp, 0, rotation.z, 0, 0, 1);

        Matrix.multiplyMM(scratch,0,mMVPMatrix,0, temp,0);

        drawHelper(scratch);
    }

    public void setRenderPreferences(int prog, int type){
        mProgram = prog;
        this.renderType = type;
    }
    public void setTexture(int[] texture){this.textureUnit.setTexture(texture);}
    public void setTextureUnit(Texture t){
        this.textureUnit = t;
    }

    public void invert(){
        float v1[] = {0f, 0f, 0f,
                0f - (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f - (h / 2), 0f,
                0f + (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f + (h / 2), 0f,
                0f - (l / 2), 0f - (h / 2), 0f};

        float[] textureCoords = {0.5f, 0.5f,
                0f, 0.0f,
                1f, 0.0f,
                1f, 1.0f,
                0f, 1.f,
                0f, 0.0f};

        vertexBuffer.clear();
        mTextureBuffer.clear();
        vertexBuffer.put(v1);
        mTextureBuffer.put(textureCoords);
        vertexBuffer.position(0);
        mTextureBuffer.position(0);
    }
    public void setLocation(SimpleVector location){
        this.location.x = location.x;
        this.location.y = location.y;
        this.location.z = location.z;
    }

    public void updateLocation(SimpleVector location){
        this.location.x += location.x;
        this.location.y += location.y;
        this.location.z += location.z;
    }
    public SimpleVector getLocation(){return this.location;}

    public void setDefaultLocation(SimpleVector location){
        this.defaultLocation.x = location.x;
        this.defaultLocation.y = location.y;
        this.defaultLocation.z = location.z;
        this.location.x = location.x;
        this.location.y = location.y;
        this.location.z = location.z;
    }

    public SimpleVector getDefaultLocation(){return this.defaultLocation;}

    public void rotateX(float angle){
        if(rotation.x+angle<=360) {
            rotation.x += angle;
        }else{
            float temp = 360-rotation.x;
            angle = angle - temp;
            rotation.x = angle;
        }
    }

    public void rotateY(float angle){
        if(rotation.y+angle<=360) {
            rotation.y += angle;
        }else{
            float temp = 360-rotation.y;
            angle = angle - temp;
            rotation.y = angle;
        }
    }

    public void rotateZ(float angle){
        if(rotation.z+angle<=360) {
            rotation.z += angle;
        }else{
            float temp = 360-rotation.z;
            angle = angle - temp;
            rotation.z = angle;
        }
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void resetRotation(){
        rotation = new SimpleVector();
    }

    public void scale(SimpleVector scale){
        this.scale.x = scale.x;
        this.scale.y = scale.y;
    }
    public SimpleVector getScale(){return scale;}

    public boolean isClicked(float tx, float ty){

        float scrW = Utilities.SCR_ACT_WIDTH;
        float scrH = Utilities.SCR_ACT_HEIGHT;
        float scrRatio = scrH/scrW;

     /*   float left = (scrW/2 + ((-l/2)/scrRatio)*scrW/2) + ((this.location.x/scrRatio)*(scrW/2));
        float top =  (scrH/2-((h/2)*scrH/2) - ((this.location.x/1.0f) * scrH/2));
        float right = (scrW/2 + ((l/2)/scrRatio)*scrW/2) + ((this.location.x/scrRatio)*(scrW/2));
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        float bottom = (scrH/2-((-h/2)*scrH/2) - ((this.location.y/1.0f)* scrH/2));*/

        float x = (tx/scrW) * 2.0f - 1.0f;
        float y = (scrH/scrW) - (ty/scrH) * 2.0f*(scrH/scrW);

        float left = location.x - l/2;
        float top =  location.y + h/2;
        float right = location.x + l/2;
        // this.bottom = (int) (scrHeight/2- ar2[1]*scrHeight/2);
        float bottom = location.y - h/2;

        if(x > left && x < right && y > bottom && y < top) {
            return true;
        }else return false;
    }

    public void setDefaultRotation(SimpleVector rotation){
        this.defaultRotation.x = rotation.x;
        this.defaultRotation.y = rotation.y;
        this.defaultRotation.z = rotation.z;
    }
    public Texture getTextureUnit(){return this.textureUnit;}

    public boolean getHorizontalBlur(){return this.horizontalBlur;}
    public void setHorizontalBlur(boolean h){this.horizontalBlur = h;}
    public float getLength(){return this.l;}
    public float getHeight(){return this.h;}
}

package com.akashd50.lb.objects;

import android.opengl.GLES30;

import com.akashd50.lb.utils.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {
    private static final int COORDS_PER_VERTEX = 4;
    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int mColorHandle;
    private int COLOR_COMPONENT_COUNT = 4;
    private int vertexCount;// = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = (COORDS_PER_VERTEX)* 4; // 4 bytes per vertex
    private FloatBuffer colorBuffer;

    private int aColorLocation, mPositionHandle, mMVPMatrixHandle;

    private float[] lineCoords, color;

    public Line(SimpleVector v1, SimpleVector v2, SimpleVector c) {
        color = new float[8];
        color[0] = c.x; color[1] = c.y;color[2] = c.z; color[3] = 1f;
        color[4] = c.x; color[5] = c.y;color[6] = c.z; color[7] = 1f;

        // initialize vertex byte buffer for shape coordinates
        lineCoords = new float[8];
        lineCoords[0] = v1.x; lineCoords[1] = v1.y;lineCoords[2] = v1.z; lineCoords[3] = 1f;
        lineCoords[4] = v2.x; lineCoords[5] = v2.y;lineCoords[6] = v2.z; lineCoords[7] = 1f;

        vertexCount= lineCoords.length/COORDS_PER_VERTEX ;
        ByteBuffer bb = ByteBuffer.allocateDirect(lineCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(color.length*4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        mProgram = Shader.getLineShaderPorgram();

        GLES30.glLineWidth(5.0f);
    }

    public void updateC1(SimpleVector v1){
        color[0] = v1.x; color[1] = v1.y;color[2] = v1.z; color[3] = 1f;
        colorBuffer.clear();
        colorBuffer.put(color);
        colorBuffer.position(0);
    }

    public void updateC2(SimpleVector v1){
        color[4] = v1.x; color[5] = v1.y;color[6] = v1.z; color[7] = 1f;
        colorBuffer.clear();
        colorBuffer.put(color);
        colorBuffer.position(0);
    }

    public void updateV1(SimpleVector v1){
        lineCoords[0] = v1.x; lineCoords[1] = v1.y;lineCoords[2] = v1.z;
        vertexBuffer.clear();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }
    public void updateV2(SimpleVector v2){
        lineCoords[4] = v2.x; lineCoords[5] = v2.y;lineCoords[6] = v2.z;
        vertexBuffer.clear();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }

    public void updateVertices(SimpleVector v1, SimpleVector v2){
        lineCoords[0] = v1.x; lineCoords[1] = v1.y;lineCoords[2] = v1.z;
        lineCoords[4] = v2.x; lineCoords[5] = v2.y;lineCoords[6] = v2.z;
        vertexBuffer.clear();
        vertexBuffer.put(lineCoords);
        vertexBuffer.position(0);
    }

    public void draw(float[] mvpMatrix) {
        GLES30.glUseProgram(mProgram);
        mMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        mPositionHandle = GLES30.glGetAttribLocation(mProgram, "vPosition");

        vertexBuffer.position(0); //start from 0
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES30.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        aColorLocation = GLES30.glGetAttribLocation(mProgram, "a_Color");
        colorBuffer.position(0); //start from 0... reading color data from the color matrix
        GLES30.glVertexAttribPointer(aColorLocation,COLOR_COMPONENT_COUNT,
                GLES30.GL_FLOAT,false,
                COLOR_COMPONENT_COUNT*4,colorBuffer);
        GLES30.glEnableVertexAttribArray(aColorLocation);

        GLES30.glDrawArrays(GLES30.GL_LINES, 0, vertexCount);

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(aColorLocation);
    }

    public static void setLineWidth(float w){
        GLES30.glLineWidth(w);
    }


}

package com.example.veronika.ball;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import android.opengl.GLSurfaceView.Renderer;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;

public class OpenGLRenderer implements Renderer {
    private Context context;
    private int programId;
    private FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private float ratio;
    int verbs = 50; //should be even

    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0.3f, 0.3f, 0.8f, 1f);
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        ratio = (float) width / (float) height;
        Log.i("ratio", String.format("%.3f", ratio));
        prepareData();
        bindData();
    }

    private void prepareData() {
        //float[] vertices = {0.0f, 0.2f, -0.5f, -0.2f, -0.0f, -0.5f, 0.5f, -0.2f,};
        float[] vertices = ballGenerate();
        vertexData = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(vertices);
    }

    private void bindData() {
        uColorLocation = glGetUniformLocation(programId, "u_Color");
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override public void onDrawFrame(GL10 arg0) {
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_FAN, 0, verbs+2);
    }

    private float[] ballGenerate() {
        final float Radius = 0.5f;
        float vertices[] = new float[2*(verbs+2)];
        vertices[0] = vertices[1] = 0.0f;
        for (int i = 1; i <= verbs + 1; i++) {
            double angle = 2 * Math.PI * (i - 1) / verbs;
            vertices[2*i] = (float)(Math.sin(angle) * Radius);
            vertices[2*i+1] = (float)(Math.cos(angle) * Radius * ratio);
        }
        return vertices;
    }
}
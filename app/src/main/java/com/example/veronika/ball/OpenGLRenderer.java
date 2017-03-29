package com.example.veronika.ball;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import android.opengl.GLSurfaceView.Renderer;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;

public class OpenGLRenderer implements Renderer {
    private Context context;
    private int programId;
    private FloatBuffer vertexData;
    float base_vertices[];
    float vertices[];
    private int uColorLocation;
    private int aPositionLocation;
    private float ratio;
    int VERBS = 50;
    MainActivity activity;

    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0.4f, 0.4f, 0.9f, 1f);
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
        ballGenerate();
        vertexData = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    private void bindData() {
        uColorLocation = glGetUniformLocation(programId, "u_Color");
        glUniform4f(uColorLocation, 0.78039f, 0.65882f, 0.92941f, 1.0f); // C7A8ED == lilac
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override public void onDrawFrame(GL10 arg0) {
        Log.i("trace", String.format("onDrawFrame: %.3f %.3f", Ball.ball_x, Ball.ball_y));
        for (int i = 0; i <= VERBS +1; i++) {
            vertices[2*i] = base_vertices[2*i] + Ball.ball_x;
            vertices[2*i+1] = base_vertices[2*i+1] + Ball.ball_y;
        }
        vertexData.position(0);
        vertexData.put(vertices);
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_FAN, 0, VERBS +2);
    }

    private void ballGenerate() {
        final float Radius = 0.3f;
        base_vertices = new float[2*(VERBS +2)];
        vertices      = new float[2*(VERBS +2)];
        base_vertices[0] = base_vertices[1] = 0.0f;
        for (int i = 1; i <= VERBS + 1; i++) {
            double angle = 2 * Math.PI * (i - 1) / VERBS;
            base_vertices[2*i] = (float)(Math.sin(angle) * Radius);
            base_vertices[2*i+1] = (float)(Math.cos(angle) * Radius * ratio);
        }
    }
}
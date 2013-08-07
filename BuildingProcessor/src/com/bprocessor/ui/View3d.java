package com.bprocessor.ui;

import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;



@SuppressWarnings("serial")
public class View3d extends GLCanvas implements GLEventListener {
	protected GLAutoDrawable drawable;
	protected GL2 gl;
	protected GLU glu;
	protected int width;
	protected int height;
	
	protected static GLUtessellator tesselator;
	protected static TesselatorCallback callback;
	
	public static GLUtessellator tesselator() {
		if (tesselator == null) {
			tesselator = GLU.gluNewTess();
			callback = new TesselatorCallback();
			GLU.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, callback);
			GLU.gluTessCallback(tesselator, GLU.GLU_TESS_END, callback);
			GLU.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, callback);
			GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
		}
		return tesselator;
	}

	protected static class TesselatorCallback extends GLUtessellatorCallbackAdapter {
		private GL2 gl;
		private GLU glu;
		
		public void init(GL2 gl, GLU glu) {
			this.gl = gl;
			this.glu = glu;
		}
		public void begin(int type) {
			gl.glBegin(type);
		}
		public void end() {
			gl.glEnd();
		}
		public void vertex(Object object) {
			double[] vertex = (double[]) object;
			if (vertex.length >= 6) {
				gl.glNormal3dv(vertex, 3);
			}
			gl.glVertex3dv(vertex, 0);
		}
		public void error(int arg0) {
			System.err.println(glu.gluErrorString(arg0));
		}
	}
	
	
	public void init(GLAutoDrawable drawable) {
		this.drawable = drawable;
		glu = new GLU();
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glShadeModel(GL_SMOOTH);

		gl.glEnable(GL2.GL_LIGHT0);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] {0.3f, 0.3f, 0.3f, 1.0f}, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[] {0.7f, 0.7f, 0.7f, 1.0f}, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[] {0.0f, 0.0f, 0.0f, 1.0f}, 0);

		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDepthMask(true);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glPointSize(7);
		gl.glLineStipple(4, (short)0xAAAA);
	}
	public void dispose(GLAutoDrawable drawable) {
		
	}
	public void display(GLAutoDrawable drawable) {
		
	}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2(); 

		if (height == 0) height = 1;
		this.width = width;
		this.height = height;
		float aspect = (float)width / height;

		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0, aspect, 5, 500.0);

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}

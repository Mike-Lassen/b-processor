package com.bprocessor.ui;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import javax.media.opengl.glu.GLUtessellatorCallbackAdapter;

import com.bprocessor.BasicComponent;
import com.bprocessor.Camera;
import com.bprocessor.Color;
import com.bprocessor.Constructor;
import com.bprocessor.ConstructorLayer;
import com.bprocessor.Edge;
import com.bprocessor.Face;
import com.bprocessor.FaceGroup;
import com.bprocessor.Geometry;
import com.bprocessor.Group;
import com.bprocessor.ItemVisitor;
import com.bprocessor.Material;
import com.bprocessor.Sketch;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.io.ObjFileReader;
import com.bprocessor.util.Plane;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

/**
 * JOGL 2.0 Program Template (GLCanvas)
 * This is a "Component" which can be added into a top-level "Container".
 * It also handles the OpenGL events to render graphics.
 */
@SuppressWarnings("serial")
public class SketchView extends GLCanvas implements GLEventListener {
	private InputListener delegate;
    private static float[] babyblue = new float[] {224f / 255, 255f / 255, 255f / 255};
    private static float[] selected_color = new float[] {0.8f, 0.2f, 0.3f};

    private SketchController controller;
    
    protected Sketch sketch;
    protected Group overlay;
    protected ConstructorLayer constructorLayer;
    
    protected BasicComponent man;
    protected Camera camera;

    protected double[] modelMatrix = new double[16];
    protected double[] projMatrix = new double[16];
    protected int[] screenport = new int[4];

    private static GLU glu;
    private static GL2 gl;
    private static GLUtessellator tesselator;
    private GLAutoDrawable drawable;
    protected int width;
    protected int height;
    
    protected Geometry selected;

    /** Constructor to setup the GUI for this Component */
    public SketchView(SketchController controller) {
    	this.controller = controller;
    	
        FileInputStream file = null;
        try {
            ObjFileReader input = new ObjFileReader();
            man = input.readObject(new File("models/man.obj"));
            man.scaleIt(1 / 100.0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                }
            }
        }
        Vertex center = new Vertex(4, 2, 1.3);
        Vertex eye = new Vertex(6, -9, 8);
        Vertex up = new Vertex(0, 0, 1);
        camera = new Camera(center, eye, up);
        overlay = new Group("overlay");
        overlay.add(man);
        constructorLayer = new ConstructorLayer("Constructors");
        overlay.add(constructorLayer);
        this.addGLEventListener(this);
    }

    public void setDelegate(InputListener listener) {
    	if (delegate != null) {
    		removeMouseListener(delegate);
    		removeMouseMotionListener(delegate);
    		removeMouseWheelListener(delegate);
    		removeKeyListener(delegate);
    	}
    	delegate = listener;
    	if (delegate != null) {
    		addMouseListener(delegate);
    		addMouseMotionListener(delegate);
    		addMouseWheelListener(delegate);
    		addKeyListener(delegate);
    	}
    }
    public InputListener getDelegate() {
    	return delegate;
    }
    
    public void setSelected(Geometry selected) {
    	this.selected = selected;
    }
    public Geometry getSelected() {
    	return selected;
    }
    
    public void addOverlay(Group geometry) {
    	overlay.add(geometry);
    }
    public void removeOverlay(Group geometry) {
    	overlay.remove(geometry);
    }
    public void addConstructor(Constructor constructor) {
    	constructorLayer.add(constructor);
    }
    public void removeConstructor(Constructor constructor) {
    	constructorLayer.remove(constructor);
    }
    public void clearConstructors() {
    	constructorLayer.clear();
    }
    public void setRestriction(Plane plane) {
    	this.restriction = plane;
    }
    public Plane getRestriction() {
    	return restriction;
    }
        
    public class Picking {
        protected int x;
        protected int y;
        protected int width;
        protected int height;
        protected int hits;
        protected IntBuffer buffer;

        public Picking(int x, int y) {
            this.x = x;
            this.y = y;
            width = 6;
            height = 6;
        }
    }
    public class PickingResult {
    	protected Geometry nearest;
        protected LinkedList<Surface> surfaces;
        protected LinkedList<Edge> edges;
        protected LinkedList<Vertex> vertices;
        public PickingResult(Geometry nearest, LinkedList<Surface> surfaces, LinkedList<Edge> edges, LinkedList<Vertex> vertices) {
            this.nearest = nearest;
        	this.edges = edges;
            this.vertices = vertices;
            this.surfaces = surfaces;
        }
    }

    protected Picking picking;

    protected Plane restriction;

    public static GLUtessellator tesselator() {
        if (tesselator == null) {
            tesselator = GLU.gluNewTess();
            GLUtessellatorCallback callback = new TesselatorCallback();
            GLU.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, callback);
            GLU.gluTessCallback(tesselator, GLU.GLU_TESS_END, callback);
            GLU.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, callback);
            GLU.gluTessProperty(tesselator, GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_ODD);
        }
        return tesselator;
    }

    
    public void setSketch(Sketch sketch) {
    	if (sketch != this.sketch) {
    		this.sketch = sketch;
    		repaint();
    	}
    }
    public Sketch getSketch() {
    	return sketch;
    }
    
    public void checkpoint() {
        controller.checkpoint();
    }

    private static class TesselatorCallback extends GLUtessellatorCallbackAdapter {
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

    // ------ Implement methods declared in GLEventListener ------

    /**
     * Called back immediately after the OpenGL context is initialized. Can be used
     * to perform one-time initialization. Run only once.
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        this.drawable = drawable;
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting

        gl.glEnable(GL2.GL_LIGHT0);
        //Tell if both sides of the model should be lighted
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
        tesselator();
        // ----- Your OpenGL initialization code here -----
    }

    /**
     * Call-back handler for window re-size event. Also called when the drawable is
     * first set to visible.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0) height = 1;   // prevent divide by zero
        this.width = width;
        this.height = height;
        float aspect = (float)width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(45.0, aspect, 5, 500.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }


    private ArrayList<Object> objectTable;

    private void initNames(GL2 gl) {
        objectTable = new ArrayList<Object>();
        gl.glInitNames();
    }
    private void pushName(GL2 gl, Object object) {
        objectTable.add(object);
        gl.glPushName(objectTable.size());
    }
    private void popName(GL2 gl) {
        gl.glPopName();
    }
    private Object getObject(int index) {
        return objectTable.get(index - 1);
    }
    private void clearNames() {
        objectTable = null;
    }

    /**
     * Called back by the animator to perform rendering.
     */
    @Override
    public void display(GLAutoDrawable drawable) {

        gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        float aspect = (float)width / height;

        if (picking != null) {
            int[] viewport = new int[] {0, 0, width, height};
            glu.gluPickMatrix(picking.x, viewport[3] - picking.y, picking.width, picking.height, viewport, 0);
        }

        glu.gluPerspective(45.0, aspect, 5, 500.0); // fovy, aspect, zNear, zFar

        if (picking != null) {
            picking.buffer = Buffers.newDirectIntBuffer(256);
            gl.glSelectBuffer(256, picking.buffer);
            gl.glRenderMode(GL2.GL_SELECT);
            initNames(gl);
        }
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset

        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glLoadIdentity();  // reset the model-view matrix

        if (sketch != null) {
            applyCamera(camera);
            if (picking != null) {
                sketch.getGroup().visit(new VisitorForPicking());
                overlay.visit(new VisitorForPicking());
                double x = 0;

                for (int i = 0; i < 41; i++) {
                    Vertex from = new Vertex(x, 0, 0);
                    Vertex to = new Vertex(x, 40, 0);
                    Edge edge = new Edge(from, to);
                    pushName(gl, edge);
                    gl.glBegin(GL_LINES);
                    gl.glVertex2d(x, 0);
                    gl.glVertex2d(x, 40);
                    gl.glEnd();
                    popName(gl);
                    x = x + 1;
                }

                double y = 0;

                for (int i = 0; i < 41; i++) {
                    Vertex from = new Vertex(0, y, 0);
                    Vertex to = new Vertex(40, y, 0);
                    Edge edge = new Edge(from, to);
                    pushName(gl, edge);
                    gl.glBegin(GL_LINES);
                    gl.glVertex2d(0, y);
                    gl.glVertex2d(40, y);
                    gl.glEnd();
                    y = y + 1;
                }

            } else {
                gl.glDepthMask(false);
                gl.glLineWidth(1.5f);
                gl.glColor4f(221.0f/255, 234.0f/255, 254.0f/ 255, 1.0f);
                double x = 0;
                gl.glBegin(GL_LINES);
                for (int i = 0; i < 41; i++) {
                    gl.glVertex2d(x, 0);
                    gl.glVertex2d(x, 40);
                    x = x + 1;
                }
                gl.glEnd();
                double y = 0;
                gl.glBegin(GL_LINES);
                for (int i = 0; i < 41; i++) {
                    gl.glVertex2d(0, y);
                    gl.glVertex2d(40, y);
                    y = y + 1;
                }
                gl.glEnd();
                gl.glDepthMask(true);

                sketch.getGroup().visit(new VisitorForDisplay());
                overlay.visit(new VisitorForDisplay());
                if (restriction != null) {
                    sketch.getGroup().visit(new VisitorForWireframe());
                    overlay.visit(new VisitorForWireframe());
                }

            }
            if (picking != null) {
                gl.glFlush();
                picking.hits = gl.glRenderMode(GL2.GL_RENDER);
            }
        }
    }



    public Transformation transformation() {
        return new Transformation(glu, modelMatrix, projMatrix, screenport);
    }
    public Vertex getPlaneIntersection(double x, double y, Plane plane) {
        y = getSize().getHeight() - y;
        Transformation transformation = transformation();
        Vertex near = new Vertex(x, y, 0.0);
        Vertex far = new Vertex(x, y, 1.0);
        Edge ray = new Edge(near, far);
        ray = transformation.unProject(ray);
        Vertex vertex = plane.intersection(ray);
        return vertex;
    }


    public PickingResult selectObjects(int x, int y) {
        picking = new Picking(x, y);
        drawable.getContext().makeCurrent();
        display(drawable);
        int bufferOffset = 0;
        int names = 0;
        long zMax = 0xFFFFFFFFL;

        LinkedList<Vertex> vertices = new LinkedList<Vertex>();
        LinkedList<Edge> edges = new LinkedList<Edge>();
        LinkedList<Surface> surfaces = new LinkedList<Surface>();
        
        Vertex currentVertex = null;
        double vertex_z = 1.0;
        Edge currentEdge = null;
        double edge_z = 1.0;
        Surface currentSurface = null;
        double surface_z = 1.0;

        for (int i = 0; i < picking.hits; i++) {
            names = picking.buffer.get(bufferOffset);
            long z1 = 0xFFFFFFFFL & picking.buffer.get(bufferOffset + 1);
            long z2 = 0xFFFFFFFFL & picking.buffer.get(bufferOffset + 2);
            double near = (double) z1 / (double) zMax;
            double far = (double) z2 / (double) zMax;
            double z = (near + far) / 2;
            bufferOffset += 3;
            if (names > 0) {
                int id = picking.buffer.get(bufferOffset + names - 1);
                bufferOffset += names;
                Object current = getObject(id);
                if (current instanceof Vertex) {
                	if (z < vertex_z) {
                		currentVertex = (Vertex) current;
                		vertex_z = z;
                	}
                    vertices.add((Vertex) current);
                } else if (current instanceof Edge) {
                	Edge edge = (Edge) current;
                	if (edge.getOwner() instanceof Group) {
                		if (z < edge_z) {
                			currentEdge = edge;
                			edge_z = z;
                		}
                	}
                    edges.add(edge);
                } else if (current instanceof Surface) {
                	if (z < surface_z) {
                		currentSurface = (Surface) current;
                		surface_z =z;
                	}
                	surfaces.add((Surface) current);
                } else {
                    System.out.println("selected " + current.getClass().getName());
                }
            }
        }
        clearNames();
        picking = null;
        if (currentSurface != null) {
        	Plane plane = currentSurface.plane();
        	if (currentVertex != null) {
        		if (surface_z < vertex_z) {
        			if (!plane.contains(currentVertex)) {
        				currentVertex = null;
        			}
        		}
        	}
        	if (currentEdge != null) {
        		if (surface_z < edge_z) {
        			if (!plane.contains(currentEdge)) {
        				currentEdge = null;
        			}
        		}
        	}
        }
        if (currentEdge != null && currentVertex != null) {
        	if (edge_z < vertex_z) {
        		if (!currentEdge.intersects(currentVertex)) {
        			currentVertex = null;
        		}
        	}
        }
        Geometry nearest = null;
        if (currentVertex != null) {
        	nearest = currentVertex;
        } else if (currentEdge != null) {
        	nearest = currentEdge;
        } else if (currentSurface != null) {
        	nearest = currentSurface;
        }
        return new PickingResult(nearest, surfaces, edges, vertices);
    }

    public Vertex selectVertex(int x, int y, Plane plane, Vertex original) {
        Vertex result = null;
        PickingResult record = selectObjects(x, y);
        LinkedList<Vertex> vertices = record.vertices;
        LinkedList<Edge> edges = record.edges;
        if (vertices.size() > 0) {
            double distance = Double.POSITIVE_INFINITY;
            for (Vertex vertex : vertices) {
                double value = vertex.distance(original);
                if (value < distance) {
                    distance = value;
                    result = vertex;
                }
            }
        } else if (edges.size() == 1) {
            Edge edge = edges.getFirst();
            result = edge.intersection(original);
        } else if (edges.size() > 1) {
            Edge e1 = edges.get(0);
            for (int i = 1; i < edges.size(); i++) {
                Edge e2 = edges.get(i);
                if (e1.orthogonal(e2)) {
                    result = e1.intersection(original);
                    result = e2.intersection(result);
                    break;
                }
            }
        }
        return result;
    }

    public Geometry selectGeometry(int x, int y, Plane plane, Vertex original) {
        Geometry result = null;
        PickingResult record = selectObjects(x, y);
        LinkedList<Vertex> vertices = record.vertices;
        LinkedList<Edge> edges = record.edges;
        if (vertices.size() > 0) {
            double distance = Double.POSITIVE_INFINITY;
            for (Vertex vertex : vertices) {
                double value = vertex.distance(original);
                if (value < distance) {
                    distance = value;
                    result = vertex;
                }
            }
        } else if (edges.size() > 0) {
            result =  edges.getFirst();
        }
        return result;
    }

    public Surface selectSurface(int x, int y, Plane plane, Vertex original) {
        Surface result = null;
        PickingResult record = selectObjects(x, y);
        if (record.surfaces.size() > 0) {
            result = record.surfaces.getLast();
        }
        return result;
    }
    
    public Geometry selectObject(int x, int y) {
    	PickingResult record = selectObjects(x, y);
    	return record.nearest;
    }

    private void applyCamera(Camera camera) {
        Vertex center = camera.getCenter();
        Vertex eye = camera.getEye();
        Vertex up = camera.getUp();
        glu.gluLookAt(eye.getX(), eye.getY(), eye.getZ(),
                center.getX(), center.getY(), center.getZ(),
                up.getX(), up.getY(), up.getZ());
        gl.glGetIntegerv(GL2.GL_VIEWPORT, screenport, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projMatrix, 0);
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelMatrix, 0);
    }

    private void apply(float[] color, float a) {
        if (a == 1.0) {
            gl.glColor3fv(color, 0);
        } else {
            float[] alpha = new float[]{color[0], color[1], color[2], a};
            gl.glColor4fv(alpha, 0);
        }
    }

    @SuppressWarnings("unused")
	private void apply(Material material) {
        Color diffuse = material.getDiffuse();
        gl.glColor3f(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue());
    }

    private void drawComponentForDisplay(BasicComponent component) {
        gl.glColor3f(0.9f, 0.9f, 1.0f);
        gl.glEnable(GL2.GL_LIGHTING);
        for (FaceGroup group : component.getGroups()) {
            Material material = group.getMaterial();
            if (material != null) {
                //apply(material);
            }
            gl.glBegin(GL2.GL_TRIANGLES);
            for (Face face : group.getFaces()) {
                List<Vertex> vertices = face.getVertices();
                List<Vertex> normals = face.getNormals();
                if (normals != null && normals.size() == vertices.size()) {
                    int n = vertices.size();
                    for (int i = 0; i < n; i++) {
                        Vertex vertex = vertices.get(i);
                        Vertex normal = normals.get(i);
                        gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
                        gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                    }
                } else {
                    for (Vertex vertex : face.getVertices()) {
                        gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                    }
                }
            }
            gl.glEnd();
        }
        gl.glDisable(GL2.GL_LIGHTING);
    }
    private void drawComponentWireFrame(BasicComponent component) {
        gl.glColor4f(0.9f, 0.9f, 1.0f, 0.1f);
        gl.glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
        for (FaceGroup group : component.getGroups()) {
            gl.glBegin(GL_TRIANGLES);
            for (Face face : group.getFaces()) {
                List<Vertex> vertices = face.getVertices();
                List<Vertex> normals = face.getNormals();
                if (normals != null && normals.size() == vertices.size()) {
                    int n = vertices.size();
                    for (int i = 0; i < n; i++) {
                        Vertex vertex = vertices.get(i);
                        Vertex normal = normals.get(i);
                        gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
                        gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                    }
                } else {
                    for (Vertex vertex : face.getVertices()) {
                        gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                    }
                }
            }
            gl.glEnd();
        }
        gl.glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        gl.glColor4f(0.9f, 0.9f, 1.0f, 1.0f);
    }


    public abstract class VisitorForDrawing implements ItemVisitor {
        public void drawSurface(Surface surface) {
            if (restriction != null) {
                if (!restriction.contains(surface)) {
                    return;
                }
            }
            Vertex normal = surface.normal();
            gl.glNormal3d(normal.getX(), normal.getY(), normal.getZ());
            if ((surface.getEdges().size() == 4 && surface.getHoles().size() == 0)) {
                drawQuad(surface.getVertices(), false);
            } else {
                GLU.gluTessBeginPolygon(tesselator, null);
                drawContour(surface, false);
                for (Surface current : surface.getHoles()) {
                    drawContour(current, false);
                }
                GLU.gluTessEndPolygon(tesselator);
            }
        }
        public void drawContour(Surface surface, boolean reverse) {
            java.util.List<Vertex> vertices = surface.getVertices();
            GLU.gluTessBeginContour(tesselator);
            for (Vertex current : vertices) {
                double[] content = new double[3];
                content[0] = current.getX();
                content[1] = current.getY();
                content[2] = current.getZ();
                GLU.gluTessVertex(tesselator, content, 0, content);
            }
            GLU.gluTessEndContour(tesselator);
        }

        public void drawQuad(java.util.List<Vertex> vertices, boolean reverse) {
            Iterator<Vertex> iter = vertices.iterator();
            Vertex v0 = iter.next();
            Vertex v1 = iter.next();
            Vertex v2 = iter.next();
            Vertex v3 = iter.next();
            if (reverse) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3d(v3.getX(), v3.getY(), v3.getZ());
                gl.glVertex3d(v2.getX(), v2.getY(), v2.getZ());
                gl.glVertex3d(v1.getX(), v1.getY(), v1.getZ());
                gl.glVertex3d(v0.getX(), v0.getY(), v0.getZ());
                gl.glEnd();
            } else {
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex3d(v0.getX(), v0.getY(), v0.getZ());
                gl.glVertex3d(v1.getX(), v1.getY(), v1.getZ());
                gl.glVertex3d(v2.getX(), v2.getY(), v2.getZ());
                gl.glVertex3d(v3.getX(), v3.getY(), v3.getZ());
                gl.glEnd();
            }
        }
        public void drawEdge(Edge edge) {
            Vertex to = edge.getTo();
            Vertex from = edge.getFrom();
            gl.glBegin(GL2.GL_LINE_STRIP);
            gl.glVertex3d(to.getX(), to.getY(), to.getZ());
            gl.glVertex3d(from.getX(), from.getY(), from.getZ());
            gl.glEnd();
        }
        public void drawConstructor(Constructor constructor) {
            Vertex to = constructor.getTo();
            Vertex from = constructor.getFrom();
            Vertex direction = from.minus(to);
            direction.normalize();
            direction.scaleIt(50);
            to = from.add(direction);
            from = from.minus(direction);
            gl.glBegin(GL2.GL_LINE_STRIP);
            gl.glVertex3d(to.getX(), to.getY(), to.getZ());
            gl.glVertex3d(from.getX(), from.getY(), from.getZ());
            gl.glEnd();
        }
    }

    public class VisitorForDisplay extends VisitorForDrawing {
        public void drawEdgesForDisplay(List<Edge> edges) {
            for (Edge current : edges) {
                if (restriction != null) {
                    if (!restriction.contains(current)) {
                        continue;
                    }
                }
                if (current != selected) {
                	drawEdge(current);
                }
            }
        }

        public void drawSurfacesForDisplay(List<Surface> surfaces) {
            gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);
            gl.glEnable(GL2.GL_LIGHTING);
            for (Surface current : surfaces) {
                if (current.isVisible()) {
                	if (current != selected) {
                		drawSurface(current);
                	}
                }
            }
            gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
            gl.glDisable(GL2.GL_LIGHTING);
            
        }

        public void onGroup(Group current) {
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            drawEdgesForDisplay(current.getEdges());
            if (selected instanceof Edge) {
            	apply(selected_color, 1.0f);
            	drawEdge((Edge) selected);
            }
            apply(babyblue, 1.0f);
            drawSurfacesForDisplay(current.getSurfaces());
            Set<Vertex> mark = new HashSet<Vertex>();
            for (Surface surface : current.getSurfaces()) {
                mark.addAll(surface.getVertices());
            }
            if (selected instanceof Surface) {
            	apply(selected_color, 1.0f);
            	drawSurface((Surface) selected);
            }
            gl.glColor3f(0.0f, 0.0f, 0.0f);

            gl.glDisable(GL_DEPTH_TEST);
            for (Vertex vertex : current.getVertices()) {
                if (!mark.contains(vertex)) {
                    gl.glBegin(GL2.GL_POINTS);
                    gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                    gl.glEnd();
                }
            }
            if (selected instanceof Vertex) {
            	Vertex vertex = (Vertex) selected;
            	apply(selected_color, 1.0f);
            	gl.glBegin(GL2.GL_POINTS);
                gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                gl.glEnd();
            }
            gl.glEnable(GL_DEPTH_TEST);
        }
        public void onBasicComponent(BasicComponent current) {
            if (restriction == null) {
                drawComponentForDisplay(current);
            }
        }

        @Override
        public void onConstructorLayer(ConstructorLayer current) {
            gl.glLineWidth(1.0f);
            gl.glEnable(GL_LINE_STIPPLE);
            for (Constructor constructor : current.getConstructors()) {
                Color color = constructor.getColor();
                if (color != null) {
                    gl.glColor3fv(color.values(), 0);
                } else {
                    gl.glColor3f(0.1f, 0.2f, 0.5f);
                }
                drawConstructor(constructor);
            }
            gl.glDisable(GL_LINE_STIPPLE);
        }
    }

    public class VisitorForPicking extends VisitorForDrawing {
        public void drawEdgesForPicking(List<Edge> edges) {
            for (Edge current : edges) {
                if (restriction != null) {
                    if (!restriction.contains(current)) {
                        continue;
                    }
                }
                pushName(gl, current);
                drawEdge(current);
                popName(gl);
            }
        }
        public void drawSurfacesForPicking(List<Surface> surfaces) {
            for (Surface current : surfaces) {
                if (current.isVisible()) {
                    pushName(gl, current);
                    drawSurface(current);
                    popName(gl);
                }
            }
        }
        public void onGroup(Group current) {
            drawEdgesForPicking(current.getEdges());
            drawSurfacesForPicking(current.getSurfaces());
            for (Vertex vertex : current.getVertices()) {
                pushName(gl, vertex);
                gl.glBegin(GL2.GL_POINTS);
                gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
                gl.glEnd();
                popName(gl);
            }
        }
        public void onBasicComponent(BasicComponent current) {
        }
        public void onConstructorLayer(ConstructorLayer current) {
            for (Constructor constructor : current.getConstructors()) {
                pushName(gl, constructor);
                drawConstructor(constructor);
                popName(gl);
            }
        }
    }

    public class VisitorForWireframe extends VisitorForDrawing {
        public void drawEdgesWireframe(List<Edge> edges) {
            for (Edge current : edges) {
                drawEdge(current);
            }
        }
        public void onGroup(Group current) {
            gl.glColor4f(0.7f, 0.7f, 7.0f, 0.3f);
            drawEdgesWireframe(current.getEdges());
            gl.glColor4f(0.7f, 0.7f, 7.0f, 1.0f);
        }
        public void onBasicComponent(BasicComponent current) {
            drawComponentWireFrame(current);
        }
        public void onConstructorLayer(ConstructorLayer current) {			
        }
    }



    /**
     * Called back before the OpenGL context is destroyed. Release resource such as buffers.
     */
    @Override
    public void dispose(GLAutoDrawable drawable) { }
}
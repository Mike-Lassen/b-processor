package com.bprocessor.ui;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
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
public class BuildingEditor extends GLCanvas implements GLEventListener {

    private static float[] babyblue = new float[] {224f / 255, 255f / 255, 255f / 255};

    protected ToolBar toolbar;
    protected StatusBar statusbar;
    
    protected Sketch sketch;
    protected Group overlay;
    protected ConstructorLayer constructorLayer;
    protected BasicComponent man;
    protected Camera camera;
    protected List<Tool> tools;
    protected Tool activeTool;

    protected double[] modelMatrix = new double[16];
    protected double[] projMatrix = new double[16];
    protected int[] screenport = new int[4];

    private static GLU glu;
    private static GL2 gl;
    private static GLUtessellator tesselator;
    private GLAutoDrawable drawable;
    protected int width;
    protected int height;

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
        protected LinkedList<Surface> surfaces;
        protected LinkedList<Edge> edges;
        protected LinkedList<Vertex> vertices;
        public PickingResult(LinkedList<Surface> surfaces, LinkedList<Edge> edges, LinkedList<Vertex> vertices) {
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

    /** Constructor to setup the GUI for this Component */
    public BuildingEditor() {
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
        ToolHandler handler = new ToolHandler();
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
        this.addMouseWheelListener(handler);
        this.addKeyListener(handler);
    }

    public void setup() {
    	
        Tool select = new SelectTool(this);

        Tool pencil = new PencilTool(this);
        Tool ruler = new RulerTool(this);
        Tool eraser = new EraserTool(this);

        Tool cameraDrag = new Tool.CameraDrag(this);
        Tool cameraRotation = new Tool.CameraRotation(this);
        Tool cameraZoom = new Tool.CameraZoom(this);

        toolbar.registerTool("select", "Biconselecttool.gif", select);
        toolbar.addSeperator(20);

        toolbar.registerTool("pencil", "Biconpentool.gif", pencil);
        toolbar.registerTool("ruler", "ruler-icon.png", ruler);
        toolbar.registerTool("eraser", "eraser-icon.png", eraser);
        toolbar.addSeperator(20);

        toolbar.registerTool("camera-drag", "Bicondrag.gif", cameraDrag);
        toolbar.registerTool("camera-rotation", "Biconrotcam.png", cameraRotation);
        toolbar.registerTool("camera-zoom", "Biconzomeinout.gif", cameraZoom);
        
        toolbar.disableAll();
    }


    public void setActiveTool(Tool tool) {
        if (activeTool != null) {
            activeTool.finish();
        }
        activeTool = tool;
        if (activeTool != null) {
            activeTool.prepare();
        }
    }

    public void setSketch(Sketch sketch) {
    	if (sketch != this.sketch) {
    		this.sketch = sketch;
    		if (sketch != null) {
    			toolbar.enableAll();
    			toolbar.selectTool("select");
    		} else {
    			toolbar.disableAll();
    			toolbar.selectTool(null);
    		}
    		repaint();
    	}
    }

    public void checkpoint() {
        if (sketch != null) {
            sketch.setModified(true);
            MainFrame.instance.setup();
        }
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


    public PickingResult selectObjects(int x, int y, Plane plane) {
        picking = new Picking(x, y);
        drawable.getContext().makeCurrent();
        display(drawable);
        int bufferOffset = 0;
        int names = 0;
        long zMax = 0xFFFFFFFFL;

        LinkedList<Vertex> vertices = new LinkedList<Vertex>();
        LinkedList<Edge> edges = new LinkedList<Edge>();
        LinkedList<Surface> surfaces = new LinkedList<Surface>();

        for (int i = 0; i < picking.hits; i++) {
            names = picking.buffer.get(bufferOffset);
            long z1 = 0xFFFFFFFFL & picking.buffer.get(bufferOffset + 1);
            long z2 = 0xFFFFFFFFL & picking.buffer.get(bufferOffset + 2);
            double near = (double) z1 / (double) zMax;
            double far = (double) z2 / (double) zMax;
            near = (near + far) / 2;
            bufferOffset += 3;
            if (names > 0) {
                int id = picking.buffer.get(bufferOffset + names - 1);
                bufferOffset += names;
                Object current = getObject(id);
                if (current instanceof Vertex) {
                    vertices.add((Vertex) current);
                } else if (current instanceof Edge) {
                    edges.add((Edge) current);
                } else if (current instanceof Surface) {
                    surfaces.add((Surface) current);
                } else {
                    System.out.println("selected " + current.getClass().getName());
                }
            }
        }
        clearNames();
        picking = null;
        return new PickingResult(surfaces, edges, vertices);
    }

    public Vertex selectVertex(int x, int y, Plane plane, Vertex original) {
        Vertex result = null;
        PickingResult record = selectObjects(x, y, plane);
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
        PickingResult record = selectObjects(x, y, plane);
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
        PickingResult record = selectObjects(x, y, plane);
        if (record.surfaces.size() > 0) {
            result = record.surfaces.getLast();
        }
        return result;
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
                drawEdge(current);
            }
        }

        public void drawSurfacesForDisplay(List<Surface> surfaces) {
            gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);
            gl.glEnable(GL2.GL_LIGHTING);
            for (Surface current : surfaces) {
                if (current.isVisible()) {
                    drawSurface(current);
                }
            }
            gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
            gl.glDisable(GL2.GL_LIGHTING);
        }

        public void onGroup(Group current) {
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            drawEdgesForDisplay(current.getEdges());
            apply(babyblue, 1.0f);
            drawSurfacesForDisplay(current.getSurfaces());
            Set<Vertex> mark = new HashSet<Vertex>();
            for (Surface surface : current.getSurfaces()) {
                mark.addAll(surface.getVertices());
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

    public class ToolHandler implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener  {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseWheelMoved(e);
            }
        }
        @Override
        public void keyPressed(KeyEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.keyPressed(e);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.keyReleased(e);
            }
        }
        @Override
        public void keyTyped(KeyEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.keyTyped(e);
            }
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseDragged(e);
            }
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseMoved(e);
            }
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseClicked(e);
            }
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseEntered(e);
            }
        }
        @Override
        public void mouseExited(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseExited(e);
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mousePressed(e);
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (activeTool != null && sketch != null) {
                activeTool.mouseReleased(e);
            }
        }
    }
}
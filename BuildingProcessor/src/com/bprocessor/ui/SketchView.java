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
import javax.media.opengl.glu.GLU;

import com.bprocessor.BasicComponent;
import com.bprocessor.Camera;
import com.bprocessor.Color;
import com.bprocessor.Composite;
import com.bprocessor.Handle;
import com.bprocessor.Mesh;
import com.bprocessor.Line;
import com.bprocessor.Grid;
import com.bprocessor.Edge;
import com.bprocessor.Face;
import com.bprocessor.PolyFace;
import com.bprocessor.Geometry;
import com.bprocessor.Polyhedron;
import com.bprocessor.ItemVisitor;
import com.bprocessor.Material;
import com.bprocessor.Sketch;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.io.ObjFileReader;
import com.bprocessor.ui.commands.DeleteGeometry;
import com.bprocessor.util.CommandManager;
import com.bprocessor.util.CoordinateSystem;
import com.bprocessor.util.Filter;
import com.bprocessor.util.Plane;
import com.jogamp.common.nio.Buffers;
import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants

@SuppressWarnings("serial")
public class SketchView extends View3d {
	private InputListener delegate;
	protected Camera camera;
	protected CoordinateSystem system;
	
	protected double[] modelMatrix = new double[16];
	protected double[] projMatrix = new double[16];
	protected int[] screenport = new int[4];
	
	
	
	
	protected Picking picking;
	
	protected Sketch sketch;
	protected Composite overlay;
	
	protected Grid guideLayer;
	protected BasicComponent man;

	
	private boolean restrictToPlane;
	private boolean gridVisible;
	private boolean snapToGrid;
	private boolean coordinateSystemVisible;
	
	private static Color babyblue = new Color(224f / 255, 255f / 255, 255f / 255);
	private static Color selected_color = new Color(0.8f, 0.2f, 0.3f);
	
	private SketchController controller;
	protected Geometry selected;
	


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
		gridVisible = true;
		snapToGrid = true;
		coordinateSystemVisible = true;
		Vertex center = new Vertex(4, 2, 1.3);
		Vertex eye = new Vertex(6, -9, 8);
		Vertex up = new Vertex(0, 0, 1);
		camera = new Camera(center, eye, up);
		overlay = new Composite("overlay");
		overlay.add(man);
		guideLayer = new Grid("guides");
		overlay.add(guideLayer);
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

	public Grid guideLayer() {
		return guideLayer;
	}
	public void setSelected(Geometry selected) {
		this.selected = selected;
	}
	public Geometry getSelected() {
		return selected;
	}
	public void addOverlay(Mesh geometry) {
		overlay.add(geometry);
	}
	public void removeOverlay(Mesh geometry) {
		overlay.remove(geometry);
	}
	public void setRestrictToPlane(boolean value) {
		restrictToPlane = value;
	}
	public boolean getRestrictToPlane() {
		return restrictToPlane;
	}
	public void setGridVisible(boolean value) {
		gridVisible = value;
	}
	public boolean isGridVisible() {
		return gridVisible;
	}
	public void setSnapToGrid(boolean value) {
		snapToGrid = value;
	}
	public boolean getSnapToGrid() {
		return snapToGrid;
	}
	public void setCoordinateSystemVisible(boolean value) {
		coordinateSystemVisible = value;
	}
	public boolean isCoordinateSystemVisible() {
		return coordinateSystemVisible;
	}

	public void setCoordinateSystem(CoordinateSystem system) {
		this.system = system;
	}
	public CoordinateSystem getCoordinateSystem() {
		if (system != null) {
			return system;
		} else {
			return CoordinateSystem.xy();
		}
	}
	public Plane getPlane() {
		return getCoordinateSystem().plane();
	}
	public Plane getRestriction() {
		if (restrictToPlane) {
			return getPlane();
		} else {
			return null;
		}
	}

	
	public boolean canDeleteSelection() {
		return selected != null;
	}
	public void deleteSelection() {
		if (selected != null) {
			CommandManager.instance().apply(new DeleteGeometry(selected));
			selected = null;
			checkpoint();
			repaint();
		}
	}
	
	public class Picking {
		protected int x;
		protected int y;
		protected int width;
		protected int height;
		protected int hits;
		protected IntBuffer buffer;
		protected ArrayList<Geometry> objects;

		public int register(Geometry object) {
			objects.add(object);
			return objects.size();
		}
		public Geometry get(int id) {
			return objects.get(id - 1);
		}

		public Picking(int x, int y) {
			this.x = x;
			this.y = y;
			width = 6;
			height = 6;
			objects = new ArrayList<Geometry>();
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

	public class CoordinateSystemView {
		
		public void paintAxis(Line line) {
			apply(line.getColor(), 1.0f);
			drawAxis(line);
		}
		public void drawAxis(Line line) {
			Vertex to = line.getTo();
			Vertex from = line.getFrom();
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
		
		public void displayNormal(GL2 gl) {
			CoordinateSystem system = getCoordinateSystem();
			Vertex i = system.getI();
			Vertex j = system.getJ();
			Vertex origin = system.getOrigin();
			Color green = new Color(0.1, 0.8, 0.1);
			Color red = new Color(0.8, 0.1, 0.1);
			Color blue = new Color(0.1, 0.1, 0.8);
			Line x_axis = new Line(origin, origin.add(i), green);
			Line y_axis = new Line(origin, origin.add(j), red);
			Line z_azis = new Line(origin, origin.add(j.cross(i)), blue);
			paintAxis(x_axis);
			paintAxis(y_axis);
			if (!restrictToPlane) {
				paintAxis(z_azis);
			}
		}
		public void displayPicking(GL2 gl) {
			CoordinateSystem system = getCoordinateSystem();
			Vertex i = system.getI();
			Vertex j = system.getJ();
			Vertex origin = system.getOrigin();
			Color green = new Color(0.1, 0.8, 0.1);
			Color red = new Color(0.8, 0.1, 0.1);
			Color blue = new Color(0.1, 0.1, 0.8);
			Line x_axis = new Line(origin, origin.add(i), green);
			Line y_axis = new Line(origin, origin.add(j), red);
			Line z_azis = new Line(origin, origin.add(j.cross(i)), blue);
			gl.glPushName(picking.register(x_axis));
			drawAxis(x_axis);
			gl.glPopName();
			gl.glPushName(picking.register(y_axis));
			drawAxis(y_axis);
			gl.glPopName();
			if (!restrictToPlane) {
				gl.glPushName(picking.register(z_azis));
				drawAxis(z_azis);
				gl.glPopName();
			}
		}
	}
	public class GridView {
		public void displayNormal(GL2 gl) {
			gl.glLineWidth(1.5f);
			gl.glColor4f(221.0f/255, 234.0f/255, 254.0f/ 255, 1.0f);
			CoordinateSystem system = getCoordinateSystem();
			Vertex i = system.getI();
			Vertex j = system.getJ();
			Vertex origin = system.getOrigin();
			{
				Vertex from = origin;
				Vertex v = i.scale(40);
				gl.glBegin(GL_LINES);
				for (int k = 0; k < 41; k++) {
					Vertex to = from.add(v);
					gl.glVertex3d(from.getX(), from.getY(), from.getZ());
					gl.glVertex3d(to.getX(), to.getY(), to.getZ());
					from = from.add(j);
				}
				gl.glEnd();
			}
			{
				Vertex from = origin;
				Vertex v = j.scale(40);
				gl.glBegin(GL_LINES);
				for (int k = 0; k < 41; k++) {
					Vertex to = from.add(v);
					gl.glVertex3d(from.getX(), from.getY(), from.getZ());
					gl.glVertex3d(to.getX(), to.getY(), to.getZ());
					from = from.add(i);
				}
				gl.glEnd();
			}
		}
		public void displayPicking(GL2 gl) {
			CoordinateSystem system = getCoordinateSystem();
			Vertex i = system.getI();
			Vertex j = system.getJ();
			Vertex origin = system.getOrigin();
			{
				Vertex from = origin;
				Vertex v = i.scale(40);
				for (int k = 0; k < 41; k++) {
					Vertex to = from.add(v);
					Edge edge = new Edge(from, to);
					gl.glPushName(picking.register(edge));
					gl.glBegin(GL_LINES);
					gl.glVertex3d(from.getX(), from.getY(), from.getZ());
					gl.glVertex3d(to.getX(), to.getY(), to.getZ());
					gl.glEnd();
					gl.glPopName();
					from = from.add(j);
				}
			}
			{
				Vertex from = origin;
				Vertex v = j.scale(40);
				for (int k = 0; k < 41; k++) {
					Vertex to = from.add(v);
					Edge edge = new Edge(from, to);
					gl.glPushName(picking.register(edge));
					gl.glBegin(GL_LINES);
					gl.glVertex3d(from.getX(), from.getY(), from.getZ());
					gl.glVertex3d(to.getX(), to.getY(), to.getZ());
					gl.glEnd();
					gl.glPopName();
					from = from.add(i);
				}
			}

		}
	}

	/**
	 * Called back immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		
		tesselator();
	}
	
	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		callback.init(gl, glu);
		
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		float aspect = (float)width / height;

		if (picking != null) {
			int[] viewport = new int[] {0, 0, width, height};
			glu.gluPickMatrix(picking.x, viewport[3] - picking.y, picking.width, picking.height, viewport, 0);
		}

		glu.gluPerspective(45.0, aspect, 5, 500.0);

		if (picking != null) {
			picking.buffer = Buffers.newDirectIntBuffer(256);
			gl.glSelectBuffer(256, picking.buffer);
			gl.glRenderMode(GL2.GL_SELECT);
			gl.glInitNames();
		}

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		if (sketch != null) {
			applyCamera(camera);
			GridView grid = new GridView();
			CoordinateSystemView systemView = new CoordinateSystemView();
			
			if (picking != null) {
				Plane restriction = getRestriction();
				PickingPainter pickingPainter = new PickingPainter(restriction);
				sketch.getGroup().accept(pickingPainter);
				overlay.accept(pickingPainter);
				if (isGridVisible() && getSnapToGrid()) {
					grid.displayPicking(gl);
				}
				if (isCoordinateSystemVisible()) {
					systemView.displayPicking(gl);
				}
			} else {
				if (isGridVisible()) {
					grid.displayNormal(gl);
				}
				if (isCoordinateSystemVisible()) {
					systemView.displayNormal(gl);
				}
				Plane restriction = getRestriction();
				BasicPainter basicPainter = new BasicPainter(restriction);
				sketch.getGroup().accept(basicPainter);
				overlay.accept(basicPainter);
				if (restriction != null) {
					WireFramePainter wireFramePainter = new WireFramePainter(null);
					sketch.getGroup().accept(wireFramePainter);
					overlay.accept(wireFramePainter);
				}

			}
			if (picking != null) {
				gl.glFlush();
				picking.hits = gl.glRenderMode(GL_RENDER);
			}
		}
	}



	public Transformation transformation() {
		return new Transformation(glu, modelMatrix, projMatrix, screenport);
	}
	public Edge getRay(double x, double y) {
		y = getSize().getHeight() - y;
		Vertex near = new Vertex(x, y, 0.0);
		Vertex far = new Vertex(x, y, 1.0);
		Edge ray = new Edge(near, far);
		return transformation().unProject(ray);
	}

	public PickingResult pickObjects(int x, int y, Filter<Geometry> filter) {
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
				Geometry current = picking.get(id);
				if (filter == null || filter.evaluate(current)) {
					if (current instanceof Vertex) {
						if (z < vertex_z) {
							currentVertex = (Vertex) current;
							vertex_z = z;
						}
						vertices.add((Vertex) current);
					} else if (current instanceof Edge) {
						Edge edge = (Edge) current;
						if (z < edge_z) {
							currentEdge = edge;
							edge_z = z;
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
		}
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

	public Geometry pickObject(int x, int y, Filter<Geometry> filter) {
		PickingResult record = pickObjects(x, y, filter);
		return record.nearest;
	}
	
	
	public Intersection getIntersection(int x, int y, Filter<Geometry> filter) {
		Vertex result = null;
		String type = null;
		Edge ray = getRay(x, y);
		PickingResult record = pickObjects(x, y, filter);
		Plane plane = getPlane();
		if (record.nearest == null) {
			result = plane.intersection(ray);
			type = Intersection.PLANE;
		} else {
			if (record.nearest instanceof Vertex) {
				result = (Vertex) record.nearest;
				type = Intersection.VERTEX;
			} else if (record.nearest instanceof Edge) {
				Edge edge = (Edge) record.nearest;
				for (Edge current : record.edges) {
					if (current != edge) {
						if (!edge.parallel(current)) {
							result = edge.intersection(current);
							if (result != null) {
								break;
							}
						}
					}
				}
				if (result == null) {
					result = edge.closest(ray);
				}
				type = Intersection.EDGE;
			} else if (record.nearest instanceof Surface) {
				Surface surface = (Surface) record.nearest;
				result = surface.plane().intersection(ray);
				type = Intersection.SURFACE;
			}
		}
		if (result != null) {
			return new Intersection(result, type);
		} else {
			return null;
		}
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

	private void apply(Color color, float alpha) {
		if (alpha == 1.0) {
			gl.glColor3f(color.getRed(), color.getGreen(), color.getBlue());
		} else {
			gl.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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
		for (PolyFace group : component.getGroups()) {
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

	public abstract class Painter implements ItemVisitor {
		protected Plane restriction;

		public Painter(Plane restriction) {
			this.restriction = restriction;
		}

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
		public void drawLine(Line line) {
			Vertex to = line.getTo();
			Vertex from = line.getFrom();
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
		public void drawVertex(Vertex vertex) {
			gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3d(vertex.getX(), vertex.getY(), vertex.getZ());
			gl.glEnd();
		}
	}

	public class BasicPainter extends Painter {
		public BasicPainter(Plane restriction) {
			super(restriction);
		}
		public void drawEdges(List<Edge> edges) {
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
		public void drawSurfaces(List<Surface> surfaces) {
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
		@Override
		public void visit(Polyhedron current) {
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			drawEdges(current.getEdges());
			if (selected instanceof Line) {
				apply(selected_color, 1.0f);
				drawLine((Line) selected);
			} else if (selected instanceof Edge) {
				apply(selected_color, 1.0f);
				drawEdge((Edge) selected);
			}
			apply(babyblue, 1.0f);
			drawSurfaces(current.getSurfaces());
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
				if (vertex != selected) {
					if (!mark.contains(vertex)) {
						drawVertex(vertex);
					}
				}
			}
			if (selected instanceof Vertex) {
				Vertex vertex = (Vertex) selected;
				apply(selected_color, 1.0f);
				drawVertex(vertex);
			}
			gl.glEnable(GL_DEPTH_TEST);
		}
		@Override
		public void visit(BasicComponent current) {
			if (restriction == null) {
				drawComponentForDisplay(current);
			}
		}

		@Override
		public void visit(Grid current) {
			gl.glLineWidth(1.0f);
			gl.glEnable(GL_LINE_STIPPLE);
			for (Line line : current.getLines()) {
				if (restriction != null) {
					if (!restriction.contains(line)) {
						continue;
					}
				}
				Color color = line.getColor();
				if (color != null) {
					gl.glColor3fv(color.values(), 0);
				} else {
					gl.glColor3f(0.1f, 0.2f, 0.5f);
				}
				drawLine(line);
			}
			gl.glDisable(GL_LINE_STIPPLE);
			gl.glDisable(GL_DEPTH_TEST);
			for (Handle handle : current.getHandles()) {
				Color color = handle.getColor();
				if (color != null) {
					gl.glColor3fv(color.values(), 0);
				} else {
					gl.glColor3f(0.1f, 0.2f, 0.5f);
				}
				drawVertex(handle);
			}
			gl.glEnable(GL_DEPTH_TEST);
		}
	}

	public class PickingPainter extends Painter {
		public PickingPainter(Plane restriction) {
			super(restriction);
		}
		public void drawEdgesForPicking(List<Edge> edges) {
			for (Edge current : edges) {
				if (restriction != null) {
					if (!restriction.contains(current)) {
						continue;
					}
				}
				gl.glPushName(picking.register(current));
				drawEdge(current);
				gl.glPopName();
			}
		}
		public void drawSurfacesForPicking(List<Surface> surfaces) {
			for (Surface current : surfaces) {
				if (current.isVisible()) {
					gl.glPushName(picking.register(current));
					drawSurface(current);
					gl.glPopName();
				}
			}
		}
		public void drawVerticesForPicking(List<Vertex> vertices) {
			for (Vertex current : vertices) {
				gl.glPushName(picking.register(current));
				drawVertex(current);
				gl.glPopName();
			}
		}

		public void visit(Polyhedron current) {
			drawEdgesForPicking(current.getEdges());
			drawSurfacesForPicking(current.getSurfaces());
			drawVerticesForPicking(current.getVertices());
		}
		public void visit(BasicComponent current) {
		}
		public void visit(Grid current) {
			for (Line line : current.getLines()) {
				gl.glPushName(picking.register(line));
				drawLine(line);
				gl.glPopName();
			}
			for (Handle handle : current.getHandles()) {
				gl.glPushName(picking.register(handle));
				drawVertex(handle);
				gl.glPopName();
			}
		}
	}

	public class WireFramePainter extends Painter {
		public WireFramePainter(Plane restriction) {
			super(restriction);
		}
		public void visit(Polyhedron current) {
			gl.glColor4f(0.7f, 0.7f, 7.0f, 0.3f);
			for (Edge edge : current.getEdges()) {
				drawEdge(edge);
			}
			gl.glColor4f(0.7f, 0.7f, 7.0f, 1.0f);
		}
		public void visit(BasicComponent current) {
			gl.glColor4f(0.9f, 0.9f, 1.0f, 0.1f);
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
			for (PolyFace group : current.getGroups()) {
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
			gl.glColor4f(0.9f, 0.9f, 1.0f, 1.0f);;
		}
		public void visit(Grid current) {			
		}
	}
}
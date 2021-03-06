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


import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

import com.bprocessor.Camera;
import com.bprocessor.Color;
import com.bprocessor.Component;
import com.bprocessor.Composite;
import com.bprocessor.Group;
import com.bprocessor.Handle;
import com.bprocessor.Mesh;
import com.bprocessor.Line;
import com.bprocessor.Net;
import com.bprocessor.Edge;
import com.bprocessor.Face;
import com.bprocessor.Path;
import com.bprocessor.PolyFace;
import com.bprocessor.Geometry;
import com.bprocessor.Polyhedron;
import com.bprocessor.ItemVisitor;
import com.bprocessor.Material;
import com.bprocessor.Sketch;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.bprocessor.io.ObjFileReader;
import com.bprocessor.ui.actions.Action;
import com.bprocessor.ui.actions.ExtrudeSufaceAction;
import com.bprocessor.ui.actions.RotateAction;
import com.bprocessor.ui.actions.ScaleAction;
import com.bprocessor.ui.actions.TranslateAction;
import com.bprocessor.ui.commands.DeleteGeometry;
import com.bprocessor.ui.panels.AttributePanel;
import com.bprocessor.util.CommandManager;
import com.bprocessor.util.CoordinateSystem;
import com.bprocessor.util.Filter;
import com.bprocessor.util.Plane;
import com.jogamp.common.nio.Buffers;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants

@SuppressWarnings("serial")
public class SketchView extends View3d {
	private InputListener delegate;
	protected CoordinateSystem system;

	protected double[] modelMatrix = new double[16];
	protected double[] projMatrix = new double[16];
	protected int[] screenport = new int[4];



	protected Picking picking;

	protected Sketch sketch;
	protected List<Mesh> overlay;

	protected Net guideLayer;
	protected Composite man;


	private boolean restrictToPlane;
	private boolean gridVisible;
	private boolean snapToGrid;
	private boolean coordinateSystemVisible;

	private static Color babyblue = new Color(224f / 255, 255f / 255, 255f / 255);
	private static Color selected_color = new Color(0.8f, 0.2f, 0.3f);

	private SketchController controller;
	private AttributePanel attributePanel;
	protected Path<? extends Geometry> selected;

	public SketchView(SketchController controller) {
		this.controller = controller;
		FileInputStream file = null;
		try {
			ObjFileReader input = new ObjFileReader();
			man = input.readObject(new File("models/man.obj"));
			man.scaleIt(1 / 100.0);
			man.moveIt(0.5, 0.5, 0);
			man.accept(new ItemVisitor() {
				@Override
				public void visit(PolyFace current) {
					current.setMaterial(null);
				}
				@Override
				public void visit(Net current) {}
				@Override
				public void visit(Polyhedron current) {}
				@Override
				public void enterComposite(Composite current) {
				}
				@Override
				public void exitComposite(Composite current) {
				}
			});
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
		coordinateSystemVisible = false;
		overlay = new LinkedList<Mesh>();
		this.addGLEventListener(this);
	}

	public void setAttributePanel(AttributePanel panel) {
		attributePanel = panel;
		attributePanel.setSketchView(this);
	}
	public AttributePanel getAttributePanel() {
		return attributePanel;
	}

	public Camera getCamera() {
		if (sketch != null) {
			return sketch.getCamera();
		} else {
			return null;
		}
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

	public Net guideLayer() {
		return sketch.getGrid().getNet();
	}

	public void setSelected(Path<? extends Geometry> selected) {
		this.selected = selected;
		if (selected != null && selected.target() != null) {
			attributePanel.setTarget(selected.target());
		} else{
			attributePanel.setTarget(sketch);
		}
	}
	public Geometry getSelected() {
		if (selected != null) {
			return selected.target();
		} else {
			return null;
		}
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
		if (system != null) {
			sketch.getGrid().apply(system);
		} else {
			sketch.getGrid().apply(CoordinateSystem.xy());
		}
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
		return getSelected() instanceof Geometry;
	}
	public void deleteSelection() {
		if (getSelected() instanceof Geometry) {
			CommandManager.instance().apply(new DeleteGeometry((Geometry) getSelected()));

			setSelected(null);
			checkpoint();
			repaint();
		}
	}

	public boolean canGroupSelection() {
		return getSelected() instanceof Geometry;
	}
	public void groupSelection() {
		if (getSelected() instanceof Geometry) {
			System.out.println("path: " + selected);
			Group group = (Group) selected.path().getLast();
			Polyhedron polyhedron = (Polyhedron) group.getMesh();
			List<Surface> surfaces = new LinkedList<Surface>(polyhedron.getSurfaces());
			List<Edge> edges = new LinkedList<Edge>(polyhedron.getEdges());
			List<Vertex> vertices = new LinkedList<Vertex>(polyhedron.getVertices());
			
			for (Surface surface : surfaces) {
				polyhedron.remove(surface);
			}
			for (Edge edge : edges) {
				polyhedron.remove(edge);
			}
			for (Vertex vertex : vertices) {
				polyhedron.remove(vertex);
			}
			
			Polyhedron mesh = new Polyhedron("Child Mesh");
			for (Vertex vertex : vertices) {
				mesh.add(vertex);
			}
			for (Edge edge : edges) {
				mesh.add(edge);
			}
			for (Surface surface : surfaces) {
				mesh.add(surface);
			}
			
			Group child = new Group("Child", mesh);
			group.add(child);
			
			setSelected(null);
			repaint();
		}
	}

	public boolean canExtrudeSelection() {
		return getSelected() instanceof Surface;
	}
	public void extrudeSelection() {
		if (getSelected() instanceof Surface) {
			Action action = new ExtrudeSufaceAction((Surface) getSelected(), 1.0);
			attributePanel.setTarget(action);
		}
	}

	public boolean canRotateSelection() {
		return getSelected() instanceof Geometry;
	}
	public void rotateSelection() {
		if (getSelected() instanceof Geometry) {
			Action action = new RotateAction(((Geometry) getSelected()).getOwner(), 45);
			attributePanel.setTarget(action);
		}
	}

	public boolean canTranslateSelection() {
		return getSelected() instanceof Geometry;
	}
	public void translateSelection() {
		if (getSelected() instanceof Geometry) {
			Action action = new TranslateAction(((Geometry) getSelected()).getOwner(), 0, 0, 0);
			attributePanel.setTarget(action);
		}
	}

	public boolean canScaleSelection() {
		return getSelected() instanceof Geometry;
	}
	public void scaleSelection() {
		if (getSelected() instanceof Geometry) {
			Action action = new ScaleAction(((Geometry) getSelected()).getOwner(), 1.0);
			attributePanel.setTarget(action);
		}
	}

	public class Picking {
		protected int x;
		protected int y;
		protected int width;
		protected int height;
		protected int hits;
		protected IntBuffer buffer;
		protected ArrayList<Object> objects;

		public int register(Object object) {
			objects.add(object);
			return objects.size();
		}
		public Object get(int id) {
			return objects.get(id - 1);
		}

		public Picking(int x, int y) {
			this.x = x;
			this.y = y;
			width = 6;
			height = 6;
			objects = new ArrayList<Object>();
		}
	}
	public class PickingResult {
		protected Path<? extends Geometry> nearest;
		protected LinkedList<Path<Surface>> surfaces;
		protected LinkedList<Path<Edge>> edges;
		protected LinkedList<Path<Vertex>> vertices;
		public PickingResult(Path<? extends Geometry> nearest, LinkedList<Path<Surface>> surfaces, LinkedList<Path<Edge>> edges, LinkedList<Path<Vertex>> vertices) {
			this.nearest = nearest;
			this.edges = edges;
			this.vertices = vertices;
			this.surfaces = surfaces;
		}
	}

	public void setSketch(Sketch sketch) {
		if (sketch != this.sketch) {
			this.sketch = sketch;
			attributePanel.setTarget(sketch);
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

	/**
	 * Called back immediately after the OpenGL context is initialized. Can be used
	 * to perform one-time initialization. Run only once.
	 */
	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);

		tesselator();
	}

	public List<Mesh> getMeshes(boolean picking) {
		LinkedList<Mesh> meshes = new LinkedList<Mesh>();
		meshes.add(sketch.display());
		meshes.addAll(overlay);
		return meshes;
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
		if (sketch != null) {
			double fov = getCamera().getFov();
			double near = getCamera().getNear();
			double far = getCamera().getFar();
			glu.gluPerspective(fov, aspect, near, far);
		}

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
			applyCamera(getCamera());
			CoordinateSystemView systemView = new CoordinateSystemView();
			List<Mesh> meshes = getMeshes(picking != null);

			if (picking != null) {
				Plane restriction = getRestriction();
				PickingPainter pickingPainter = new PickingPainter(restriction);
				for (Mesh current : meshes) {
					current.accept(pickingPainter);
				}
				if (isCoordinateSystemVisible()) {
					systemView.displayPicking(gl);
				}
			} else {
				if (isCoordinateSystemVisible()) {
					systemView.displayNormal(gl);
				}
				Plane restriction = getRestriction();
				BasicPainter basicPainter = new BasicPainter(restriction);

				for (Mesh current : meshes) {
					current.accept(basicPainter);
				}
				if (restriction != null) {
					WireFramePainter wireFramePainter = new WireFramePainter(null);
					for (Mesh current : meshes) {
						if (!(current instanceof Net)) {
							current.accept(wireFramePainter);
						}
					}
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

		LinkedList<Path<Vertex>> vertices = new LinkedList<Path<Vertex>>();
		LinkedList<Path<Edge>> edges = new LinkedList<Path<Edge>>();
		LinkedList<Path<Surface>> surfaces = new LinkedList<Path<Surface>>();

		Path<Vertex> currentVertex = null;
		double vertex_z = 1.0;
		Path<Edge> currentEdge = null;
		double edge_z = 1.0;
		Path<Surface> currentSurface = null;
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
				LinkedList<Component> components = new LinkedList<Component>();
				for (int j = 0; j < names; j++) {
					int id = picking.buffer.get(bufferOffset + j);
					Object current = picking.get(id);
					if (current instanceof Geometry) {
						Path<? extends Geometry> path = new Path<Geometry>(components, (Geometry) current);
						if (filter == null || filter.evaluate(path.target())) {
							if (path.target() instanceof Vertex) {
								if (z < vertex_z) {
									currentVertex = (Path<Vertex>) path;
									vertex_z = z;
								}
								vertices.add((Path<Vertex>) path);
							} else if (path.target() instanceof Edge) {
								if (z < edge_z) {
									currentEdge = (Path<Edge>) path;
									edge_z = z;
								}
								edges.add((Path<Edge>) path);
							} else if (current instanceof Surface) {
								if (z < surface_z) {
									currentSurface = (Path<Surface>) path;
									surface_z =z;
								}
								surfaces.add((Path<Surface>) path);
							} else {
								System.out.println("selected " + current.getClass().getName());
							}
						}
					} else if (current instanceof Component){
						components.add((Component) current);
					}
				}
				bufferOffset += names;
			}
		}
		picking = null;
		if (currentSurface != null) {
			Plane plane = currentSurface.target().plane();
			if (currentVertex != null) {
				if (surface_z < vertex_z) {
					if (!plane.contains(currentVertex.target())) {
						currentVertex = null;
					}
				}
			}
			if (currentEdge != null) {
				if (surface_z < edge_z) {
					if (!plane.contains(currentEdge.target())) {
						currentEdge = null;
					}
				}
			}
		}
		if (currentEdge != null && currentVertex != null) {
			if (edge_z < vertex_z) {
				if (!currentEdge.target().intersects(currentVertex.target())) {
					currentVertex = null;
				}
			}
		}
		Path<? extends Geometry> nearest = null;
		if (currentVertex != null) {
			nearest = currentVertex;
		} else if (currentEdge != null) {
			nearest = currentEdge;
		} else if (currentSurface != null) {
			nearest = currentSurface;
		}
		return new PickingResult(nearest, surfaces, edges, vertices);
	}

	public Path<? extends Geometry> pickObject(int x, int y, Filter<Geometry> filter) {
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
			if (record.nearest.target() instanceof Vertex) {
				result = (Vertex) record.nearest.target();
				type = Intersection.VERTEX;
			} else if (record.nearest.target() instanceof Edge) {
				Edge edge = (Edge) record.nearest.target();
				for (Path<Edge> current : record.edges) {

					if (current.target() != edge) {
						if (!edge.parallel(current.target())) {
							result = edge.intersection(current.target());
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
			} else if (record.nearest.target() instanceof Surface) {
				Surface surface = (Surface) record.nearest.target();
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

	private void apply(Material material) {
		Color diffuse = material.getDiffuse();
		gl.glColor3f(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue());
	}

	private void drawPolyFaceForDisplay(PolyFace mesh) {
		gl.glColor3f(0.9f, 0.9f, 1.0f);
		gl.glEnable(GL2.GL_LIGHTING);
		Material material = mesh.getMaterial();
		if (material != null) {
			apply(material);
		}
		gl.glBegin(GL2.GL_TRIANGLES);
		for (Face face : mesh.getFaces()) {
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
				if (current != getSelected()) {
					drawEdge(current);
				}
			}
		}
		public void drawSurfaces(List<Surface> surfaces) {
			gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1.0f, 1.0f);
			for (Surface current : surfaces) {
				if (current.isVisible()) {
					if (current != getSelected()) {
						drawSurface(current);
					}
				}
			}
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

		}
		@Override
		public void visit(Polyhedron current) {
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			drawEdges(current.getEdges());
			if (getSelected() instanceof Edge) {
				apply(selected_color, 1.0f);
				drawEdge((Edge) getSelected());
			}
			apply(babyblue, 1.0f);
			drawSurfaces(current.getSurfaces());
			Set<Vertex> mark = new HashSet<Vertex>();
			for (Surface surface : current.getSurfaces()) {
				mark.addAll(surface.getVertices());
			}
			if (getSelected() instanceof Surface) {
				apply(selected_color, 0.2f);
				drawSurface((Surface) getSelected());
			}
			gl.glColor3f(0.0f, 0.0f, 0.0f);
			gl.glDisable(GL_DEPTH_TEST);
			for (Vertex vertex : current.getVertices()) {
				if (vertex != getSelected()) {
					if (!mark.contains(vertex)) {
						drawVertex(vertex);
					}
				}
			}
			if (getSelected() instanceof Vertex) {
				Vertex vertex = (Vertex) getSelected();
				apply(selected_color, 1.0f);
				drawVertex(vertex);
			}
			gl.glEnable(GL_DEPTH_TEST);
		}
		@Override
		public void visit(Net current) {
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
				gl.glLineWidth(line.getWidth());
				if (line.getStippled()) {
					gl.glEnable(GL_LINE_STIPPLE);
				} else {
					gl.glDisable(GL_LINE_STIPPLE);
				}
				drawEdge(line);
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
		@Override
		public void visit(PolyFace current) {
			if (restriction == null) {
				drawPolyFaceForDisplay(current);
			}
		}
		@Override
		public void enterComposite(Composite current) {
			// TODO Auto-generated method stub

		}
		@Override
		public void exitComposite(Composite current) {
			// TODO Auto-generated method stub

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
		public void visit(Net current) {
			for (Line line : current.getLines()) {
				gl.glPushName(picking.register(line));
				drawEdge(line);
				gl.glPopName();
			}
			for (Handle handle : current.getHandles()) {
				gl.glPushName(picking.register(handle));
				drawVertex(handle);
				gl.glPopName();
			}
		}
		@Override
		public void visit(PolyFace current) {
		}
		@Override
		public void enterComposite(Composite current) {
			if (current.getTag() != null) {
				gl.glPushName(picking.register(current.getTag()));
			}
		}
		@Override
		public void exitComposite(Composite current) {

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
		public void visit(Net current) {			
		}
		@Override
		public void visit(PolyFace current) {
			gl.glColor4f(0.9f, 0.9f, 1.0f, 0.1f);
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
			gl.glBegin(GL_TRIANGLES);
			for (Face face : current.getFaces()) {
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
			gl.glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
			gl.glColor4f(0.9f, 0.9f, 1.0f, 1.0f);
		}
		@Override
		public void enterComposite(Composite current) {
			// TODO Auto-generated method stub

		}
		@Override
		public void exitComposite(Composite current) {
			// TODO Auto-generated method stub

		}
	}
}
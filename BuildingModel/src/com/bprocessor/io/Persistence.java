package com.bprocessor.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bprocessor.Edge;
import com.bprocessor.Entity;
import com.bprocessor.Geometry;
import com.bprocessor.Net;
import com.bprocessor.Polyhedron;
import com.bprocessor.Mesh;
import com.bprocessor.Sketch;
import com.bprocessor.Surface;
import com.bprocessor.Vertex;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Persistence {
    public static class PGeometry {
        protected Entity original;
        private int id;
        private int owner;
        public PGeometry() {}
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public int getOwner() {
            return owner;
        }
        public void setOwner(int owner) {
            this.owner = owner;
        }
    }
    public static class PSketch extends PGeometry {
        protected Entity original;
        private int uid;
        private String name;
        private PGroup group;
        public PSketch() {}
        public int getUid() {
            return uid;
        }
        public void setUid(int uid) {
            this.uid = uid;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public PGroup getGroup() {
            return group;
        }
        public void setGroup(PGroup group) {
            this.group = group;
        }
    }
    public static class PEdge extends PGeometry {
        private int from;
        private int to;
        public PEdge() {}
        public int getFrom() {
            return from;
        }
        public void setFrom(int from) {
            this.from = from;
        }
        public int getTo() {
            return to;
        }
        public void setTo(int to) {
            this.to = to;
        }
    }
    public static class PVertex extends PGeometry {
        private double x;
        private double y;
        private double z;
        public PVertex() {}
        public double getX() {
            return x;
        }
        public void setX(double x) {
            this.x = x;
        }
        public double getY() {
            return y;
        }
        public void setY(double y) {
            this.y = y;
        }
        public double getZ() {
            return z;
        }
        public void setZ(double z) {
            this.z = z;
        }
    }
    public static class PSurface extends PGeometry {
        private boolean visible;
        private int exterior;
        private List<Integer> holes;
        private List<Integer> edges;

        public PSurface() {}
        public boolean isVisible() {
            return visible;
        }
        public void setVisible(boolean visible) {
            this.visible = visible;
        }
        public int getExterior() {
            return exterior;
        }
        public void setExterior(int exterior) {
            this.exterior = exterior;
        }
        public List<Integer> getHoles() {
            return holes;
        }
        public void setHoles(List<Integer> holes) {
            this.holes = holes;
        }
        public List<Integer> getEdges() {
            return edges;
        }
        public void setEdges(List<Integer> edges) {
            this.edges = edges;
        }
    }
    public static class PGroup extends PGeometry {
        private String name;
        private List<PGroup> groups;
        private List<PSurface> surfaces;
        private List<PEdge> edges;
        private List<PVertex> vertices;

        public PGroup() {}

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<PGroup> getGroups() {
            return groups;
        }
        public void setGroups(List<PGroup> groups) {
            this.groups = groups;
        }
        public List<PSurface> getSurfaces() {
            return surfaces;
        }
        public void setSurfaces(List<PSurface> surfaces) {
            this.surfaces = surfaces;
        }
        public List<PEdge> getEdges() {
            return edges;
        }
        public void setEdges(List<PEdge> edges) {
            this.edges = edges;
        }
        public List<PVertex> getVertices() {
            return vertices;
        }
        public void setVertices(List<PVertex> vertices) {
            this.vertices = vertices;
        }
    }

    public static void save(Sketch sketch, String filename) {
        current_id = 1;
        PSketch psketch = externalize(sketch);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(filename), psketch);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void serialize(Sketch sketch, OutputStream output) throws Exception {
        current_id = 1;
        PSketch psketch = externalize(sketch);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(output, psketch);
    }
    public static Sketch unserialize(InputStream input) {
    	Sketch sketch = null;
    	ObjectMapper mapper = new ObjectMapper();
    	PSketch psketch;
		try {
			psketch = mapper.readValue(input, PSketch.class);
			Map<Integer, Entity> map = new HashMap<Integer, Entity>();
	        sketch = internalize(psketch, map);
	        internalizeReferences(psketch, map);
		} catch (JsonParseException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
    	return sketch;
    }

    public static Sketch load(String filename) {
        return load(new File(filename));
    }
    public static Sketch load(File file) {
        ObjectMapper mapper = new ObjectMapper();
        Sketch sketch = null;
        try {
            PSketch psketch = mapper.readValue(file, PSketch.class);
            Map<Integer, Entity> map = new HashMap<Integer, Entity>();
            sketch = internalize(psketch, map);
            internalizeReferences(psketch, map);
        } catch (JsonParseException e) {
        } catch (JsonMappingException e) {
        } catch (IOException e) {
        }
        return sketch;
    }

    private static int current_id;

    private static PSketch externalize(Sketch sketch) {
        PSketch psketch = new PSketch();
        psketch.original = sketch;
        int id = current_id++;
        psketch.setId(id);
        sketch.setId(id);
        psketch.setUid(sketch.getUid());
        psketch.setName(sketch.getName());
        psketch.setGroup(externalize(sketch.getPolyhedron()));
        externalizeReferenes(psketch);
        return psketch;
    }
    private static PGroup externalize(Polyhedron group) {
        PGroup pgroup = new PGroup();
        pgroup.original = group;
        pgroup.setId(current_id++);
        group.setId(pgroup.getId());
        pgroup.setName(group.getName());
        {
            List<PVertex> lst = new LinkedList<PVertex>();
            for (Vertex vertex : group.getVertices()) {
                lst.add(externalize(vertex));
            }
            pgroup.setVertices(lst);
        }
        {
            List<PEdge> lst = new LinkedList<PEdge>();
            for (Edge edge : group.getEdges()) {
                lst.add(externalize(edge));
            }
            pgroup.setEdges(lst);
        }
        {
            List<PSurface> lst = new LinkedList<PSurface>();
            for (Surface surface : group.getSurfaces()) {
                lst.add(externalize(surface));
            }
            pgroup.setSurfaces(lst);
        }
        return pgroup;
    }
    private static PSurface externalize(Surface surface) {
        PSurface psurface = new PSurface();
        psurface.original = surface;
        int id = current_id++;
        psurface.setId(id);
        surface.setId(id);
        psurface.setVisible(surface.isVisible());
        return psurface;
    }
    private static PEdge externalize(Edge edge) {
        PEdge pedge = new PEdge();
        pedge.original = edge;
        pedge.setId(current_id++);
        edge.setId(pedge.getId());
        return pedge;
    }
    private static PVertex externalize(Vertex vertex) {
        PVertex pvertex = new PVertex();
        pvertex.original = vertex;
        pvertex.setId(current_id++);
        vertex.setId(pvertex.getId());
        pvertex.setX(vertex.getX());
        pvertex.setY(vertex.getY());
        pvertex.setZ(vertex.getZ());
        return pvertex;
    }

    private static void externalizeReferenes(PSketch psketch) {
        externalizeReferenes(psketch.getGroup());
    }

    private static void externalizeReferenes(PGroup pgroup) {
        for (PSurface psurface : pgroup.getSurfaces()) {
            externalizeReferences(psurface);
        }
        for (PEdge pedge : pgroup.getEdges()) {
            externalizeReferences(pedge);
        }
        for (PVertex pvertex : pgroup.getVertices()) {
            externalizeReferences(pvertex);
        }
    }

    private static void externalizeReferences(PSurface psurface) {
        Surface surface = (Surface) psurface.original;
        psurface.setOwner(surface.getOwner().getId());
        List<Integer> pedges = new LinkedList<Integer>();
        for (Edge edge : surface.getEdges()) {
            pedges.add(edge.getId());
        }
        psurface.setEdges(pedges);
        if (surface.getHoles().size() > 0) {
            List<Integer> pholes = new LinkedList<Integer>();
            for (Surface hole : surface.getHoles()) {
                pholes.add(hole.getId());
            }
            psurface.setHoles(pholes);
        }
        if (surface.getExterior() != null) {
            psurface.setExterior(surface.getExterior().getId());
        }
    }
    private static void externalizeReferences(PEdge pedge) {
        Edge edge = (Edge) pedge.original;
        pedge.setOwner(edge.getOwner().getId());
        pedge.setFrom(edge.getFrom().getId());
        pedge.setTo(edge.getTo().getId());
    }
    private static void externalizeReferences(PVertex pvertex) {
        Vertex vertex  = (Vertex) pvertex.original;
        pvertex.setOwner(vertex.getOwner().getId());
    }

    private static Sketch internalize(PSketch psketch, Map<Integer, Entity> map) {
        Sketch sketch = new Sketch();
        map.put(psketch.getId(), sketch);
        psketch.original = sketch;
        sketch.setUid(psketch.getUid());
        sketch.setName(psketch.getName());
        sketch.setPolyhedron(internalize(psketch.getGroup(), map));
        sketch.setGrid(new Net("Main"));
        return sketch;
    }
    private static Polyhedron internalize(PGroup pgroup, Map<Integer, Entity> map) {
        Polyhedron group = new Polyhedron();
        map.put(pgroup.getId(), group);
        group.setName(pgroup.getName());
        pgroup.original = group;
        {
            List<Surface> surfaces = new LinkedList<Surface>();
            for (PSurface psurface : pgroup.getSurfaces()) {
                surfaces.add(internalize(psurface, map));
            }
            group.setSurfaces(surfaces);
        }
        {
            List<Edge> edges = new LinkedList<Edge>();
            for (PEdge pedge : pgroup.getEdges()) {
                edges.add(internalize(pedge, map));
            }
            group.setEdges(edges);
        }
        {
            List<Vertex> vertices = new LinkedList<Vertex>();
            for (PVertex pvertex : pgroup.getVertices()) {
                vertices.add(internalize(pvertex, map));
            }
            group.setVertices(vertices);
        }
        return group;
    }
    private static Surface internalize(PSurface psurface,  Map<Integer, Entity> map) {
        Surface surface = new Surface();
        map.put(psurface.getId(), surface);
        psurface.original = surface;
        surface.setVisible(psurface.isVisible());
        return surface;
    }
    private static Edge internalize(PEdge pedge, Map<Integer, Entity> map) {
        Edge edge = new Edge();
        map.put(pedge.getId(), edge);
        pedge.original = edge;
        return edge;
    }
    private static Vertex internalize(PVertex pvertex, Map<Integer, Entity> map) {
        Vertex vertex = new Vertex();
        map.put(pvertex.getId(), vertex);
        pvertex.original = vertex;
        vertex.setX(pvertex.getX());
        vertex.setY(pvertex.getY());
        vertex.setZ(pvertex.getZ());
        return vertex;
    }

    private static void internalizeReferences(PSketch psketch, Map<Integer, Entity> map) {
        internalizeReferences(psketch.getGroup(), map);
    }
    private static void internalizeReferences(PGroup pgroup, Map<Integer, Entity> map) {
    	Polyhedron poly = (Polyhedron) pgroup.original;
        for (PSurface psurface : pgroup.getSurfaces()) {
            internalizeReferences(psurface, map);
        }
        for (PEdge pedge : pgroup.getEdges()) {
            internalizeReferences(pedge, map);
        }
        for (PVertex pvertex : pgroup.getVertices()) {
            internalizeReferences(pvertex, map);
        }
    }
    private static void internalizeReferences(PSurface psurface, Map<Integer, Entity> map) {
        Surface surface = (Surface) psurface.original;
        surface.setOwner((Mesh) map.get(psurface.getOwner()));
        List<Edge> edges = new LinkedList<Edge>();
        for (Integer id : psurface.getEdges()) {
            edges.add((Edge) map.get(id));
        }
        surface.setEdges(edges);
        if (psurface.getHoles() != null) {
            Set<Surface> holes = new HashSet<Surface>();
            for (Integer id : psurface.getHoles()) {
                holes.add((Surface) map.get(id));
            }
            surface.setHoles(holes);
        }
        if (psurface.getExterior() != 0) {
            surface.setExterior((Surface) map.get(psurface.getExterior()));
        }
    }
    private static void internalizeReferences(PEdge pedge, Map<Integer, Entity> map) {
        Edge edge = (Edge) pedge.original;
        edge.setOwner((Mesh) map.get(pedge.getOwner()));
        edge.setFrom((Vertex) map.get(pedge.getFrom()));
        edge.setTo((Vertex) map.get(pedge.getTo()));
    }
    private static void internalizeReferences(PVertex pvertex, Map<Integer, Entity> map) {
        Vertex vertex = (Vertex) pvertex.original;
        vertex.setOwner((Mesh) map.get(pvertex.getOwner()));
    }
}

package com.bprocessor.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bprocessor.BasicComponent;
import com.bprocessor.Color;
import com.bprocessor.Face;
import com.bprocessor.FaceGroup;
import com.bprocessor.Material;
import com.bprocessor.MaterialLibrary;
import com.bprocessor.Vertex;

/**
 * Importer for the obj format that is as follows<br>
 * # some text<br>
 *    Line is a comment until the end of the line<br>
 * v float float float<br>
 *    A single vertex's geometric position in space. The first vertex listed in <br>
 *    the file has index 1, and subsequent vertices are numbered sequentially.<br>
 * vn float float float<br>
 *    A normal. The first normal in the file is index 1, and subsequent normals <br>
 *    are numbered sequentially.<br>
 * vt float float<br>
 *    A texture coordinate. The first texture coordinate in the file is index 1, <br>
 *    and subsequent textures are numbered sequentially.<br>
 * f int int int ...<br>
 *    or<br>
 * f int/int int/int int/int . . .<br>
 *    or<br>
 * f int/int/int int/int/int int/int/int ...<br>
 *    A polygonal face. The numbers are indexes into the arrays of vertex positions, <br>
 *    texture coordinates, and normals respectively. A number may be omitted if, for <br>
 *    example, texture coordinates are not being defined in the model.<br>
 *    There is no maximum number of vertices that a single polygon may contain. <br>
 *    The .obj file specification says that each face must be flat and convex. <br>
 *    In JavaView polygonal face may be triangulated. <br>
 */
public class ObjFileReader {
    private String path;
    public ObjFileReader() {

    }


    public BasicComponent readObject(File file) throws IOException {
        path = file.getParent();
        return readObject(new FileReader(file));
    }
    public BasicComponent readObject(Reader reader) throws IOException {
        BufferedReader input = new BufferedReader(reader);
        MaterialLibrary library = null;
        Material material = null;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Vertex> normals = new ArrayList<Vertex>();
        List<FaceGroup> groups = new LinkedList<FaceGroup>();
        FaceGroup group = new FaceGroup("wavefront");
        groups.add(group);
        while (input.ready()) {
            String line = input.readLine();
            String[] parts = line.split("\\s");
            if (parts.length > 0) {
                String op = parts[0];
                if (op.equals("v")) {
                    double x = Double.valueOf(parts[1]);
                    double y = Double.valueOf(parts[2]);
                    double z = Double.valueOf(parts[3]);
                    vertices.add(new Vertex(z, x, y));
                } else if (op.equals("vn")) {
                    double x = Double.valueOf(parts[1]);
                    double y = Double.valueOf(parts[2]);
                    double z = Double.valueOf(parts[3]);
                    normals.add(new Vertex(z, x, y));
                } else if (op.equals("f")) {
                    List<Vertex> vlist = new ArrayList<Vertex>();
                    List<Vertex> nlist = new ArrayList<Vertex>();
                    for (int i = 1; i < parts.length; i++) {
                        String[] indices = parts[i].split("/");
                        int vinx = Integer.valueOf(indices[0]);
                        int ninx = Integer.valueOf(indices[2]);
                        Vertex vertex = vertices.get(vinx - 1);
                        Vertex normal = normals.get(ninx - 1);
                        vlist.add(vertex);
                        nlist.add(normal);
                    }
                    Face face = new Face(vlist, nlist);
                    group.add(face);
                } else if (op.equals("g")) { 
                    group = new FaceGroup(parts[1]);
                    group.setMaterial(material);
                    groups.add(group);
                } else if (op.equals("mtllib")) { 
                    String name = parts[1];
                    File mtlfile = new File(path, name);
                    library = readMaterialLibrary(mtlfile);
                    material = library.findByName("default");
                } else if (op.equals("usemtl")) { 
                    String name = parts[1];
                    material = library.findByName(name);
                } else {
                }

            }
        }
        return new BasicComponent("wavefront", groups, vertices);
    }
    public MaterialLibrary readMaterialLibrary(File file) throws IOException {
        MaterialLibrary library = new MaterialLibrary(file.getName());
        BufferedReader input = new BufferedReader(new FileReader(file));
        Material current = null;
        while (input.ready()) {
            String line = input.readLine();
            String[] parts = line.split("\\s");
            if (parts.length > 0) {
                String op = parts[0];
                if (op.equals("newmtl")) {
                    String name = parts[1];
                    current = new Material(name);
                    library.add(current);
                } else if (op.equals("Ns")) {
                    float shininess = Float.valueOf(parts[1]);
                    current.setShininess(shininess);
                } else if (op.equals("d")) {
                    float alpha = Float.valueOf(parts[1]);
                    current.setAlpha(alpha);
                } else if (op.equals("Kd")) {
                    float red = Float.valueOf(parts[1]);
                    float green = Float.valueOf(parts[2]);
                    float blue = Float.valueOf(parts[3]);
                    current.setDiffuse(new Color(red, green, blue));
                }
            }
        }
        input.close();
        return library;
    }
}

package com.bprocessor;

import java.util.Collection;

import com.bprocessor.util.Matrix;

/**
 * Utility
 */
public class GeometryMath {

	public static void rotate(double angle, double x, double y, double z, 
			Vertex vector, Vertex center) {

		double[] v = new double[4];

		v[0] = vector.getX() - center.getX();
		v[1] = vector.getY() - center.getY();
		v[2] = vector.getZ() - center.getZ();
		v[3] = 1;
		Matrix rotation = Matrix.rotation(angle, x, y, z);
		v = rotation.multiply(v);
		vector.setX(v[0] + center.getX());
		vector.setY(v[1] + center.getY());
		vector.setZ(v[2] + center.getZ());
	}

	/**
	 * Represents a bounding box
	 *
	 */
	public static class BoundingBox {
		private Vertex xmin;
		private Vertex xmax;
		private Vertex ymin;
		private Vertex ymax;
		private Vertex zmin;
		private Vertex zmax;

		/**
		 * Constructs bounding box for
		 * @param vertices Collection
		 */
		public BoundingBox(Collection<Vertex> vertices) {
			compute(vertices);
		}

		/**
		 * Compute boudning box
		 * @param vertices collection of vertices
		 */
		public void compute(Collection<Vertex> vertices) {
			xmin = new Vertex(Double.POSITIVE_INFINITY, 
					Double.POSITIVE_INFINITY, 
					Double.POSITIVE_INFINITY);
			xmax = new Vertex(Double.NEGATIVE_INFINITY, 
					Double.NEGATIVE_INFINITY, 
					Double.NEGATIVE_INFINITY);
			ymin = xmin;
			ymax = xmax;
			zmin = xmin;
			zmax = xmax;

			for (Vertex current : vertices) {
				if (current.x < xmin.x) {
					xmin = current;
				}
				if (current.x > xmax.x) {
					xmax = current;
				}
				if (current.y < ymin.y) {
					ymin = current;
				}
				if (current.y > ymax.y) {
					ymax = current;
				}
				if (current.z < zmin.z) {
					zmin = current;
				}
				if (current.z > zmax.z) {
					zmax = current;
				}
			}
		}

		/**
		 * 
		 * @return vertex
		 */
		public Vertex xmin() {
			return xmin;
		}

		/**
		 * 
		 * @return vertex
		 */
		public Vertex xmax() {
			return xmax;
		}

		/**
		 * 
		 * @return vertex
		 */
		public Vertex ymin() {
			return ymin;
		}

		/**
		 * 
		 * @return vertex
		 */
		public Vertex ymax() {
			return ymax;
		}

		/**
		 * 
		 * @return vertex
		 */
		public Vertex zmin() {
			return zmin;
		}

		/**
		 * 
		 * @return vertex
		 */
		public Vertex zmax() {
			return zmax;
		}
	}
	
	
	public static class BoundingSphere {
		private Vertex center;
		private double radius;

		
		public BoundingSphere(Collection<Vertex> vertices) {
			compute(vertices);
		}

		public BoundingSphere(Vertex center, double radius) {
			this.center = center;
			this.radius = radius;
		}

		public Vertex center() {
			return center;
		}

		public double radius() {
			return radius;
		}

		/**
		 * Compute a bounding sphere for specified vertices
		 * using Jack Ritters algorithm from Graphics Gems.
		 */
		public void compute(Collection<Vertex> vertices) {
			BoundingBox box = new BoundingBox(vertices);

			Vertex xmin = box.xmin();
			Vertex xmax = box.xmax();
			Vertex ymin = box.ymin();
			Vertex ymax = box.ymax();
			Vertex zmin = box.zmin();
			Vertex zmax = box.zmax();

			Vertex xspan = xmax.minus(xmin);
			Vertex yspan = ymax.minus(ymin);
			Vertex zspan = zmax.minus(zmin);


			double dia = xspan.length();
			Vertex v1 = xmin;
			Vertex v2 = xmax;

			if (yspan.length() > dia) {
				dia = yspan.length();
				v1 = ymin;
				v2 = ymax;
			}
			if (zspan.length() > dia) {
				dia = zspan.length();
				v1 = zmin;
				v2 = zmax;
			}

			center = v1.add(v2).scale(0.5);
			center.x = (xmin.x + xmax.x) / 2;
			center.y = (ymin.y + ymax.y) / 2;
			center.z = (zmin.z + zmax.z) / 2;
			radius = dia / 2;

			for (Vertex current : vertices) {
				Vertex d = current.minus(center);
				double length = d.length();
				if (length > radius) {
					radius = (radius + length) / 2;
					double diff = length - radius;
					center = center.add(d.scale(diff / length));
				}
			}
			if (radius < 0.00001) {
				radius = 0.1;
			}
		}
	}
}

package com.bprocessor.util;

import com.bprocessor.Vertex;

/**
 * Utility
 * 
 * The Matrix is a 4x4 matrix, represented as
 * a an array of 16 double values in column-major
 * order. This matches the representation of
 * matrices in OpenGL. 
 */
public class Matrix {
  /** The storage - 16 double precision floating point values in
   * column-major order.
   */
  private double[] storage;
  
  /** The id matrix */
  public static final double[] ID = 
  {1, 0, 0, 0, 
   0, 1, 0, 0, 
   0, 0, 1, 0,
   0, 0, 0, 1};
  
  /**
   * Construct a rotation matrix that rotates around the (x, y, z)
   * vector, which is expected to have length = 1.
   * the constructed matrix has the following layout:
   *   x^2(1-c)+c     xy(1-c)-zs     xz(1-c)+ys     0
   *   yx(1-c)+zs     y^2(1-c)+c     yz(1-c)-xs     0
   *   xz(1-c)-ys     yz(1-c)+xs     z^2(1-c)+c     0
   *        0              0               0        1
   * where c = cos(angle) and s = sin(angle).
   * @param angle The angle to rotate
   * @param x The x
   * @param y The y
   * @param z The z
   * @return The Matrix
   */
  public static Matrix rotation(double angle, double x, double y, double z) {
    double s = Math.sin(angle);
    double c = Math.cos(angle);
    double[] v = new double[16];
    v[0] = x * x * (1 - c) + c;
    v[1] = y * x * (1 - c) + z * s;
    v[2] = x * z * (1 - c) - y * s;
    v[3] = 0;
    v[4] = x * y * (1 - c) - z * s;
    v[5] = y * y * (1 - c) + c;
    v[6] = y * z * (1 - c) + x * s;
    v[7] = 0;
    v[8] = x * z * (1 - c) + y * s;
    v[9] = y * z * (1 - c) - x * s;
    v[10] = z * z * (1 - c) + c;
    v[11] = 0;
    v[12] = 0;
    v[13] = 0;
    v[14] = 0;
    v[15] = 1;
    return new Matrix(v);
  }
  
  /**
   * Add a translation to the matrix
   * @param x dx
   * @param y dy
   * @param z dz
   */
  public void translate(double x, double y, double z) {
    set(3, 0, get(3, 0) + x);
    set(3, 1, get(3, 1) + y);
    set(3, 2, get(3, 2) + z);
  }
  
  /**
   * Construct a matrix with specified values
   * @param values The values in column-major order
   */
  public Matrix(double[] values) {
    super();
    this.storage = values;
  }
  
  /**
   * Construct a default matrix - the ID
   */
  public Matrix() {
    this((double[]) ID.clone());
  }

  /**
   * Multiply a Matrix to a vector: Mx
   * @param vector The vector
   * @return A vector multiplied by this Matrix
   */
  public double[] multiply(double[] vector) {
    double[] result = new double[4];
    result[0] = storage[0] * vector[0]
      + storage[4] * vector[1]
      + storage[8] * vector[2]
      + storage[12] * vector[3];
    result[1] = storage[1] * vector[0]
        + storage[5] * vector[1]
        + storage[9] * vector[2]
        + storage[13] * vector[3];
    result[2] = storage[2] * vector[0]
        + storage[6] * vector[1]
        + storage[10] * vector[2]
        + storage[14] * vector[3];
    result[3] = storage[3] * vector[0]
        + storage[7] * vector[1]
        + storage[11] * vector[2]
        + storage[15] * vector[3];
    return result;
  }
  
  /**
   * Multiply a vertex with a matrix either as a vertex or vector
   * @param v the vertex to multiply with this matrix
   * @param asVertex Add the translation as well
   * @return the result
   */
  public Vertex multiply(Vertex v, boolean asVertex) {
    double fourth = asVertex ? 1 : 0;
    double res[] = multiply(new double[]{v.getX(), v.getY(), v.getZ(), fourth});
    return new Vertex(res[0], res[1], res[2]);
  }
  
  /**
   * Invert a 4x4 matrix by for each indice finding the determinant of the other rows and collums
   * @see http://mathworld.wolfram.com/MatrixInverse.html
   * @return the inverse of the matrix
   */
  public Matrix invert() {
    Matrix inverted = new Matrix();
    inverted.set(0, 0, get(1, 2) * get(2, 3) * get(3, 1) - get(1, 3) * get(2, 2) * get(3, 1) + 
          get(1, 3) * get(2, 1) * get(3, 2) - get(1, 1) * get(2, 3) * get(3, 2) - 
          get(1, 2) * get(2, 1) * get(3, 3) + get(1, 1) * get(2, 2) * get(3, 3));
    inverted.set(0, 1, get(0, 3) * get(2, 2) * get(3, 1) - get(0, 2) * get(2, 3) * get(3, 1) - 
          get(0, 3) * get(2, 1) * get(3, 2) + get(0, 1) * get(2, 3) * get(3, 2) + 
          get(0, 2) * get(2, 1) * get(3, 3) - get(0, 1) * get(2, 2) * get(3, 3));
    inverted.set(0, 2, get(0, 2) * get(1, 3) * get(3, 1) - get(0, 3) * get(1, 2) * get(3, 1) + 
          get(0, 3) * get(1, 1) * get(3, 2) - get(0, 1) * get(1, 3) * get(3, 2) - 
          get(0, 2) * get(1, 1) * get(3, 3) + get(0, 1) * get(1, 2) * get(3, 3));
    inverted.set(0, 3, get(0, 3) * get(1, 2) * get(2, 1) - get(0, 2) * get(1, 3) * get(2, 1) - 
          get(0, 3) * get(1, 1) * get(2, 2) + get(0, 1) * get(1, 3) * get(2, 2) + 
          get(0, 2) * get(1, 1) * get(2, 3) - get(0, 1) * get(1, 2) * get(2, 3));
    inverted.set(1, 0, get(1, 3) * get(2, 2) * get(3, 0) - get(1, 2) * get(2, 3) * get(3, 0) - 
          get(1, 3) * get(2, 0) * get(3, 2) + get(1, 0) * get(2, 3) * get(3, 2) + 
          get(1, 2) * get(2, 0) * get(3, 3) - get(1, 0) * get(2, 2) * get(3, 3));
    inverted.set(1, 1, get(0, 2) * get(2, 3) * get(3, 0) - get(0, 3) * get(2, 2) * get(3, 0) + 
          get(0, 3) * get(2, 0) * get(3, 2) - get(0, 0) * get(2, 3) * get(3, 2) - 
          get(0, 2) * get(2, 0) * get(3, 3) + get(0, 0) * get(2, 2) * get(3, 3));
    inverted.set(1, 2, get(0, 3) * get(1, 2) * get(3, 0) - get(0, 2) * get(1, 3) * get(3, 0) - 
          get(0, 3) * get(1, 0) * get(3, 2) + get(0, 0) * get(1, 3) * get(3, 2) + 
          get(0, 2) * get(1, 0) * get(3, 3) - get(0, 0) * get(1, 2) * get(3, 3));
    inverted.set(1, 3, get(0, 2) * get(1, 3) * get(2, 0) - get(0, 3) * get(1, 2) * get(2, 0) + 
          get(0, 3) * get(1, 0) * get(2, 2) - get(0, 0) * get(1, 3) * get(2, 2) - 
          get(0, 2) * get(1, 0) * get(2, 3) + get(0, 0) * get(1, 2) * get(2, 3));
    inverted.set(2, 0, get(1, 1) * get(2, 3) * get(3, 0) - get(1, 3) * get(2, 1) * get(3, 0) + 
          get(1, 3) * get(2, 0) * get(3, 1) - get(1, 0) * get(2, 3) * get(3, 1) - 
          get(1, 1) * get(2, 0) * get(3, 3) + get(1, 0) * get(2, 1) * get(3, 3));
    inverted.set(2, 1, get(0, 3) * get(2, 1) * get(3, 0) - get(0, 1) * get(2, 3) * get(3, 0) - 
          get(0, 3) * get(2, 0) * get(3, 1) + get(0, 0) * get(2, 3) * get(3, 1) + 
          get(0, 1) * get(2, 0) * get(3, 3) - get(0, 0) * get(2, 1) * get(3, 3));
    inverted.set(2, 2, get(0, 1) * get(1, 3) * get(3, 0) - get(0, 3) * get(1, 1) * get(3, 0) + 
          get(0, 3) * get(1, 0) * get(3, 1) - get(0, 0) * get(1, 3) * get(3, 1) - 
          get(0, 1) * get(1, 0) * get(3, 3) + get(0, 0) * get(1, 1) * get(3, 3));
    inverted.set(2, 3, get(0, 3) * get(1, 1) * get(2, 0) - get(0, 1) * get(1, 3) * get(2, 0) - 
          get(0, 3) * get(1, 0) * get(2, 1) + get(0, 0) * get(1, 3) * get(2, 1) + 
          get(0, 1) * get(1, 0) * get(2, 3) - get(0, 0) * get(1, 1) * get(2, 3));
    inverted.set(3, 0, get(1, 2) * get(2, 1) * get(3, 0) - get(1, 1) * get(2, 2) * get(3, 0) - 
          get(1, 2) * get(2, 0) * get(3, 1) + get(1, 0) * get(2, 2) * get(3, 1) + 
          get(1, 1) * get(2, 0) * get(3, 2) - get(1, 0) * get(2, 1) * get(3, 2));
    inverted.set(3, 1, get(0, 1) * get(2, 2) * get(3, 0) - get(0, 2) * get(2, 1) * get(3, 0) + 
          get(0, 2) * get(2, 0) * get(3, 1) - get(0, 0) * get(2, 2) * get(3, 1) - 
          get(0, 1) * get(2, 0) * get(3, 2) + get(0, 0) * get(2, 1) * get(3, 2));
    inverted.set(3, 2, get(0, 2) * get(1, 1) * get(3, 0) - get(0, 1) * get(1, 2) * get(3, 0) - 
          get(0, 2) * get(1, 0) * get(3, 1) + get(0, 0) * get(1, 2) * get(3, 1) + 
          get(0, 1) * get(1, 0) * get(3, 2) - get(0, 0) * get(1, 1) * get(3, 2));
    inverted.set(3, 3, get(0, 1) * get(1, 2) * get(2, 0) - get(0, 2) * get(1, 1) * get(2, 0) + 
          get(0, 2) * get(1, 0) * get(2, 1) - get(0, 0) * get(1, 2) * get(2, 1) - 
          get(0, 1) * get(1, 0) * get(2, 2) + get(0, 0) * get(1, 1) * get(2, 2));
    inverted.scale(1 / this.determinant());
    return inverted;
  }

  /**
   * Calculate the determinant
   * @return the determinant
   */
  public double determinant() {
    double value;
    value =
       get(0, 3) *  get(1, 2) *  get(2, 1)  *  get(3, 0) - 
       get(0, 2) *  get(1, 3) *  get(2, 1)  *  get(3, 0) - 
       get(0, 3) *  get(1, 1) *  get(2, 2)  *  get(3, 0)  +  
       get(0, 1) *  get(1, 3) *  get(2, 2) * get(3, 0) +
       get(0, 2) *  get(1, 1) *  get(2, 3)  *  get(3, 0) - 
       get(0, 1) *  get(1, 2) *  get(2, 3)  *  get(3, 0) - 
       get(0, 3) *  get(1, 2) *  get(2, 0)  *  get(3, 1)  +  
       get(0, 2) *  get(1, 3) *  get(2, 0) * get(3, 1) +
       get(0, 3) *  get(1, 0) *  get(2, 2)  *  get(3, 1) - 
       get(0, 0) *  get(1, 3) *  get(2, 2)  *  get(3, 1) - 
       get(0, 2) *  get(1, 0) *  get(2, 3)  *  get(3, 1)  +  
       get(0, 0) *  get(1, 2) *  get(2, 3) * get(3, 1) +
       get(0, 3) *  get(1, 1) *  get(2, 0)  *  get(3, 2) -
       get(0, 1) *  get(1, 3) *  get(2, 0)  *  get(3, 2) - 
       get(0, 3) *  get(1, 0) *  get(2, 1)  *  get(3, 2)  +  
       get(0, 0) *  get(1, 3) *  get(2, 1) * get(3, 2) +
       get(0, 1) *  get(1, 0) *  get(2, 3)  *  get(3, 2) - 
       get(0, 0) *  get(1, 1) *  get(2, 3)  *  get(3, 2) - 
       get(0, 2) *  get(1, 1) *  get(2, 0)  *  get(3, 3)  +  
       get(0, 1) *  get(1, 2) *  get(2, 0) * get(3, 3) +
       get(0, 2) *  get(1, 0) *  get(2, 1)  *  get(3, 3) - 
       get(0, 0) *  get(1, 2) *  get(2, 1)  *  get(3, 3) - 
       get(0, 1) *  get(1, 0) *  get(2, 2)  *  get(3, 3)  +  
       get(0, 0) *  get(1, 1) *  get(2, 2) * get(3, 3);
    return value;
  } 

  /**
   * Scale all indices in the matrix by the given value
   * @param value the value to scale with
   */
  public void scale(double value) {
    for (int i = 0; i < this.storage.length; i++) {
      if (this.storage[i] != 0) {
        this.storage[i] *= value;
      }
    }
  }
  
  private final double get(int i, int j) {
    return this.storage[4 * i + j];
  }
  
  private final void set(int i, int j, double val) {
    this.storage[4 * i + j] = val;
  }
  
  /**
   * Equal if all values in the matrix are the same
   * @return true if this and obj are the equal
   * @param obj The object to compare with
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Matrix) {
      boolean equal = true;
      Matrix other = (Matrix)obj;
      for (int i = 0; i < this.storage.length; i++) {
        equal &= this.storage[i] == other.storage[i];
      }
      return equal;
    }
    
    return super.equals(obj);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}

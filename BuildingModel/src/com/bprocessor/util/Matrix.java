package com.bprocessor.util;

import com.bprocessor.Vertex;


public class Matrix {
	private double[] storage;

	public static final double[] ID = 
		{1, 0, 0, 0, 
		0, 1, 0, 0, 
		0, 0, 1, 0,
		0, 0, 0, 1};

	/*
	 * Rotation Matrix:
	 *   x^2(1-c)+c     xy(1-c)-zs     xz(1-c)+ys     0
	 *   yx(1-c)+zs     y^2(1-c)+c     yz(1-c)-xs     0
	 *   xz(1-c)-ys     yz(1-c)+xs     z^2(1-c)+c     0
	 *        0              0               0        1
	 * where c = cos(angle) and s = sin(angle).
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
	
	public Matrix(double[] values) {
		this.storage = values;
	}

	public Matrix() {
		this((double[]) ID.clone());
	}
	
	public void translateIt(double x, double y, double z) {
		set(3, 0, get(3, 0) + x);
		set(3, 1, get(3, 1) + y);
		set(3, 2, get(3, 2) + z);
	}
	public void scaleIt(double value) {
		for (int i = 0; i < storage.length; i++) {
			if (storage[i] != 0) {
				storage[i] *= value;
			}
		}
	}

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
	public Vertex multiply(Vertex vertex) {
		Vertex result = new Vertex();
		result.set(multiply(vertex.values()));
		return result;
	}

	/*
	 * @see http://mathworld.wolfram.com/MatrixInverse.html
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
		inverted.scaleIt(1 / this.determinant());
		return inverted;
	}

	public double determinant() {
		double value;
		value =
				get(0, 3) *  get(1, 2) *  get(2, 1)  *  get(3, 0) - 
				get(0, 2) *  get(1, 3) *  get(2, 1)  *  get(3, 0) - 
				get(0, 3) *  get(1, 1) *  get(2, 2)  *  get(3, 0)  +  
				get(0, 1) *  get(1, 3) *  get(2, 2)  *  get(3, 0) +
				get(0, 2) *  get(1, 1) *  get(2, 3)  *  get(3, 0) - 
				get(0, 1) *  get(1, 2) *  get(2, 3)  *  get(3, 0) - 
				get(0, 3) *  get(1, 2) *  get(2, 0)  *  get(3, 1)  +  
				get(0, 2) *  get(1, 3) *  get(2, 0)  *  get(3, 1) +
				get(0, 3) *  get(1, 0) *  get(2, 2)  *  get(3, 1) - 
				get(0, 0) *  get(1, 3) *  get(2, 2)  *  get(3, 1) - 
				get(0, 2) *  get(1, 0) *  get(2, 3)  *  get(3, 1)  +  
				get(0, 0) *  get(1, 2) *  get(2, 3)  *  get(3, 1) +
				get(0, 3) *  get(1, 1) *  get(2, 0)  *  get(3, 2) -
				get(0, 1) *  get(1, 3) *  get(2, 0)  *  get(3, 2) - 
				get(0, 3) *  get(1, 0) *  get(2, 1)  *  get(3, 2)  +  
				get(0, 0) *  get(1, 3) *  get(2, 1)  *  get(3, 2) +
				get(0, 1) *  get(1, 0) *  get(2, 3)  *  get(3, 2) - 
				get(0, 0) *  get(1, 1) *  get(2, 3)  *  get(3, 2) - 
				get(0, 2) *  get(1, 1) *  get(2, 0)  *  get(3, 3)  +  
				get(0, 1) *  get(1, 2) *  get(2, 0)  *  get(3, 3) +
				get(0, 2) *  get(1, 0) *  get(2, 1)  *  get(3, 3) - 
				get(0, 0) *  get(1, 2) *  get(2, 1)  *  get(3, 3) - 
				get(0, 1) *  get(1, 0) *  get(2, 2)  *  get(3, 3)  +  
				get(0, 0) *  get(1, 1) *  get(2, 2)  *  get(3, 3);
		return value;
	}

	private final double get(int i, int j) {
		return this.storage[4 * i + j];
	}

	private final void set(int i, int j, double val) {
		this.storage[4 * i + j] = val;
	}
}

package com.jaeger.core;

import java.io.Serializable;

public class Vector3 implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public double x, y, z;
    
    public Vector3() {
        this(0, 0, 0);
    }
    
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }
    
    public Vector3 multiply(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }
    
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public Vector3 normalize() {
        double len = length();
        if (len == 0) return new Vector3();
        return multiply(1.0 / len);
    }
    
    public double distance(Vector3 other) {
        return subtract(other).length();
    }
    
    public Vector3 cross(Vector3 other) {
        return new Vector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    @Override
    public String toString() {
        return String.format("(%.3f, %.3f, %.3f)", x, y, z);
    }
}

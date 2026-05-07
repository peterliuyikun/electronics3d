package com.jaeger.core;

import java.io.Serializable;

public class Transform implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;
    
    public Transform() {
        this.position = new Vector3();
        this.rotation = new Vector3();
        this.scale = new Vector3(1, 1, 1);
    }
    
    public Vector3 getPosition() { return position; }
    public void setPosition(Vector3 position) { this.position = position; }
    
    public Vector3 getRotation() { return rotation; }
    public void setRotation(Vector3 rotation) { this.rotation = rotation; }
    
    public Vector3 getScale() { return scale; }
    public void setScale(Vector3 scale) { this.scale = scale; }
    
    public void translate(Vector3 delta) {
        position = position.add(delta);
    }
    
    public void rotate(Vector3 delta) {
        rotation = rotation.add(delta);
    }
    
    public void setLocalPosition(double x, double y, double z) {
        position = new Vector3(x, y, z);
    }
    
    public void setLocalRotation(double x, double y, double z) {
        rotation = new Vector3(x, y, z);
    }
    
    public void setLocalScale(double x, double y, double z) {
        scale = new Vector3(x, y, z);
    }
    
    public Transform copy() {
        Transform t = new Transform();
        t.position = new Vector3(position.x, position.y, position.z);
        t.rotation = new Vector3(rotation.x, rotation.y, rotation.z);
        t.scale = new Vector3(scale.x, scale.y, scale.z);
        return t;
    }
}

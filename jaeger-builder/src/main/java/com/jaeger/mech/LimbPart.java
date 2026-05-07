package com.jaeger.mech;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.core.Transform;
import com.jaeger.core.Vector3;

public class LimbPart extends Part {
    private static final long serialVersionUID = 1L;
    
    private double length; // meters
    private double width; // meters
    private double rangeOfMotion; // degrees
       private double maxTorque; // Nm
    
    public LimbPart(String name, PartType type, double mass, double length, double width) {
        super(name, type);
        this.length = length;
        this.width = width;
        this.mass = mass;
        this.maxStructuralStress = 350;
        
        setupConnectionPoints(type);
    }
    
    private void setupConnectionPoints(PartType type) {
        switch (type) {
            case UPPER_ARM:
                addConnectionPoint(new ConnectionPoint("shoulder", 
                    new Vector3(0, length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("elbow", 
                    new Vector3(0, -length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MEDIUM));
                this.rangeOfMotion = 180; // Full rotation at shoulder
                break;
                
            case FOREARM:
                addConnectionPoint(new ConnectionPoint("elbow", 
                    new Vector3(0, length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MEDIUM));
                addConnectionPoint(new ConnectionPoint("wrist", 
                    new Vector3(0, -length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MEDIUM));
                this.rangeOfMotion = 135; // Elbow bend
                break;
                
            case HAND:
                addConnectionPoint(new ConnectionPoint("wrist", 
                    new Vector3(0, 0.3, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MEDIUM));
                addConnectionPoint(new ConnectionPoint("weapon_mount", 
                    new Vector3(0, -0.2, 0.1), ConnectionPoint.ConnectionType.WEAPON_MOUNT, ConnectionPoint.ConnectionSize.MEDIUM));
                this.rangeOfMotion = 90; // Wrist rotation
                break;
                
            case THIGH:
                addConnectionPoint(new ConnectionPoint("hip", 
                    new Vector3(0, length/2, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("knee", 
                    new Vector3(0, -length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                this.rangeOfMotion = 120;
                break;
                
            case SHIN:
                addConnectionPoint(new ConnectionPoint("knee", 
                    new Vector3(0, length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("ankle", 
                    new Vector3(0, -length/2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MEDIUM));
                this.rangeOfMotion = 135;
                break;
                
            case FOOT:
                addConnectionPoint(new ConnectionPoint("ankle", 
                    new Vector3(0, 0.2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MEDIUM));
                addConnectionPoint(new ConnectionPoint("ground", 
                    new Vector3(0, -0.3, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.LARGE));
                this.rangeOfMotion = 45;
                break;
        }
    }
    
    @Override
    public void update(double deltaTime) {
        calculateStress();
    }
    
    @Override
    public void render() {
        // Render limb
    }
    
    @Override
    public Part copy() {
        LimbPart copy = new LimbPart(name, type, mass, length, width);
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        return width * width * Math.PI / 4;
    }
    
    // Getters
    public double getLength() { return length; }
    public double getWidth() { return width; }
    public double getRangeOfMotion() { return rangeOfMotion; }
    public double getMaxTorque() { return maxTorque; }
}

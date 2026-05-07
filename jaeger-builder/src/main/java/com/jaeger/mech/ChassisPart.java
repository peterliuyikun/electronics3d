package com.jaeger.mech;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.core.Transform;
import com.jaeger.core.Vector3;

public class ChassisPart extends Part {
    private static final long serialVersionUID = 1L;
    
    private double armorThickness; // mm
    private String material;
    private double internalVolume; // m³
    
    public ChassisPart(String name, PartType type, double mass, double armorThickness) {
        super(name, type);
        this.armorThickness = armorThickness;
        this.material = "Titanium Alloy";
        this.mass = mass;
        this.maxStructuralStress = 400;
        
        // Add connection points based on type
        setupConnectionPoints(type);
    }
    
    private void setupConnectionPoints(PartType type) {
        switch (type) {
            case TORSO:
                // Main body - connects to everything
                addConnectionPoint(new ConnectionPoint("head_mount", 
                    new Vector3(0, 2, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("left_shoulder", 
                    new Vector3(-1.5, 1, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("right_shoulder", 
                    new Vector3(1.5, 1, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("left_hip", 
                    new Vector3(-1, -1, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("right_hip", 
                    new Vector3(1, -1, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("reactor_bay", 
                    new Vector3(0, 0, -1), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("cockpit_mount", 
                    new Vector3(0, 0.5, 1), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                break;
                
            case HIP:
                addConnectionPoint(new ConnectionPoint("torso_attach", 
                    new Vector3(0, 1, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("left_thigh", 
                    new Vector3(-1, -0.5, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("right_thigh", 
                    new Vector3(1, -0.5, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.LARGE));
                break;
                
            case SHOULDER_MOUNT:
                addConnectionPoint(new ConnectionPoint("torso_attach", 
                    new Vector3(0, 0, 0), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.MASSIVE));
                addConnectionPoint(new ConnectionPoint("arm_attach", 
                    new Vector3(0, -1, 0), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("weapon_mount", 
                    new Vector3(0, 0, 1), ConnectionPoint.ConnectionType.WEAPON_MOUNT, ConnectionPoint.ConnectionSize.MEDIUM));
                break;
                
            case CONN_POD:
                addConnectionPoint(new ConnectionPoint("torso_attach", 
                    new Vector3(0, -0.5, -0.5), ConnectionPoint.ConnectionType.COMBINED, ConnectionPoint.ConnectionSize.LARGE));
                addConnectionPoint(new ConnectionPoint("hatch", 
                    new Vector3(0, 0.5, 0.5), ConnectionPoint.ConnectionType.STRUCTURAL, ConnectionPoint.ConnectionSize.SMALL));
                break;
        }
    }
    
    @Override
    public void update(double deltaTime) {
        calculateStress();
    }
    
    @Override
    public void render() {
        // Render chassis
    }
    
    @Override
    public Part copy() {
        ChassisPart copy = new ChassisPart(name, type, mass, armorThickness);
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        return mass / 1000; // Simplified
    }
    
    // Getters
    public double getArmorThickness() { return armorThickness; }
    public String getMaterial() { return material; }
}

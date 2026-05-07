package com.jaeger.core;

import com.jaeger.electrical.PowerConsumer;
import com.jaeger.hydraulics.HydraulicConsumer;
import com.jaeger.mech.ConnectionPoint;

import java.io.Serializable;
import java.util.*;

public abstract class Part implements Serializable, PowerConsumer, HydraulicConsumer {
    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected String name;
    protected String description;
    protected PartType type;
    protected Transform transform;
    protected Transform localTransform;
    
    protected double mass; // kg
    protected double maxStructuralStress; // MPa
    protected double currentStress; // MPa
    protected boolean isDamaged;
    
    protected List<ConnectionPoint> connectionPoints;
    protected List<Part> childParts;
    protected Part parentPart;
    
    // Power requirements
    protected double powerRequirement; // watts
    protected double currentPowerDraw;
    protected boolean isPowered;
    
    // Hydraulic requirements
    protected double hydraulicPressureRequirement; // bar
    protected double hydraulicFlowRequirement; // L/min
    protected double currentHydraulicPressure;
    protected double currentHydraulicFlow;
    protected boolean isHydraulicallyPowered;
    
    // Visual properties
    protected String colorHex;
    protected boolean visible;
    protected boolean selected;
    
    public Part(String name, PartType type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.transform = new Transform();
        this.localTransform = new Transform();
        this.connectionPoints = new ArrayList<>();
        this.childParts = new ArrayList<>();
        this.colorHex = "#808080";
        this.visible = true;
    }
    
    public abstract void update(double deltaTime);
    public abstract void render();
    public abstract Part copy();
    
    public void addChildPart(Part child, ConnectionPoint attachPoint) {
        child.parentPart = this;
        childParts.add(child);
        if (attachPoint != null) {
            attachPoint.setConnectedPart(child);
        }
    }
    
    public void removeChildPart(Part child) {
        childParts.remove(child);
        child.parentPart = null;
    }
    
    public void addConnectionPoint(ConnectionPoint point) {
        connectionPoints.add(point);
    }
    
    public Vector3 getWorldPosition() {
        if (parentPart == null) {
            return transform.getPosition();
        }
        return parentPart.getWorldPosition().add(transform.getPosition());
    }
    
    public double getTotalMass() {
        double total = mass;
        for (Part child : childParts) {
            total += child.getTotalMass();
        }
        return total;
    }
    
    public void calculateStress() {
        double load = getTotalMass() * 9.81; // Newtons
        double crossSection = getCrossSectionalArea();
        currentStress = crossSection > 0 ? (load / crossSection) / 1_000_000 : 0; // MPa
        
        if (currentStress > maxStructuralStress) {
            isDamaged = true;
        }
    }
    
    protected abstract double getCrossSectionalArea();
    
    // PowerConsumer interface
    @Override
    public double getPowerRequirement() { return powerRequirement; }
    
    @Override
    public void setPowerDraw(double watts) { 
        currentPowerDraw = watts;
        isPowered = watts >= powerRequirement * 0.9;
    }
    
    @Override
    public boolean isPowered() { return isPowered; }
    
    // HydraulicConsumer interface
    @Override
    public double getHydraulicPressureRequirement() { return hydraulicPressureRequirement; }
    
    @Override
    public double getHydraulicFlowRequirement() { return hydraulicFlowRequirement; }
    
    @Override
    public void setHydraulicSupply(double pressure, double flow) {
        currentHydraulicPressure = pressure;
        currentHydraulicFlow = flow;
        isHydraulicallyPowered = pressure >= hydraulicPressureRequirement * 0.8 &&
                                  flow >= hydraulicFlowRequirement * 0.8;
    }
    
    @Override
    public boolean isHydraulicallyPowered() { return isHydraulicallyPowered; }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PartType getType() { return type; }
    public Transform getTransform() { return transform; }
    public void setTransform(Transform transform) { this.transform = transform; }
    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = mass; }
    public boolean isDamaged() { return isDamaged; }
    public List<ConnectionPoint> getConnectionPoints() { return connectionPoints; }
    public List<Part> getChildParts() { return childParts; }
    public Part getParentPart() { return parentPart; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public double getCurrentStress() { return currentStress; }
    public double getMaxStructuralStress() { return maxStructuralStress; }
}

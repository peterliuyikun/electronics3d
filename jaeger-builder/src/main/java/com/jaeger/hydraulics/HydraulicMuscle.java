package com.jaeger.hydraulics;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.core.Transform;
import com.jaeger.core.Vector3;

public class HydraulicMuscle extends Part {
    private static final long serialVersionUID = 1L;
    
    // Muscle properties
    private double restingLength; // meters
    private double currentLength; // meters
    private double minLength; // fully contracted
    private double maxLength; // fully extended
    private double diameter; // meters
    
    // Hydraulic properties
    private double workingPressure; // bar
    private double currentPressure; // bar
    private double maxPressure; // bar
    private double flowRate; // L/min
    
    // Force calculations
    private double contractileForce; // Newtons
    private double maxForce; // Newtons
    private double contractionRatio; // 0-1 (0 = fully extended, 1 = fully contracted)
    
    // State
    private MuscleState state;
    private double fatigue; // 0-100%
    private double temperature; // Celsius
    private double fluidVolume; // liters
    
    // Target
    private double targetContraction; // 0-1
    private double contractionSpeed; // % per second
    
    public enum MuscleState {
        RELAXED("Relaxed"),
        CONTRACTING("Contracting"),
        HOLDING("Holding"),
        EXTENDING("Extending"),
        FATIGUED("Fatigued"),
        DAMAGED("Damaged");
        
        private final String display;
        MuscleState(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
    
    public HydraulicMuscle(String name, double restingLength, double diameter) {
        super(name, PartType.UPPER_ARM); // Default, can be changed
        this.restingLength = restingLength;
        this.currentLength = restingLength;
        this.diameter = diameter;
        this.minLength = restingLength * 0.6; // 60% contraction
        this.maxLength = restingLength * 1.2; // 20% extension
        this.workingPressure = 350; // 350 bar typical
        this.maxPressure = 500;
        this.maxForce = calculateMaxForce();
        this.state = MuscleState.RELAXED;
        this.contractionRatio = 0;
        this.targetContraction = 0;
        this.contractionSpeed = 50; // 50% per second
        this.mass = calculateMass();
        this.maxStructuralStress = 200;
        this.hydraulicPressureRequirement = workingPressure;
        this.hydraulicFlowRequirement = 20; // L/min
    }
    
    @Override
    public void update(double deltaTime) {
        if (!isHydraulicallyPowered) {
            state = MuscleState.RELAXED;
            return;
        }
        
        // Update pressure based on supply
        currentPressure = currentHydraulicPressure;
        
        // Temperature from work
        double work = Math.abs(contractileForce) * Math.abs(currentLength - restingLength) * deltaTime;
        temperature += work / 1000 * 0.1;
        temperature = Math.max(20, temperature - 5 * deltaTime); // Cooling
        
        // Update contraction
        updateContraction(deltaTime);
        
        // Calculate force
        calculateForce();
        
        // Fatigue
        if (state == MuscleState.CONTRACTING || state == MuscleState.HOLDING) {
            fatigue += 0.1 * deltaTime;
        } else {
            fatigue = Math.max(0, fatigue - 0.5 * deltaTime);
        }
        
        if (fatigue > 80) {
            state = MuscleState.FATIGUED;
        }
        
        // Check for damage
        if (currentPressure > maxPressure || temperature > 150) {
            isDamaged = true;
            state = MuscleState.DAMAGED;
        }
        
        calculateStress();
    }
    
    private void updateContraction(double deltaTime) {
        double diff = targetContraction - contractionRatio;
        
        if (Math.abs(diff) < 0.01) {
            state = MuscleState.HOLDING;
            return;
        }
        
        double speed = contractionSpeed * deltaTime;
        if (currentPressure < workingPressure * 0.5) {
            speed *= currentPressure / (workingPressure * 0.5); // Reduced speed at low pressure
        }
        
        if (diff > 0) {
            // Contracting
            contractionRatio = Math.min(targetContraction, contractionRatio + speed);
            state = MuscleState.CONTRACTING;
        } else {
            // Extending
            contractionRatio = Math.max(targetContraction, contractionRatio - speed);
            state = MuscleState.EXTENDING;
        }
        
        // Update length
        currentLength = maxLength - (maxLength - minLength) * contractionRatio;
        
        // Update fluid volume
        double area = Math.PI * (diameter / 2) * (diameter / 2);
        fluidVolume = area * currentLength * 1000; // Convert to liters
    }
    
    private void calculateForce() {
        // Force-length relationship (simplified)
        // Maximum force at resting length, decreases at extremes
        double lengthRatio = (currentLength - minLength) / (maxLength - minLength);
        double forceFactor = 1.0 - Math.abs(lengthRatio - 0.5) * 0.4; // 20% reduction at extremes
        
        // Pressure factor
        double pressureFactor = currentPressure / workingPressure;
        
        // Calculate contractile force
        if (contractionRatio > 0) {
            contractileForce = maxForce * forceFactor * pressureFactor * contractionRatio;
        } else {
            contractileForce = 0;
        }
    }
    
    private double calculateMaxForce() {
        // F = P * A
        double area = Math.PI * (diameter / 2) * (diameter / 2); // m²
        return workingPressure * 100_000 * area; // Convert bar to Pa
    }
    
    private double calculateMass() {
        // Approximate mass based on volume
        double volume = Math.PI * (diameter / 2) * (diameter / 2) * restingLength;
        return volume * 1500; // Density ~1500 kg/m³ (reinforced rubber/bladder)
    }
    
    public void setTargetContraction(double ratio) {
        this.targetContraction = Math.max(0, Math.min(1, ratio));
    }
    
    public void contract() {
        setTargetContraction(1.0);
    }
    
    public void extend() {
        setTargetContraction(0.0);
    }
    
    public void relax() {
        setTargetContraction(0.0);
        state = MuscleState.RELAXED;
    }
    
    @Override
    public void render() {
        // Render muscle visualization
    }
    
    @Override
    public Part copy() {
        HydraulicMuscle copy = new HydraulicMuscle(name, restingLength, diameter);
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        return Math.PI * (diameter / 2) * (diameter / 2);
    }
    
    // Getters
    public double getRestingLength() { return restingLength; }
    public double getCurrentLength() { return currentLength; }
    public double getMinLength() { return minLength; }
    public double getMaxLength() { return maxLength; }
    public double getDiameter() { return diameter; }
    public double getCurrentPressure() { return currentPressure; }
    public double getContractileForce() { return contractileForce; }
    public double getMaxForce() { return maxForce; }
    public double getContractionRatio() { return contractionRatio; }
    public MuscleState getState() { return state; }
    public double getFatigue() { return fatigue; }
    public double getTemperature() { return temperature; }
    public double getFluidVolume() { return fluidVolume; }
}

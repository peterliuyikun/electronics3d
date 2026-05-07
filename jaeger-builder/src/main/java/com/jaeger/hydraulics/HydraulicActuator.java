package com.jaeger.hydraulics;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.core.Vector3;

public class HydraulicActuator extends Part {
    private static final long serialVersionUID = 1L;
    
    // Actuator type
    private ActuatorType type;
    
    // Linear actuator specs
    private double boreDiameter; // meters
    private double rodDiameter; // meters
    private double stroke; // meters
    private double currentExtension; // 0-1 (0 = retracted, 1 = extended)
    
    // Rotary actuator specs
    private double displacement; // cc per revolution
    private double currentAngle; // degrees
    
    // Performance
    private double maxForce; // Newtons
    private double maxSpeed; // m/s or deg/s
    private double currentForce; // Newtons
    private double currentSpeed; // m/s or deg/s
    
    // Hydraulic
    private double workingPressure; // bar
    private double currentPressure; // bar
    private double flowRate; // L/min
    
    // State
    private ActuatorState state;
    private double targetPosition; // 0-1 for linear, degrees for rotary
    private boolean positionControl;
    
    public enum ActuatorType {
        LINEAR("Linear Piston"),
        ROTARY("Rotary Vane"),
        LINEAR_DOUBLE("Double-Acting Cylinder");
        
        private final String display;
        ActuatorType(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
    
    public enum ActuatorState {
        IDLE("Idle"),
        EXTENDING("Extending"),
        RETRACTING("Retracting"),
        HOLDING("Holding Position"),
        ROTATING_CW("Rotating CW"),
        ROTATING_CCW("Rotating CCW"),
        OVERLOAD("OVERLOAD");
        
        private final String display;
        ActuatorState(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
    
    // Linear actuator constructor
    public HydraulicActuator(String name, double boreDiameter, double rodDiameter, double stroke) {
        super(name, PartType.FOREARM);
        this.type = ActuatorType.LINEAR_DOUBLE;
        this.boreDiameter = boreDiameter;
        this.rodDiameter = rodDiameter;
        this.stroke = stroke;
        this.workingPressure = 280; // bar
        this.maxForce = calculateLinearForce();
        this.maxSpeed = 0.5; // m/s
        this.state = ActuatorState.IDLE;
        this.mass = calculateLinearMass();
        this.maxStructuralStress = 250;
        this.hydraulicPressureRequirement = workingPressure;
        this.hydraulicFlowRequirement = calculateFlowRequirement();
    }
    
    // Rotary actuator constructor
    public HydraulicActuator(String name, double displacement, boolean isRotary) {
        super(name, PartType.SHOULDER_MOUNT);
        this.type = ActuatorType.ROTARY;
        this.displacement = displacement;
        this.workingPressure = 210; // bar
        this.maxForce = calculateRotaryTorque(); // Actually torque in Nm
        this.maxSpeed = 180; // deg/s
        this.state = ActuatorState.IDLE;
        this.mass = displacement * 0.5; // Approximate
        this.maxStructuralStress = 300;
        this.hydraulicPressureRequirement = workingPressure;
        this.hydraulicFlowRequirement = calculateFlowRequirement();
    }
    
    @Override
    public void update(double deltaTime) {
        if (!isHydraulicallyPowered) {
            state = ActuatorState.IDLE;
            return;
        }
        
        currentPressure = currentHydraulicPressure;
        flowRate = currentHydraulicFlow;
        
        switch (type) {
            case LINEAR:
            case LINEAR_DOUBLE:
                updateLinear(deltaTime);
                break;
            case ROTARY:
                updateRotary(deltaTime);
                break;
        }
        
        // Check overload
        if (currentForce > maxForce * 1.1) {
            state = ActuatorState.OVERLOAD;
        }
        
        calculateStress();
    }
    
    private void updateLinear(double deltaTime) {
        double targetExtension = targetPosition;
        double diff = targetExtension - currentExtension;
        
        if (Math.abs(diff) < 0.01) {
            state = ActuatorState.HOLDING;
            currentSpeed = 0;
            return;
        }
        
        // Calculate speed based on flow rate
        double speedFactor = flowRate / hydraulicFlowRequirement;
        double speed = maxSpeed * speedFactor * deltaTime;
        
        // Direction
        if (diff > 0) {
            currentExtension = Math.min(targetExtension, currentExtension + speed);
            state = ActuatorState.EXTENDING;
        } else {
            currentExtension = Math.max(targetExtension, currentExtension - speed);
            state = ActuatorState.RETRACTING;
        }
        
        // Calculate force
        double area = Math.PI * (boreDiameter / 2) * (boreDiameter / 2);
        currentForce = currentPressure * 100_000 * area * currentExtension;
        currentSpeed = speed / deltaTime;
    }
    
    private void updateRotary(double deltaTime) {
        double targetAngle = targetPosition;
        double diff = targetAngle - currentAngle;
        
        // Normalize to -180 to 180
        while (diff > 180) diff -= 360;
        while (diff < -180) diff += 360;
        
        if (Math.abs(diff) < 1.0) {
            state = ActuatorState.HOLDING;
            currentSpeed = 0;
            return;
        }
        
        double speedFactor = flowRate / hydraulicFlowRequirement;
        double speed = maxSpeed * speedFactor * deltaTime;
        
        if (diff > 0) {
            currentAngle += Math.min(Math.abs(diff), speed);
            state = ActuatorState.ROTATING_CW;
        } else {
            currentAngle -= Math.min(Math.abs(diff), speed);
            state = ActuatorState.ROTATING_CCW;
        }
        
        // Torque calculation
        currentForce = (displacement / 1000) * currentPressure * 100_000 / (2 * Math.PI);
        currentSpeed = speed / deltaTime;
    }
    
    private double calculateLinearForce() {
        double area = Math.PI * (boreDiameter / 2) * (boreDiameter / 2);
        return workingPressure * 100_000 * area;
    }
    
    private double calculateRotaryTorque() {
        // Torque = displacement * pressure / (2 * PI)
        return (displacement / 1000) * workingPressure * 100_000 / (2 * Math.PI);
    }
    
    private double calculateLinearMass() {
        double volume = Math.PI * (boreDiameter / 2) * (boreDiameter / 2) * stroke;
        return volume * 7800; // Steel density
    }
    
    private double calculateFlowRequirement() {
        if (type == ActuatorType.ROTARY) {
            return displacement * maxSpeed / 360 * 60 / 1000; // L/min
        } else {
            double area = Math.PI * (boreDiameter / 2) * (boreDiameter / 2);
            return area * maxSpeed * 60000; // L/min
        }
    }
    
    public void setTargetPosition(double position) {
        if (type == ActuatorType.ROTARY) {
            this.targetPosition = position; // degrees
        } else {
            this.targetPosition = Math.max(0, Math.min(1, position));
        }
    }
    
    public void extend() {
        if (type != ActuatorType.ROTARY) {
            setTargetPosition(1.0);
        }
    }
    
    public void retract() {
        if (type != ActuatorType.ROTARY) {
            setTargetPosition(0.0);
        }
    }
    
    public void rotateTo(double angle) {
        if (type == ActuatorType.ROTARY) {
            setTargetPosition(angle);
        }
    }
    
    @Override
    public void render() {
        // Render actuator
    }
    
    @Override
    public Part copy() {
        HydraulicActuator copy;
        if (type == ActuatorType.ROTARY) {
            copy = new HydraulicActuator(name, displacement, true);
        } else {
            copy = new HydraulicActuator(name, boreDiameter, rodDiameter, stroke);
        }
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        if (type == ActuatorType.ROTARY) {
            return displacement / 1000; // Simplified
        }
        return Math.PI * (boreDiameter / 2) * (boreDiameter / 2);
    }
    
    // Getters
    public ActuatorType getActuatorType() { return type; }
    public double getCurrentExtension() { return currentExtension; }
    public double getCurrentAngle() { return currentAngle; }
    public double getStroke() { return stroke; }
    public double getMaxForce() { return maxForce; }
    public double getCurrentForce() { return currentForce; }
    public double getCurrentSpeed() { return currentSpeed; }
    public double getCurrentPressure() { return currentPressure; }
    public ActuatorState getState() { return state; }
    public double getTargetPosition() { return targetPosition; }
}
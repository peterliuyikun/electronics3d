package com.jaeger.electrical;

import com.jaeger.core.Part;
import com.jaeger.core.Vector3;

import java.util.UUID;

public class Wire {
    private String id;
    private String name;
    private WireType type;
    private double gauge; // AWG
    private double length; // meters
    private double maxCurrent; // Amps
    
    // Connection
    private Part sourcePart;
    private Part destinationPart;
    private Vector3 startPoint;
    private Vector3 endPoint;
    
    // Electrical properties
    private double resistance; // Ohms
    private double voltageDrop; // Volts
    private double currentFlow; // Amps
    private double powerLoss; // Watts
    private double temperature; // Celsius
    
    // Status
    private boolean damaged;
    private boolean shortCircuited;
    
    public enum WireType {
        POWER_HIGH("High Voltage Power", 1000, 1000, 2.0),  // 1000V, 1000A, 2 AWG
        POWER_LOW("Low Voltage Power", 48, 100, 10.0),      // 48V, 100A, 10 AWG
        SIGNAL_ANALOG("Analog Signal", 5, 0.1, 22.0),       // 5V, 0.1A, 22 AWG
        SIGNAL_DIGITAL("Digital Signal", 3.3, 0.01, 24.0),  // 3.3V, 0.01A, 24 AWG
        HYDRAULIC_CONTROL("Hydraulic Control", 24, 5, 16.0), // 24V, 5A, 16 AWG
        DATA_FIBER("Fiber Optic", 0, 0, 0);                  // No electrical properties
        
        private final String display;
        private final double maxVoltage;
        private final double maxCurrent;
        private final double defaultGauge;
        
        WireType(String display, double maxVoltage, double maxCurrent, double defaultGauge) {
            this.display = display;
            this.maxVoltage = maxVoltage;
            this.maxCurrent = maxCurrent;
            this.defaultGauge = defaultGauge;
        }
        
        public String getDisplay() { return display; }
        public double getMaxVoltage() { return maxVoltage; }
        public double getMaxCurrent() { return maxCurrent; }
        public double getDefaultGauge() { return defaultGauge; }
    }
    
    public Wire(String name, WireType type, double gauge, double length) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.gauge = gauge;
        this.length = length;
        this.maxCurrent = calculateMaxCurrent();
        this.resistance = calculateResistance();
    }
    
    private double calculateMaxCurrent() {
        // Simplified AWG current capacity
        // AWG 2: ~200A, AWG 10: ~30A, AWG 16: ~10A, AWG 22: ~1A, AWG 24: ~0.5A
        return Math.pow(2, (2 - gauge) / 3) * 200;
    }
    
    private double calculateResistance() {
        if (type == WireType.DATA_FIBER) return 0;
        
        // Resistivity of copper: 1.68e-8 ohm-meters
        // Cross-sectional area based on AWG
        double diameterMeters = 0.127 * Math.pow(92, (36 - gauge) / 39) * 0.001; // mm to m
        double area = Math.PI * (diameterMeters / 2) * (diameterMeters / 2);
        
        return (1.68e-8 * length) / area;
    }
    
    public void update(double current, double time) {
        this.currentFlow = current;
        
        if (type == WireType.DATA_FIBER) return;
        
        // Voltage drop: V = I * R
        voltageDrop = current * resistance;
        
        // Power loss: P = I² * R
        powerLoss = current * current * resistance;
        
        // Temperature rise (simplified)
        double tempRise = powerLoss / (length * 10); // Heat dissipation
        temperature = 25 + tempRise; // Ambient + rise
        
        // Check for overload
        if (current > maxCurrent) {
            damaged = true;
        }
        
        // Check for short circuit
        if (voltageDrop > type.getMaxVoltage() * 0.5) {
            shortCircuited = true;
        }
    }
    
    public void connect(Part source, Part destination) {
        this.sourcePart = source;
        this.destinationPart = destination;
    }
    
    public void setEndpoints(Vector3 start, Vector3 end) {
        this.startPoint = start;
        this.endPoint = end;
        this.length = start.distance(end);
        this.resistance = calculateResistance();
    }
    
    // Getters
    public String getName() { return name; }
    public WireType getType() { return type; }
    public double getGauge() { return gauge; }
    public double getResistance() { return resistance; }
    public double getVoltageDrop() { return voltageDrop; }
    public double getCurrentFlow() { return currentFlow; }
    public double getPowerLoss() { return powerLoss; }
    public double getTemperature() { return temperature; }
    public boolean isDamaged() { return damaged; }
    public boolean isShortCircuited() { return shortCircuited; }
    public Part getSourcePart() { return sourcePart; }
    public Part getDestinationPart() { return destinationPart; }
}
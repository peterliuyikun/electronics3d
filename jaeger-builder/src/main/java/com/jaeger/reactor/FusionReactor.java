package com.jaeger.reactor;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;

public class FusionReactor extends Part {
    private static final long serialVersionUID = 1L;
    
    // Tokamak parameters
    private double plasmaTemperature; // Kelvin
    private double plasmaDensity; // particles per m³
    private double magneticFieldStrength; // Tesla
    private double confinementTime; // seconds
    
    // Fuel
    private double deuteriumLevel; // 0-100%
    private double tritiumLevel; // 0-100%
    private double helium3Level; // 0-100%
    
    // Output
    private double thermalOutput; // MW
    private double electricalOutput; // MW
    private double efficiency;
    
    // Status
    private ReactorState state;
    private double stability; // 0-100%
    private double radiationLevel; // Sieverts/hour
    private boolean magneticConfinementActive;
    
    // Constants
    private static final double MAX_PLASMA_TEMP = 150_000_000; // 150 million K
    private static final double MIN_IGNITION_TEMP = 100_000_000; // 100 million K
    private static final double MAX_MAGNETIC_FIELD = 20.0; // Tesla
    
    public enum ReactorState {
        OFFLINE("Offline"),
        STARTING("Starting"),
        HEATING("Heating Plasma"),
        IGNITION("Ignition"),
        SUSTAINED("Sustained"),
        SHUTDOWN("Shutdown"),
        EMERGENCY("EMERGENCY"),
        SCRAM("SCRAM");
        
        private final String display;
        ReactorState(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
    
    public FusionReactor(String name) {
        super(name, PartType.FUSION_REACTOR);
        this.state = ReactorState.OFFLINE;
        this.plasmaTemperature = 300; // Room temp
        this.plasmaDensity = 1e19;
        this.magneticFieldStrength = 0;
        this.confinementTime = 0;
        this.deuteriumLevel = 100;
        this.tritiumLevel = 100;
        this.helium3Level = 0;
        this.efficiency = 0.4; // 40% thermal to electric
        this.stability = 100;
        this.mass = 250_000; // 250 tons
        this.maxStructuralStress = 500;
        this.powerRequirement = 50_000_000; // 50 MW to start
    }
    
    @Override
    public void update(double deltaTime) {
        switch (state) {
            case STARTING:
                updateStarting(deltaTime);
                break;
            case HEATING:
                updateHeating(deltaTime);
                break;
            case IGNITION:
                updateIgnition(deltaTime);
                break;
            case SUSTAINED:
                updateSustained(deltaTime);
                break;
            case EMERGENCY:
                updateEmergency(deltaTime);
                break;
            case SHUTDOWN:
                updateShutdown(deltaTime);
                break;
            default:
                break;
        }
        
        calculateRadiation();
        calculateStress();
    }
    
    private void updateStarting(double deltaTime) {
        if (isPowered) {
            magneticFieldStrength = Math.min(magneticFieldStrength + deltaTime * 2, MAX_MAGNETIC_FIELD);
            if (magneticFieldStrength >= MAX_MAGNETIC_FIELD * 0.8) {
                magneticConfinementActive = true;
                state = ReactorState.HEATING;
            }
        }
    }
    
    private void updateHeating(double deltaTime) {
        if (!magneticConfinementActive) {
            state = ReactorState.EMERGENCY;
            return;
        }
        
        double heatingRate = 5_000_000 * deltaTime; // 5 million K per second
        plasmaTemperature += heatingRate;
        
        if (plasmaTemperature >= MIN_IGNITION_TEMP) {
            state = ReactorState.IGNITION;
        }
    }
    
    private void updateIgnition(double deltaTime) {
        if (deuteriumLevel > 0 && tritiumLevel > 0) {
            // Fusion reaction: D + T → He + n + 17.6 MeV
            double burnRate = 0.1 * deltaTime;
            deuteriumLevel = Math.max(0, deuteriumLevel - burnRate);
            tritiumLevel = Math.max(0, tritiumLevel - burnRate);
            
            // Energy output
            thermalOutput = calculateFusionPower();
            electricalOutput = thermalOutput * efficiency;
            
            confinementTime += deltaTime;
            if (confinementTime > 1.0) {
                state = ReactorState.SUSTAINED;
            }
        } else {
            state = ReactorState.SHUTDOWN;
        }
    }
    
    private void updateSustained(double deltaTime) {
        if (!magneticConfinementActive || plasmaTemperature < MIN_IGNITION_TEMP * 0.9) {
            state = ReactorState.EMERGENCY;
            return;
        }
        
        // Consume fuel
        double burnRate = 0.05 * deltaTime;
        if (deuteriumLevel > burnRate && tritiumLevel > burnRate) {
            deuteriumLevel -= burnRate;
            tritiumLevel -= burnRate;
            thermalOutput = calculateFusionPower();
            electricalOutput = thermalOutput * efficiency;
        } else {
            state = ReactorState.SHUTDOWN;
        }
        
        // Stability calculation
        double targetStability = 100 - (plasmaTemperature / MAX_PLASMA_TEMP) * 20;
        stability = stability * 0.99 + targetStability * 0.01;
    }
    
    private void updateEmergency(double deltaTime) {
        stability -= 10 * deltaTime;
        if (stability <= 0) {
            triggerScram();
        }
    }
    
    private void updateShutdown(double deltaTime) {
        plasmaTemperature *= Math.pow(0.1, deltaTime); // Exponential cooling
        magneticFieldStrength *= Math.pow(0.5, deltaTime);
        thermalOutput *= Math.pow(0.1, deltaTime);
        electricalOutput *= Math.pow(0.1, deltaTime);
        
        if (plasmaTemperature < 1000) {
            state = ReactorState.OFFLINE;
            magneticConfinementActive = false;
        }
    }
    
    private double calculateFusionPower() {
        // Simplified fusion power formula
        double n = plasmaDensity;
        double T = plasmaTemperature;
        double tau = confinementTime;
        
        // Triple product: n * T * tau
        double tripleProduct = n * T * tau;
        
        // Lawson criterion approximation
        if (tripleProduct < 1e21) {
            return 0;
        }
        
        // Power output in MW (simplified)
        return Math.min(1000, tripleProduct / 1e21 * 500);
    }
    
    private void calculateRadiation() {
        if (state == ReactorState.SUSTAINED || state == ReactorState.IGNITION) {
            radiationLevel = plasmaTemperature / 1_000_000 * 0.1; // 0.1 Sv/h at 1M K
        } else {
            radiationLevel *= 0.99;
        }
    }
    
    public void start() {
        if (state == ReactorState.OFFLINE && isPowered) {
            state = ReactorState.STARTING;
        }
    }
    
    public void shutdown() {
        if (state != ReactorState.OFFLINE && state != ReactorState.SHUTDOWN) {
            state = ReactorState.SHUTDOWN;
        }
    }
    
    public void triggerScram() {
        state = ReactorState.SCRAM;
        magneticFieldStrength = 0;
        magneticConfinementActive = false;
        plasmaTemperature = 300;
        thermalOutput = 0;
        electricalOutput = 0;
    }
    
    public void refuel(double dPercent, double tPercent, double he3Percent) {
        deuteriumLevel = Math.min(100, deuteriumLevel + dPercent);
        tritiumLevel = Math.min(100, tritiumLevel + tPercent);
        helium3Level = Math.min(100, helium3Level + he3Percent);
    }
    
    @Override
    public void render() {
        // Render reactor visualization
    }
    
    @Override
    public Part copy() {
        FusionReactor copy = new FusionReactor(this.name);
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        return 50.0; // m²
    }
    
    // Getters
    public ReactorState getState() { return state; }
    public double getPlasmaTemperature() { return plasmaTemperature; }
    public double getMagneticFieldStrength() { return magneticFieldStrength; }
    public double getThermalOutput() { return thermalOutput; }
    public double getElectricalOutput() { return electricalOutput; }
    public double getStability() { return stability; }
    public double getRadiationLevel() { return radiationLevel; }
    public double getDeuteriumLevel() { return deuteriumLevel; }
    public double getTritiumLevel() { return tritiumLevel; }
    public boolean isMagneticConfinementActive() { return magneticConfinementActive; }
}
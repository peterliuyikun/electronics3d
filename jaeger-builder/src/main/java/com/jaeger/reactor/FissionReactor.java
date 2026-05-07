package com.jaeger.reactor;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;

public class FissionReactor extends Part {
    private static final long serialVersionUID = 1L;
    
    // Core parameters
    private double coreTemperature; // Celsius
    private double neutronFlux; // neutrons/cm²/s
    private double controlRodPosition; // 0-100% (0 = fully inserted, 100 = fully withdrawn)
    
    // Fuel
    private double fuelEnrichment; // Percent U-235
    private double fuelRemaining; // 0-100%
    private double burnup; // MWd/kg
    
    // Output
    private double thermalOutput; // MW
    private double electricalOutput; // MW
    private double efficiency;
    
    // Status
    private ReactorState state;
    private double reactivity; // delta-k/k
    private double xenonPoisoning; // 0-100%
    private double decayHeat; // MW (after shutdown)
    private double radiationLevel; // Sieverts/hour
    
    // Safety systems
    private boolean scramActive;
    private boolean coolantPumpActive;
    private double coolantFlowRate; // kg/s
    private double coolantTemperature; // Celsius
    
    // Constants
    private static final double MAX_CORE_TEMP = 2000; // Celsius
    private static final double CRITICAL_TEMP = 1500; // Emergency threshold
    private static final double NOMINAL_FLUX = 1e13; // neutrons/cm²/s
    
    public enum ReactorState {
        COLD_SHUTDOWN("Cold Shutdown"),
        HOT_SHUTDOWN("Hot Shutdown"),
        STARTING("Starting"),
        LOW_POWER("Low Power"),
        FULL_POWER("Full Power"),
        EMERGENCY("EMERGENCY"),
        SCRAM("SCRAM"),
        MELTDOWN("MELTDOWN");
        
        private final String display;
        ReactorState(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
    
    public FissionReactor(String name) {
        super(name, PartType.FISSION_REACTOR);
        this.state = ReactorState.COLD_SHUTDOWN;
        this.coreTemperature = 40; // Ambient
        this.neutronFlux = 0;
        this.controlRodPosition = 0;
        this.fuelEnrichment = 4.5; // 4.5% enriched
        this.fuelRemaining = 100;
        this.efficiency = 0.33; // 33% thermal to electric
        this.reactivity = -0.05; // Subcritical
        this.coolantFlowRate = 0;
        this.coolantTemperature = 40;
        this.mass = 150_000; // 150 tons
        this.maxStructuralStress = 300;
        this.powerRequirement = 10_000_000; // 10 MW for startup
    }
    
    @Override
    public void update(double deltaTime) {
        switch (state) {
            case STARTING:
                updateStarting(deltaTime);
                break;
            case LOW_POWER:
            case FULL_POWER:
                updateRunning(deltaTime);
                break;
            case HOT_SHUTDOWN:
                updateHotShutdown(deltaTime);
                break;
            case EMERGENCY:
                updateEmergency(deltaTime);
                break;
            case SCRAM:
                updateScram(deltaTime);
                break;
            default:
                break;
        }
        
        updateXenonPoisoning(deltaTime);
        calculateRadiation();
        calculateStress();
    }
    
    private void updateStarting(double deltaTime) {
        if (!isPowered) return;
        
        // Withdraw control rods to reach criticality
        if (controlRodPosition < 30) {
            controlRodPosition += 5 * deltaTime;
        }
        
        // Calculate reactivity
        reactivity = calculateReactivity();
        
        if (reactivity > 0) {
            // Reactor is critical
            neutronFlux = Math.min(neutronFlux + deltaTime * 1e12, NOMINAL_FLUX);
            coreTemperature += neutronFlux / NOMINAL_FLUX * 50 * deltaTime;
            
            if (neutronFlux > NOMINAL_FLUX * 0.1) {
                state = ReactorState.LOW_POWER;
            }
        }
        
        thermalOutput = neutronFlux / NOMINAL_FLUX * 1000; // Up to 1000 MW
        electricalOutput = thermalOutput * efficiency;
    }
    
    private void updateRunning(double deltaTime) {
        // Adjust control rods to maintain power
        double targetPower = state == ReactorState.FULL_POWER ? 1000 : 300; // MW
        double currentPower = thermalOutput;
        
        if (currentPower < targetPower * 0.95) {
            controlRodPosition = Math.min(controlRodPosition + deltaTime, 80);
        } else if (currentPower > targetPower * 1.05) {
            controlRodPosition = Math.max(controlRodPosition - deltaTime, 20);
        }
        
        reactivity = calculateReactivity();
        
        // Update neutron flux based on reactivity
        if (reactivity > 0) {
            neutronFlux *= (1 + reactivity * deltaTime);
        } else {
            neutronFlux *= (1 + reactivity * deltaTime * 2); // Faster decay
        }
        neutronFlux = Math.max(0, Math.min(neutronFlux, NOMINAL_FLUX * 1.2));
        
        // Temperature based on flux and cooling
        double heatGeneration = neutronFlux / NOMINAL_FLUX * 1500; // Max 1500°C
        double cooling = coolantFlowRate / 10000 * 500; // Cooling effect
        coreTemperature += (heatGeneration - cooling - coreTemperature) * 0.1 * deltaTime;
        
        // Check for emergency
        if (coreTemperature > CRITICAL_TEMP) {
            state = ReactorState.EMERGENCY;
        }
        
        // Burn fuel
        fuelRemaining -= thermalOutput / 1000 * 0.001 * deltaTime;
        burnup += thermalOutput * deltaTime / 86400; // MWd
        
        thermalOutput = neutronFlux / NOMINAL_FLUX * 1000;
        electricalOutput = thermalOutput * efficiency;
    }
    
    private void updateHotShutdown(double deltaTime) {
        // Decay heat from fission products
        decayHeat = 0.07 * thermalOutput * Math.exp(-deltaTime / 100);
        coreTemperature = Math.max(100, coreTemperature - 10 * deltaTime);
        neutronFlux *= 0.1;
        
        if (coreTemperature < 100) {
            state = ReactorState.COLD_SHUTDOWN;
        }
    }
    
    private void updateEmergency(double deltaTime) {
        if (!scramActive) {
            triggerScram();
        }
        
        // Check for meltdown
        if (coreTemperature > MAX_CORE_TEMP) {
            state = ReactorState.MELTDOWN;
        }
    }
    
    private void updateScram(double deltaTime) {
        controlRodPosition = 0; // Fully insert
        neutronFlux *= Math.exp(-deltaTime * 10); // Rapid shutdown
        reactivity = -0.1; // Deep subcritical
        
        // Decay heat
        decayHeat = 0.07 * 1000 * Math.pow(0.5, deltaTime / 3600);
        coreTemperature = Math.max(100, coreTemperature - 5 * deltaTime);
        
        if (neutronFlux < 1e8 && coreTemperature < 200) {
            state = ReactorState.HOT_SHUTDOWN;
        }
    }
    
    private double calculateReactivity() {
        // Simplified reactivity model
        double rodReactivity = (controlRodPosition / 100.0) * 0.15 - 0.05;
        double tempReactivity = -0.0001 * (coreTemperature - 300); // Negative temp coefficient
        double xenonReactivity = -xenonPoisoning / 100.0 * 0.03;
        
        return rodReactivity + tempReactivity + xenonReactivity;
    }
    
    private void updateXenonPoisoning(double deltaTime) {
        // Xenon-135 buildup from fission
        double productionRate = neutronFlux / NOMINAL_FLUX * 0.1;
        double decayRate = 0.01; // 9.1 hour half-life
        
        xenonPoisoning += (productionRate - xenonPoisoning * decayRate) * deltaTime;
        xenonPoisoning = Math.max(0, Math.min(100, xenonPoisoning));
    }
    
    private void calculateRadiation() {
        if (state == ReactorState.MELTDOWN) {
            radiationLevel = 1000; // Extreme
        } else if (state == ReactorState.FULL_POWER || state == ReactorState.LOW_POWER) {
            radiationLevel = 50 + neutronFlux / NOMINAL_FLUX * 200;
        } else {
            radiationLevel = Math.max(0, radiationLevel * 0.99);
        }
    }
    
    public void start() {
        if (state == ReactorState.COLD_SHUTDOWN || state == ReactorState.HOT_SHUTDOWN) {
            state = ReactorState.STARTING;
        }
    }
    
    public void shutdown() {
        if (state != ReactorState.COLD_SHUTDOWN) {
            state = ReactorState.HOT_SHUTDOWN;
            controlRodPosition = 0;
        }
    }
    
    public void triggerScram() {
        scramActive = true;
        state = ReactorState.SCRAM;
        controlRodPosition = 0;
    }
    
    public void setControlRodPosition(double position) {
        this.controlRodPosition = Math.max(0, Math.min(100, position));
    }
    
    public void setCoolantFlow(double flowRate) {
        this.coolantFlowRate = Math.max(0, flowRate);
    }
    
    @Override
    public void render() {
        // Render reactor
    }
    
    @Override
    public Part copy() {
        FissionReactor copy = new FissionReactor(this.name);
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        return 30.0;
    }
    
    // Getters
    public ReactorState getState() { return state; }
    public double getCoreTemperature() { return coreTemperature; }
    public double getNeutronFlux() { return neutronFlux; }
    public double getControlRodPosition() { return controlRodPosition; }
    public double getThermalOutput() { return thermalOutput; }
    public double getElectricalOutput() { return electricalOutput; }
    public double getReactivity() { return reactivity; }
    public double getFuelRemaining() { return fuelRemaining; }
    public double getRadiationLevel() { return radiationLevel; }
    public boolean isScramActive() { return scramActive; }
    public double getDecayHeat() { return decayHeat; }
}
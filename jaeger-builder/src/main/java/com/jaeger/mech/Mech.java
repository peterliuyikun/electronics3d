package com.jaeger.mech;

import com.jaeger.core.Part;
import com.jaeger.core.Vector3;
import com.jaeger.electrical.PowerBus;
import com.jaeger.electrical.Supercomputer;
import com.jaeger.reactor.FusionReactor;

import java.util.*;

public class Mech {
    private String id;
    private String name;
    private String designation;
    
    // Core systems
    private Part rootPart;
    private List<Part> allParts;
    private FusionReactor primaryReactor;
    private Supercomputer supercomputer;
    private PowerBus mainPowerBus;
    
    // Status
    private double totalMass; // kg
    private double height; // meters
    private Vector3 centerOfMass;
    private boolean isOperational;
    private MechStatus status;
    
    // Pilots
    private List<Supercomputer.Pilot> pilots;
    private int maxPilots;
    
    public enum MechStatus {
        CONSTRUCTION("Under Construction"),
        STANDBY("Standby"),
        POWERED("Powered"),
        PILOTS_CONNECTED("Pilots Connected"),
        NEURAL_LINK("Neural Link Active"),
        COMBAT_READY("Combat Ready"),
        DAMAGED("Damaged"),
        CRITICAL("Critical Damage"),
        DESTROYED("Destroyed");
        
        private final String display;
        MechStatus(String display) { this.display = display; }
        public String getDisplay() { return display; }
    }
    
    public Mech(String name, String designation) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.designation = designation;
        this.allParts = new ArrayList<>();
        this.pilots = new ArrayList<>();
        this.maxPilots = 2; // Most Jaegers have 2 pilots
        this.status = MechStatus.CONSTRUCTION;
        this.centerOfMass = new Vector3();
    }
    
    public void setRootPart(Part root) {
        this.rootPart = root;
        allParts.clear();
        collectParts(root);
        calculateStats();
    }
    
    private void collectParts(Part part) {
        allParts.add(part);
        for (Part child : part.getChildParts()) {
            collectParts(child);
        }
    }
    
    public void addPart(Part part, Part parent, ConnectionPoint attachPoint) {
        if (parent != null) {
            parent.addChildPart(part, attachPoint);
        }
        allParts.add(part);
        calculateStats();
    }
    
    public void removePart(Part part) {
        if (part.getParentPart() != null) {
            part.getParentPart().removeChildPart(part);
        }
        allParts.remove(part);
        calculateStats();
    }
    
    public void installReactor(FusionReactor reactor) {
        this.primaryReactor = reactor;
        
        // Setup power bus
        mainPowerBus = new PowerBus("Main Bus", 1000, 10000); // 1000V, 10kA
        
        // Connect all power consumers
        for (Part part : allParts) {
            if (part.getPowerRequirement() > 0) {
                mainPowerBus.addConsumer(part);
            }
        }
    }
    
    public void installSupercomputer(Supercomputer computer) {
        this.supercomputer = computer;
        
        // Register systems
        computer.registerSystem("Locomotion", 90);
        computer.registerSystem("Weapons", 85);
        computer.registerSystem("Sensors", 80);
        computer.registerSystem("Balance", 95);
        computer.registerSystem("Life Support", 100);
        computer.registerSystem("Communications", 70);
    }
    
    public void addPilot(String name, double compatibility) {
        if (pilots.size() < maxPilots) {
            Supercomputer.Pilot pilot = new Supercomputer.Pilot(name, compatibility);
            pilots.add(pilot);
            if (supercomputer != null) {
                supercomputer.connectPilot(pilot);
            }
        }
    }
    
    public void update(double deltaTime) {
        // Update reactor
        if (primaryReactor != null) {
            primaryReactor.update(deltaTime);
            
            // Distribute power
            if (mainPowerBus != null && primaryReactor.getElectricalOutput() > 0) {
                mainPowerBus.distributePower(primaryReactor.getElectricalOutput() * 1_000_000);
            }
        }
        
        // Update supercomputer
        if (supercomputer != null) {
            supercomputer.update(deltaTime);
        }
        
        // Update all parts
        for (Part part : allParts) {
            part.update(deltaTime);
        }
        
        // Update status
        updateStatus();
        
        // Recalculate stats periodically
        calculateStats();
    }
    
    private void updateStatus() {
        if (primaryReactor == null || primaryReactor.getState() == FusionReactor.ReactorState.OFFLINE) {
            status = MechStatus.STANDBY;
            return;
        }
        
        if (primaryReactor.getState() == FusionReactor.ReactorState.SUSTAINED) {
            status = MechStatus.POWERED;
        }
        
        if (!pilots.isEmpty()) {
            status = MechStatus.PILOTS_CONNECTED;
        }
        
        if (supercomputer != null && supercomputer.getState() == Supercomputer.ComputerState.NEURAL_LINK_ACTIVE) {
            status = MechStatus.NEURAL_LINK;
        }
        
        if (supercomputer != null && supercomputer.getState() == Supercomputer.ComputerState.DRIFT_MODE) {
            status = MechStatus.COMBAT_READY;
        }
        
        // Check for damage
        long damagedParts = allParts.stream().filter(Part::isDamaged).count();
        if (damagedParts > allParts.size() * 0.5) {
            status = MechStatus.CRITICAL;
        } else if (damagedParts > 0) {
            status = MechStatus.DAMAGED;
        }
    }
    
    private void calculateStats() {
        // Total mass
        totalMass = rootPart != null ? rootPart.getTotalMass() : 0;
        
        // Height (approximate from highest part)
        height = 0;
        for (Part part : allParts) {
            double y = part.getWorldPosition().y;
            if (y > height) height = y;
        }
        
        // Center of mass
        if (!allParts.isEmpty()) {
            Vector3 weightedSum = new Vector3();
            for (Part part : allParts) {
                weightedSum = weightedSum.add(part.getWorldPosition().multiply(part.getMass()));
            }
            centerOfMass = weightedSum.multiply(1.0 / totalMass);
        }
    }
    
    public void powerOn() {
        if (primaryReactor != null) {
            primaryReactor.start();
        }
        if (supercomputer != null) {
            supercomputer.boot();
        }
    }
    
    public void powerOff() {
        if (primaryReactor != null) {
            primaryReactor.shutdown();
        }
        if (supercomputer != null) {
            supercomputer.shutdown();
        }
    }
    
    public void activateNeuralLink() {
        if (supercomputer != null && pilots.size() >= 1) {
            supercomputer.activateNeuralLink();
        }
    }
    
    // Getters
    public String getName() { return name; }
    public String getDesignation() { return designation; }
    public MechStatus getStatus() { return status; }
    public double getTotalMass() { return totalMass; }
    public double getHeight() { return height; }
    public Vector3 getCenterOfMass() { return centerOfMass; }
    public List<Part> getAllParts() { return allParts; }
    public FusionReactor getPrimaryReactor() { return primaryReactor; }
    public Supercomputer getSupercomputer() { return supercomputer; }
    public List<Supercomputer.Pilot> getPilots() { return pilots; }
    public boolean isOperational() { return status.ordinal() >= MechStatus.POWERED.ordinal(); }
}

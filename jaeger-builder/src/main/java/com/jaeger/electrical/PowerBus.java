package com.jaeger.electrical;

import java.util.*;

public class PowerBus {
    private String id;
    private String name;
    private double voltage; // Volts
    private double maxCurrent; // Amps
    private double currentLoad; // Amps
    private double totalPower; // Watts
    
    private List<PowerConsumer> consumers;
    private List<PowerBus> subBuses;
    private PowerBus parentBus;
    
    // Circuit protection
    private double breakerRating; // Amps
    private boolean breakerTripped;
    private double tripTime; // seconds remaining before trip
    
    // Status
    private double busVoltage; // Actual voltage under load
    private double efficiency;
    
    public PowerBus(String name, double voltage, double maxCurrent) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.voltage = voltage;
        this.maxCurrent = maxCurrent;
        this.breakerRating = maxCurrent * 1.2; // 20% safety margin
        this.consumers = new ArrayList<>();
        this.subBuses = new ArrayList<>();
        this.efficiency = 0.98; // 2% loss
    }
    
    public void addConsumer(PowerConsumer consumer) {
        consumers.add(consumer);
    }
    
    public void removeConsumer(PowerConsumer consumer) {
        consumers.remove(consumer);
    }
    
    public void addSubBus(PowerBus bus) {
        bus.parentBus = this;
        subBuses.add(bus);
    }
    
    public double calculateLoad() {
        double totalPowerDraw = 0;
        
        // Direct consumers
        for (PowerConsumer consumer : consumers) {
            totalPowerDraw += consumer.getPowerRequirement();
        }
        
        // Sub-buses
        for (PowerBus subBus : subBuses) {
            totalPowerDraw += subBus.calculateLoad();
        }
        
        this.totalPower = totalPowerDraw;
        this.currentLoad = totalPowerDraw / voltage;
        
        // Calculate voltage drop
        double loadFactor = currentLoad / maxCurrent;
        busVoltage = voltage * (1 - loadFactor * 0.05); // 5% drop at max load
        
        return totalPowerDraw;
    }
    
    public void distributePower(double availablePower) {
        if (breakerTripped) {
            // No power available
            for (PowerConsumer consumer : consumers) {
                consumer.setPowerDraw(0);
            }
            return;
        }
        
        double totalRequired = calculateLoad();
        
        if (totalRequired <= 0) return;
        
        // Check for overload
        if (currentLoad > breakerRating) {
            tripTime -= 0.1; // Assume 0.1s update rate
            if (tripTime <= 0) {
                tripBreaker();
                return;
            }
        } else {
            tripTime = 5.0; // Reset trip timer
        }
        
        // Distribute power proportionally if insufficient
        double ratio = Math.min(1.0, availablePower / totalRequired);
        
        for (PowerConsumer consumer : consumers) {
            double allocated = consumer.getPowerRequirement() * ratio;
            consumer.setPowerDraw(allocated);
        }
        
        // Distribute to sub-buses
        double remainingPower = availablePower - (totalRequired * ratio);
        for (PowerBus subBus : subBuses) {
            double subRequired = subBus.calculateLoad();
            double subAllocation = Math.min(subRequired, remainingPower * (subRequired / totalRequired));
            subBus.distributePower(subAllocation * efficiency);
        }
    }
    
    public void tripBreaker() {
        breakerTripped = true;
        for (PowerConsumer consumer : consumers) {
            consumer.setPowerDraw(0);
        }
        for (PowerBus subBus : subBuses) {
            subBus.tripBreaker();
        }
    }
    
    public void resetBreaker() {
        breakerTripped = false;
        tripTime = 5.0;
    }
    
    // Getters
    public String getName() { return name; }
    public double getVoltage() { return voltage; }
    public double getCurrentLoad() { return currentLoad; }
    public double getMaxCurrent() { return maxCurrent; }
    public double getTotalPower() { return totalPower; }
    public double getBusVoltage() { return busVoltage; }
    public boolean isBreakerTripped() { return breakerTripped; }
    public double getBreakerRating() { return breakerRating; }
}

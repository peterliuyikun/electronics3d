package com.jaeger.ui;

import com.jaeger.mech.Mech;

import java.util.ArrayList;
import java.util.List;

public class SimulationEngine {
    
    private List<Mech> activeMechs;
    private boolean isRunning;
    private double timeScale;
    private Thread simulationThread;
    
    public SimulationEngine() {
        this.activeMechs = new ArrayList<>();
        this.isRunning = false;
        this.timeScale = 1.0;
    }
    
    public void start() {
        if (isRunning) return;
        
        isRunning = true;
        simulationThread = new Thread(this::simulationLoop);
        simulationThread.setDaemon(true);
        simulationThread.start();
    }
    
    public void stop() {
        isRunning = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }
    
    private void simulationLoop() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0; // 60 TPS
        double delta = 0;
        
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            
            while (delta >= 1) {
                tick(1.0 / 60.0 * timeScale);
                delta--;
            }
            
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    private void tick(double deltaTime) {
        for (Mech mech : activeMechs) {
            mech.update(deltaTime);
        }
    }
    
    public void addMech(Mech mech) {
        if (!activeMechs.contains(mech)) {
            activeMechs.add(mech);
        }
    }
    
    public void removeMech(Mech mech) {
        activeMechs.remove(mech);
    }
    
    public void setTimeScale(double scale) {
        this.timeScale = Math.max(0.1, Math.min(10.0, scale));
    }
    
    public double getTimeScale() {
        return timeScale;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public List<Mech> getActiveMechs() {
        return new ArrayList<>(activeMechs);
    }
}

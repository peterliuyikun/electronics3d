package com.jaeger.hydraulics;

public interface HydraulicConsumer {
    double getHydraulicPressureRequirement(); // Bar
    double getHydraulicFlowRequirement(); // L/min
    void setHydraulicSupply(double pressure, double flow);
    boolean isHydraulicallyPowered();
}

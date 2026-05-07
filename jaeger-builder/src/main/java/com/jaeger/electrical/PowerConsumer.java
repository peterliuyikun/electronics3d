package com.jaeger.electrical;

public interface PowerConsumer {
    double getPowerRequirement(); // Watts
    void setPowerDraw(double watts);
    boolean isPowered();
}

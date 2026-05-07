package com.jaeger.mech;

import com.jaeger.core.Part;
import com.jaeger.core.Vector3;

import java.io.Serializable;

public class ConnectionPoint implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private Vector3 localPosition;
    private Vector3 localRotation;
    private ConnectionType type;
    private ConnectionSize size;
    private boolean occupied;
    private Part parentPart;
    private Part connectedPart;
    
    public enum ConnectionType {
        STRUCTURAL("Structural", true, false, false),
        HYDRAULIC("Hydraulic", true, true, false),
        ELECTRICAL("Electrical", true, false, true),
        COMBINED("Combined", true, true, true),
        WEAPON_MOUNT("Weapon Mount", true, true, true),
        SENSOR_MOUNT("Sensor Mount", true, true, true);
        
        private final String display;
        private final boolean structural;
        private final boolean hydraulic;
        private final boolean electrical;
        
        ConnectionType(String display, boolean structural, boolean hydraulic, boolean electrical) {
            this.display = display;
            this.structural = structural;
            this.hydraulic = hydraulic;
            this.electrical = electrical;
        }
        
        public String getDisplay() { return display; }
        public boolean isStructural() { return structural; }
        public boolean isHydraulic() { return hydraulic; }
        public boolean isElectrical() { return electrical; }
        
        public boolean isCompatible(ConnectionType other) {
            // Structural can connect to anything structural
            if (this.structural && other.structural) return true;
            // Same type always compatible
            if (this == other) return true;
            // Combined is compatible with everything
            if (this == COMBINED || other == COMBINED) return true;
            return false;
        }
    }
    
    public enum ConnectionSize {
        SMALL(0.5, "Small"),
        MEDIUM(1.0, "Medium"),
        LARGE(2.0, "Large"),
        MASSIVE(5.0, "Massive");
        
        private final double scale;
        private final String display;
        
        ConnectionSize(double scale, String display) {
            this.scale = scale;
            this.display = display;
        }
        
        public double getScale() { return scale; }
        public String getDisplay() { return display; }
        
        public boolean isCompatible(ConnectionSize other) {
            // Can connect same size or one size different
            double ratio = this.scale / other.scale;
            return ratio >= 0.5 && ratio <= 2.0;
        }
    }
    
    public ConnectionPoint(String name, Vector3 position, ConnectionType type, ConnectionSize size) {
        this.name = name;
        this.localPosition = position;
        this.localRotation = new Vector3();
        this.type = type;
        this.size = size;
        this.occupied = false;
    }
    
    public boolean canConnect(ConnectionPoint other) {
        if (this.occupied || other.occupied) return false;
        if (!this.type.isCompatible(other.type)) return false;
        if (!this.size.isCompatible(other.size)) return false;
        return true;
    }
    
    public void setConnectedPart(Part part) {
        this.connectedPart = part;
        this.occupied = (part != null);
    }
    
    // Getters
    public String getName() { return name; }
    public Vector3 getLocalPosition() { return localPosition; }
    public ConnectionType getType() { return type; }
    public ConnectionSize getSize() { return size; }
    public boolean isOccupied() { return occupied; }
    public Part getConnectedPart() { return connectedPart; }
    public void setParentPart(Part part) { this.parentPart = part; }
    public Part getParentPart() { return parentPart; }
}

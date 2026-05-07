package com.jaeger.core;

public enum PartType {
    // Chassis
    TORSO("Torso", "Main body structure", Category.CHASSIS),
    HIP("Hip", "Lower body mounting point", Category.CHASSIS),
    SHOULDER_MOUNT("Shoulder Mount", "Arm attachment point", Category.CHASSIS),
    
    // Limbs
    UPPER_ARM("Upper Arm", "Upper arm segment", Category.LIMB),
    FOREARM("Forearm", "Lower arm segment", Category.LIMB),
    THIGH("Thigh", "Upper leg segment", Category.LIMB),
    SHIN("Shin", "Lower leg segment", Category.LIMB),
    FOOT("Foot", "Ground contact point", Category.LIMB),
    HAND("Hand", "Manipulator end", Category.LIMB),
    
    // Cockpit
    CONN_POD("Conn-Pod", "Pilot cockpit", Category.COCKPIT),
    
    // Armor
    ARMOR_PLATE_SMALL("Armor Plate (S)", "Small protective plate", Category.ARMOR),
    ARMOR_PLATE_MEDIUM("Armor Plate (M)", "Medium protective plate", Category.ARMOR),
    ARMOR_PLATE_LARGE("Armor Plate (L)", "Large protective plate", Category.ARMOR),
    
    // Weapons
    PLASMA_CASTER("Plasma Caster", "Energy weapon", Category.WEAPON),
    CHAIN_SWORD("Chain Sword", "Melee weapon", Category.WEAPON),
    ROCKET_POD("Rocket Pod", "Missile launcher", Category.WEAPON),
    
    // Sensors
    RADAR_ARRAY("Radar Array", "Detection system", Category.SENSOR),
    CAMERA_POD("Camera Pod", "Visual sensors", Category.SENSOR),
    LIDAR_UNIT("LIDAR Unit", "Laser ranging system", Category.SENSOR),
    
    // Systems
    FUSION_REACTOR("Fusion Reactor", "Primary power source", Category.SYSTEM),
    FISSION_REACTOR("Fission Reactor", "Backup power source", Category.SYSTEM),
    HYDRAULIC_PUMP("Hydraulic Pump", "Fluid power generator", Category.SYSTEM),
    SUPERCOMPUTER("Supercomputer", "Neural processing unit", Category.SYSTEM),
    COOLING_TOWER("Cooling Tower", "Heat dissipation", Category.SYSTEM);
    
    public enum Category {
        CHASSIS, LIMB, COCKPIT, ARMOR, WEAPON, SENSOR, SYSTEM
    }
    
    private final String displayName;
    private final String description;
    private final Category category;
    
    PartType(String displayName, String description, Category category) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
}

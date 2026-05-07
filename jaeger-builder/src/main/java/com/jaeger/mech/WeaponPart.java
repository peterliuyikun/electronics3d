package com.jaeger.mech;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.core.Vector3;

public class WeaponPart extends Part {
    private static final long serialVersionUID = 1L;
    
    private WeaponType weaponType;
    private double damage; // Damage per hit
    private double fireRate; // Rounds per minute
    private double range; // meters
    private double ammoCapacity;
    private double currentAmmo;
    private double reloadTime; // seconds
    private boolean isReloading;
    private double heatLevel; // 0-100%
    private double maxHeat;
    
    // Ammo types
    private String ammoType;
    private double ammoMass; // kg per round
    
    public enum WeaponType {
        PLASMA_CASTER("Plasma Caster", 1000, 60, 5000, "Plasma Cell", 5),
        CHAIN_SWORD("Chain Sword", 500, 0, 50, "None", 0),
        ROCKET_POD("Rocket Pod", 2000, 12, 10000, "HE Rocket", 50),
        RAILGUN("Railgun", 5000, 6, 20000, "Tungsten Rod", 100),
        LASER_CANNON("Laser Cannon", 800, 120, 8000, "Battery", 2),
        MISSILE_LAUNCHER("Missile Launcher", 3000, 4, 15000, "Guided Missile", 80);
        
        private final String display;
        private final double baseDamage;
        private final double baseFireRate;
        private final double baseRange;
        private final String baseAmmoType;
        private final double baseAmmoMass;
        
        WeaponType(String display, double damage, double fireRate, double range, 
                   String ammoType, double ammoMass) {
            this.display = display;
            this.baseDamage = damage;
            this.baseFireRate = fireRate;
            this.baseRange = range;
            this.baseAmmoType = ammoType;
            this.baseAmmoMass = ammoMass;
        }
        
        public String getDisplay() { return display; }
        public double getBaseDamage() { return baseDamage; }
        public double getBaseFireRate() { return baseFireRate; }
        public double getBaseRange() { return baseRange; }
        public String getBaseAmmoType() { return baseAmmoType; }
        public double getBaseAmmoMass() { return baseAmmoMass; }
    }
    
    public WeaponPart(String name, WeaponType type) {
        super(name, PartType.PLASMA_CASTER); // Default, will be set properly
        this.weaponType = type;
        this.damage = type.baseDamage;
        this.fireRate = type.baseFireRate;
        this.range = type.baseRange;
        this.ammoType = type.baseAmmoType;
        this.ammoMass = type.baseAmmoMass;
        this.ammoCapacity = type == WeaponType.PLASMA_CASTER ? 100 : 
                           type == WeaponType.ROCKET_POD ? 24 : 
                           type == WeaponType.RAILGUN ? 12 : 50;
        this.currentAmmo = ammoCapacity;
        this.reloadTime = 5.0; // 5 seconds
        this.maxHeat = 100;
        this.mass = calculateMass();
        this.maxStructuralStress = 200;
        this.powerRequirement = type == WeaponType.PLASMA_CASTER ? 50_000_000 :
                               type == WeaponType.RAILGUN ? 100_000_000 :
                               type == WeaponType.LASER_CANNON ? 30_000_000 : 0;
        
        setupConnectionPoints(type);
    }
    
    private double calculateMass() {
        return switch (weaponType) {
            case PLASMA_CASTER -> 15000; // 15 tons
            case CHAIN_SWORD -> 8000;
            case ROCKET_POD -> 12000;
            case RAILGUN -> 25000;
            case LASER_CANNON -> 10000;
            case MISSILE_LAUNCHER -> 18000;
        };
    }
    
    private void setupConnectionPoints(WeaponType type) {
        addConnectionPoint(new ConnectionPoint("mount", 
            new Vector3(0, 0, 0), ConnectionPoint.ConnectionType.WEAPON_MOUNT, ConnectionPoint.ConnectionSize.LARGE));
        
        if (type == WeaponType.PLASMA_CASTER || type == WeaponType.RAILGUN || type == WeaponType.LASER_CANNON) {
            addConnectionPoint(new ConnectionPoint("power_feed", 
                new Vector3(0, 0, -0.5), ConnectionPoint.ConnectionType.ELECTRICAL, ConnectionPoint.ConnectionSize.LARGE));
        }
        
        if (type == WeaponType.PLASMA_CASTER) {
            addConnectionPoint(new ConnectionPoint("coolant", 
                new Vector3(0.5, 0, 0), ConnectionPoint.ConnectionType.HYDRAULIC, ConnectionPoint.ConnectionSize.MEDIUM));
        }
    }
    
    @Override
    public void update(double deltaTime) {
        if (isReloading) {
            reloadTime -= deltaTime;
            if (reloadTime <= 0) {
                currentAmmo = ammoCapacity;
                isReloading = false;
                reloadTime = 5.0;
            }
        }
        
        // Cool down
        heatLevel = Math.max(0, heatLevel - 10 * deltaTime);
        
        calculateStress();
    }
    
    public boolean fire() {
        if (isReloading || currentAmmo <= 0 || heatLevel >= maxHeat || !isPowered) {
            return false;
        }
        
        currentAmmo--;
        heatLevel += weaponType == WeaponType.PLASMA_CASTER ? 15 :
                     weaponType == WeaponType.RAILGUN ? 25 :
                     weaponType == WeaponType.LASER_CANNON ? 10 : 5;
        
        return true;
    }
    
    public void reload() {
        if (!isReloading && currentAmmo < ammoCapacity) {
            isReloading = true;
        }
    }
    
    @Override
    public void render() {
        // Render weapon
    }
    
    @Override
    public Part copy() {
        WeaponPart copy = new WeaponPart(name, weaponType);
        copy.transform = this.transform.copy();
        return copy;
    }
    
    @Override
    protected double getCrossSectionalArea() {
        return mass / 2000;
    }
    
    // Getters
    public WeaponType getWeaponType() { return weaponType; }
    public double getDamage() { return damage; }
    public double getFireRate() { return fireRate; }
    public double getRange() { return range; }
    public double getCurrentAmmo() { return currentAmmo; }
    public double getAmmoCapacity() { return ammoCapacity; }
    public double getHeatLevel() { return heatLevel; }
    public boolean isReloading() { return isReloading; }
    public String getAmmoType() { return ammoType; }
}

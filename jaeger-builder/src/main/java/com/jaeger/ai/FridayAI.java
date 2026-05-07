package com.jaeger.ai;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.core.Vector3;
import com.jaeger.mech.*;
import com.jaeger.reactor.FusionReactor;
import com.jaeger.electrical.Supercomputer;
import com.jaeger.hydraulics.HydraulicMuscle;
import com.jaeger.hydraulics.HydraulicActuator;

import java.util.*;

public class FridayAI {
    
    private static final String[] GREETINGS = {
        "Good day. I'm F.R.I.D.A.Y. - your Jaeger construction assistant.",
        "Online and ready. What shall we build today?",
        "Systems nominal. How may I assist with your mech design?",
        "Hello. I've analyzed your workspace and I'm ready to help."
    };
    
    private static final String[] DESIGN_TIPS = {
        "Remember: A balanced Jaeger requires equal weight distribution.",
        "Pro tip: Always install redundant power systems.",
        "Hydraulic muscles provide 10x the force of electric motors.",
        "Fusion reactors need magnetic containment - don't skip the cooling.",
        "Two pilots are better than one - for Drift compatibility.",
        "Armor thickness vs. mobility: find your sweet spot.",
        "Plasma casters require massive power - plan your grid accordingly."
    };
    
    private Mech currentMech;
    private List<String> conversationHistory;
    private boolean isActive;
    private double confidenceLevel;
    
    // Model generation templates
    private Map<String, ModelTemplate> modelTemplates;
    
    public FridayAI() {
        this.conversationHistory = new ArrayList<>();
        this.isActive = true;
        this.confidenceLevel = 0.95;
        initializeTemplates();
    }
    
    private void initializeTemplates() {
        modelTemplates = new HashMap<>();
        
        // Mark-1 Template (Basic)
        modelTemplates.put("mark1", new ModelTemplate(
            "Mark-1",
            "First generation Jaeger - simple, reliable, effective",
            2500, // height
            800,  // width
            2000, // mass tons
            new String[]{"TORSO", "HIP", "UPPER_ARM", "FOREARM", "HAND", 
                        "THIGH", "SHIN", "FOOT", "CONN_POD"},
            new String[]{"Fusion Reactor", "Supercomputer", "Plasma Caster"}
        ));
        
        // Mark-3 Template (Advanced)
        modelTemplates.put("mark3", new ModelTemplate(
            "Mark-3",
            "Advanced Jaeger with improved armor and weapons",
            2800,
            900,
            2400,
            new String[]{"TORSO", "HIP", "SHOULDER_MOUNT", "UPPER_ARM", "FOREARM", 
                        "HAND", "THIGH", "SHIN", "FOOT", "CONN_POD"},
            new String[]{"Fusion Reactor", "Fission Reactor", "Supercomputer", 
                        "Plasma Caster", "Chain Sword", "Rocket Pod"}
        ));
        
        // Mark-5 Template (Latest)
        modelTemplates.put("mark5", new ModelTemplate(
            "Mark-5",
            "Latest generation - digital, agile, powerful",
            2600,
            850,
            1800,
            new String[]{"TORSO", "HIP", "SHOULDER_MOUNT", "UPPER_ARM", "FOREARM",
                        "HAND", "THIGH", "SHIN", "FOOT", "CONN_POD"},
            new String[]{"Fusion Reactor x2", "Supercomputer", "Plasma Caster x2", 
                        "Railgun", "Advanced Cooling"}
        ));
        
        // Brawler Template (Melee focused)
        modelTemplates.put("brawler", new ModelTemplate(
            "Brawler",
            "Close combat specialist with reinforced armor",
            2700,
            1100,
            3200,
            new String[]{"TORSO", "HIP", "SHOULDER_MOUNT", "UPPER_ARM", "FOREARM",
                        "HAND", "THIGH", "SHIN", "FOOT", "CONN_POD"},
            new String[]{"Fusion Reactor", "Supercomputer", "Chain Sword x2", 
                        "Armor Plates", "Hydraulic Boosters"}
        ));
        
        // Ranger Template (Speed focused)
        modelTemplates.put("ranger", new ModelTemplate(
            "Ranger",
            "Fast scout unit with long-range capabilities",
            2400,
            700,
            1500,
            new String[]{"TORSO", "HIP", "SHOULDER_MOUNT", "UPPER_ARM", "FOREARM",
                        "HAND", "THIGH", "SHIN", "FOOT", "CONN_POD"},
            new String[]{"Fusion Reactor", "Supercomputer", "Railgun", 
                        "Missile Launcher", "Light Armor"}
        ));
    }
    
    public String greet() {
        return getRandom(GREETINGS);
    }
    
    public String processCommand(String command) {
        conversationHistory.add("USER: " + command);
        
        String response = analyzeCommand(command.toLowerCase());
        
        conversationHistory.add("FRIDAY: " + response);
        return response;
    }
    
    private String analyzeCommand(String cmd) {
        // Generate model commands
        if (cmd.contains("generate") || cmd.contains("create") || cmd.contains("build")) {
            if (cmd.contains("mark 1") || cmd.contains("mark1")) {
                return generateModel("mark1");
            } else if (cmd.contains("mark 3") || cmd.contains("mark3")) {
                return generateModel("mark3");
            } else if (cmd.contains("mark 5") || cmd.contains("mark5")) {
                return generateModel("mark5");
            } else if (cmd.contains("brawler")) {
                return generateModel("brawler");
            } else if (cmd.contains("ranger")) {
                return generateModel("ranger");
            } else if (cmd.contains("random") || cmd.contains("surprise")) {
                String[] keys = modelTemplates.keySet().toArray(new String[0]);
                return generateModel(keys[new Random().nextInt(keys.length)]);
            } else {
                return "Available templates: Mark-1, Mark-3, Mark-5, Brawler, Ranger. " +
                       "Say 'generate [template]' to create a model.";
            }
        }
        
        // Design advice
        if (cmd.contains("tip") || cmd.contains("advice") || cmd.contains("help")) {
            return getRandom(DESIGN_TIPS);
        }
        
        // Status check
        if (cmd.contains("status") || cmd.contains("how are you")) {
            return String.format("All systems nominal. Confidence level: %.1f%%. " +
                             "Ready to assist with Jaeger construction.", confidenceLevel * 100);
        }
        
        // Analyze current mech
        if (cmd.contains("analyze") || cmd.contains("check") || cmd.contains("status of mech")) {
            return analyzeCurrentMech();
        }
        
        // Optimize
        if (cmd.contains("optimize") || cmd.contains("improve")) {
            return optimizeMech();
        }
        
        // Component info
        if (cmd.contains("reactor")) {
            return "Fusion reactors require: 1) Magnetic confinement, 2) Deuterium-Tritium fuel, " +
                   "3) Cooling systems, 4) 50MW startup power. Output: up to 1000MW thermal.";
        }
        if (cmd.contains("hydraulic") || cmd.contains("muscle")) {
            return "Hydraulic muscles provide contractile force based on fluid pressure. " +
                   "Typical working pressure: 350 bar. Can achieve 60% contraction.";
        }
        if (cmd.contains("pilot") || cmd.contains("neural")) {
            return "Neural link requires: 1) Compatible pilots, 2) Supercomputer online, " +
                   "3) Drift synchronization >80%. Two pilots recommended for optimal performance.";
        }
        
        // Default response
        return "I'm not sure I understand. I can help you: generate models, analyze designs, " +
               "provide tips, or explain systems. What would you like to do?";
    }
    
    private String generateModel(String templateKey) {
        ModelTemplate template = modelTemplates.get(templateKey);
        if (template == null) {
            return "Template not found. Available: Mark-1, Mark-3, Mark-5, Brawler, Ranger.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Generating ").append(template.name).append("...\n\n");
        sb.append("Description: ").append(template.description).append("\n");
        sb.append(String.format("Specifications:\n", template.height));
        sb.append(String.format("  Height: %.1f meters\n", template.height));
        sb.append(String.format("  Width: %.1f meters\n", template.width));
        sb.append(String.format("  Mass: %.0f tons\n\n", template.mass));
        
        sb.append("Components:\n");
        for (String component : template.components) {
            sb.append("  • ").append(component).append("\n");
        }
        
        sb.append("\nSystems:\n");
        for (String system : template.systems) {
            sb.append("  • ").append(system).append("\n");
        }
        
        sb.append("\nModel generation complete. Would you like me to instantiate this design?");
        
        return sb.toString();
    }
    
    private String analyzeCurrentMech() {
        if (currentMech == null) {
            return "No active Jaeger detected. Create or load a mech first.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== JAEGER ANALYSIS ===\n\n");
        sb.append("Name: ").append(currentMech.getName()).append("\n");
        sb.append("Status: ").append(currentMech.getStatus().getDisplay()).append("\n");
        sb.append(String.format("Total Mass: %.1f tons\n", currentMech.getTotalMass() / 1000));
        sb.append(String.format("Height: %.1f meters\n\n", currentMech.getHeight()));
        
        sb.append("Center of Mass: ").append(currentMech.getCenterOfMass()).append("\n");
        
        if (currentMech.getPrimaryReactor() != null) {
            sb.append("\nReactor Status:\n");
            sb.append("  State: ").append(currentMech.getPrimaryReactor().getState().getDisplay()).append("\n");
            sb.append(String.format("  Output: %.1f MW\n", currentMech.getPrimaryReactor().getElectricalOutput()));
        }
        
        if (currentMech.getSupercomputer() != null) {
            sb.append("\nComputer Status:\n");
            sb.append("  State: ").append(currentMech.getSupercomputer().getState().getDisplay()).append("\n");
            sb.append(String.format("  Load: %.1f%%\n", currentMech.getSupercomputer().getSystemLoad()));
            sb.append(String.format("  Neural Sync: %.1f%%\n", currentMech.getSupercomputer().getNeuralSyncLevel()));
        }
        
        return sb.toString();
    }
    
    private String optimizeMech() {
        if (currentMech == null) {
            return "No mech to optimize. Load or create a Jaeger first.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== OPTIMIZATION ANALYSIS ===\n\n");
        
        // Check balance
        Vector3 com = currentMech.getCenterOfMass();
        double balanceScore = 100 - Math.abs(com.x) * 10 - Math.abs(com.z) * 10;
        balanceScore = Math.max(0, Math.min(100, balanceScore));
        
        sb.append(String.format("Balance Score: %.1f/100\n", balanceScore));
        if (balanceScore < 80) {
            sb.append("  WARNING: Center of mass is off-center.\n");
            sb.append("  Suggestion: Redistribute mass or add counterweights.\n");
        } else {
            sb.append("  Status: GOOD\n");
        }
        
        // Check power
        if (currentMech.getPrimaryReactor() != null) {
            double powerOutput = currentMech.getPrimaryReactor().getElectricalOutput();
            sb.append(String.format("\nPower Output: %.1f MW\n", powerOutput));
            
            // Estimate power needs
            double estimatedNeed = currentMech.getAllParts().size() * 5; // Rough estimate
            if (powerOutput < estimatedNeed) {
                sb.append(String.format("  WARNING: Insufficient power (need ~%.0f MW)\n", estimatedNeed));
                sb.append("  Suggestion: Upgrade reactor or add secondary power source.\n");
            } else {
                sb.append("  Status: SUFFICIENT\n");
            }
        }
        
        // Check structural integrity
        long damagedParts = currentMech.getAllParts().stream()
            .filter(Part::isDamaged).count();
        if (damagedParts > 0) {
            sb.append(String.format("\nDamaged Parts: %d\n", damagedParts));
            sb.append("  WARNING: Structural damage detected.\n");
        }
        
        sb.append("\nOptimization complete.");
        return sb.toString();
    }
    
    private String getRandom(String[] options) {
        return options[new Random().nextInt(options.length)];
    }
    
    public void setCurrentMech(Mech mech) {
        this.currentMech = mech;
    }
    
    public List<String> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }
    
    public void clearHistory() {
        conversationHistory.clear();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public Set<String> getAvailableTemplates() {
        return modelTemplates.keySet();
    }
    
    // Model Template class
    private static class ModelTemplate {
        String name;
        String description;
        double height;
        double width;
        double mass;
        String[] components;
        String[] systems;
        
        ModelTemplate(String name, String description, double height, double width, 
                     double mass, String[] components, String[] systems) {
            this.name = name;
            this.description = description;
            this.height = height;
            this.width = width;
            this.mass = mass;
            this.components = components;
            this.systems = systems;
        }
    }
}
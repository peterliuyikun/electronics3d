# Jaeger Builder 3D

A comprehensive 3D mech construction and simulation system for building Pacific Rim-style Jaegers, featuring realistic nuclear reactors, hydraulic systems, and neural interfaces.

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![LWJGL](https://img.shields.io/badge/LWJGL-3.3.3-green.svg)

## Features

### 🔋 Power Systems
- **Fusion Reactors**: Tokamak-style magnetic confinement with plasma physics
  - Plasma temperature up to 150 million Kelvin
  - Deuterium-Tritium fuel cycle
  - Magnetic field strength control
  - SCRAM emergency shutdown
  - Radiation simulation

- **Fission Reactors**: Backup power with realistic control rod physics
  - Neutron flux simulation
  - Xenon-135 poisoning effects
  - Decay heat after shutdown
  - Control rod positioning

### 💪 Hydraulic Systems
- **Hydraulic Muscles**: Contractile actuators with realistic force curves
  - 60% contraction capability
  - Fatigue and damage modeling
  - Temperature effects
  - 350 bar working pressure

- **Hydraulic Actuators**: Linear pistons and rotary vanes
  - Position control with feedback
  - Pressure-based force calculation
  - Flow rate simulation

### 🧠 Neural Systems
- **Supercomputer**: 1 exaFLOP processing power
  - Neural link interface for pilots
  - Balance processor with center of mass calculation
  - Targeting processor with lead calculation
  - Movement processor for gait optimization
  - Weapon processor for firing solutions

- **Drift Compatibility**: Two-pilot neural synchronization
  - Compatibility scoring
  - Stress level monitoring
  - Sync level visualization

### 🔌 Electrical Systems
- **Power Distribution**: Hierarchical bus architecture
  - Circuit breaker simulation
  - Voltage drop calculation
  - Load balancing
  - Short circuit detection

- **Wiring Network**: Realistic wire properties
  - AWG gauge simulation
  - Resistance and power loss
  - Temperature rise
  - Connector types

### 🤖 F.R.I.D.A.Y. AI Assistant
Your personal Jaeger construction assistant:
- Natural language command processing
- Model generation from templates (Mark-1, Mark-3, Mark-5, Brawler, Ranger)
- Real-time design analysis
- Optimization suggestions
- System explanations
- Chat interface with terminal aesthetic

### 🎨 User Interface
- Dark terminal theme with green accents
- Real-time system monitors
- Part catalog with categories
- Property inspector
- Chat panel with F.R.I.D.A.Y.
- Tabbed system displays

## Architecture

```
jaeger-builder/
├── src/main/java/com/jaeger/
│   ├── Main.java                 # Entry point
│   ├── core/                     # Core classes
│   │   ├── Vector3.java         # 3D math
│   │   ├── Transform.java       # Position/rotation/scale
│   │   ├── Part.java            # Base part class
│   │   └── PartType.java        # Part categories
│   ├── reactor/                  # Nuclear systems
│   │   ├── FusionReactor.java
│   │   └── FissionReactor.java
│   ├── hydraulics/               # Hydraulic systems
│   │   ├── HydraulicMuscle.java
│   │   └── HydraulicActuator.java
│   ├── electrical/               # Power systems
│   │   ├── PowerConsumer.java
│   │   ├── PowerBus.java
│   │   ├── Wire.java
│   │   └── Supercomputer.java
│   ├── mech/                     # Mech components
│   │   ├── ConnectionPoint.java
│   │   ├── ChassisPart.java
│   │   ├── LimbPart.java
│   │   ├── WeaponPart.java
│   │   └── Mech.java
│   ├── ai/                       # AI assistant
│   │   └── FridayAI.java
│   └── ui/                       # User interface
│       ├── MainWindow.java
│       ├── FridayChatPanel.java
│       └── SimulationEngine.java
└── src/main/resources/styles/
    └── dark-theme.css
```

## Building

### Prerequisites
- Java 21 or higher
- Maven 3.8+

### Build Commands

```bash
# Clone the repository
git clone https://github.com/yourusername/jaeger-builder.git
cd jaeger-builder

# Build with Maven
mvn clean package

# Run the application
java -jar target/jaeger-builder-1.0.0.jar
```

## Usage

### Starting the Application
```bash
java -jar target/jaeger-builder-1.0.0.jar
```

### Using F.R.I.D.A.Y.
Click the "◉ F.R.I.D.A.Y." tab to open the AI assistant panel.

**Available Commands:**
- `generate mark1` - Create a Mark-1 Jaeger template
- `generate mark3` - Create a Mark-3 Jaeger template
- `generate mark5` - Create a Mark-5 Jaeger template
- `generate brawler` - Create a melee-focused Jaeger
- `generate ranger` - Create a speed-focused Jaeger
- `analyze` - Analyze current mech design
- `optimize` - Get optimization suggestions
- `status` - Check F.R.I.D.A.Y. status
- `reactor info` - Learn about fusion reactors
- `hydraulic info` - Learn about hydraulic systems
- `pilot info` - Learn about neural links
- `help` - Get design tips

### Building a Jaeger

1. **Create New Jaeger**: File → New Mech
2. **Add Parts**: Select from the Part Catalog on the left
3. **Configure Systems**: Monitor reactor, power grid, and computer in bottom tabs
4. **Add Pilots**: Connect two pilots for Drift compatibility
5. **Activate**: Power on reactor and activate neural link
6. **Analyze**: Ask F.R.I.D.A.Y. to analyze your design

### System Monitors

**Reactor Tab:**
- Plasma temperature (0-150M K)
- Power output (0-1000 MW)
- Fuel levels
- Magnetic field strength

**Power Grid Tab:**
- Grid load percentage
- Bus voltage and current
- Power distribution

**Supercomputer Tab:**
- System load
- Neural sync level
- CPU temperature
- Active systems

## Technical Details

### Physics Simulation
- Real-time delta-time based updates
- Accurate unit conversions
- Temperature-dependent properties
- Fatigue and damage accumulation

### Power Calculations
- Fusion: Triple product (n × T × τ)
- Fission: Reactivity and neutron flux
- Electrical: Ohm's law, voltage drop
- Hydraulic: Pressure × Area = Force

### Neural Interface
- Bandwidth: 100 TB/s
- Latency: 1 ms
- Drift sync required: >80%
- Dual pilot recommended

## Part Catalog

### Chassis
- Torso (main body)
- Hip (lower mounting)
- Shoulder Mount (arm attachment)
- Conn-Pod (pilot cockpit)

### Limbs
- Upper Arm / Forearm / Hand
- Thigh / Shin / Foot

### Systems
- Fusion Reactor
- Fission Reactor
- Hydraulic Pump
- Supercomputer
- Cooling Tower

### Weapons
- Plasma Caster (energy)
- Chain Sword (melee)
- Rocket Pod (missiles)
- Railgun (kinetic)
- Laser Cannon

### Actuators
- Hydraulic Muscle
- Linear Piston
- Rotary Vane

## Roadmap

- [ ] LWJGL 3D viewport integration
- [ ] Part snapping and connection validation
- [ ] Save/load mech designs
- [ ] Export to 3D formats (OBJ, STL)
- [ ] Physics-based animation
- [ ] Combat simulation
- [ ] Multiplayer support
- [ ] VR/AR integration

## License

MIT License - See LICENSE file

## Credits

Inspired by Pacific Rim and the Jaeger program.

F.R.I.D.A.Y. AI assistant inspired by Tony Stark's AI from the Marvel universe.

---

**"Today we cancel the apocalypse!"**
# Electronics 3D

A collection of 3D electronics and engineering simulation tools.

## 🚀 Quick Download & Run

### Jaeger Builder 3D - Pacific Rim Mech Simulator

**One-line download and run (requires Java 21):**

```bash
# Download and run directly
git clone https://github.com/peterliuyikun/electronics3d.git && cd electronics3d/jaeger-builder && mvn clean package && java -jar target/jaeger-builder-1.0.0.jar
```

**Or step by step:**

```bash
# 1. Clone the repository
git clone https://github.com/peterliuyikun/electronics3d.git

# 2. Enter the project directory
cd electronics3d/jaeger-builder

# 3. Build with Maven
mvn clean package

# 4. Run the application
java -jar target/jaeger-builder-1.0.0.jar
```

---

## 📦 Jaeger Builder 3D

Build and simulate Pacific Rim-style Jaegers with realistic nuclear reactors, hydraulic systems, and neural interfaces.

### ⚡ System Requirements
- **Java 21** or higher
- **Maven 3.8+** (for building)
- **4GB RAM** minimum
- **OpenGL-compatible** graphics (for future 3D viewport)

### 🎮 Features

| System | Description |
|--------|-------------|
| **Fusion Reactor** | Tokamak with 150M K plasma, magnetic confinement |
| **Fission Reactor** | Control rods, neutron flux, xenon poisoning |
| **Hydraulics** | Muscles (350 bar) and actuators with force curves |
| **Supercomputer** | 1 exaFLOP, neural link, balance/targeting processors |
| **F.R.I.D.A.Y. AI** | Built-in assistant for model generation and analysis |
| **Power Grid** | Hierarchical buses, circuit breakers, wiring |

### 🖥️ User Interface

```
┌─────────────────────────────────────────────────────────────┐
│  File  Edit  View  Simulate                                 │
├──────────┬──────────────────────────────┬───────────────────┤
│          │                              │                   │
│  PART    │                              │   PROPERTIES      │
│  CATALOG │      3D VIEWPORT             │                   │
│          │                              │   (selected part) │
│  •Torso  │                              │                   │
│  •Reactor│                              │                   │
│  •Weapons│                              │                   │
│          │                              │                   │
├──────────┴──────────────────────────────┴───────────────────┤
│  [REACTOR] [POWER GRID] [SUPERCOMPUTER] [◉ F.R.I.D.A.Y.]    │
│                                                             │
│  F.R.I.D.A.Y.: Good day. I'm your Jaeger construction       │
│  assistant. Type 'help' for commands.                       │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 🗣️ F.R.I.D.A.Y. AI Commands

Chat with F.R.I.D.A.Y. in the AI panel:

| Command | Description |
|---------|-------------|
| `generate mark1` | Create Mark-1 Jaeger template |
| `generate mark3` | Create Mark-3 Jaeger template |
| `generate mark5` | Create Mark-5 Jaeger template |
| `generate brawler` | Create melee-focused Jaeger |
| `generate ranger` | Create speed-focused Jaeger |
| `analyze` | Analyze current mech design |
| `optimize` | Get optimization suggestions |
| `reactor info` | Learn about fusion reactors |
| `hydraulic info` | Learn about hydraulic systems |
| `help` | Get design tips |

### 📊 System Monitors

- **Reactor**: Plasma temp, power output, fuel levels, stability
- **Power Grid**: Load percentage, voltage, current draw
- **Supercomputer**: CPU load, neural sync, system status

---

## 🔧 Development

### Project Structure
```
jaeger-builder/
├── pom.xml                      # Maven configuration
├── src/main/java/com/jaeger/    # Source code
│   ├── core/                    # Vector3, Transform, Part
│   ├── reactor/                 # Fusion & Fission reactors
│   ├── hydraulics/              # Muscles & actuators
│   ├── electrical/              # Power & supercomputer
│   ├── mech/                    # Chassis, limbs, weapons
│   ├── ai/                      # F.R.I.D.A.Y. assistant
│   └── ui/                      # JavaFX interface
└── src/main/resources/          # Stylesheets
```

### Build from Source

```bash
cd jaeger-builder
mvn clean compile    # Compile only
mvn clean package    # Build JAR
mvn clean install    # Install to local repo
```

---

## 📄 License

MIT License - See individual project directories for details.

---

**"Today we cancel the apocalypse!"**

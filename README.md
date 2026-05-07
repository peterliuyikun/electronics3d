# Electronics 3D

A collection of 3D electronics and engineering simulation tools.

---

## 🎮 Jaeger Builder 3D

**Build and simulate Pacific Rim-style Jaegers with realistic nuclear reactors, hydraulic systems, and neural interfaces.**

### ⬇️ Download & Run (Java 21 Required)

[![Download JAR](https://img.shields.io/badge/Download-JAR-blue?style=for-the-badge&logo=java)](https://github.com/peterliuyikun/electronics3d/releases/latest/download/jaeger-builder-1.0.0.jar)

**Quick Start:**
```bash
# Download the JAR (click button above or use wget)
wget https://github.com/peterliuyikun/electronics3d/releases/latest/download/jaeger-builder-1.0.0.jar

# Run with Java 21
java -jar jaeger-builder-1.0.0.jar
```

**Or build from source:**
```bash
git clone https://github.com/peterliuyikun/electronics3d.git
cd electronics3d/jaeger-builder
mvn clean package
java -jar target/jaeger-builder-1.0.0.jar
```

---

### ⚡ System Requirements

| Requirement | Minimum |
|-------------|---------|
| **Java** | 21 or higher |
| **RAM** | 4GB |
| **Storage** | 100MB |
| **Graphics** | OpenGL-compatible |

**Check your Java version:**
```bash
java --version
```

If you don't have Java 21, download it from:
- [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [OpenJDK 21](https://jdk.java.net/21/)

---

### 🚀 Features

| System | Description |
|--------|-------------|
| 🔋 **Fusion Reactor** | Tokamak with 150M K plasma, magnetic confinement, SCRAM |
| ⚛️ **Fission Reactor** | Control rods, neutron flux, xenon poisoning, decay heat |
| 💪 **Hydraulics** | Muscles (350 bar) and actuators with realistic force curves |
| 🧠 **Supercomputer** | 1 exaFLOP, neural link, balance/targeting/movement processors |
| 🤖 **F.R.I.D.A.Y. AI** | Built-in assistant for model generation and analysis |
| ⚡ **Power Grid** | Hierarchical buses, circuit breakers, wiring simulation |

---

### 🖥️ Interface Preview

```
╔══════════════════════════════════════════════════════════════╗
║  File  Edit  View  Simulate                                  ║
╠══════════╦══════════════════════════════╦════════════════════╣
║          ║                              ║                    ║
║  PART    ║                              ║   PROPERTIES       ║
║  CATALOG ║      3D VIEWPORT             ║                    ║
║          ║                              ║   (selected part)  ║
║  •Torso  ║                              ║                    ║
║  •Reactor║                              ║                    ║
║  •Weapons║                              ║                    ║
║          ║                              ║                    ║
╠══════════╩══════════════════════════════╩════════════════════╣
║  [REACTOR] [POWER GRID] [SUPERCOMPUTER] [◉ F.R.I.D.A.Y.]     ║
║                                                              ║
║  F.R.I.D.A.Y.: Good day. I'm your Jaeger construction        ║
║  assistant. Type 'help' for commands.                        ║
╚══════════════════════════════════════════════════════════════╝
```

---

### 🗣️ F.R.I.D.A.Y. AI Commands

Chat with F.R.I.D.A.Y. in the AI panel:

| Command | Description |
|---------|-------------|
| `generate mark1` | Create Mark-1 Jaeger (2,500 tons) |
| `generate mark3` | Create Mark-3 Jaeger (2,400 tons) |
| `generate mark5` | Create Mark-5 Jaeger (1,800 tons) |
| `generate brawler` | Create melee-focused Jaeger |
| `generate ranger` | Create speed-focused Jaeger |
| `analyze` | Analyze current mech design |
| `optimize` | Get optimization suggestions |
| `reactor info` | Learn about fusion reactors |
| `hydraulic info` | Learn about hydraulic systems |
| `pilot info` | Learn about neural links |
| `help` | Get design tips |

---

### 📊 System Monitors

- **Reactor Tab**: Plasma temp (0-150M K), power output (0-1000 MW), fuel levels
- **Power Grid Tab**: Load %, voltage, current draw, breaker status
- **Supercomputer Tab**: CPU load, neural sync %, pilot connection status

---

### 🔧 Development

**Build from source:**
```bash
cd jaeger-builder
mvn clean compile    # Compile only
mvn clean package    # Build JAR
mvn clean install    # Install to local repo
```

**Project Structure:**
```
jaeger-builder/
├── pom.xml                      # Maven configuration
├── src/main/java/com/jaeger/    # Source code (22 files, ~7,000 LOC)
│   ├── core/                    # Vector3, Transform, Part
│   ├── reactor/                 # Fusion & Fission reactors
│   ├── hydraulics/              # Muscles & actuators
│   ├── electrical/              # Power & supercomputer
│   ├── mech/                    # Chassis, limbs, weapons
│   ├── ai/                      # F.R.I.D.A.Y. assistant
│   └── ui/                      # JavaFX interface
└── src/main/resources/          # Stylesheets
```

---

### 📄 License

MIT License - See LICENSE file

---

**"Today we cancel the apocalypse!"** 🦾

package com.jaeger.ui;

import com.jaeger.core.Part;
import com.jaeger.core.PartType;
import com.jaeger.mech.*;
import com.jaeger.reactor.FusionReactor;
import com.jaeger.electrical.Supercomputer;
import com.jaeger.hydraulics.HydraulicMuscle;
import com.jaeger.hydraulics.HydraulicActuator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MainWindow extends Application {
    
    private Mech currentMech;
    private Part selectedPart;
    private SimulationEngine simulation;
    
    // UI Components
    private BorderPane mainLayout;
    private VBox partBrowser;
    private VBox propertyPanel;
    private TabPane bottomPanel;
    private Label statusLabel;
    private Label mechInfoLabel;
    
    // System monitors
    private ProgressBar reactorTempBar;
    private ProgressBar reactorOutputBar;
    private ProgressBar powerLoadBar;
    private ProgressBar neuralSyncBar;
    private Label reactorStatusLabel;
    private Label computerStatusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Jaeger Builder 3D");
        
        // Initialize simulation
        simulation = new SimulationEngine();
        simulation.start();
        
        // Create UI
        createMenuBar();
        createMainLayout();
        createPartBrowser();
        createPropertyPanel();
        createBottomPanel();
        createStatusBar();
        
        // Create default mech
        createDefaultMech();
        
        Scene scene = new Scene(mainLayout, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/styles/dark-theme.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Start update timer
        startUpdateTimer();
    }
    
    private void createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New Mech");
        newItem.setOnAction(e -> createNewMech());
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(newItem, openItem, saveItem, new SeparatorMenuItem(), exitItem);
        
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        MenuItem deleteItem = new MenuItem("Delete");
        editMenu.getItems().addAll(undoItem, redoItem, deleteItem);
        
        // View Menu
        Menu viewMenu = new Menu("View");
        MenuItem resetCameraItem = new MenuItem("Reset Camera");
        MenuItem wireframeItem = new MenuItem("Wireframe Mode");
        viewMenu.getItems().addAll(resetCameraItem, wireframeItem);
        
        // Simulate Menu
        Menu simulateMenu = new Menu("Simulate");
        MenuItem startSimItem = new MenuItem("Start");
        MenuItem pauseSimItem = new MenuItem("Pause");
        MenuItem resetSimItem = new MenuItem("Reset");
        simulateMenu.getItems().addAll(startSimItem, pauseSimItem, resetSimItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, simulateMenu);
        
        mainLayout = new BorderPane();
        mainLayout.setTop(menuBar);
    }
    
    private void createMainLayout() {
        // Center: 3D Viewport (placeholder)
        StackPane viewport = new StackPane();
        viewport.setStyle("-fx-background-color: #1a1a2e;");
        
        Label placeholderLabel = new Label("3D Viewport\n(OpenGL/LWJGL Integration)");
        placeholderLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 24));
        placeholderLabel.setTextFill(Color.web("#00ff00"));
        viewport.getChildren().add(placeholderLabel);
        
        mainLayout.setCenter(viewport);
    }
    
    private void createPartBrowser() {
        partBrowser = new VBox(10);
        partBrowser.setPadding(new Insets(10));
        partBrowser.setStyle("-fx-background-color: #16213e;");
        partBrowser.setPrefWidth(250);
        
        Label title = new Label("PART CATALOG");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#00ff00"));
        
        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search parts...");
        searchField.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white;");
        
        // Part categories
        Accordion categories = new Accordion();
        
        // Chassis
        TitledPane chassisPane = createPartCategory("CHASSIS", new String[]{
            "Torso", "Hip Assembly", "Shoulder Mount", "Conn-Pod"
        });
        
        // Limbs
        TitledPane limbsPane = createPartCategory("LIMBS", new String[]{
            "Upper Arm", "Forearm", "Thigh", "Shin", "Foot", "Hand"
        });
        
        // Systems
        TitledPane systemsPane = createPartCategory("SYSTEMS", new String[]{
            "Fusion Reactor", "Fission Reactor", "Hydraulic Pump", 
            "Supercomputer", "Cooling Tower"
        });
        
        // Weapons
        TitledPane weaponsPane = createPartCategory("WEAPONS", new String[]{
            "Plasma Caster", "Chain Sword", "Rocket Pod", "Railgun"
        });
        
        // Actuators
        TitledPane actuatorsPane = createPartCategory("ACTUATORS", new String[]{
            "Hydraulic Muscle", "Linear Piston", "Rotary Vane"
        });
        
        categories.getPanes().addAll(chassisPane, limbsPane, systemsPane, weaponsPane, actuatorsPane);
        
        partBrowser.getChildren().addAll(title, searchField, categories);
        
        mainLayout.setLeft(partBrowser);
    }
    
    private TitledPane createPartCategory(String title, String[] parts) {
        VBox content = new VBox(5);
        content.setPadding(new Insets(5));
        
        for (String part : parts) {
            Button partButton = new Button(part);
            partButton.setMaxWidth(Double.MAX_VALUE);
            partButton.setStyle("-fx-background-color: #0f3460; -fx-text-fill: #00ff00;" +
                               "-fx-font-family: monospace;");
            partButton.setOnAction(e -> addPartToMech(part));
            content.getChildren().add(partButton);
        }
        
        TitledPane pane = new TitledPane(title, content);
        pane.setStyle("-fx-text-fill: #00ff00; -fx-font-family: monospace;");
        return pane;
    }
    
    private void createPropertyPanel() {
        propertyPanel = new VBox(10);
        propertyPanel.setPadding(new Insets(10));
        propertyPanel.setStyle("-fx-background-color: #16213e;");
        propertyPanel.setPrefWidth(300);
        
        Label title = new Label("PROPERTIES");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#00ff00"));
        
        // Placeholder for selected part properties
        Label noSelection = new Label("No part selected");
        noSelection.setTextFill(Color.GRAY);
        
        propertyPanel.getChildren().addAll(title, noSelection);
        
        mainLayout.setRight(propertyPanel);
    }
    
    private void createBottomPanel() {
        bottomPanel = new TabPane();
        bottomPanel.setPrefHeight(200);
        bottomPanel.setStyle("-fx-background-color: #16213e;");
        
        // Reactor Monitor Tab
        Tab reactorTab = new Tab("REACTOR");
        reactorTab.setClosable(false);
        reactorTab.setContent(createReactorMonitor());
        
        // Power Grid Tab
        Tab powerTab = new Tab("POWER GRID");
        powerTab.setClosable(false);
        powerTab.setContent(createPowerMonitor());
        
        // Computer Tab
        Tab computerTab = new Tab("SUPERCOMPUTER");
        computerTab.setClosable(false);
        computerTab.setContent(createComputerMonitor());
        
        // F.R.I.D.A.Y. AI Tab
        Tab fridayTab = new Tab("◉ F.R.I.D.A.Y.");
        fridayTab.setClosable(false);
        FridayChatPanel fridayPanel = new FridayChatPanel(this);
        fridayTab.setContent(fridayPanel);
        
        bottomPanel.getTabs().addAll(reactorTab, powerTab, computerTab, fridayTab);
        
        mainLayout.setBottom(bottomPanel);
    }
    
    private VBox createReactorMonitor() {
        VBox monitor = new VBox(10);
        monitor.setPadding(new Insets(10));
        monitor.setStyle("-fx-background-color: #0a0a1a;");
        
        HBox statusRow = new HBox(20);
        reactorStatusLabel = new Label("STATUS: OFFLINE");
        reactorStatusLabel.setTextFill(Color.web("#ff0000"));
        reactorStatusLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        statusRow.getChildren().add(reactorStatusLabel);
        
        // Plasma temperature
        HBox tempRow = new HBox(10);
        Label tempLabel = new Label("PLASMA TEMP:");
        tempLabel.setTextFill(Color.web("#00ff00"));
        tempLabel.setFont(Font.font("Monospace", 12));
        reactorTempBar = new ProgressBar(0);
        reactorTempBar.setPrefWidth(300);
        reactorTempBar.setStyle("-fx-accent: #ff6600;");
        tempRow.getChildren().addAll(tempLabel, reactorTempBar);
        
        // Power output
        HBox outputRow = new HBox(10);
        Label outputLabel = new Label("OUTPUT:");
        outputLabel.setTextFill(Color.web("#00ff00"));
        outputLabel.setFont(Font.font("Monospace", 12));
        reactorOutputBar = new ProgressBar(0);
        reactorOutputBar.setPrefWidth(300);
        reactorOutputBar.setStyle("-fx-accent: #00ff00;");
        outputRow.getChildren().addAll(outputLabel, reactorOutputBar);
        
        // Fuel levels
        HBox fuelRow = new HBox(20);
        Label fuelLabel = new Label("FUEL: D:100% T:100%");
        fuelLabel.setTextFill(Color.web("#00ff00"));
        fuelLabel.setFont(Font.font("Monospace", 12));
        fuelRow.getChildren().add(fuelLabel);
        
        monitor.getChildren().addAll(statusRow, tempRow, outputRow, fuelRow);
        return monitor;
    }
    
    private VBox createPowerMonitor() {
        VBox monitor = new VBox(10);
        monitor.setPadding(new Insets(10));
        monitor.setStyle("-fx-background-color: #0a0a1a;");
        
        HBox loadRow = new HBox(10);
        Label loadLabel = new Label("GRID LOAD:");
        loadLabel.setTextFill(Color.web("#00ff00"));
        loadLabel.setFont(Font.font("Monospace", 12));
        powerLoadBar = new ProgressBar(0);
        powerLoadBar.setPrefWidth(400);
        powerLoadBar.setStyle("-fx-accent: #00ff00;");
        loadRow.getChildren().addAll(loadLabel, powerLoadBar);
        
        Label detailsLabel = new Label("Main Bus: 1000V | Available: 0 MW | Draw: 0 MW");
        detailsLabel.setTextFill(Color.web("#00ff00"));
        detailsLabel.setFont(Font.font("Monospace", 11));
        
        monitor.getChildren().addAll(loadRow, detailsLabel);
        return monitor;
    }
    
    private VBox createComputerMonitor() {
        VBox monitor = new VBox(10);
        monitor.setPadding(new Insets(10));
        monitor.setStyle("-fx-background-color: #0a0a1a;");
        
        HBox statusRow = new HBox(20);
        computerStatusLabel = new Label("STATUS: OFFLINE");
        computerStatusLabel.setTextFill(Color.web("#ff0000"));
        computerStatusLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 14));
        statusRow.getChildren().add(computerStatusLabel);
        
        // Neural sync
        HBox syncRow = new HBox(10);
        Label syncLabel = new Label("NEURAL SYNC:");
        syncLabel.setTextFill(Color.web("#00ff00"));
        syncLabel.setFont(Font.font("Monospace", 12));
        neuralSyncBar = new ProgressBar(0);
        neuralSyncBar.setPrefWidth(300);
        neuralSyncBar.setStyle("-fx-accent: #ff00ff;");
        syncRow.getChildren().addAll(syncLabel, neuralSyncBar);
        
        // System load
        HBox loadRow = new HBox(10);
        Label loadLabel = new Label("CPU LOAD:");
        loadLabel.setTextFill(Color.web("#00ff00"));
        loadLabel.setFont(Font.font("Monospace", 12));
        ProgressBar cpuBar = new ProgressBar(0);
        cpuBar.setPrefWidth(300);
        cpuBar.setStyle("-fx-accent: #00ffff;");
        loadRow.getChildren().addAll(loadLabel, cpuBar);
        
        monitor.getChildren().addAll(statusRow, syncRow, loadRow);
        return monitor;
    }
    
    private void createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #0a0a1a;" +
                         "-fx-border-color: #00ff00;" +
                         "-fx-border-width: 1px 0 0 0;");
        
        statusLabel = new Label("Ready");
        statusLabel.setTextFill(Color.web("#00ff00"));
        statusLabel.setFont(Font.font("Monospace", 11));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        mechInfoLabel = new Label("No Jaeger loaded");
        mechInfoLabel.setTextFill(Color.web("#00ff00"));
        mechInfoLabel.setFont(Font.font("Monospace", 11));
        
        statusBar.getChildren().addAll(statusLabel, spacer, mechInfoLabel);
        
        mainLayout.setBottom(new VBox(bottomPanel, statusBar));
    }
    
    private void createDefaultMech() {
        currentMech = new Mech("Gipsy Danger", "Mark-3");
        
        // Create basic structure
        ChassisPart torso = new ChassisPart("Torso", PartType.TORSO, 500000, 200);
        currentMech.setRootPart(torso);
        
        // Install reactor
        FusionReactor reactor = new FusionReactor("Primary Reactor");
        currentMech.installReactor(reactor);
        
        // Install supercomputer
        Supercomputer computer = new Supercomputer("Main Computer");
        currentMech.installSupercomputer(computer);
        
        // Add pilots
        currentMech.addPilot("Raleigh Becket", 95);
        currentMech.addPilot("Mako Mori", 92);
        
        updateMechInfo();
        
        // Update F.R.I.D.A.Y. with current mech
        for (javafx.scene.control.Tab tab : bottomPanel.getTabs()) {
            if (tab.getContent() instanceof FridayChatPanel) {
                ((FridayChatPanel) tab.getContent()).setCurrentMech(currentMech);
            }
        }
    }
    
    private void createNewMech() {
        // Dialog for new mech
        TextInputDialog dialog = new TextInputDialog("New Jaeger");
        dialog.setTitle("New Jaeger");
        dialog.setHeaderText("Create New Jaeger");
        dialog.setContentText("Enter Jaeger name:");
        
        dialog.showAndWait().ifPresent(name -> {
            currentMech = new Mech(name, "Custom");
            ChassisPart torso = new ChassisPart("Torso", PartType.TORSO, 500000, 200);
            currentMech.setRootPart(torso);
            updateMechInfo();
            statusLabel.setText("Created new Jaeger: " + name);
        });
    }
    
    private void addPartToMech(String partName) {
        if (currentMech == null) {
            statusLabel.setText("Create a Jaeger first!");
            return;
        }
        
        statusLabel.setText("Adding part: " + partName);
        // Part addition logic would go here
    }
    
    private void updateMechInfo() {
        if (currentMech != null) {
            mechInfoLabel.setText(String.format("%s | Mass: %.1f tons | Status: %s",
                currentMech.getName(),
                currentMech.getTotalMass() / 1000,
                currentMech.getStatus().getDisplay()));
        }
    }
    
    private void startUpdateTimer() {
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentMech != null) {
                    currentMech.update(0.016); // ~60fps
                    updateUI();
                }
            }
        };
        timer.start();
    }
    
    private void updateUI() {
        if (currentMech == null) return;
        
        // Update reactor display
        if (currentMech.getPrimaryReactor() != null) {
            FusionReactor reactor = currentMech.getPrimaryReactor();
            reactorStatusLabel.setText("STATUS: " + reactor.getState().getDisplay());
            reactorStatusLabel.setTextFill(switch (reactor.getState()) {
                case OFFLINE, SCRAM -> Color.web("#ff0000");
                case STARTING, HEATING -> Color.web("#ffaa00");
                case SUSTAINED -> Color.web("#00ff00");
                case EMERGENCY -> Color.web("#ff00ff");
                default -> Color.web("#00aaff");
            });
            
            double tempRatio = reactor.getPlasmaTemperature() / 150_000_000;
            reactorTempBar.setProgress(Math.min(1.0, tempRatio));
            
            double outputRatio = reactor.getElectricalOutput() / 1000;
            reactorOutputBar.setProgress(Math.min(1.0, outputRatio));
        }
        
        // Update computer display
        if (currentMech.getSupercomputer() != null) {
            Supercomputer computer = currentMech.getSupercomputer();
            computerStatusLabel.setText("STATUS: " + computer.getState().getDisplay());
            computerStatusLabel.setTextFill(switch (computer.getState()) {
                case OFFLINE, EMERGENCY_SHUTDOWN -> Color.web("#ff0000");
                case BOOTING -> Color.web("#ffaa00");
                case ONLINE -> Color.web("#00aaff");
                case NEURAL_LINK_ACTIVE -> Color.web("#00ff00");
                case DRIFT_MODE -> Color.web("#ff00ff");
                default -> Color.web("#ffffff");
            });
            
            neuralSyncBar.setProgress(currentMech.getSupercomputer().getNeuralSyncLevel() / 100);
        }
        
        // Update mech info
        updateMechInfo();
    }
    
    public Mech getCurrentMech() {
        return currentMech;
    }
    
    public void setCurrentMech(Mech mech) {
        this.currentMech = mech;
        updateMechInfo();
    }
}
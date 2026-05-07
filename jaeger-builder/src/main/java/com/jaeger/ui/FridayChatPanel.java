package com.jaeger.ui;

import com.jaeger.ai.FridayAI;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class FridayChatPanel extends VBox {
    
    private FridayAI friday;
    private TextFlow chatHistory;
    private TextField inputField;
    private ScrollPane scrollPane;
    private MainWindow mainWindow;
    
    private static final String FRIDAY_COLOR = "#00ff00";
    private static final String USER_COLOR = "#00aaff";
    private static final String SYSTEM_COLOR = "#ffaa00";
    
    public FridayChatPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.friday = new FridayAI();
        
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #0a0a1a;" +
                "-fx-border-color: #00ff00;" +
                "-fx-border-width: 1px;");
        setPrefWidth(350);
        
        // Header
        HBox header = createHeader();
        
        // Chat history
        chatHistory = new TextFlow();
        chatHistory.setStyle("-fx-background-color: #0f0f1f;" +
                           "-fx-padding: 10px;");
        chatHistory.setPrefHeight(400);
        
        scrollPane = new ScrollPane(chatHistory);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0f0f1f;" +
                          "-fx-control-inner-background: #0f0f1f;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Input area
        HBox inputArea = createInputArea();
        
        // Quick commands
        FlowPane quickCommands = createQuickCommands();
        
        getChildren().addAll(header, scrollPane, inputArea, quickCommands);
        
        // Initial greeting
        addFridayMessage(friday.greet());
        
        // Focus input
        Platform.runLater(() -> inputField.requestFocus());
    }
    
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("◉ F.R.I.D.A.Y.");
        title.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        title.setTextFill(Color.web(FRIDAY_COLOR));
        
        Label status = new Label("ONLINE");
        status.setFont(Font.font("Monospace", FontWeight.NORMAL, 10));
        status.setTextFill(Color.web(FRIDAY_COLOR));
        status.setStyle("-fx-background-color: #003300;" +
                       "-fx-padding: 2px 8px;" +
                       "-fx-background-radius: 3px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button clearBtn = new Button("✕");
        clearBtn.setStyle("-fx-background-color: transparent;" +
                        "-fx-text-fill: #666;" +
                        "-fx-font-size: 12px;");
        clearBtn.setOnAction(e -> clearChat());
        
        header.getChildren().addAll(title, status, spacer, clearBtn);
        return header;
    }
    
    private HBox createInputArea() {
        HBox inputArea = new HBox(5);
        inputArea.setAlignment(Pos.CENTER_LEFT);
        
        inputField = new TextField();
        inputField.setPromptText("Ask F.R.I.D.A.Y...");
        inputField.setStyle("-fx-background-color: #1a1a2e;" +
                          "-fx-text-fill: white;" +
                          "-fx-prompt-text-fill: #666;" +
                          "-fx-font-family: monospace;" +
                          "-fx-border-color: #333;" +
                          "-fx-border-radius: 3px;");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        
        inputField.setOnAction(e -> sendMessage());
        
        Button sendBtn = new Button("→");
        sendBtn.setStyle("-fx-background-color: #00ff00;" +
                       "-fx-text-fill: black;" +
                       "-fx-font-weight: bold;" +
                       "-fx-font-family: monospace;");
        sendBtn.setOnAction(e -> sendMessage());
        
        inputArea.getChildren().addAll(inputField, sendBtn);
        return inputArea;
    }
    
    private FlowPane createQuickCommands() {
        FlowPane commands = new FlowPane(5, 5);
        commands.setAlignment(Pos.CENTER_LEFT);
        
        String[] quickCmds = {
            "Generate Mark-3", "Analyze", "Optimize", 
            "Reactor Info", "Status", "Help"
        };
        
        for (String cmd : quickCmds) {
            Button btn = new Button(cmd);
            btn.setStyle("-fx-background-color: #1a1a3e;" +
                       "-fx-text-fill: #00ff00;" +
                       "-fx-font-family: monospace;" +
                       "-fx-font-size: 10px;" +
                       "-fx-padding: 4px 8px;" +
                       "-fx-border-color: #00ff00;" +
                       "-fx-border-width: 1px;");
            btn.setOnAction(e -> {
                inputField.setText(cmd.toLowerCase());
                sendMessage();
            });
            commands.getChildren().add(btn);
        }
        
        return commands;
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;
        
        addUserMessage(message);
        inputField.clear();
        
        // Process with F.R.I.D.A.Y.
        String response = friday.processCommand(message);
        
        // Add slight delay for realism
        javafx.animation.PauseTransition pause = 
            new javafx.animation.PauseTransition(Duration.millis(500));
        pause.setOnFinished(e -> addFridayMessage(response));
        pause.play();
    }
    
    private void addUserMessage(String message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        Text timeText = new Text("[" + timestamp + "] ");
        timeText.setFill(Color.web("#666"));
        timeText.setFont(Font.font("Monospace", 10));
        
        Text userText = new Text("YOU: ");
        userText.setFill(Color.web(USER_COLOR));
        userText.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        
        Text msgText = new Text(message + "\n\n");
        msgText.setFill(Color.web(USER_COLOR));
        msgText.setFont(Font.font("Monospace", 11));
        
        chatHistory.getChildren().addAll(timeText, userText, msgText);
        scrollToBottom();
    }
    
    private void addFridayMessage(String message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        Text timeText = new Text("[" + timestamp + "] ");
        timeText.setFill(Color.web("#666"));
        timeText.setFont(Font.font("Monospace", 10));
        
        Text fridayText = new Text("FRIDAY: ");
        fridayText.setFill(Color.web(FRIDAY_COLOR));
        fridayText.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        
        Text msgText = new Text(message + "\n\n");
        msgText.setFill(Color.web(FRIDAY_COLOR));
        msgText.setFont(Font.font("Monospace", 11));
        
        chatHistory.getChildren().addAll(timeText, fridayText, msgText);
        scrollToBottom();
        
        // Fade in animation
        FadeTransition fade = new FadeTransition(Duration.millis(300), chatHistory);
        fade.setFromValue(0.8);
        fade.setToValue(1.0);
        fade.play();
    }
    
    private void addSystemMessage(String message) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        Text timeText = new Text("[" + timestamp + "] ");
        timeText.setFill(Color.web("#666"));
        timeText.setFont(Font.font("Monospace", 10));
        
        Text sysText = new Text("SYSTEM: ");
        sysText.setFill(Color.web(SYSTEM_COLOR));
        sysText.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        
        Text msgText = new Text(message + "\n\n");
        msgText.setFill(Color.web(SYSTEM_COLOR));
        msgText.setFont(Font.font("Monospace", 11));
        
        chatHistory.getChildren().addAll(timeText, sysText, msgText);
        scrollToBottom();
    }
    
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
    
    private void clearChat() {
        chatHistory.getChildren().clear();
        friday.clearHistory();
        addSystemMessage("Chat history cleared.");
        addFridayMessage(friday.greet());
    }
    
    public void setCurrentMech(com.jaeger.mech.Mech mech) {
        friday.setCurrentMech(mech);
    }
    
    public FridayAI getFriday() {
        return friday;
    }
}
package com.jaeger;

import com.jaeger.ui.MainWindow;
import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           JAEGER BUILDER 3D - System Boot                    ║");
        System.out.println("║           Java 21 | LWJGL 3.3.3 | JavaFX 21                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        Application.launch(MainWindow.class, args);
    }
}

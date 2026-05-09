package com.decroly.todotabla.model;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Window;

import java.io.IOException;

public class WindowWatcher {
    public static void init() {

        Window.getWindows().addListener((javafx.collections.ListChangeListener<Window>) change -> {

            boolean hayVentanasAbiertas = false;

            for (Window w : Window.getWindows()) {
                if (w.isShowing()) {
                    hayVentanasAbiertas = true;
                    break;
                }
            }

            if (!hayVentanasAbiertas) {
                System.out.println("No hay ventanas abiertas");
                Platform.exit();
            }
        });

    }
}

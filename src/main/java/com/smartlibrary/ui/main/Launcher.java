package com.smartlibrary.ui.main;

/**
 * Punto de entrada real del jar empaquetado.
 *
 * Esta clase NO extiende javafx.application.Application a proposito. Desde
 * Java 11+, si la clase indicada como Main-Class en el jar extiende
 * Application directamente, el comando "java -jar" se niega a iniciarla con
 * el error "JavaFX runtime components are missing, and are required to run
 * this application" -- incluso si JavaFX ya esta empaquetado dentro del jar.
 *
 * Al usar una clase intermedia que solo llama a App.main(), se evita esa
 * verificacion y la aplicacion arranca normalmente con:
 *     java -jar SmartLibraryFX-1.0.jar
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}

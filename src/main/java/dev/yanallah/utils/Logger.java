package dev.yanallah.utils;

public class Logger {
    // Constantes pour les couleurs ANSI
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    private final String prefix;

    /**
     * Constructeur du logger avec un préfixe
     *
     * @param prefix Le préfixe à utiliser pour les logs
     */
    public Logger(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Log un message avec le niveau DEBUG
     *
     * @param message Le message à logger
     */
    public void debug(String message) {
        log(Level.DEBUG, message, BLUE);
    }

    /**
     * Log un message avec le niveau INFO
     *
     * @param message Le message à logger
     */
    public void info(String message) {
        log(Level.INFO, message, GREEN);
    }

    /**
     * Log un message avec le niveau WARNING
     *
     * @param message Le message à logger
     */
    public void warning(String message) {
        log(Level.WARNING, message, YELLOW);
    }

    /**
     * Log un message avec le niveau ERROR
     *
     * @param message Le message à logger
     */
    public void error(String message) {
        log(Level.ERROR, message, RED);
    }

    /**
     * Log un message avec le niveau FATAL
     *
     * @param message Le message à logger
     */
    public void fatal(String message) {
        log(Level.FATAL, message, PURPLE);
    }

    /**
     * Méthode interne pour loguer un message avec un niveau et une couleur
     *
     * @param level   Le niveau de log
     * @param message Le message à logger
     * @param color   La couleur ANSI à utiliser
     */
    private void log(Level level, String message, String color) {
        System.out.println(color + "[" + prefix + "] [" + level + "] " + message + RESET);
    }

    // Niveaux de log
    public enum Level {
        DEBUG, INFO, WARNING, ERROR, FATAL
    }
}
package dev.yanallah.models;

public enum OrderStatus {
    CREATED("Créée (pas payée)"),
    PREPARING("En préparation (payée)"),
    SENT("Envoyée"),
    RECEIVED("Reçue"),
    CANCELLED("Annulée");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public static OrderStatus fromName(String name) {
        return OrderStatus.valueOf(name.toUpperCase());
    }

    public String getDisplayName() {
        return displayName;
    }
} 
package com.stefancooper.SpigotUHC.resources;

public enum DeathAction {
    SPECTATE("spectate"),
    KICK("kick");

    public final String name;

    DeathAction(String name) {
        this.name = name;
    }

    public static DeathAction fromString(String deathAction) {
        for (DeathAction Key : DeathAction.values()) {
            if (Key.name.equalsIgnoreCase(deathAction)) {
                return Key;
            }
        }
        return null;
    }
}

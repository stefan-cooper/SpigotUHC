package com.stefancooper.SpigotUHC.enums;

public enum PerformanceTrackingEvent {
    KILL("kills"),
    DEATH("deaths"),
    DAMAGE_DEALT("damage_dealt"),
    PVE_DAMAGE("pve_damage"),
    GOLD_ORE_MINED("gold_ore_mined"),
    LOOT_CHEST_CLAIMED("loot_chests_claimed"),
    RANKING("ranking");

    public final String name;

    PerformanceTrackingEvent(String name) {
        this.name = name;
    }

    public static PerformanceTrackingEvent fromString(String deathAction) {
        for (PerformanceTrackingEvent Key : PerformanceTrackingEvent.values()) {
            if (Key.name.equalsIgnoreCase(deathAction)) {
                return Key;
            }
        }
        return null;
    }
}

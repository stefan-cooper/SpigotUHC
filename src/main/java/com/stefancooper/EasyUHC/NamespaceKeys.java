package com.stefancooper.EasyUHC;

import org.bukkit.NamespacedKey;

import static com.stefancooper.EasyUHC.utils.Constants.BLASTWAVE_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.CRAFTABLE_PLAYER_HEAD;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_UPGRADE_AVAILABLE_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_UPGRADE_TOTAL_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_UPGRADE_TYPE_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_USER_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.EVOLVING_SHIELD_XP_KEY;
import static com.stefancooper.EasyUHC.utils.Constants.NOTCH_APPLE;
import static com.stefancooper.EasyUHC.utils.Constants.PLAYER_HEAD;
import static com.stefancooper.EasyUHC.utils.Constants.QUICKBOOM_ENCHANTMENT;
import static com.stefancooper.EasyUHC.utils.Constants.SPIGOT_NAMESPACE;

public class NamespaceKeys {

    final private NamespacedKey playerHead;
    final private NamespacedKey craftablePlayerHead;
    final private NamespacedKey notchApple;
    final private NamespacedKey quickboomEnchantment;
    final private NamespacedKey blastwaveEnchantment;
    final private NamespacedKey evolvingShieldUserKey;
    final private NamespacedKey evolvingShieldXPKey;
    final private NamespacedKey evolvingShieldUpgradeTypeKey;
    final private NamespacedKey evolvingShieldUpgradeReadyKey;
    final private NamespacedKey evolvingShieldUpgradeStageKey;

    public NamespaceKeys(final Config config) {
        // Misc
        this.playerHead = new NamespacedKey(config.getPlugin(), PLAYER_HEAD);
        this.craftablePlayerHead = new NamespacedKey(config.getPlugin(), CRAFTABLE_PLAYER_HEAD);
        this.notchApple = new NamespacedKey(config.getPlugin(), NOTCH_APPLE);

        // Enchantments
        this.quickboomEnchantment = new NamespacedKey(SPIGOT_NAMESPACE, QUICKBOOM_ENCHANTMENT);
        this.blastwaveEnchantment = new NamespacedKey(SPIGOT_NAMESPACE, BLASTWAVE_ENCHANTMENT);

        // Evolving shield
        this.evolvingShieldUserKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_USER_KEY);
        this.evolvingShieldXPKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_XP_KEY);
        this.evolvingShieldUpgradeTypeKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_UPGRADE_TYPE_KEY);
        this.evolvingShieldUpgradeReadyKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_UPGRADE_AVAILABLE_KEY);
        this.evolvingShieldUpgradeStageKey = new NamespacedKey(SPIGOT_NAMESPACE, EVOLVING_SHIELD_UPGRADE_TOTAL_KEY);
    }

    public NamespacedKey getPlayerHeadKey() {
        return playerHead;
    }

    public NamespacedKey getCraftablePlayerHeadKey() {
        return craftablePlayerHead;
    }

    public NamespacedKey getNotchAppleKey() {
        return notchApple;
    }

    public NamespacedKey getQuickboomEnchantment() { return quickboomEnchantment; }

    public NamespacedKey getBlastwaveEnchantment() { return blastwaveEnchantment; }

    public NamespacedKey getEvolvingShieldUserKey() {
        return evolvingShieldUserKey;
    }

    public NamespacedKey getEvolvingShieldXPKey() {
        return evolvingShieldXPKey;
    }

    public NamespacedKey getEvolvingShieldUpgradeTypeKey() {
        return evolvingShieldUpgradeTypeKey;
    }

    public NamespacedKey getEvolvingShieldUpgradeReadyKey() {
        return evolvingShieldUpgradeReadyKey;
    }

    public NamespacedKey getEvolvingShieldUpgradeStageKey() {
        return evolvingShieldUpgradeStageKey;
    }

}

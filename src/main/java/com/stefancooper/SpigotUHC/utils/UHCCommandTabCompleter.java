package com.stefancooper.SpigotUHC.utils;

import com.stefancooper.SpigotUHC.enums.ConfigKey;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UHCCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("uhc")) {
            // First argument
            if (args.length == 1) {
                suggestions.addAll(Arrays.asList("set", "view", "start", "resume", "latestart", "cancel", "pvp"));
            }
            // Second argument for "set" command
            else if (args.length >= 2 && args[0].equalsIgnoreCase("set")) {
                List<String> configKeys = Arrays.stream(ConfigKey.values()).toList().stream().map((key) -> key.configName + "=").toList();
                suggestions.addAll(configKeys);
            }
        }

        // Filter suggestions based on the last argument typed
        List<String> filteredSuggestions = new ArrayList<>();
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                filteredSuggestions.add(suggestion);
            }
        }

        return filteredSuggestions;
    }
}

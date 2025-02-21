package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.stefancooper.SpigotUHC.Config;
import org.bukkit.inventory.ItemStack;

public class StartCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "start";

    public StartCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    private World getWorld (String name) {
        final String worldName = name != null ? name : "world";
        if (Bukkit.getWorld(worldName) == null) return Bukkit.createWorld(WorldCreator.name(worldName).environment(World.Environment.NORMAL));
        else return Bukkit.getWorld(worldName);
    }

    @Override
    public void execute() {
        final World world = getWorld("world");
        final int chestX = 0;
        final int chestY = 100;
        final int chestZ = 0;

        final Block lootChestBlock = world.getBlockAt(chestX, chestY, chestZ);
        lootChestBlock.setType(Material.CHEST);
        final Chest lootChest = (Chest) lootChestBlock.getState();

        Bukkit.getScheduler().runTaskLater(getConfig().getPlugin(), () -> {
            lootChest.getBlockInventory().clear();
            final ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            lootChest.getBlockInventory().addItem(item);

        }, Utils.secondsToTicks(5));
    }
}

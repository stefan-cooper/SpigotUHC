package com.stefancooper.SpigotUHC.commands;

import com.stefancooper.SpigotUHC.Config;
import com.stefancooper.SpigotUHC.enums.ConfigKey;
import com.stefancooper.SpigotUHC.types.UHCTeam;
import com.stefancooper.SpigotUHC.types.Worlds;
import com.stefancooper.SpigotUHC.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_TEAMS_POT_ONE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_TEAMS_POT_THREE;
import static com.stefancooper.SpigotUHC.enums.ConfigKey.RANDOM_TEAMS_POT_TWO;

public class RandomiseTeamsCommand extends AbstractCommand {

    public static final String COMMAND_KEY = "randomise";
    public static final String TOO_MANY_TEAMS = "tooManyTeams";

    public RandomiseTeamsCommand(CommandSender sender, Command cmd, String[] args, Config config) {
        super(sender, cmd, args, config);
    }

    @Override
    public void execute() {
        if (getArgs().length == 1) {
            final int teamSize = Integer.parseInt(getArgs()[0]);
            final Set<String> potOne = getConfig().getProperty(RANDOM_TEAMS_POT_ONE);
            final Set<String> potTwo = getConfig().getProperty(RANDOM_TEAMS_POT_TWO);
            final Set<String> potThree = getConfig().getProperty(RANDOM_TEAMS_POT_THREE);
            final List<List<String>> teams = new ArrayList<>();
            if (potOne != null && potTwo != null && potThree != null) {
                final int totalPlayers = potOne.size() + potTwo.size() + potThree.size();

                if (totalPlayers % teamSize != 0) {
                    System.out.println("Team size is not divisible by total players in pots");
                    getSender().sendMessage("Team size is not divisible by total players in pots");
                    return;
                }

                for (int i = 0; i < totalPlayers / teamSize; i++) {
                    final Set<String> team = new HashSet<>();
                    int potOnes = 0;
                    int potTwos = 0;
                    int potThrees = 0;
                    for (int j = 0; j < teamSize; j++) {
                        if (teamSize % 2 == 0) {
                            if ((team.isEmpty() && !potOne.isEmpty()) || potThrees > potOnes && !potOne.isEmpty() || potTwos > potOnes && !potOne.isEmpty() || (potTwo.isEmpty() && potThree.isEmpty() && !potOne.isEmpty())) {
                                final String rand = getRandomPlayerFromPot(potOne);
                                team.add(rand);
                                potOne.remove(rand);
                                potOnes++;
                            } else if ((team.isEmpty() && !potThree.isEmpty()) || potTwos > potThrees && !potThree.isEmpty() || potOnes > potThrees && !potThree.isEmpty() || (potOne.isEmpty() && potTwo.isEmpty() && !potThree.isEmpty())) {
                                final String rand = getRandomPlayerFromPot(potThree);
                                team.add(rand);
                                potThree.remove(rand);
                                potThrees++;
                            } else {
                                final String rand = getRandomPlayerFromPot(potTwo);
                                team.add(rand);
                                potTwo.remove(rand);
                                potTwos++;
                            }
                        } else {
                            if ((team.isEmpty() && !potOne.isEmpty()) || potThrees > potOnes && !potOne.isEmpty() || potTwos > potOnes && !potOne.isEmpty() || (potTwo.isEmpty() && potThree.isEmpty() && !potOne.isEmpty())) {
                                final String rand = getRandomPlayerFromPot(potOne);
                                team.add(rand);
                                potOne.remove(rand);
                                potOnes++;
                            } else if ((team.isEmpty() && !potTwo.isEmpty()) || potThrees > potTwos && !potTwo.isEmpty() || potOnes > potTwos && !potTwo.isEmpty() || (potOne.isEmpty() && potThree.isEmpty() && !potTwo.isEmpty())) {
                                final String rand = getRandomPlayerFromPot(potTwo);
                                team.add(rand);
                                potTwo.remove(rand);
                                potTwos++;
                            } else {
                                final String rand = getRandomPlayerFromPot(potThree);
                                team.add(rand);
                                potThree.remove(rand);
                                potThrees++;
                            }
                        }
                    }
                    teams.add(team.stream().toList());
                    System.out.println(Arrays.stream(team.toArray()).toList());
                }

                for (int i = 0; i < teams.size(); i++) {
                    final String teamColor = mapIndexToTeamColor(i);
                    if (teamColor.equals(TOO_MANY_TEAMS)) {
                        System.out.println("Too many teams generated. Failed.");
                        getSender().sendMessage("Too many teams generated. Failed.");
                        return;
                    }
                    final UHCTeam team = new UHCTeam(teamColor, String.join(",", teams.get(i)), mapColorToTeamColor(teamColor));
                    UHCTeam.createTeam(team);
                }
            }

        } else {
            System.out.println("Bad arguments provided to randomise command");
            getSender().sendMessage("Bad arguments provided to randomise command");
        }
    }

    public static <String> String getRandomPlayerFromPot(Set<String> set) {
        final int randomIndex = new Random().nextInt(set.size());
        int i = 0;
        for (String element : set) {
            if (i == randomIndex) {
                return element;
            }
            i++;
        }
        throw new IllegalStateException("Error picking a random player from pot");
    }

    private ChatColor mapColorToTeamColor(String teamColor) {
        return switch (teamColor) {
            case "Red" -> ChatColor.RED;
            case "Orange" -> ChatColor.GOLD;
            case "Blue" -> ChatColor.AQUA;
            case "Green" -> ChatColor.GREEN;
            case "Yellow" -> ChatColor.YELLOW;
            case "Pink" -> ChatColor.LIGHT_PURPLE;
            default -> ChatColor.DARK_PURPLE;
        };
    }

    private String mapIndexToTeamColor(int index) {
        if (index > 6) {
            return TOO_MANY_TEAMS;
        }
        return switch (index) {
            case 0 -> "Red";
            case 1 -> "Orange";
            case 2 -> "Blue";
            case 3 -> "Green";
            case 4 -> "Yellow";
            case 5 -> "Pink";
            default -> "Purple";
        };
    }
}

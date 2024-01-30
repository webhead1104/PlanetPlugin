package webhead1104.planetplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import webhead1104.planetplugin.PlanetPlugin;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class PlanetCommand implements CommandExecutor {
    private final PlanetPlugin plugin;
    private Player player;

    public PlanetCommand(PlanetPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        player = (Player) sender;
        if (sender instanceof Player) {
            if (args.length == 0) {

                try {
                    World world = Bukkit.getWorld(plugin.getConfig().getString("WorldName"));
                    Statement statement = plugin.connection.createStatement();
                    String thing = plugin.database;

                    ResultSet res = statement.executeQuery("SELECT * FROM " + thing + " WHERE PlayerUUID = '" + player.getUniqueId() + "';");
                    res.next();

                    int x = res.getInt("X");
                    int y = res.getInt("Y");
                    int z = res.getInt("Z");

                    Location planet = new Location(world, x, y, z);
                    player.teleport(planet);

                }catch (RuntimeException | SQLException e) {
                    player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                    plugin.getLogger().log(Level.SEVERE, "ERROR " + e + "Please tell Webhead1104 about this");
                }
            }
            }
                if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "goto" -> {
                            try {
                                World world = Bukkit.getWorld(plugin.getConfig().getString("WorldName"));
                                Statement statement = plugin.connection.createStatement();
                                String thing = plugin.database;

                                ResultSet res = statement.executeQuery("SELECT * FROM `" + thing + "` WHERE PlayerUUID = '" + player.getUniqueId() + "';");
                                res.next();

                                int x = res.getInt("X");
                                int y = res.getInt("Y");
                                int z = res.getInt("Z");

                                Location planet = new Location(world, x, y, z);
                                player.teleport(planet);

                            }catch (RuntimeException | SQLException e) {
                                player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                                plugin.getLogger().log(Level.SEVERE, "ERROR " + e + "Please tell Webhead1104 about this");
                            }
                    }
                }
            }
        return true;
    }
    }

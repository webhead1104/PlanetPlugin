package webhead1104.planetplugin.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import webhead1104.planetplugin.PlanetPlugin;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
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
                islandGoto();
            }
            }
                if (args.length >= 1) {
                    switch (args[0].toLowerCase()) {
                        case "goto" -> islandGoto();}}
        return true;
    }

    public void islandGoto() {
        try {

            World world = Bukkit.getWorld("planet");
            plugin.connect();
            PreparedStatement planetGet = plugin.connection.prepareStatement("SELECT * FROM PlanetPlugin WHERE PlayerUUID = ?");
            planetGet.setString(1, player.getUniqueId().toString());
            ResultSet res = planetGet.executeQuery();
            res.next();
            int x = res.getInt("X");
            int y = res.getInt("Y");
            int z = res.getInt("Z");
            Location planet = new Location(world, x, y, z);
            player.teleport(planet);
            player.setGameMode(GameMode.SURVIVAL);
        }catch (RuntimeException | SQLException e) {
            player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
            plugin.getLogger().log(Level.SEVERE, "ERROR " + e + "Please tell Webhead1104 about this");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    }

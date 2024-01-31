package webhead1104.planetplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import webhead1104.planetplugin.PlanetPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

public class AdminCommand implements CommandExecutor, TabCompleter {
    private final PlanetPlugin plugin;
    private Player player;

    public AdminCommand(PlanetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        player = (Player) sender;
        if (player.hasPermission("planetplugin.admin")) {
            if (args.length >= 1) {
                switch (args[0].toLowerCase()) {
                    case "databaseadd" -> {
                        Player target = Bukkit.getPlayer(args[1].toLowerCase());
                        if (target != null) {
                            int y = 130;
                            int x = plugin.getConfig().getInt("x");
                            int z = plugin.getConfig().getInt("z");
                            int xthing = x + 250;
                            int zthing = z + 250;
                            this.plugin.getConfig().set("x", xthing);
                            this.plugin.getConfig().set("z", zthing);
                            try {
                                PreparedStatement thing
                                        = plugin.connection.prepareStatement("INSERT INTO PlayerDATA (PlayerUUID, X, Y, Z) VALUES (?, ?, ?, ?);");
                                thing.setString(1, target.getUniqueId().toString());
                                thing.setInt(2, x);
                                thing.setInt(3, y);
                                thing.setInt(4, z);
                                thing.executeUpdate();
                            } catch (SQLException e) {
                                plugin.getLogger().log(Level.SEVERE, e.toString());
                            }
                        } else if (target == null) {
                            player.sendMessage("NO");
                        }
                        player.sendMessage(ChatColor.GREEN + "Player added to database");
                        return true;
                    }
                }
            }


        } else player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot do this!");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("databaseadd");
    }
}

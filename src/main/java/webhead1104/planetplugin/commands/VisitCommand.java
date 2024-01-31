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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class VisitCommand implements CommandExecutor {
    private final PlanetPlugin plugin;
    public VisitCommand(PlanetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player visitor = (Player) sender;
        Player planet = Bukkit.getPlayer(args[0].toLowerCase());
        if (planet != null) {
            try {
                World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("world")));
                PreparedStatement planetGet = plugin.connection.prepareStatement("SELECT * FROM PlayerDATA WHERE PlayerUUID = ?;");
                planetGet.setString(1, planet.getUniqueId().toString());
                ResultSet res = planetGet.executeQuery();
                res.next();

                int x = res.getInt("X");
                int y = res.getInt("Y");
                int z = res.getInt("Z");

                Location loc = new Location(world, x, y, z);
                visitor.teleport(loc);

            } catch (RuntimeException | SQLException e) {
                visitor.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                plugin.getLogger().log(Level.SEVERE, "ERROR " + e + "Please tell Webhead1104 about this");
            }
        }
        visitor.sendMessage(ChatColor.RED + "Please chose a real player");
        return false;
    }
}

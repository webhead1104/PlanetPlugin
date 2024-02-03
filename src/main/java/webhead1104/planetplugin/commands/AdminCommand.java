package webhead1104.planetplugin.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class AdminCommand implements CommandExecutor, TabCompleter {
    private final PlanetPlugin plugin;

    public AdminCommand(PlanetPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Clipboard clipboard = this.plugin.clipboard;
        if (player.hasPermission("planetplugin.admin")) {
            if (args.length >= 1) {
                switch (args[0].toLowerCase()) {
                    case "databaseadd" -> {
                        Player target = Bukkit.getPlayer(args[1].toLowerCase());
                        if (target != null) {
                            int x = this.plugin.getConfig().getInt("x");
                            int z = this.plugin.getConfig().getInt("z");
                            int y = 130;
                            org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("world")));
                            try {
                                com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
                                EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);
                                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                                        .to(BlockVector3.at(x, y, z)).ignoreAirBlocks(true).build();
                                try {
                                    Operations.complete(operation);
                                    editSession.flushSession();
                                } catch (WorldEditException e) {
                                    player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                                    plugin.getLogger().log(Level.SEVERE, e.toString());
                                }
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
                                plugin.getLogger().log(Level.SEVERE, e.toString());
                                throw new RuntimeException(e);
                            }
                            int xthing = x + 250;
                            int zthing = z + 250;
                            this.plugin.getConfig().set("x", xthing);
                            this.plugin.getConfig().set("z", zthing);
                            Location loc = new Location(world, x, y, z);
                            target.teleport(loc);
                            try {
                                PreparedStatement thing = plugin.connection.prepareStatement("INSERT IGNORE INTO PlayerDATA (PlayerUUID, X, Y, Z VALUES (?, ?, ?, ?);");
                                thing.setString(1, target.getUniqueId().toString());
                                thing.setInt(2, x);
                                thing.setInt(3, y);
                                thing.setInt(4, z);
                                thing.executeUpdate();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (target == null) {
                            player.sendMessage(ChatColor.RED + "Please select a real player!");
                        }
                        player.sendMessage(ChatColor.GREEN + "Player added to database");
                        return true;
                    }
                    case "reconnect" -> {
                        try {
                            plugin.connect();
                            if (!plugin.connection.isValid(1)) {
                                player.sendMessage("ERROR");
                                throw new SQLException("Could not establish database connection.");
                            }
                            player.sendMessage(ChatColor.GREEN + "Reconnected to database!");
                        }catch (SQLException | ClassNotFoundException e) {
                            player.sendMessage("ERROR");
                            plugin.getLogger().log(Level.SEVERE, "ERROR " + e + "Please tell Webhead1104 about this");
                            throw new RuntimeException(e);
                        }
                    }
                    case "make-tables" -> {
                        try {
                            plugin.createTables();
                        } catch (SQLException e) {
                            player.sendMessage("ERROR");
                            plugin.getLogger().log(Level.SEVERE, "ERROR " + e + " Please tell Webhead1104 about this");
                            throw new RuntimeException(e);
                        }
                        player.sendMessage(ChatColor.GREEN + "Tables remade!");
                    }
                }
            }


        } else player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You cannot do this!");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> returnme = new ArrayList<>();
        if(sender.hasPermission("planetplugin.admin")) {
            if(args.length == 1) {
                returnme.addAll(List.of("databaseadd", "reconnect","make-tables"));
            } else if (args.length == 2) {
                List<String> list = new ArrayList<String>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                    returnme.addAll(list);
                }
                return list;
            }
        }
        return returnme;
    }
}
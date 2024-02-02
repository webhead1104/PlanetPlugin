package webhead1104.planetplugin.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import webhead1104.planetplugin.PlanetPlugin;

public class JoinListener implements Listener {
  private final PlanetPlugin plugin;

    public JoinListener(PlanetPlugin plugin) {
    this.plugin = plugin;
  }


  @EventHandler
  private void onJoin(PlayerJoinEvent event) throws SQLException {
      Player player = event.getPlayer();
      String playerUUID = player.getUniqueId().toString();
    this.plugin.joinPlayer = event.getPlayer();
    if (!player.hasPlayedBefore()) {
      Clipboard clipboard = this.plugin.clipboard;
      int x = this.plugin.getConfig().getInt("x");
      int z = this.plugin.getConfig().getInt("z");
      int y = 130;
      org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("world")));
      try { //Pasting Operation
// We need to adapt our world into a format that worldedit accepts. This looks like this:
// Ensure it is using com.sk89q... otherwise we'll just be adapting a world into the same world.
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);
// Saves our operation and builds the paste - ready to be completed.
        Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                .to(BlockVector3.at(x, y, z)).ignoreAirBlocks(true).build();
        try { // This simply completes our paste and then cleans up.
          Operations.complete(operation);
          editSession.flushSession();
        } catch (WorldEditException e) { // If worldedit generated an exception it will go here
          player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
          plugin.getLogger().log(Level.SEVERE, e.toString());
        }} catch (Exception e) {
        player.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
        plugin.getLogger().log(Level.SEVERE, e.toString());
        throw new RuntimeException(e);}
      int xthing = x + 250;
      int zthing = z + 250;
      this.plugin.getConfig().set("x", xthing);
      this.plugin.getConfig().set("z", zthing);
      Location loc = new Location(world, x, y, z);
      player.teleport(loc);
      PreparedStatement thing = plugin.connection.prepareStatement("INSERT INTO PlayerDATA (PlayerUUID, X, Y, Z VALUES ('?', '?', '?', '?');");
      thing.setString(1, playerUUID);
      thing.setInt(2, x);
      thing.setInt(3, y);
      thing.setInt(4, z);
      thing.executeUpdate();
      Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawnpoint " + player.getName() + " "+ x + " "+ y + " " + z);

    }else if (player.hasPlayedBefore()) {
      World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("world")));
      PreparedStatement planetGet = plugin.connection.prepareStatement("SELECT * FROM PlayerDATA WHERE PlayerUUID = ?");
      planetGet.setString(1, player.getUniqueId().toString());
      ResultSet res = planetGet.executeQuery();
      res.next();
      int x = res.getInt("X");
      int y = res.getInt("Y");
      int z = res.getInt("Z");

      Location planet = new Location(world, x, y, z);
      player.teleport(planet);
    }
    }
}
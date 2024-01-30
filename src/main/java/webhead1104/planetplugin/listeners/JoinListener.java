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
import java.sql.Statement;
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
  private Player player;

  public JoinListener(PlanetPlugin plugin) {
    this.plugin = plugin;
  }



  @EventHandler
  private void onJoin(PlayerJoinEvent event) throws SQLException {
    player = event.getPlayer();
    if (!event.getPlayer().hasPlayedBefore()) {

      this.plugin.joinPlayer = event.getPlayer();
      Clipboard clipboard = this.plugin.clipboard;

      int x = this.plugin.getConfig().getInt("x");
      int z = this.plugin.getConfig().getInt("z");
      int y = 130;

      org.bukkit.World world = Bukkit.getWorld(plugin.getConfig().getString("WorldName"));

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
          e.printStackTrace();
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }


      int xthing = x + 250;
      int zthing = z + 250;
      this.plugin.getConfig().set("x", Integer.valueOf(xthing));
      this.plugin.getConfig().set("z", Integer.valueOf(zthing));
      Location loc = new Location(world, x, y, z);
      player.sendMessage("OH NO IT WORKED");
      player.teleport(loc);
      String playerName = player.getName();
      Statement statement = plugin.connection.createStatement();
      PreparedStatement preparedStatement = plugin.connection.prepareStatement("INSERT INTO player_data(player_name, player_uidd) VALUES('SpraX', 120)");
      preparedStatement.execute();

      try {
        statement.executeUpdate("UPDATE INTO PlayerDATA(PlayerUUID) VALUES ('" + player.getUniqueId() + "')");
        statement.executeUpdate("UPDATE INTO PlayerDATA(PlayerNAME) VALUES ('" + playerName + "')");
        statement.executeUpdate("UPDATE INTO PlayerDATA(X) VALUES ('" + x + "')");
        statement.executeUpdate("UPDATE INTO PlayerDATA(Y) VALUES ('" + y + "')");
        statement.executeUpdate("UPDATE INTO PlayerDATA(Z) VALUES ('" + z + "')");
      } catch (SQLException e) {
        e.printStackTrace();

      }
    } else if (event.getPlayer().hasPlayedBefore()) {
      World world = Bukkit.getWorld(plugin.getConfig().getString("WorldName"));
      Statement statement = plugin.connection.createStatement();
      ResultSet res = statement.executeQuery("SELECT * FROM PlayerDATA WHERE PlayerUUID = '1e27cf68-9d59-45ba-8190-e4b55fabaf57';");
      res.next();

      int x = res.getInt("X");
      int y = res.getInt("Y");
      int z = res.getInt("Z");

      Location planet = new Location(world, x, y, z);
      player.teleport(planet);


    }


  }
}




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

  private String playerUUID;
  private int x, y, z;
  private Player joinPlayer;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) throws SQLException {
      joinPlayer = event.getPlayer();
      playerUUID = joinPlayer.getUniqueId().toString();
      x = this.plugin.getConfig().getInt("x");
      z = this.plugin.getConfig().getInt("z");
      y = 130;
      PreparedStatement preparedStatement = plugin.connection.prepareStatement("SELECT * FROM PlayerDATA WHERE PlayerUUID = ?");
      preparedStatement.setString(1, joinPlayer.getUniqueId().toString());
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();

      try {
          if (Objects.equals(resultSet.getString("PlayerUUID"), joinPlayer.getUniqueId().toString())) {
              oldPlayer();
          }
      } catch (SQLException e) {
              newPlayer();
      }



  }

  //new player
    private void newPlayer () throws SQLException {
        Clipboard clipboard = this.plugin.clipboard;
        x = this.plugin.getConfig().getInt("x");
        z = this.plugin.getConfig().getInt("z");
        y = 130;
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
            joinPlayer.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
            plugin.getLogger().log(Level.SEVERE, e.toString());
          }
        } catch (Exception e) {
          joinPlayer.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
          plugin.getLogger().log(Level.SEVERE, e.toString());
          throw new RuntimeException(e);
        }
        int xthing = x + 250;
        int zthing = z + 250;
        this.plugin.getConfig().set("x", xthing);
        this.plugin.getConfig().set("z", zthing);
        Location loc = new Location(world, x, y, z);
        joinPlayer.teleport(loc);
        PreparedStatement thing = plugin.connection.prepareStatement("INSERT INTO PlayerDATA (PlayerUUID, X, Y, Z)VALUES (?, ?, ?, ?);");
        thing.setString(1, playerUUID);
        thing.setInt(2, x);
        thing.setInt(3, y);
        thing.setInt(4, z);
        thing.executeUpdate();
      }

      //old player
      private void oldPlayer() throws SQLException {
        World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("world")));
        PreparedStatement planetGet = plugin.connection.prepareStatement("SELECT * FROM PlayerDATA WHERE PlayerUUID = ?");
        planetGet.setString(1, joinPlayer.getUniqueId().toString());
        ResultSet res = planetGet.executeQuery();
        res.next();
        int x = res.getInt("X");
        int y = res.getInt("Y");
        int z = res.getInt("Z");

        Location planet = new Location(world, x, y, z);
        joinPlayer.teleport(planet);
      }
    }
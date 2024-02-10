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
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
  public Player thing;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) throws SQLException, ClassNotFoundException {
      thing = event.getPlayer();
      playerUUID = thing.getUniqueId().toString();
      x = this.plugin.getConfig().getInt("x");
      z = this.plugin.getConfig().getInt("z");
      y = 130;
      plugin.connect();
      PreparedStatement preparedStatement = plugin.connection.prepareStatement("SELECT * FROM PlanetPlugin WHERE PlayerUUID = ?");
      preparedStatement.setString(1, thing.getUniqueId().toString());
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();

      try {
          if (Objects.equals(resultSet.getString("PlayerUUID"), thing.getUniqueId().toString())) {
              oldPlayer();
          }
      } catch (SQLException e) {
              newPlayer();
      }



  }

  //new player
    private void newPlayer () throws SQLException, ClassNotFoundException {
        Clipboard clipboard = this.plugin.clipboard;
        x = this.plugin.getConfig().getInt("x");
        z = this.plugin.getConfig().getInt("z");
        y = 130;
        org.bukkit.World world = Bukkit.getWorld("planet");
        try {
          com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
          EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);
          Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                  .to(BlockVector3.at(x, y, z)).ignoreAirBlocks(true).build();
          try {
            Operations.complete(operation);
            editSession.flushSession();
          } catch (WorldEditException e) {
            thing.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
            plugin.getLogger().log(Level.SEVERE, e.toString());
          }
        } catch (Exception e) {
          thing.sendMessage(ChatColor.RED + "OOPS! Something went wrong, please contact an administrator");
          plugin.getLogger().log(Level.SEVERE, e.toString());
          throw new RuntimeException(e);
        }
        int xthing = x + 250;
        int zthing = z + 250;
        this.plugin.getConfig().set("x", xthing);
        this.plugin.getConfig().set("z", zthing);
        Location loc = new Location(world, x, y, z);
        thing.teleport(loc);
        plugin.connect();
        PreparedStatement thing = plugin.connection.prepareStatement("INSERT INTO PlanetPlugin (PlayerUUID, X, Y, Z, PlayerALIEN)VALUES (?, ?, ?, ?, ?);");
        thing.setString(1, playerUUID);
        thing.setInt(2, x);
        thing.setInt(3, y);
        thing.setInt(4, z);
        thing.setString(5, "false");
        thing.executeUpdate();
        worldborder();
      }

      //old player
      private void oldPlayer() throws SQLException, ClassNotFoundException {
        World world = Bukkit.getWorld("planet");
        plugin.connect();
        PreparedStatement planetGet = plugin.connection.prepareStatement("SELECT * FROM PlanetPlugin WHERE PlayerUUID = ?");
        planetGet.setString(1, thing.getUniqueId().toString());
        ResultSet res = planetGet.executeQuery();
        res.next();
        int x = res.getInt("X");
        int y = res.getInt("Y");
        int z = res.getInt("Z");

        Location planet = new Location(world, x, y, z);
        thing.teleport(planet);
      }

      public void worldborder() throws SQLException, ClassNotFoundException {
      plugin.connect();
          PreparedStatement planetGet = plugin.connection.prepareStatement("SELECT * FROM PlanetPlugin WHERE PlayerUUID = ?");
          planetGet.setString(1, thing.getUniqueId().toString());
          ResultSet res = planetGet.executeQuery();
          res.next();
          int thingx = res.getInt("X");
          int thingy = res.getInt("Y");
          int thingz = res.getInt("Z");

          int worldx = thingx + 250;
          int worldy = thingy + 1000;
          int worldz = thingz + 250;

          int _worldx = thingx - 250;
          int _worldy = thingy - 1000;
          int _worldz = thingz - 250;

          BlockVector3 plus = BlockVector3.at(worldx,worldy,worldz);
          BlockVector3 minus = BlockVector3.at(_worldx,_worldy,_worldz);
          ProtectedRegion region = new ProtectedCuboidRegion(playerUUID, plus, minus);
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " +playerUUID+ " -w planet exit deny");
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rg flag " +playerUUID+ " -w planet entry deny");

  }
    }
package webhead1104.planetplugin.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import webhead1104.planetplugin.PlanetPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LeaveListener implements Listener {
    private final PlanetPlugin plugin;

    public LeaveListener(PlanetPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) throws SQLException, ClassNotFoundException {
        plugin.connect();
        PreparedStatement thing = plugin.connection.prepareStatement("UPDATE PlanetPlugin SET PlayerALIEN = ? WHERE PlayerUUID = ?;");
        thing.setString(1, "false");
        thing.setString(2, event.getPlayer().getUniqueId().toString());
        thing.executeUpdate();

    }
}

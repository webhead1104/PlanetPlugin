package webhead1104.planetplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import webhead1104.planetplugin.PlanetPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class DeathListener implements Listener {
    private final PlanetPlugin plugin;


    public DeathListener(PlanetPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) throws SQLException, ClassNotFoundException {
        Entity enty = event.getEntity();
        if (event.getEntity() instanceof Player player) {
            PreparedStatement preparedStatement = plugin.connection.prepareStatement("SELECT PlayerALIEN FROM PlanetPlugin WHERE PlayerUUID = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (Objects.equals(resultSet.getString("PlayerALIEN"), "true")) {

                Location location = player.getLocation();
                List<Entity> enemies = (List<Entity>) location.getNearbyEntities(20, 20, 20);
                for (Entity e : enemies) {
                    if (e instanceof Zombie) {
                        if (Objects.equals(e.getCustomName(), ChatColor.RED + "" + ChatColor.BOLD + "ALIEN ZOMBIE")) {
                            ((LivingEntity) e).setHealth(0);
                        }
                    }
                }
                plugin.connect();
                PreparedStatement thing = plugin.connection.prepareStatement("UPDATE PlanetPlugin SET PlayerALIEN = ? WHERE PlayerUUID = ?;");
                thing.setString(1, "false");
                thing.setString(2, player.getUniqueId().toString());
                thing.executeUpdate();
            }
        }

    }
}

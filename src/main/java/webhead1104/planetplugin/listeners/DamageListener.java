package webhead1104.planetplugin.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import webhead1104.planetplugin.PlanetPlugin;

public class DamageListener implements Listener {
  public DamageListener(PlanetPlugin planetPlugin) {}
  
  @EventHandler
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    Entity attacker = event.getDamager();
    Entity victim = event.getEntity();
    if (attacker instanceof org.bukkit.entity.Player && 
      victim instanceof org.bukkit.entity.Player)
      event.setCancelled(true); 
  }
}

package webhead1104.planetplugin;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import webhead1104.planetplugin.listeners.DamageListener;
import webhead1104.planetplugin.listeners.JoinListener;

public final class PlanetPlugin extends JavaPlugin {
  public Clipboard clipboard;
  
  public void onEnable() {
    registerListeners();
    File file = new File("plugins/PlanetPlugin/config.yml");
    if (!file.exists())
      saveResource("config.yml", false); 
    try {
      File schem = new File(getResource("planet.schem").toString());
      ClipboardFormat format = ClipboardFormats.findByFile(schem);
      try {
        ClipboardReader reader = format.getReader(new FileInputStream(schem));
        try {
          Clipboard clipboard = reader.read();
          if (reader != null)
            reader.close(); 
        } catch (Throwable throwable) {
          if (reader != null)
            try {
              reader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException e) {
        Logger log = Bukkit.getLogger();
        log.info("THERE WAS A ERROR LOADING THE SCHEM" + e);
        Bukkit.getPluginManager().disablePlugin((Plugin)this);
        throw new RuntimeException(e);
      } 
    } catch (NullPointerException error) {
      getLogger().log(Level.SEVERE, "error" + error);
    } 
  }
  
  public void onDisable() {}
  
  private void registerListeners() {
    getServer().getPluginManager().registerEvents((Listener)new DamageListener(this), (Plugin)this);
    getServer().getPluginManager().registerEvents((Listener)new JoinListener(this), (Plugin)this);
  }
  
  private void registerCommands() {}
}

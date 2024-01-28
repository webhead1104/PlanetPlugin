package webhead1104.planetplugin.listeners;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
  private void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    File schem = new File(this.plugin.getResource("fileTest.txt").toString());
    Clipboard clipboard = this.plugin.clipboard;
    int y = 130;
    String xinput = this.plugin.getConfig().getString("x");
    String zinput = this.plugin.getConfig().getString("z");
    int x = Integer.parseInt(xinput);
    int z = Integer.parseInt(zinput);
    org.bukkit.World thing = Bukkit.getWorld(this.plugin.getConfig().getString("WorldName"));
    World world = (World)Bukkit.getWorld(this.plugin.getConfig().getString("WorldName"));
    EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
    try {
      Operation operation = (new ClipboardHolder(clipboard)).createPaste((Extent)editSession).to(BlockVector3.at(x, y, z)).ignoreAirBlocks(false).build();
      Operations.complete(operation);
      if (editSession != null)
        editSession.close(); 
    } catch (Throwable throwable) {
      if (editSession != null)
        try {
          editSession.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    int xthing = x + 250;
    int zthing = z + 250;
    this.plugin.getConfig().set("x", Integer.valueOf(xthing));
    this.plugin.getConfig().set("z", Integer.valueOf(zthing));
    Location loc = new Location(thing, x, y, z);
    player.teleport(loc);
  }
}

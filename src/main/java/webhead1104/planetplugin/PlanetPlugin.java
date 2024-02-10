package webhead1104.planetplugin;

import java.io.File;
import java.sql.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import webhead1104.planetplugin.commands.AdminCommand;
import webhead1104.planetplugin.commands.PlanetCommand;
import webhead1104.planetplugin.commands.VisitCommand;
import webhead1104.planetplugin.listeners.DamageListener;
import webhead1104.planetplugin.listeners.DeathListener;
import webhead1104.planetplugin.listeners.JoinListener;
import webhead1104.planetplugin.listeners.LeaveListener;
import webhead1104.planetplugin.managers.WorldManager;

import static java.lang.Math.random;

public final class PlanetPlugin extends JavaPlugin {
    public Clipboard clipboard;
    private final String worldname = "planet";

    public void onEnable() {

        File file = new File("plugins/PlanetPlugin/config.yml");
        if(!file.exists())
            this.saveResource("config.yml", true);
        registerListeners();
        setup();
        registerCommands();
        //mysql stuff
        try {
            connect();
            createTables();
            schem();

        } catch (SQLException | ClassNotFoundException | IOException e) {
            this.getLogger().log(Level.SEVERE, e.toString());
            throw new RuntimeException(e);
        }
    }
    public void onDisable() {
        this.saveConfig();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents((Listener) new DamageListener(this), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new JoinListener(this), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new DeathListener(this), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new LeaveListener(this), (Plugin) this);
    }

    private void registerCommands() {
        getCommand("planet").setExecutor(new PlanetCommand(this));
        getCommand("visit").setExecutor(new VisitCommand(this));
        getCommand("pladmin").setExecutor(new AdminCommand(this));
    }

    public Connection connection;
    public String host = this.getConfig().getString("mysql.host");
    public String databasename = this.getConfig().getString("mysql.databasename");
    public String username = this.getConfig().getString("mysql.username");
    public String password = this.getConfig().getString("mysql.password");
    public int port = 3306;


    public void connect() throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.databasename, this.username, this.password);
            if (!connection.isValid(1)) {
                throw new SQLException("Could not establish database connection.");

            }
    }

    public void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `PlanetPlugin` (`PlayerUUID` VARCHAR(255), `X` INT, `Y` INT, `Z` INT, `PlayerALIEN` TEXT)");
    }

    public void resetTable() throws SQLException, ClassNotFoundException {
        this.connect();
        PreparedStatement planetGet = this.connection.prepareStatement("DROP TABLE PlanetPlugin;");
        planetGet.executeUpdate();
        createTables();

    }

            public void setup() {
        if (Bukkit.getWorld(worldname) == null){
                WorldCreator wc = new WorldCreator(worldname);
                wc.generator(new WorldManager()); //The chunk generator from step 1
                wc.createWorld();
        }
    }
    private void schem() throws IOException {
        File schem = new File(this.getDataFolder().getAbsolutePath() + "/planet.schem");

        ClipboardFormat format = ClipboardFormats.findByFile(schem);

        ClipboardReader reader = format.getReader(new FileInputStream(schem));

        clipboard = reader.read();
    }
    public int random_int;
    public void timer() {
        long min = 3600;
        long max = 18000;
        random_int = (int) Math.floor(random() * (max - min + 1) + min);
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, () -> {
                    random_int = (int) Math.floor(random() * (max - min + 1) + min);
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        try {
                            alienSpawn();
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                , random_int, random_int);

    }

    public Collection<? extends Player> playerList = Bukkit.getServer().getOnlinePlayers();

    public void alienSpawn() throws SQLException, ClassNotFoundException {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Your planet is being raided!", "", 1, 20, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 100, 10);
            for (int i = 0; i < 5; i++) {

                int newX = (int) (player.getLocation().getX() + new Random().nextInt(10) + 1);
                int newZ = (int) (player.getLocation().getZ() + new Random().nextInt(10) + 1);

                Location spawnLoc = new Location(Bukkit.getWorld("planet"), newX, 0, newZ);

                spawnLoc.setY(spawnLoc.getWorld().getHighestBlockYAt(spawnLoc) + 1);
                int y;
                y = spawnLoc.getBlockY() - 1;

                Location block = new Location(Bukkit.getWorld("planet"), newX, y, newZ);

                World world = Bukkit.getWorld("planet");

                ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD, 1);
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                ItemStack helmet = new ItemStack(Material.PLAYER_HEAD, 1);

                LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
                LeatherArmorMeta meta3 = (LeatherArmorMeta) pants.getItemMeta();
                LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
                SkullMeta skullMeta = (SkullMeta) helmet.getItemMeta();
                meta4.setColor(Color.BLACK);
                meta3.setColor(Color.BLACK);
                meta2.setColor(Color.BLACK);
                boots.setItemMeta(meta4);
                pants.setItemMeta(meta3);
                chestplate.setItemMeta(meta2);


                Zombie s = Objects.requireNonNull(world).spawn(spawnLoc, Zombie.class);
                s.setCustomNameVisible(true);
                s.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "ALIEN ZOMBIE");
                s.getEquipment().setHelmet(helmet);
                s.getEquipment().setChestplate(chestplate);
                s.getEquipment().setLeggings(pants);
                s.getEquipment().setBoots(boots);
                s.getEquipment().setItemInMainHand(weapon);
                s.setBaby(false);
                s.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 2, true));
                s.getEquipment().setItemInMainHandDropChance(0);
                s.getEquipment().setBootsDropChance(0);
                s.getEquipment().setLeggingsDropChance(0);
                s.getEquipment().setChestplateDropChance(0);
                s.getEquipment().setHelmetDropChance(0);
            }
            this.connect();
            PreparedStatement thing = this.connection.prepareStatement("UPDATE PlanetPlugin SET PlayerALIEN = ? WHERE PlayerUUID = ?;");
            thing.setString(1, "true");
            thing.setString(2, player.getUniqueId().toString());
            thing.executeUpdate();
        }
    }
}
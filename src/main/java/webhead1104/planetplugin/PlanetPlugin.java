package webhead1104.planetplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import webhead1104.planetplugin.commands.AdminCommand;
import webhead1104.planetplugin.commands.PlanetCommand;
import webhead1104.planetplugin.commands.VisitCommand;
import webhead1104.planetplugin.listeners.DamageListener;
import webhead1104.planetplugin.listeners.JoinListener;
import webhead1104.planetplugin.managers.WorldManager;

public final class PlanetPlugin extends JavaPlugin {
    public Clipboard clipboard;
    private final String WorldName = this.getConfig().getString("world");

    public void onEnable() {

        File file = new File("plugins/PlanetPlugin/config.yml");
        if(!file.exists())
            this.saveResource("config.yml", false);


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
    }

    private void registerCommands() {
        getCommand("planet").setExecutor(new PlanetCommand(this));
        getCommand("visit").setExecutor(new VisitCommand(this));
        getCommand("pladmin").setExecutor(new AdminCommand(this));
    }

    public Connection connection;
    public String host;
    public String database;
    public String username;
    public String password;
    public int port;


    public void connect() throws SQLException, ClassNotFoundException {
        host = this.getConfig().getString("mysql.host");
        port = this.getConfig().getInt("mysql.port");
        username = this.getConfig().getString("mysql.username");
        password = this.getConfig().getString("mysql.password");
        database = this.getConfig().getString("mysql.database-name");

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
            if (!connection.isValid(1)) {
                throw new SQLException("Could not establish database connection.");

            }
    }

    public void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `PlayerDATA` (`PlayerUUID` VARCHAR(255), `X` INT(1), `Y` INT(1), `Z` INT(1))");
    }

            public void setup() {
        if (Bukkit.getWorld(WorldName) == null){
                WorldCreator wc = new WorldCreator(WorldName);
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
}
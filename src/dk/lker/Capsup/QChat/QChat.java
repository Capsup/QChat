package dk.lker.Capsup.QChat;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;


public class QChat extends JavaPlugin
{
	private final QChatPlayerListener playerListener = new QChatPlayerListener(this);
	public static PermissionHandler permissionHandler;
	Logger logger = Logger.getLogger("Minecraft");
	public Configuration config = this.getConfiguration();
	

	public String format = "[+group]+prefix+name&f: +message";
	public boolean playersCanShout = true;
	public int shoutRadius = 30;
	public String shoutPrefix = "!";

	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Low, this);
		
		if (!new File(getDataFolder(), "config.yml").exists()) {
			createConfig();
		}
		loadConfig();

		PluginDescriptionFile pdf = this.getDescription();
		System.out.println("[" + pdf.getName() + "] version " + pdf.getVersion() + " loaded.");
		
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");

        if (QChat.permissionHandler == null) {
            if (permissionsPlugin != null) {
                QChat.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
            } else {
                logger.log(Level.WARNING, "Permission system not detected. Disabling QChat.");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
	}
	
	public void createConfig()
	{
		config = new Configuration(new File(getDataFolder().getPath() + "/config.yml"));
		config.setHeader(
				"#The shoutprefix determines what char(s) you have to write ingame for it to be global chat.",
				"#The shout-radius is how many blocks away local chat can be heard.",
				"#players-can-shout determines whether or not normal players can shout. If it's set to false, only ops can shout.",
				"#The format is how the plugin will format your chat in the global chat. It supports the following variables:",
				"#+group: Adds the name of the group that the player is part of.",
				"#+prefix: Adds the prefix of the user, or if the user has none, adds the prefix of the group.",
				"#+suffix: Adds the suffix of the user, or if the user has none, the suffix of the group.",
				"#+name: Adds the name of the user.",
				"#+msg: Adds the message that the player has entered.",
				"#Besides that, color codes can also be used. They are formatted like so: &f is forexample the color white."
				);
		config.setProperty("format", this.format);
		config.setProperty("players-can-shout", this.playersCanShout);
		config.setProperty("shout-radius", this.shoutRadius);
		config.setProperty("shoutPrefix", this.shoutPrefix);
		config.save();
	}
	
	public void loadConfig()
	{
		config.load();
		this.format = config.getString("format", format);
		this.playersCanShout = config.getBoolean("players-can-shout", playersCanShout);
		this.shoutRadius = config.getInt("shout-radius", shoutRadius);
		this.shoutPrefix = config.getString("shout-prefix", shoutPrefix);
	}

	public void onDisable()
	{
		logger.log(Level.INFO, "QChat disabled.");
	}
}

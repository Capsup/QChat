package dk.lker.Capsup.QChat;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;


public class QChatPlayerListener extends PlayerListener
{
	private final QChat parent;

	public QChatPlayerListener(QChat instance)
	{
		parent = instance;
	}
	
	public String replaceVars(String format, String[] search, String[] replace) {
		if (search.length != replace.length) return "";
		for (int i = 0; i < search.length; i++) {
			if (search[i].contains(",")) {
				for (String s : search[i].split(",")) {
					if (s == null || replace[i] == null) continue;
					format = format.replace(s, replace[i]);
				}
			} else {
				format = format.replace(search[i], replace[i]);
			}
		}
		return format.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	
	@Override
	public void onPlayerChat( PlayerChatEvent event )
	{
		String msg = event.getMessage();
		Player ply = event.getPlayer();
		
		if( msg.startsWith("!") ){
			if( !parent.playersCanShout && !ply.isOp() ){
				ply.sendMessage("You do not have permissions to shout.");
			}
		msg = msg.replaceFirst("!", "");
		String prefix = QChat.permissionHandler.getUserPrefix( ply.getWorld().getName(), ply.getName() );
		String suffix = QChat.permissionHandler.getUserSuffix( ply.getWorld().getName(), ply.getName() );
		String group = QChat.permissionHandler.getPrimaryGroup( ply.getWorld().getName(), ply.getName() );
		
		String[] search = new String[] {"+suffix","+prefix","+name", "+group","+message"};
		String[] replace = new String[] { suffix, prefix, ply.getName(), group, msg };
		
		event.setFormat( replaceVars( parent.format, search, replace ) );
		String message = String.format(event.getFormat(), ply.getDisplayName(), event.getMessage());
		Logger.getLogger("Minecraft").log(Level.INFO, String.format("Shout: %1$s", message));
		}
		else if( msg.startsWith("/qchat reload")){
			parent.loadConfig();
			ply.sendMessage("QChat reloaded.");
			event.setCancelled(true);
		}
		else{
			Location loc1 = event.getPlayer().getLocation();
			event.setCancelled(true);
			String message = String.format(event.getFormat(), ply.getDisplayName(), event.getMessage());
			Logger.getLogger("Minecraft").log(Level.INFO, String.format("Local: %1$s", message));

			for (Player p : parent.getServer().getOnlinePlayers())
			{
				if (p != ply && p.getItemInHand().getType() != Material.BEDROCK)
				{
					Location loc2 = p.getLocation();
					if (loc1.getWorld() != loc1.getWorld()) continue;
					int x = loc1.getBlockX() - loc2.getBlockX();
					int y = loc1.getBlockY() - loc2.getBlockY();
					int z = loc1.getBlockZ() - loc2.getBlockZ();
					x = Math.abs(x);
					y = Math.abs(y);
					z = Math.abs(z);
					if (x + y + z > parent.shoutRadius) continue;
				}

				p.sendMessage(message);
			}
		}
	}
}

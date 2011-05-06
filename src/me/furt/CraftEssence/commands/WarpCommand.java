package me.furt.CraftEssence.commands;

import me.furt.CraftEssence.CraftEssence;
import me.furt.CraftEssence.sql.WarpTable;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
	CraftEssence plugin;

	public WarpCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.isPlayer(sender)) {
			if (!CraftEssence.Permissions.has((Player) sender,
					"craftessence.warp")) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
		}
		if (!plugin.isPlayer(sender))
			return false;

		if (args.length == 0)
			return false;

		Player player = (Player) sender;
		String world = player.getWorld().getName();
		Location loc = null;
		WarpTable wt = plugin.getDatabase().find(WarpTable.class).where()
				.ieq("name", args[0]).ieq("world", world).findUnique();
		if (wt != null) {
			loc = wt.getLocation();
		} else {
			sender.sendMessage(CraftEssence.premessage
					+ "Warp location not found.");
			return true;
		}

		player.teleport(loc);
		player.sendMessage(CraftEssence.premessage + "Warping to " + args[0]
				+ "...");
		return true;
	}
}

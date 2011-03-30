package me.furt.CraftEssence.commands;

import me.furt.CraftEssence.CraftEssence;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand implements CommandExecutor {
	CraftEssence plugin;

	public TpHereCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.isPlayer(sender)) {
			if (!CraftEssence.Permissions.has((Player) sender,
					"craftessence.tphere")) {
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
		if (args[0].equalsIgnoreCase("*")) {
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (player != p)
					p.teleport(player);
			}
			return true;
		} else {
			if (plugin.playerMatch(args[0]) == null) {
				sender.sendMessage("Player not found");
				return false;
			} else {
				Player p = this.plugin.getServer().getPlayer(args[0]);
				p.teleport(player);
				sender.sendMessage(CraftEssence.premessage + "Teleporting "
						+ player.getDisplayName() + " to " + p.getName() + ".");
				return true;
			}
		}
	}

}

package me.furt.CraftEssence.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.furt.CraftEssence.CraftEssence;

public class SupportCommand implements CommandExecutor {
	CraftEssence plugin;
	public SupportCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.isPlayer(sender)) {
			if (!CraftEssence.Permissions.has((Player) sender, "craftessence.support")) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
		}
		if (args.length < 1) {
			sender.sendMessage(CraftEssence.premessage
					+ "To request help from the staff");
			sender.sendMessage(ChatColor.YELLOW
					+ "type /support, followed by your question.");
			return true;
		} else {
			String msg = plugin.message(args);
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (p.isOp())
					continue;
				p.sendMessage(ChatColor.RED + "[Support]" + ChatColor.GRAY
						+ "<" + ((Player) sender).getDisplayName() + "> "
						+ ChatColor.WHITE + msg);
			}
			return true;
		}
	}
}

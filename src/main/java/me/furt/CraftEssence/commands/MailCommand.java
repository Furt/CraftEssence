package me.furt.CraftEssence.commands;

import java.util.List;

import me.furt.CraftEssence.CraftEssence;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MailCommand implements CommandExecutor {
	CraftEssence plugin;

	public MailCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!plugin.hasPerm(sender, "mail", false)) {
			sender.sendMessage(ChatColor.YELLOW
					+ "You do not have permission to use /" + label);
			return true;
		}

		Player player = (Player) sender;
		if (args.length < 1) {
			return false;
		} else {
			if (args.length == 1 && "read".equalsIgnoreCase(args[0])) {
				List<String> mail = plugin.readMail(player);
				if (mail.isEmpty())
					player.sendMessage(CraftEssence.premessage
							+ "You do not have any mail!");
				else
					for (String s : mail)
						player.sendMessage(s);
			}
			String msg = plugin.message(args);
			if (args.length >= 3 && "send".equalsIgnoreCase(args[0])) {
				plugin.sendMail(player, args[1], msg.split(" +", 3)[2]);
			}
			if (args.length >= 1 && "delete".equalsIgnoreCase(args[0])) {
				plugin.clearMail(player);
			}
		}
		return true;
	}

}

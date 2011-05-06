package me.furt.CraftEssence.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.furt.CraftEssence.CraftEssence;

public class KitCommand implements CommandExecutor {
	CraftEssence plugin;

	public KitCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.isPlayer(sender)) {
			if (!CraftEssence.Permissions.has((Player) sender,
					"craftessence.kit")) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have permission to use that command.");
				return true;
			}
		}
		if (!plugin.isPlayer(sender))
			return false;

		Player player = (Player) sender;
		if (args.length < 1) {
			try {
				List<String> kits = plugin.kitList(player);
				StringBuilder list = new StringBuilder();
				for (String k : kits)
					list.append(" ").append(k);
				player.sendMessage(ChatColor.YELLOW + "Kits:" + ChatColor.WHITE
						+ list.toString());
			} catch (Exception ex) {
				player.sendMessage(CraftEssence.premessage
						+ "There are no valid kits.");
			}
		} else {
			try {
				if (plugin.hasKitRank(player, args) == true) {
					for (String d : plugin.getKit(player, args)) {
						String[] parts = d.split("[^0-9]+", 2);
						int id = Integer.parseInt(parts[0]);
						int amount = parts.length > 1 ? Integer
								.parseInt(parts[1]) : 1;
						player.getWorld().dropItem(player.getLocation(),
								new ItemStack(id, amount));
					}
					player.sendMessage(CraftEssence.premessage + "Giving "
							+ args[0].toLowerCase() + " kit.");
				}
			} catch (Exception ex) {
				player.sendMessage("That kit does not exist");
				player.sendMessage(ex.getMessage());

			}
		}
		return true;
	}

}

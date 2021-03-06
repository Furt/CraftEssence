package me.furt.CraftEssence.commands;

import me.furt.CraftEssence.CraftEssence;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MotdCommand implements CommandExecutor {
	CraftEssence plugin;

	public MotdCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!plugin.hasPerm(sender, "motd", false)) {
			sender.sendMessage(ChatColor.YELLOW
					+ "You do not have permission to use /" + label);
			return true;
		}

		Player player = (Player) sender;
		String[] motd = plugin.getMotd();
		if (motd == null || motd.length < 1) {
			player.sendMessage(ChatColor.GRAY + "No Motd set.");
		} else {
			int intonline = 0;
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if ((p == null) || (!p.isOnline())) {
					continue;
				}
				++intonline;
			}
			String online = intonline + "/"
					+ plugin.getServer().getMaxPlayers();

			String location = (int) player.getLocation().getX() + "x, "
					+ (int) player.getLocation().getY() + "y, "
					+ (int) player.getLocation().getZ() + "z";
			String ip = player.getAddress().getAddress().getHostAddress();

			for (String line : motd) {
				player.sendMessage(plugin.argument(line, new String[] {
						"+dname,+d", "+name,+n", "+location,+l", "+ip",
						"+online" }, new String[] { player.getDisplayName(),
						player.getName(), location, ip, online }));
			}
		}
		return true;
	}

}

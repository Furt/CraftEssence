package me.furt.CraftEssence.commands;

import me.furt.CraftEssence.CraftEssence;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherCommand implements CommandExecutor {

	private final CraftEssence plugin;

	public WeatherCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!plugin.hasPerm(sender, "weather", true)) {
			sender.sendMessage(ChatColor.YELLOW
					+ "You do not have permission to use /" + label);
			return true;
		}

		Player player = (Player) sender;
		World world = player.getWorld();
		if (args[0].equalsIgnoreCase("sunny")) {
			world.setStorm(false);
			world.setThundering(false);
			plugin.getServer().broadcastMessage(
					CraftEssence.premessage + "Weather is set to sunny");
			return true;
		} else if (args[0].equalsIgnoreCase("storm")) {
			world.setStorm(true);
			plugin.getServer().broadcastMessage(
					CraftEssence.premessage + "Weather is set to storm");
			return true;
		} else if (args[0].equalsIgnoreCase("thunder")) {
			world.setThundering(true);
			plugin.getServer().broadcastMessage(
					CraftEssence.premessage + "Weather is set to thundering");
			return true;
		} else {
			player.sendMessage(CraftEssence.premessage
					+ "Invalid weather command parameter");
			return true;
		}
	}

}

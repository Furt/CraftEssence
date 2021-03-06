package me.furt.CraftEssence.commands;

import me.furt.CraftEssence.CraftEssence;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {
	private final CraftEssence plugin;

	public VoteCommand(CraftEssence instance) {
		this.plugin = instance;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.getConfig().getBoolean("ENABLE_VOTE") == false) {
			sender.sendMessage(CraftEssence.premessage
					+ "Voting is currently disabled.");
			return true;
		}

		if (!plugin.hasPerm(sender, "vote", false)) {
			sender.sendMessage(ChatColor.YELLOW
					+ "You do not have permission to use /" + label);
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage("-" + ChatColor.AQUA + "Vote Help"
					+ ChatColor.WHITE + "-");
			sender.sendMessage("/vote day - 'Start a vote to change time to day.'");
			sender.sendMessage("/vote night - Start a vote to change time to night.");
			sender.sendMessage("/vote kick - 'Start a vote to kick a player.'");
			sender.sendMessage("/vote yes - 'To vote yes to a current vote in progress");
			sender.sendMessage("/vote no - 'To vote no to a current vote in progress");
			return true;
		}
		Player player = (Player) sender;
		World world = player.getWorld();
		Server srv = plugin.getServer();
		if (args[0].equalsIgnoreCase("kick")) {
			if (!plugin.hasPerm(sender, "vote.kick", false)) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}

			if (plugin.vote != null) {
				sender.sendMessage("A vote has already been started.");
				return true;
			}

			plugin.vote = args[0] + ":" + args[1];
			plugin.vuser.put(player.getName(), "yes");
			srv.broadcastMessage(CraftEssence.premessage
					+ "A vote has started to kick " + args[1]
					+ ", to vote type '/vote yes' or '/vote no'");
			plugin.startVoteTimer();
			return true;
		} else if (args[0].equalsIgnoreCase("day")) {
			if (!plugin.hasPerm(sender, "vote.day", false)) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
			if (plugin.vote != null) {
				sender.sendMessage(CraftEssence.premessage
						+ "A vote has already been started.");
				return true;
			}
			String wn = world.getName();
			plugin.vote = args[0] + ":" + wn;
			plugin.vuser.put(player.getName(), "yes");
			srv.broadcastMessage(CraftEssence.premessage
					+ "A vote has started to make it day on " + wn
					+ ", to vote type '/vote yes' or '/vote no'");
			plugin.startVoteTimer();
			return true;
		} else if (args[0].equalsIgnoreCase("night")) {
			if (!plugin.hasPerm(sender, "vote.night", false)) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
			if (plugin.vote != null) {
				sender.sendMessage(CraftEssence.premessage
						+ "A vote has already been started.");
				return true;
			}
			String wn = world.getName();
			plugin.vote = args[0] + ":" + wn;
			plugin.vuser.put(player.getName(), "yes");
			plugin.startVoteTimer();
			srv.broadcastMessage(CraftEssence.premessage
					+ "A vote has started to make it night on " + wn
					+ ", to vote type '/vote yes' or '/vote no'");
			return true;
		} else if (args[0].equalsIgnoreCase("yes")) {
			if (plugin.vote == null) {
				sender.sendMessage(CraftEssence.premessage
						+ "There is nothing to vote on.");
				return true;
			}
			if (plugin.vuser.containsKey(player.getName())) {
				sender.sendMessage(CraftEssence.premessage
						+ "You have already voted.");
				return true;
			}
			plugin.vuser.put(player.getName(), "yes");
			// Get total votes
			int yesno = plugin.vuser.values().size();
			srv.broadcastMessage(CraftEssence.premessage + "There are " + yesno
					+ " votes so far.");

			return true;
		} else if (args[0].equalsIgnoreCase("no")) {
			if (plugin.vote == null) {
				sender.sendMessage(CraftEssence.premessage
						+ "There is nothing to vote on.");
				return true;
			}
			return true;
		} else if (args[0].equalsIgnoreCase("clear")) {
			if (!plugin.hasPerm(sender, "vote.clear", false)) {
				sender.sendMessage(ChatColor.YELLOW
						+ "You to dont have proper permissions for that command.");
				return true;
			}
			plugin.vote = null;
			plugin.vuser.clear();
			plugin.getServer().broadcastMessage(
					CraftEssence.premessage
							+ "Current vote timer has been stoped.");
			return true;
		} else {
			if (plugin.vote != null) {
				sender.sendMessage(CraftEssence.premessage
						+ "A vote has already been started.");
				return true;
			} else {
				sender.sendMessage(CraftEssence.premessage
						+ "The option you want to vote on is invalid.");
				return true;
			}
		}
	}
}

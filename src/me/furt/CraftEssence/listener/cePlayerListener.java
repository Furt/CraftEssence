package me.furt.CraftEssence.listener;

import java.util.List;
import me.furt.CraftEssence.CraftEssence;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class cePlayerListener extends PlayerListener {
	private final CraftEssence plugin;

	public cePlayerListener(CraftEssence instance) {
		this.plugin = instance;
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		String player = event.getPlayer().getName();
		String[] banList = plugin.getBans();
		for (String p : banList) {
			if (p.equalsIgnoreCase(player)) {
				event.getResult();
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
						"You are banned from this server!");
			}
		}
	}
	
	/*@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		Location loc = world.getSpawnLocation();
		player.teleport(loc);
	}*/

	public void onPlayerQuit(PlayerQuitEvent event) {
		if (CraftEssence.godmode.contains(event.getPlayer().getName()
				.toLowerCase()))
			CraftEssence.godmode.remove(event.getPlayer().getName()
					.toLowerCase());
	}

	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String prefixData = this.plugin.getPrefix(player);
		if (CraftEssence.muteList.contains(player.getName().toLowerCase())) {
			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + player.getName()
							+ " tried chatting but is muted.");
			event.setCancelled(true);
		}
		if (prefixData != null) {
			String prefix = prefixData.replaceAll("(&([a-f0-9]))", "§$2");
			event.setFormat(event.getFormat().replace("%1$s",
					prefix + "%1$s" + ChatColor.WHITE));
		}
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
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

		List<String> mail = plugin.readMail(player);
		if (mail.isEmpty())
			player.sendMessage(ChatColor.GRAY + "You have no new mail.");
		else
			player.sendMessage(ChatColor.YELLOW + "You have " + mail.size()
					+ " messages! Type /mail read to view your mail.");
	}

	public Player playerMatch(String name) {
		if (plugin.getServer().getOnlinePlayers().length < 1) {
			return null;
		}

		Player[] online = plugin.getServer().getOnlinePlayers();
		Player lastPlayer = null;

		for (Player player : online) {
			String playerName = player.getName();
			String playerDisplayName = player.getDisplayName();

			if (playerName.equalsIgnoreCase(name)) {
				lastPlayer = player;
				break;
			} else if (playerDisplayName.equalsIgnoreCase(name)) {
				lastPlayer = player;
				break;
			}

			if (playerName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
				if (lastPlayer != null) {
					return null;
				}

				lastPlayer = player;
			} else if (playerDisplayName.toLowerCase().indexOf(
					name.toLowerCase()) != -1) {
				if (lastPlayer != null) {
					return null;
				}

				lastPlayer = player;
			}
		}

		return lastPlayer;
	}
}

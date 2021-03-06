package me.furt.CraftEssence.listener;

import java.util.List;

import me.furt.CraftEssence.CraftEssence;
import me.furt.CraftEssence.sql.UserTable;
import me.furt.CraftEssence.sql.WarpTable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

@SuppressWarnings("deprecation")
public class cePlayerListener implements Listener {
	private final CraftEssence plugin;

	public cePlayerListener(CraftEssence instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(final PlayerMoveEvent event) {
		Player player = event.getPlayer();
		String pName = player.getName();
		UserTable ut = plugin.getDatabase().find(UserTable.class).where()
				.ieq("userName", pName).findUnique();
		if (ut.isAfk()) {
			ut.setAfk(false);
			ut.setAfkTime(0);
			plugin.getDatabase().save(ut);
			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + player.getDisplayName()
							+ " is no longer afk");
		}
		this.playerActive(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogin(final PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String pName = player.getName();
		String[] banList = plugin.getBans();
		for (String p : banList) {
			if (p.equalsIgnoreCase(pName)) {
				event.getResult();
				event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
						"You are banned from this server!");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		String world = player.getWorld().getName();
		Location loc = null;
		if (event.isBedSpawn()) {
			loc = event.getRespawnLocation();
		} else {
			WarpTable wt = plugin.getDatabase().find(WarpTable.class).where()
					.ieq("name", "spwn").ieq("world", world).findUnique();
			if (wt != null)
				loc = this.getLocation(wt);

			if (loc == null)
				loc = player.getWorld().getSpawnLocation();
		}

		event.setRespawnLocation(loc);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		String pName = event.getPlayer().getName();
		plugin.users.remove(pName);
		UserTable ut = plugin.getDatabase().find(UserTable.class).where()
				.ieq("userName", pName).findUnique();
		ut.setOnline(false);
		ut.setAfkTime(0);
		event.setQuitMessage(ChatColor.YELLOW + ut.getDisplyName()
				+ " has left the game");
		plugin.getDatabase().save(ut);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(final PlayerChatEvent event) {
		Player player = event.getPlayer();
		String pName = player.getName();
		this.playerActive(event);
		UserTable ut = plugin.getDatabase().find(UserTable.class).where()
				.ieq("userName", pName).findUnique();
		if (ut != null && ut.isMuted()) {
			player.sendMessage(ChatColor.YELLOW
					+ "No one can hear you because your muted.");
			event.setCancelled(true);
		}
		event.setFormat(event.getFormat().replace("%1$s",
				player.getDisplayName()));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String pName = player.getName();

		if (this.newPlayer(event)) {
			Location loc = null;
			WarpTable wt = plugin.getDatabase().find(WarpTable.class).where()
					.ieq("name", "spwn")
					.ieq("world", player.getWorld().getName()).findUnique();
			if (wt != null)
				loc = this.getLocation(wt);

			if (loc == null)
				loc = player.getWorld().getSpawnLocation();
			player.teleport(loc);
		}

		UserTable ut = plugin.getDatabase().find(UserTable.class).where()
				.ieq("userName", pName).findUnique();

		if (ut.getLogins() <= 1)
			plugin.getServer()
					.broadcastMessage(
							ChatColor.GOLD
									+ plugin.getConfig()
											.getString("UNIQUE_MSG"));

		player.setDisplayName(ut.getDisplyName());

		if (ut.isAfk()) {
			ut.setAfk(false);
			plugin.getDatabase().save(ut);
		}

		event.setJoinMessage(ChatColor.YELLOW + player.getDisplayName()
				+ " joined the game");

		plugin.users.put(pName, (System.currentTimeMillis()));

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
				player.sendMessage(plugin.colorizeText(plugin.argument(
						line,
						new String[] { "+dname,+d", "+name,+n", "+location,+l",
								"+ip", "+online" },
						new String[] { player.getDisplayName(),
								player.getName(), location, ip, online })));
			}
		}

		List<String> mail = plugin.readMail(player);

		if (mail.isEmpty())
			player.sendMessage(ChatColor.GRAY + "You have no new mail.");
		else
			player.sendMessage(ChatColor.YELLOW + "You have " + mail.size()
					+ " messages! Type /mail read to view your mail.");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String pName = player.getName();

		UserTable ut = plugin.getDatabase().find(UserTable.class).where()
				.ieq("userName", pName).findUnique();

		if (ut.isAfk()) {
			ut.setAfk(false);
			ut.setAfkTime(0);
			plugin.getDatabase().save(ut);
			plugin.getServer().broadcastMessage(
					ChatColor.YELLOW + player.getDisplayName()
							+ " is no longer afk");
		}

		this.playerActive(event);
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

	public void playerActive(PlayerEvent event) {
		Player player = event.getPlayer();
		String playerName = player.getName();
		plugin.users.put(playerName, System.currentTimeMillis());
	}

	public boolean newPlayer(PlayerEvent event) {
		Player player = event.getPlayer();
		String pName = player.getName();
		UserTable ut = plugin.getDatabase().find(UserTable.class).where()
				.ieq("userName", pName).findUnique();
		if (ut == null) {
			ut = new UserTable();
			ut.setUserName(player.getName());
			ut.setDisplyName(player.getDisplayName());
			ut.setOnline(true);
			ut.setAfk(false);
			ut.setAfkTime(0);
			ut.setMuted(false);
			ut.setLogins(1);
			plugin.getDatabase().save(ut);
			return true;
		} else {
			ut.setOnline(true);
			ut.setLogins(ut.getLogins() + 1);
			plugin.getDatabase().save(ut);
			return false;
		}

	}

	private Location getLocation(WarpTable sl) {
		World world = Bukkit.getServer().getWorld(sl.getWorld());
		return new Location(world, sl.getX(), sl.getY(), sl.getZ(),
				sl.getYaw(), sl.getPitch());
	}
}

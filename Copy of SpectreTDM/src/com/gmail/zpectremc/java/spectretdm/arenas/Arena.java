package com.gmail.zpectremc.java.spectretdm.arenas;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.zpectremc.java.spectretdm.SpectreTDM;
import com.gmail.zpectremc.java.spectretdm.players.PlayerManager;
import com.gmail.zpectremc.java.spectretdm.teams.Team;
import com.gmail.zpectremc.java.spectretdm.teams.TeamManager;

import net.md_5.bungee.api.ChatColor;

public class Arena implements Listener {

	private Map<OfflinePlayer, PlayerManager> players;
	private int maxPlayers = 20;
	private int minPlayers = 2;
	private TeamManager teams;
	private GameState state;
	private String name;
	private int timeRemaining = 300;
	private int timerCount = 30;
	private boolean timerStarted = false;

	protected Arena(String name) {
		this.name = name;
		setState(GameState.LOBBY);
		teams.createNewTeam("RED", ChatColor.RED + "RED");
		teams.createNewTeam("BLUE", ChatColor.BLUE + "BLUE");
	}

	public void endGame() {
		setState(GameState.ENDING);
		for (Team t : teams.getAllTeams())
			t.clearTeam();
		for (OfflinePlayer p : players.keySet()) {
			removePlayer(p);
		}
	}

	public void startGame() {
		for (OfflinePlayer p : players.keySet()) {
			teams.addPlayerToShuffle(p);
		}
		teams.initialShuffle(teams.getTeamByName("RED"), teams.getTeamByName("BLUE"));
		teleportPlayersToSpawns();
	}

	public void waitForPlayers() {
		if (players.size() >= 2 && timerStarted == false) {
			startTimer();
		}
	}

	public void forceEndGame(String reason) {
		endGame();
		// TODO Broadcast message to players in game
	}

	public void setTeamSpawn(Team t, Location l) {
		t.setSpawn(l);
	}

	public void teleportPlayersToSpawns() {
		for (OfflinePlayer p : players.keySet()) {
			Player toTeleport = (Player) p;
			for (Team t : teams.getAllTeams()) {
				if (t.hasPlayer(toTeleport))
					toTeleport.teleport(t.getSpawn());
			}
		}
	}

	public void startTimer() {
		timerStarted = true;
		new BukkitRunnable() {

			@Override
			public void run() {
				if (players.size() < 2) {
					timerStarted = false;
					setTimerCount(30);
					cancel();
					return;
				} else if (getTimerCount() < 0) {
					startGame();
					timerStarted = false;
					setTimerCount(30);
					setState(GameState.INGAME);
					cancel();
					return;
				}
				setTimerCount(getTimerCount() - 1);
				for (OfflinePlayer p : players.keySet()) {
					Player sendTo = (Player) p;
					sendTo.sendMessage("Game begins in " + getTimerCount() + " seconds...");
				}
			}
		}.runTaskTimer(SpectreTDM.getInstance(), 0L, 20L);
	}

	public boolean hasPlayer(OfflinePlayer p) {
		return players.containsKey(p);
	}

	public String getName() {
		return name;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public int getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public Map<OfflinePlayer, PlayerManager> getPlayers() {
		return players;
	}

	public void addPlayer(OfflinePlayer op) {
		players.put(op, new PlayerManager((Player) op));
		players.get(op).overwriteData();
	}

	public void removePlayer(OfflinePlayer op) {
		players.get(op).restore();
		players.remove(op);
	}

	public TeamManager getTeams() {
		return teams;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void tick() {
		teams.rebalanceTeams();
		// QUICK CHECK
		for (OfflinePlayer p : players.keySet()) {
			Player toTest = (Player) p;
			toTest.sendMessage("The tick function is working.");
		}
		// REMOVE THIS LATER
		setTimeRemaining(getTimeRemaining() - 1);
		if (gameNeedsEnding())
			return;
	}

	public boolean checkPlayerSize() {
		if (getState() == GameState.INGAME && players.size() < 2)
			return false;
		return true;
	}

	public boolean gameNeedsEnding() {
		if (!checkPlayerSize()) {
			endGame();
			// TODO reward remaining player
			return true;
		}
		return false;
	}

	public int getTimerCount() {
		return timerCount;
	}

	public void setTimerCount(int timerCount) {
		this.timerCount = timerCount;
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		OfflinePlayer p = e.getPlayer();
		removePlayer(p);
		for (Team t : teams.getAllTeams()) {
			t.removePlayer(p);
		}
		Player toSpawn = (Player) p;
		toSpawn.teleport(toSpawn.getWorld().getSpawnLocation());

	}

	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		Entity target = e.getEntity();
		Entity attacker = e.getDamager();
		if (target instanceof Player && attacker instanceof Player) {
			if (players.containsKey((OfflinePlayer) target) && players.containsKey((OfflinePlayer) attacker))
				if (players.get(target).getTeam() == players.get(attacker).getTeam())
					e.setCancelled(true);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (players.containsKey((OfflinePlayer) p))
			p.teleport(players.get(p).getTeam().getSpawn());
		else
			p.teleport(p.getWorld().getSpawnLocation());
	}
}

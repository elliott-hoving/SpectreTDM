package com.gmail.zpectremc.java.spectretdm.teams;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import com.gmail.zpectremc.java.spectretdm.utils.Utility;
import com.google.common.collect.Iterables;

public class TeamManager {

	private Set<Team> teams;
	private Set<OfflinePlayer> players;

	public TeamManager() {
		this.teams = new HashSet<Team>();
		this.players = new HashSet<OfflinePlayer>();
	}

	public Team createNewTeam(String name) {
		Team team = new Team(name);
		teams.add(team);
		return team;
	}

	public Team createNewTeam(String name, String prefix) {
		Team team = new Team(name, prefix);
		teams.add(team);
		return team;
	}

	public Team getTeamByName(String name) {
		for (Team t : teams)
			if (t.getName().equalsIgnoreCase(name))
				return t;
		return null;
	}

	public boolean teamExists(String name) {
		return getTeamByName(name) != null;
	}

	public boolean doTeamsNeedShuffle() {
		return (getLargestTeam().getSize() - getSmallestTeam().getSize()) > 1;
	}

	public void transferPlayer(OfflinePlayer player, Team to) {
		for (Team t : teams)
			t.removePlayer(player);
		to.addPlayer(player);
	}

	public void rebalanceTeams() {
		while (doTeamsNeedShuffle())
			transferPlayer(Iterables.getFirst(getLargestTeam().getPlayers(), null), getSmallestTeam());
	}

	public Team getLargestTeam() {
		int currentSize = Integer.MIN_VALUE;
		Team currentTeam = null;
		for (Team t : teams)
			if (t.getSize() > currentSize) {
				currentSize = t.getSize();
				currentTeam = t;
			}
		return currentTeam;
	}

	public Team getSmallestTeam() {
		int currentSize = Integer.MAX_VALUE;
		Team currentTeam = null;
		for (Team t : teams)
			if (t.getSize() < currentSize) {
				currentSize = t.getSize();
				currentTeam = t;
			}
		return currentTeam;
	}

	public void scrambleTeams() {
		if (teams.isEmpty())
			return;
		for (Team t : teams)
			t.clearTeam();
	}

	public boolean addPlayerToShuffle(OfflinePlayer player) {
		return players.add(player);
	}

	public OfflinePlayer getNextToShuffle() {
		return Iterables.getFirst(players, null);
	}

	public void initialShuffle(Team one, Team two) {
		if (one.getSize() <= two.getSize()) {
			one.addPlayer(getNextToShuffle());
			Utility.convertToOLP(getNextToShuffle()).sendMessage("You have been added to " + one.getName());
			players.remove(getNextToShuffle());
		} else {
			two.addPlayer(getNextToShuffle());
			Utility.convertToOLP(getNextToShuffle()).sendMessage("You have been added to " + two.getName());
			players.remove(getNextToShuffle());
		}
	}

	public Set<Team> getAllTeams() {
		return teams;
	}
}

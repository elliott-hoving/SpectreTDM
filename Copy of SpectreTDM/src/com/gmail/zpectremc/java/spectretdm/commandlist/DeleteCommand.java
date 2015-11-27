package com.gmail.zpectremc.java.spectretdm.commandlist;

import org.bukkit.command.CommandSender;

import com.gmail.zpectremc.java.spectretdm.commands.Argument;

public class DeleteCommand extends Argument{

	public DeleteCommand() {
		super("delete");
		setAliases("d");
		setPermNode("tdm.delete");
		setArgParams("<arenaName>");
		setUsage(getDefaultUsage());
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		// TODO Auto-generated method stub
		
	}

}

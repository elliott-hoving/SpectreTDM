package com.gmail.zpectremc.java.spectretdm.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.shampaggon.crackshot.CSUtility;

import net.md_5.bungee.api.ChatColor;

public class Utility {
	
	private Utility() {}
	
	private static CSUtility cs = new CSUtility();
	
	@SuppressWarnings("deprecation")
	public static ItemStack createStack(Material material, int amount, int data, String name, String... lore){
		ItemStack is = new ItemStack(material, amount, material.getMaxDurability(), (byte) data);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		for(int i = 0; i < lore.length; i++){
			lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i]);
		}
		List<String> newLore = Arrays.asList(lore);
		meta.setLore(newLore);
		is.setItemMeta(meta);
		return is;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack createStack(Material material, int amount, short damage, int data, String name, String... lore){
		ItemStack is = new ItemStack(material, amount, damage, (byte) data);
		is.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		List<String> newLore = Arrays.asList(lore);
		for(int i = 0; i < newLore.size(); i++){
			is.getItemMeta().getLore().add(ChatColor.translateAlternateColorCodes('&', newLore.get(i)));
		}
		is.getItemMeta().setLore(newLore);
		return is;
	}
	
	public static ItemStack getGun(String s){
		if(cs.generateWeapon(s) == null){
			return createStack(Material.PISTON_EXTENSION, 1, 0, "&cThis gun is broke ¯\\_(ツ)_/¯ whoops.");
		}
		return cs.generateWeapon(s);
	}
	
	public static Player convertToOLP(OfflinePlayer op){
		Player converted = (Player) op;
		return converted;
	}
	
	public CSUtility getCSUtility(){
		return cs;
	}
}

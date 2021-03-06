package com.expl0itz.worldwidechat.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.misc.ActiveTranslator;
import com.expl0itz.worldwidechat.misc.CommonDefinitions;
import com.expl0itz.worldwidechat.misc.SupportedLanguageObject;

public class WWCTabCompleter implements TabCompleter {

	private WorldwideChat main = WorldwideChat.getInstance();
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// Init out list
		List<String> out = new ArrayList<String>();
		
		if (command.getName().equals("wwct")) {
			if (args.length > 0 && args.length < 4) {
				if (args[args.length - 1].isEmpty()) {
					if (args.length == 1) {
						if (main.getActiveTranslator(((Player)sender).getUniqueId().toString()) != null) {
							out.add("stop");
						}
						for (Player eaPlayer : Bukkit.getServer().getOnlinePlayers()) {
							if (!eaPlayer.getName().equals(sender.getName())) {
								out.add(eaPlayer.getName());
							}
						}	
					}
					if (args.length == 2 && Bukkit.getPlayer(args[0]) != null && !args[0].equalsIgnoreCase(sender.getName()) && main.getActiveTranslator(Bukkit.getPlayer(args[0]).getUniqueId().toString()) != null) {
						out.add("stop");
					}
					if (args.length == 1 || (args.length == 2 && !args[0].equalsIgnoreCase(sender.getName()) && (CommonDefinitions.getSupportedTranslatorLang(args[0]) != null || Bukkit.getPlayer(args[0]) != null)) || (args.length == 3 && CommonDefinitions.getSupportedTranslatorLang(args[1]) != null && !args[0].equalsIgnoreCase(sender.getName()) && Bukkit.getPlayer(args[0]) != null)) {
						for (SupportedLanguageObject eaObj : main.getSupportedTranslatorLanguages()) {
							out.add(eaObj.getLangName());
							out.add(eaObj.getLangCode());
						}
					}
				} else {
					if (args.length == 1) {
						if (main.getActiveTranslator(((Player)sender).getUniqueId().toString()) != null) {
							if ("stop".startsWith(args[0].toLowerCase())) {
								out.add("stop");
							}
						}
						for (Player eaPlayer: Bukkit.getServer().getOnlinePlayers()) {
							if (!eaPlayer.getName().equals(sender.getName()) && eaPlayer.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
								out.add(eaPlayer.getName());
							}
						}
					}
					if (args.length == 2) {
						if (Bukkit.getPlayer(args[0]) != null && !args[0].equalsIgnoreCase(sender.getName()) && main.getActiveTranslator(Bukkit.getPlayer(args[0]).getUniqueId().toString()) != null) {
							if ("stop".startsWith(args[args.length - 1].toLowerCase())) {
								out.add("stop");
							}
						}
						
					}
					if (args.length == 1 || (args.length == 2 && !args[0].equalsIgnoreCase(sender.getName()) && (CommonDefinitions.getSupportedTranslatorLang(args[0]) != null || Bukkit.getPlayer(args[0]) != null)) || (args.length == 3 && CommonDefinitions.getSupportedTranslatorLang(args[1]) != null && !args[0].equalsIgnoreCase(sender.getName()) && Bukkit.getPlayer(args[0]) != null)) {
						for (SupportedLanguageObject eaObj : main.getSupportedTranslatorLanguages()) {
							if (eaObj.getLangName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
								out.add(eaObj.getLangName());
							}
							if (eaObj.getLangCode().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
								out.add(eaObj.getLangCode());
							}
						}
					}
				}
			}
		} else if (command.getName().equals("wwcg")) {
			if (args.length > 0 && args.length < 3) {
				if (args[args.length - 1].isEmpty()) {
					if (args.length == 1) {
						if (main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") != null) {
							out.add("stop");
						}
					}
					if (args.length == 1 || (args.length == 2 && CommonDefinitions.getSupportedTranslatorLang(args[0]) != null)) {
						for (SupportedLanguageObject eaObj : main.getSupportedTranslatorLanguages()) {
							out.add(eaObj.getLangName());
							out.add(eaObj.getLangCode());
					    }
					}
				} else {
					if (args.length == 1) {
						if ("stop".startsWith(args[0].toLowerCase()) && main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") != null) {
							out.add("stop");
						}
					}
					if (args.length == 1 || (args.length == 2 && CommonDefinitions.getSupportedTranslatorLang(args[0]) != null)) {
						for (SupportedLanguageObject eaObj : main.getSupportedTranslatorLanguages()) {
							if (eaObj.getLangName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
								out.add(eaObj.getLangName());
							}
							if (eaObj.getLangCode().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
								out.add(eaObj.getLangCode());
							}
						}
					}
				}
			}
		} else if (command.getName().equals("wwcts") || command.getName().equals("wwctb")) {
			if (args.length == 1) {
				if (args[0].isEmpty()) {
					synchronized (main.getActiveTranslators()) {
						for (ActiveTranslator eaTranslator : main.getActiveTranslators()) {
							if (!eaTranslator.getUUID().equals("GLOBAL-TRANSLATE-ENABLED") && !eaTranslator.getUUID().equals(((Player)sender).getUniqueId().toString()) && Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())) != null) {
								out.add(Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())).getName());
							}
						}
					}
				} else {
					synchronized (main.getActiveTranslators()) {
						for (ActiveTranslator eaTranslator : main.getActiveTranslators()) {
							if (!eaTranslator.getUUID().equals("GLOBAL-TRANSLATE-ENABLED") && !eaTranslator.getUUID().equals(((Player)sender).getUniqueId().toString()) && Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())) != null && (Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())).getName()).toLowerCase().startsWith(args[0].toLowerCase())) {
								out.add(Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())).getName());
							}
						}
					}
				}
			}
		} else if (command.getName().equals("wwcs")) {
			if (args.length == 1) {
				if (args[0].isEmpty()) {
					for (Player eaPlayer : Bukkit.getServer().getOnlinePlayers()) {
						out.add(eaPlayer.getName());
					}
				} else {
					for (Player eaPlayer : Bukkit.getServer().getOnlinePlayers()) {
						if (eaPlayer.getName().toLowerCase().startsWith(args[0].toLowerCase()) && !eaPlayer.getName().equals(sender.getName())) {
							out.add(eaPlayer.getName());
						}
					}
				}
			}
		} else if (command.getName().equals("wwcrl")) {
			if (args.length > 0 && args.length < 3) {
				if (args[args.length - 1].isEmpty()) {
					if (args.length == 1) {
						synchronized (main.getActiveTranslators()) {
							for (ActiveTranslator eaTranslator : main.getActiveTranslators()) {
								if (!eaTranslator.getUUID().equals("GLOBAL-TRANSLATE-ENABLED") && !eaTranslator.getUUID().equals(((Player)sender).getUniqueId().toString()) && Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())) != null) {
									out.add(Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())).getName());
								}
							}
						}
					}
				} else {
					if (args.length == 1) {
						synchronized (main.getActiveTranslators()) {
							for (ActiveTranslator eaTranslator : main.getActiveTranslators()) {
								if (!eaTranslator.getUUID().equals("GLOBAL-TRANSLATE-ENABLED") && !eaTranslator.getUUID().equals(((Player)sender).getUniqueId().toString()) && Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())) != null && (Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())).getName()).toLowerCase().startsWith(args[0].toLowerCase())) {
									out.add(Bukkit.getPlayer(UUID.fromString(eaTranslator.getUUID())).getName());
								}
							}
						}
					}
				}
				if (args.length == 1 || (args.length == 2 && !args[0].matches("[0-9]+") && Bukkit.getPlayer(args[0]) != null && main.getActiveTranslator(Bukkit.getPlayer(args[0]).getUniqueId().toString()) != null)) {
					out.add("0");
					out.add("3");
					out.add("5");
					out.add("10");
				}
			}
		}
		return out;
	}

}

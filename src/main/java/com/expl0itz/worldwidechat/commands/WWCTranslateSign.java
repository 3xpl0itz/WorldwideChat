package com.expl0itz.worldwidechat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.misc.ActiveTranslator;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class WWCTranslateSign extends BasicCommand{

    private WorldwideChat main = WorldwideChat.getInstance();
    
    public WWCTranslateSign(CommandSender sender, Command command, String label, String[] args) {
        super(sender, command, label, args);
    }
    
    //Permission: worldwidechat.wwcts, worldwidechat.wwcts.otherplayers
    
    public boolean processCommand() {
        /* Check args */
        Audience adventureSender = main.adventure().sender(sender);
        if (args.length > 1) {
            final TextComponent invalidArgs = Component.text()
                    .append(main.getPluginPrefix().asComponent())
                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctInvalidArgs")).color(NamedTextColor.RED))
                    .build();
                adventureSender.sendMessage(invalidArgs);
            return false;
        }
        
        /* If no args are provided */
        if (args.length == 0) {
            if (main.getActiveTranslator(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString()) != null) {
                /* Enable book translation for sender! */
                ActiveTranslator currentTranslator = main.getActiveTranslator(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString());
                currentTranslator.setTranslatingSign(!currentTranslator.getTranslatingSign());
                if (currentTranslator.getTranslatingSign()) {
                    final TextComponent toggleTranslation = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsOnSender")).color(NamedTextColor.LIGHT_PURPLE))
                            .build();
                        adventureSender.sendMessage(toggleTranslation);
                } else {
                    final TextComponent toggleTranslation = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsOffSender")).color(NamedTextColor.LIGHT_PURPLE))
                            .build();
                        adventureSender.sendMessage(toggleTranslation);
                }
                return true;
            } else {
                // Player is not an active translator
                final TextComponent notATranslator = Component.text()
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsNotATranslator")).color(NamedTextColor.RED))
                        .build();
                    adventureSender.sendMessage(notATranslator);
                return true;
            }
        }
        
        /* If there is an argument (another player)*/
        if (args.length == 1) {
            if (sender.hasPermission("worldwidechat.wwcts.otherplayers")) {
                if (args[0] != sender.getName() && args[0] instanceof String && Bukkit.getServer().getPlayer(args[0]) != null && main.getActiveTranslator(Bukkit.getServer().getPlayer(args[0]).getUniqueId().toString()) != null && !(args[0].equals(sender.getName()))) {
                    /* Enable book translation for target! */
                    ActiveTranslator currentTranslator = main.getActiveTranslator(Bukkit.getServer().getPlayer(args[0]).getUniqueId().toString());
                    currentTranslator.setTranslatingSign(!currentTranslator.getTranslatingSign());
                    Audience targetSender = main.adventure().player(Bukkit.getServer().getPlayer(args[0]));
                    if (currentTranslator.getTranslatingSign()) {
                        final TextComponent toggleTranslation = Component.text()
                                .append(main.getPluginPrefix().asComponent())
                                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsOnTarget").replace("%i", args[0])).color(NamedTextColor.LIGHT_PURPLE))
                                .build();
                            adventureSender.sendMessage(toggleTranslation);
                        final TextComponent toggleTranslationTarget = Component.text()
                                .append(main.getPluginPrefix().asComponent())
                                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsOnSender")).color(NamedTextColor.LIGHT_PURPLE))
                                .build();
                            targetSender.sendMessage(toggleTranslationTarget);     
                    } else {
                        final TextComponent toggleTranslation = Component.text()
                                .append(main.getPluginPrefix().asComponent())
                                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsOffTarget").replace("%i", args[0])).color(NamedTextColor.LIGHT_PURPLE))
                                .build();
                            adventureSender.sendMessage(toggleTranslation);
                        final TextComponent toggleTranslationTarget = Component.text()
                                .append(main.getPluginPrefix().asComponent())
                                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsOffSender")).color(NamedTextColor.LIGHT_PURPLE))
                                .build();
                            targetSender.sendMessage(toggleTranslationTarget);
                    }
                } else {
                    // If target is not a string, active translator, or is themselves:
                    final TextComponent notAPlayer = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctsPlayerNotFound").replace("%i", args[0])).color(NamedTextColor.RED))
                            .build();
                        adventureSender.sendMessage(notAPlayer);
                    return false;
                }
            } else {
                final TextComponent badPerms = Component.text() //Bad perms
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcBadPerms")).color(NamedTextColor.RED))
                        .append(Component.text().content(" (" + "worldwidechat.wwcts.otherplayers" + ")").color(NamedTextColor.LIGHT_PURPLE))
                        .build();
                    adventureSender.sendMessage(badPerms);
                    return true;
            }
        }
        return true;
    }
}

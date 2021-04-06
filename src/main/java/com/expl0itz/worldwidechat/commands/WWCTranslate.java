package com.expl0itz.worldwidechat.commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.misc.ActiveTranslator;
import com.expl0itz.worldwidechat.misc.CommonDefinitions;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class WWCTranslate extends BasicCommand {

    private WorldwideChat main = WorldwideChat.getInstance();
    
    public WWCTranslate(CommandSender sender, Command command, String label, String[] args) {
        super(sender, command, label, args);
    }

    /*
     * Correct Syntax: /wwct <id-in> <id-out>
     * EX for id: en, ar, cs, nl, etc.
     * id MUST be valid, we will check with CommonDefinitions class
     */

    public boolean processCommand(boolean isGlobal) {
        /* Sanity checks */
        Audience adventureSender = main.adventure().sender(sender);
        ActiveTranslator currTarget = main.getActiveTranslator(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString());
        if (!isGlobal && currTarget instanceof ActiveTranslator) { //Don't let a person be multiple ActiveTranslator objs
            main.removeActiveTranslator(currTarget);
            final TextComponent chatTranslationStopped = Component.text()
                .append(main.getPluginPrefix().asComponent())
                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctTranslationStopped")).color(NamedTextColor.LIGHT_PURPLE))
                .build();
            //Delete their userData file
            File fileToBeDeleted = main.getConfigManager().getUserSettingsFile(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString());
            if (fileToBeDeleted != null) {
                fileToBeDeleted.delete();
            }
            adventureSender.sendMessage(chatTranslationStopped);
            if (args.length == 0 || args[0].equalsIgnoreCase("Stop")) {
                return true;
            }
        } else if (isGlobal && main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) //If /wwcg is called
        {
            main.removeActiveTranslator(main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED"));
            final TextComponent chatTranslationStopped = Component.text()
                .append(main.getPluginPrefix().asComponent())
                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcgTranslationStopped")).color(NamedTextColor.LIGHT_PURPLE))
                .build();
            //Delete global data file
            File fileToBeDeleted = main.getConfigManager().getUserSettingsFile("GLOBAL-TRANSLATE-ENABLED");
            if (fileToBeDeleted != null) {
                fileToBeDeleted.delete();
            }
            for (Player eaPlayer: Bukkit.getOnlinePlayers()) {
                main.adventure().sender(eaPlayer).sendMessage(chatTranslationStopped);
            }
            if (args.length == 0 || args[0].equalsIgnoreCase("Stop")) {
                return true;
            }
        }

        /* Sanitize args */
        if (args.length == 0 || args.length > 3) {
            //Not enough/too many args
            final TextComponent invalidArgs = Component.text()
                .append(main.getPluginPrefix().asComponent())
                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctInvalidArgs")).color(NamedTextColor.RED))
                .build();
            adventureSender.sendMessage(invalidArgs);
            return false;
        }
        
        /* Check if already running on another player; Sanity Checks P2 */
        Player testPlayer = Bukkit.getServer().getPlayer(args[0]);
        if (testPlayer != null && main.getActiveTranslator(testPlayer.getUniqueId().toString()) instanceof ActiveTranslator) {
            Audience targetSender = main.adventure().sender(testPlayer);
            main.removeActiveTranslator(main.getActiveTranslator(testPlayer.getUniqueId().toString()));
            //Delete target player's existing config file
            File fileToBeDeleted = main.getConfigManager().getUserSettingsFile(testPlayer.getUniqueId().toString());
            if (fileToBeDeleted != null) {
                fileToBeDeleted.delete();
            }
            final TextComponent chatTranslationStopped = Component.text()
                    .append(main.getPluginPrefix().asComponent())
                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctTranslationStopped")).color(NamedTextColor.LIGHT_PURPLE))
                    .build();
            targetSender.sendMessage(chatTranslationStopped);
            final TextComponent chatTranslationStoppedOtherPlayer = Component.text()
                .append(main.getPluginPrefix().asComponent())
                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctTranslationStoppedOtherPlayer").replace("%i", args[0])).color(NamedTextColor.LIGHT_PURPLE))
                .build();
            adventureSender.sendMessage(chatTranslationStoppedOtherPlayer);
            if (args.length == 1 || args[1].equalsIgnoreCase("Stop")) {
                return true;
            }
        }
        
        /* Process input */
        CommonDefinitions defs = new CommonDefinitions();
        if (args[0] instanceof String && args.length == 1) {
            if (defs.isSupportedLangForTarget(args[0], main.getTranslatorName())) {
                //We got a valid lang code, continue and add player to ArrayList
                if (!isGlobal) //Not global
                {
                    main.addActiveTranslator(new ActiveTranslator(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(),
                        "None",
                        args[0],
                        false));
                    final TextComponent autoTranslate = Component.text()
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctAutoTranslateStart").replace("%o", args[0])).color(NamedTextColor.LIGHT_PURPLE))
                        .build();
                    main.getConfigManager().createUserDataConfig(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(), "None", args[0]);
                    adventureSender.sendMessage(autoTranslate);
                    return true;
                } else { //Is global
                    main.addActiveTranslator(new ActiveTranslator("GLOBAL-TRANSLATE-ENABLED",
                        "None",
                        args[0],
                        false));
                    final TextComponent autoTranslate = Component.text()
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcgAutoTranslateStart").replace("%o", args[0])).color(NamedTextColor.LIGHT_PURPLE))
                        .build();
                    main.getConfigManager().createUserDataConfig("GLOBAL-TRANSLATE-ENABLED", "None", args[0]);
                    for (Player eaPlayer: Bukkit.getOnlinePlayers()) {
                        main.adventure().sender(eaPlayer).sendMessage(autoTranslate);
                    }
                    return true;
                }
            }
        } else if (args[0] instanceof String && args[1] instanceof String && args.length == 2) {
            if (defs.isSupportedLangForSource(args[0], main.getTranslatorName())) {
                if (defs.isSupportedLangForTarget(args[1], main.getTranslatorName())) {
                    //We got a valid lang code 2x, continue and add player to ArrayList
                    if (args[0].equalsIgnoreCase(args[1]) || (defs.isSameLang(args[0], args[1], main.getTranslatorName()))) {
                        final TextComponent sameLangError = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctSameLangError")).color(NamedTextColor.RED))
                            .build();
                        adventureSender.sendMessage(sameLangError);
                        return false;
                    }
                    if (!isGlobal) //Not global
                    {
                        //Add Player to ArrayList
                        main.addActiveTranslator(new ActiveTranslator(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(),
                            args[0],
                            args[1],
                            false));
                        //Add player's data to their own config in /data
                        main.getConfigManager().createUserDataConfig(Bukkit.getServer().getPlayer(sender.getName()).getUniqueId().toString(), args[0], args[1]);
                        final TextComponent langToLang = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctLangToLangStart").replace("%i", args[0]).replace("%o", args[1])).color(NamedTextColor.LIGHT_PURPLE))
                            .build();
                        adventureSender.sendMessage(langToLang);
                    } else {
                        main.addActiveTranslator(new ActiveTranslator("GLOBAL-TRANSLATE-ENABLED",
                            args[0],
                            args[1],
                            false));
                        final TextComponent langToLang = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcgLangToLangStart").replace("%i", args[0]).replace("%o", args[1])).color(NamedTextColor.LIGHT_PURPLE))
                            .build();
                        //Add global data to its own config in /data
                        main.getConfigManager().createUserDataConfig("GLOBAL-TRANSLATE-ENABLED", args[0], args[1]);
                        for (Player eaPlayer: Bukkit.getOnlinePlayers()) {
                            Audience eaAudience = main.adventure().sender(eaPlayer);
                            eaAudience.sendMessage(langToLang);
                        }
                    }
                    return true;
                }
            } else if (Bukkit.getServer().getPlayer(args[0]) != null && (!(args[0].equals(sender.getName())))) { /* First arg is another player who is not ourselves...*/
                if (sender.hasPermission("worldwidechat.wwct.otherplayers")) {
                    if (defs.isSupportedLangForTarget(args[1], main.getTranslatorName())) {
                    //Add target player to ArrayList
                    main.addActiveTranslator(new ActiveTranslator(Bukkit.getServer().getPlayer(args[0]).getUniqueId().toString(),
                        "None",
                        args[1],
                        false));
                    main.getConfigManager().createUserDataConfig(Bukkit.getServer().getPlayer(args[0]).getUniqueId().toString(), "None", args[1]);
                    final TextComponent autoTranslateOtherPlayer = Component.text()
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctAutoTranslateStartOtherPlayer").replace("%i", args[0]).replace("%o", args[1])).color(NamedTextColor.LIGHT_PURPLE))
                        .build();
                    adventureSender.sendMessage(autoTranslateOtherPlayer);
                    final TextComponent autoTranslate = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctAutoTranslateStart").replace("%o", args[1])).color(NamedTextColor.LIGHT_PURPLE))
                            .build();
                    main.adventure().sender(main.getServer().getPlayer(args[0])).sendMessage(autoTranslate);
                    return true;
                    }
                } else { //Bad Perms
                    final TextComponent badPerms = Component.text()
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcBadPerms")).color(NamedTextColor.RED))
                            .append(Component.text().content(" (" + "worldwidechat.wwct.otherplayers" + ")").color(NamedTextColor.LIGHT_PURPLE))
                            .build();
                        adventureSender.sendMessage(badPerms);
                    return true;
                }
            }
        } else if (args[0] instanceof String && args[1] instanceof String && args[2] instanceof String && args.length == 3) {
            //If args[0] == a player, see if sender has perms to change player translation preferences
            if (sender.hasPermission("worldwidechat.wwct.otherplayers")) {
                if (Bukkit.getServer().getPlayer(args[0]) != null && (!(args[0].equals(sender.getName())))) {
                    if (defs.isSupportedLangForSource(args[1], main.getTranslatorName())) {
                        if (defs.isSupportedLangForTarget(args[2], main.getTranslatorName())) {
                            //We got a valid lang code 2x, continue and add player to ArrayList
                            if (args[1].equalsIgnoreCase(args[2]) || (defs.isSameLang(args[1], args[2], main.getTranslatorName()))) {
                                final TextComponent sameLangError = Component.text()
                                    .append(main.getPluginPrefix().asComponent())
                                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctSameLangError")).color(NamedTextColor.RED))
                                    .build();
                                adventureSender.sendMessage(sameLangError);
                                return false;
                            }
                             //Add target user to ArrayList with input + output lang
                             main.addActiveTranslator(new ActiveTranslator(Bukkit.getServer().getPlayer(args[0]).getUniqueId().toString(),
                                 args[1],
                                 args[2],
                                 false));
                             //Add target user to config
                             main.getConfigManager().createUserDataConfig(Bukkit.getServer().getPlayer(args[0]).getUniqueId().toString(), args[1], args[2]);
                             final TextComponent langToLangOtherPlayer = Component.text()
                                 .append(main.getPluginPrefix().asComponent())
                                 .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctLangToLangStartOtherPlayer").replace("%o", args[0]).replace("%i", args[1]).replace("%e", args[2])).color(NamedTextColor.LIGHT_PURPLE))
                                 .build();
                             adventureSender.sendMessage(langToLangOtherPlayer);
                             final TextComponent langToLang = Component.text()
                                     .append(main.getPluginPrefix().asComponent())
                                     .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctLangToLangStart").replace("%i", args[1]).replace("%o", args[2])).color(NamedTextColor.LIGHT_PURPLE))
                                     .build();
                             main.adventure().sender(main.getServer().getPlayer(args[0])).sendMessage(langToLang);
                             return true;
                        }
                    }
                } else {
                    final TextComponent playerNotFound = Component.text() //Target player not found
                            .append(main.getPluginPrefix().asComponent())
                            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcPlayerNotFound").replace("%i", args[0])).color(NamedTextColor.RED))
                            .build();
                        adventureSender.sendMessage(playerNotFound);
                        return true;
                }
            } else {
                final TextComponent badPerms = Component.text() //Bad perms
                    .append(main.getPluginPrefix().asComponent())
                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcBadPerms")).color(NamedTextColor.RED))
                    .append(Component.text().content(" (" + "worldwidechat.wwct.otherplayers" + ")").color(NamedTextColor.LIGHT_PURPLE))
                    .build();
                adventureSender.sendMessage(badPerms);
                return true;
            }
        }
        
        /* If we got here, invalid lang code */
        final TextComponent invalidLangCode = Component.text()
            .append(main.getPluginPrefix().asComponent())
            .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwctInvalidLangCode").replace("%o", defs.getValidLangCodes())).color(NamedTextColor.RED))
            .build();
        adventureSender.sendMessage(invalidLangCode);
        return false;
    }

}
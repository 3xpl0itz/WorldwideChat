package com.expl0itz.worldwidechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ibm.cloud.sdk.core.service.exception.NotFoundException;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.googletranslate.GoogleTranslateTranslation;
import com.expl0itz.worldwidechat.misc.ActiveTranslator;
import com.expl0itz.worldwidechat.watson.WatsonTranslation;
import com.google.cloud.translate.TranslateException;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ChatListener implements Listener {

    private WorldwideChat main = WorldwideChat.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (main.getActiveTranslator(event.getPlayer().getUniqueId().toString()) instanceof ActiveTranslator || main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) {
            ActiveTranslator currPlayer;
            if (!(main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator)) {
                //This UDID is never valid, but we can use it as a less elegant way to check if global translate (/wwcg) is enabled.
                currPlayer = main.getActiveTranslator(event.getPlayer().getUniqueId().toString());
            } else {
                currPlayer = main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED");
            }
            if (main.getTranslatorName().equals("Watson")) {
                try {
                WatsonTranslation watsonInstance = new WatsonTranslation(event.getMessage(),
                    currPlayer.getInLangCode(),
                    currPlayer.getOutLangCode(),
                    main.getConfigManager().getMainConfig().getString("Translator.watsonAPIKey"),
                    main.getConfigManager().getMainConfig().getString("Translator.watsonURL"),
                    event.getPlayer());
                //Get username + pass from config
                event.setMessage(watsonInstance.translate());
                } catch (NotFoundException lowConfidenceInAnswer) {
                    /* This exception happens if the Watson translator is auto-detecting the input language.
                     * By definition, the translator is unsure if the source language detected is accurate due to 
                     * confidence levels being below a certain threshold.
                     * Usually, either already translated input is given or occasionally a phrase is not fully translatable.
                     * This is where we catch that and send the player a message telling them that their message was unable to be
                     * parsed by the translator.
                     * You should be able to turn this off in the config.
                     */
                    final TextComponent lowConfidence = Component.text()
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.watsonNotFoundExceptionNotification")).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, true))
                        .build();
                    main.adventure().sender(event.getPlayer()).sendMessage(lowConfidence);
                }
            } else if (main.getTranslatorName().equals("Google Translate")) {
                try {
                    GoogleTranslateTranslation googleTranslateInstance = new GoogleTranslateTranslation(event.getMessage(),
                        currPlayer.getInLangCode(),
                        currPlayer.getOutLangCode(),
                        event.getPlayer());
                    event.setMessage(googleTranslateInstance.translate());
                } catch (TranslateException e) {
                    /* This exception happens for the same reason that Watson does: low confidence.
                     * Usually when a player tries to get around our same language translation block.
                     * Examples of when this triggers:
                     * .wwct en and typing in English.
                     */
                    final TextComponent lowConfidence = Component.text()
                        .append(main.getPluginPrefix().asComponent())
                        .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.watsonNotFoundExceptionNotification")).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, true))
                        .build();
                    main.adventure().sender(event.getPlayer()).sendMessage(lowConfidence);
                }
            }
        }
    }

}
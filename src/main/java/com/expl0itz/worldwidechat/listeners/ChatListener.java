package com.expl0itz.worldwidechat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.misc.ActiveTranslator;
import com.expl0itz.worldwidechat.misc.CommonDefinitions;

public class ChatListener implements Listener {

    private WorldwideChat main = WorldwideChat.getInstance();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!main.isReloading() && main.getActiveTranslator(event.getPlayer().getUniqueId().toString()) instanceof ActiveTranslator || main.getActiveTranslator("GLOBAL-TRANSLATE-ENABLED") instanceof ActiveTranslator) {
        	event.setMessage(CommonDefinitions.translateText(event.getMessage(), event.getPlayer())); 
        }
    }
}
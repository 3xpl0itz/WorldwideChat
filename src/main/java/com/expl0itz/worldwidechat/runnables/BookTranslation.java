package com.expl0itz.worldwidechat.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.expl0itz.worldwidechat.WorldwideChat;
import com.expl0itz.worldwidechat.misc.CommonDefinitions;

import co.aikar.taskchain.TaskChainTasks.Task;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class BookTranslation implements Task<ItemStack, ItemStack>{

    private PlayerInteractEvent event;
    private WorldwideChat main = WorldwideChat.getInstance();
    
    public BookTranslation(PlayerInteractEvent e) {
        event = e;
    }

    @Override
    public ItemStack run(ItemStack currentBook) {
        /* Init vars */
        BookMeta meta = (BookMeta) currentBook.getItemMeta();
        List<String> pages = meta.getPages();
        List<String> translatedPages = new ArrayList<String>();
        boolean sameResult = false;
        
        /* Translate pages */
        for (String eaPage : pages) {
            String out = CommonDefinitions.translateText(eaPage, event.getPlayer());
            if (out.equals("") || out.equalsIgnoreCase(eaPage)) {
                sameResult = true;
                out = main.getConfigManager().getMessagesConfig().getString("Messages.wwctbTranslatePageFail").replace("%i", eaPage);
            }
            translatedPages.add(out);
        }
        
        if (!sameResult && currentBook != null) {
            /* Set completed message */
            final TextComponent bookDone = Component.text()
                .append(main.getPluginPrefix().asComponent())
                .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcBookDone")).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, true))
                .build();
            main.adventure().sender(event.getPlayer()).sendMessage(bookDone);
        }
        else if (sameResult) {
            /* If we are here, one or more translations was unsuccessful */
            final TextComponent translationNoticeMsg = Component.text()
                    .append(main.getPluginPrefix().asComponent())
                    .append(Component.text().content(main.getConfigManager().getMessagesConfig().getString("Messages.wwcBookTranslationFail").replace("%i", main.getTranslatorName())).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, true))
                    .build();
                main.adventure().sender(event.getPlayer()).sendMessage(translationNoticeMsg);
        }
        
        /* Create the modified book */
        ItemStack newBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta newMeta = (BookMeta) newBook.getItemMeta();
            newMeta.setAuthor(meta.getAuthor());
            newMeta.setGeneration(meta.getGeneration());
            newMeta.setTitle(meta.getTitle());
            newMeta.setPages(translatedPages);
            newBook.setItemMeta(newMeta);
        getCurrentChain().setTaskData("translatedBook", newBook);
        return newBook;
    }

}

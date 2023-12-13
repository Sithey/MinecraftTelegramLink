package net.sithey.minecrafttelegramlink.listener;

import net.sithey.minecrafttelegramlink.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Main.get().getTelegramBot().sendJoinMessage(event.getPlayer());

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        Main.get().getTelegramBot().sendQuitMessage(event.getPlayer());

    }

}

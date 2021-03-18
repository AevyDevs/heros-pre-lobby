package net.herospvp.prelobby.listener;

import com.connorlinfoot.titleapi.TitleAPI;
import net.herospvp.prelobby.PreLobby;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OutEvents implements Listener {

    public OutEvents(PreLobby preLobby) {
        preLobby.getServer().getPluginManager().registerEvents(this, preLobby);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        TitleAPI.clearTitle(event.getPlayer());
    }

}

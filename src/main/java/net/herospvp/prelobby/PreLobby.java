package net.herospvp.prelobby;

import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import lombok.Getter;
import net.herospvp.prelobby.api.IP2C;
import net.herospvp.prelobby.elements.PBank;
import net.herospvp.prelobby.listener.InEvents;
import net.herospvp.prelobby.listener.OutEvents;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PreLobby extends JavaPlugin {

    private PBank pBank;
    private IP2C ip2C;

    private BungeeChannelApi bungeeChannelApi;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        pBank = new PBank();
        ip2C = new IP2C(this);

        this.bungeeChannelApi = BungeeChannelApi.of(this);

        new InEvents(this);
        new OutEvents(this);
    }

    @Override
    public void onDisable() {
    }

}
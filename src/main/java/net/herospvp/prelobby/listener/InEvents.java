package net.herospvp.prelobby.listener;

import com.connorlinfoot.titleapi.TitleAPI;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import net.herospvp.prelobby.PreLobby;
import net.herospvp.prelobby.api.IP2C;
import net.herospvp.prelobby.elements.PBank;
import net.herospvp.prelobby.elements.PPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class InEvents implements Listener {

    private final PreLobby preLobby;
    private final List<String> blacklistedNames;
    private final PBank pBank;
    private final IP2C ip2C;
    private final BungeeChannelApi bungeeChannelApi;

    private final String kickMessage;

    private final Location safeLocation, unsafeLocation;

    public InEvents(PreLobby preLobby) {
        this.preLobby = preLobby;
        this.pBank = preLobby.getPBank();
        this.ip2C = preLobby.getIp2C();
        this.bungeeChannelApi = preLobby.getBungeeChannelApi();

        this.blacklistedNames = preLobby.getConfig().getStringList("antibot.blacklisted-names");
        this.kickMessage = ChatColor.RED + "Mi spiace, non hai passato la verifica!";

        World world = preLobby.getServer().getWorld("lobby");
        this.safeLocation = new Location(world, 0.5, 70, 0.5);
        this.unsafeLocation = new Location(world, 0.5, 120, 0.5);

        preLobby.getServer().getPluginManager().registerEvents(this, preLobby);
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();

        Optional<String> res = blacklistedNames.parallelStream()
                .filter(name::contains)
                .findAny();

        if (!res.isPresent()) {
            return;
        }

        event.setKickMessage(kickMessage);
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String ip = player.getAddress().getAddress().getHostAddress();

        PPlayer pPlayer = pBank.get(player);
        if (pPlayer == null) {
            pPlayer = new PPlayer(
                    player.getName(),
                    player.getAddress().getAddress().getHostAddress(),
                    false,
                    false
            );
            pBank.add(pPlayer);
        }
        else {
            if (!pPlayer.getIp().equals(ip)) {
                pPlayer.setConnectionSafe(false);
                pPlayer.setIp(ip);
            }
        }

        PPlayer finalPPlayer = pPlayer;

        if (!pPlayer.isConnectionSafe()) {
            CompletableFuture.supplyAsync(() -> {
                boolean res = ip2C.makeRequest(ip);
                finalPPlayer.setConnectionSafe(res);
                return null;
            });
        }

        preLobby.getServer().getScheduler().runTaskLater(preLobby, () -> {
            if (!player.isOnline()) {
                return;
            }
            if (!finalPPlayer.isConnectionSafe()) {
                player.kickPlayer(kickMessage);
                return;
            }
            if (player.getLocation().getY() > 72) {
                finalPPlayer.setBehaviourSafe(false);
                player.kickPlayer(kickMessage);
                return;
            }
            verificationComplete(player);
        }, 100L);

        TitleAPI.sendTitle(player, 10, 60, 10, "", ChatColor.RED + "Verifica della connessione...");
        player.teleport(pPlayer.isBehaviourSafe() ? safeLocation : unsafeLocation);

        event.setJoinMessage(null);
        player.setGameMode(GameMode.ADVENTURE);
    }

    private void verificationComplete(Player player) {
        TitleAPI.sendTitle(player, 10, 60, 10, "", ChatColor.GREEN + "Verifica completata!");
        preLobby.getServer().getScheduler().runTaskLater(preLobby, () -> bungeeChannelApi.connect(player, "lobby-1"), 20L);
    }

    @EventHandler (ignoreCancelled = true)
    public void on(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Mi spiace, non e' possibile mandare messaggi qui!");
    }

    @EventHandler (ignoreCancelled = true)
    public void on(PlayerCommandPreprocessEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Mi spiace, non e' possibile usare comandi qui!");
    }

    @EventHandler (ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        event.setCancelled(true);
    }

}

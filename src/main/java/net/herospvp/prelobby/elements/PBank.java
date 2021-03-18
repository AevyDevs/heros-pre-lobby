package net.herospvp.prelobby.elements;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PBank {

    private final List<PPlayer> playerList;

    public PBank() {
        this.playerList = new ArrayList<>();
    }

    public void add(PPlayer pPlayer) {
        playerList.add(pPlayer);
    }

    public void remove(PPlayer pPlayer) {
        playerList.remove(pPlayer);
    }

    public PPlayer get(Player player) {
        return playerList.parallelStream()
                .filter(
                        p -> p.getUsername().equals(player.getName())
                ).findAny().orElse(null);
    }

}

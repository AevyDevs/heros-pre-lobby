package net.herospvp.prelobby.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PPlayer {

    private final String username;
    private String ip;
    private boolean connectionSafe, behaviourSafe;

}

package net.herospvp.prelobby.api;

import lombok.Setter;
import lombok.SneakyThrows;
import net.herospvp.prelobby.PreLobby;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class IP2C {

    private final List<String> whitelistedRegions;

    public IP2C(PreLobby preLobby) {
        whitelistedRegions = preLobby.getConfig().getStringList("antibot.allowed-regions");
    }

    @SneakyThrows
    public boolean makeRequest(String address) {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://ip2c.org/" + address).openConnection();
        urlConnection.setDefaultUseCaches(false);
        urlConnection.setUseCaches(false);
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        int c;
        StringBuilder s = new StringBuilder();
        while((c = inputStream.read()) != -1) s.append((char) c);
        inputStream.close();

        if (s.charAt(0) != '1') {
            return true;
        }

        String[] reply = s.toString().split(";");
        return whitelistedRegions.contains(reply[1]);
    }

}

package dev.nxms.guardcore.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Zarządza bypassami dla graczy.
 */
public class BypassManager {

    private final Map<UUID, Boolean> disallowedBlocksBypass = new HashMap<>();

    /**
     * Sprawdza czy gracz ma włączony bypass na zakazane bloki.
     */
    public boolean hasDisallowedBlocksBypass(UUID uuid) {
        return disallowedBlocksBypass.getOrDefault(uuid, false);
    }

    /**
     * Ustawia bypass na zakazane bloki dla gracza.
     */
    public void setDisallowedBlocksBypass(UUID uuid, boolean enabled) {
        if (enabled) {
            disallowedBlocksBypass.put(uuid, true);
        } else {
            disallowedBlocksBypass.remove(uuid);
        }
    }

    /**
     * Przełącza bypass dla gracza.
     * @return nowy stan bypass
     */
    public boolean toggleDisallowedBlocksBypass(UUID uuid) {
        boolean newState = !hasDisallowedBlocksBypass(uuid);
        setDisallowedBlocksBypass(uuid, newState);
        return newState;
    }

    /**
     * Usuwa gracza z bypass (przy wyjściu z serwera).
     */
    public void removePlayer(UUID uuid) {
        disallowedBlocksBypass.remove(uuid);
    }

    /**
     * Czyści wszystkie bypassy.
     */
    public void clearAll() {
        disallowedBlocksBypass.clear();
    }
}
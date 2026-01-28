package dev.nxms.guardcore.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Zarządza bypassami dla graczy.
 */
public class BypassManager {

    private final Map<UUID, Boolean> disallowedBlocksBypass = new HashMap<>();
    private final Map<UUID, Boolean> blockDespawnBypass = new HashMap<>();
    private final Map<UUID, Boolean> blockDestructionBypass = new HashMap<>();

    // ===== DISALLOWED BLOCKS BYPASS =====

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
     * Przełącza bypass zakazanych bloków dla gracza.
     * @return nowy stan bypass
     */
    public boolean toggleDisallowedBlocksBypass(UUID uuid) {
        boolean newState = !hasDisallowedBlocksBypass(uuid);
        setDisallowedBlocksBypass(uuid, newState);
        return newState;
    }

    // ===== BLOCK DESPAWN BYPASS =====

    /**
     * Sprawdza czy gracz ma włączony bypass na znikanie bloków.
     */
    public boolean hasBlockDespawnBypass(UUID uuid) {
        return blockDespawnBypass.getOrDefault(uuid, false);
    }

    /**
     * Ustawia bypass na znikanie bloków dla gracza.
     */
    public void setBlockDespawnBypass(UUID uuid, boolean enabled) {
        if (enabled) {
            blockDespawnBypass.put(uuid, true);
        } else {
            blockDespawnBypass.remove(uuid);
        }
    }

    /**
     * Przełącza bypass znikania bloków dla gracza.
     * @return nowy stan bypass
     */
    public boolean toggleBlockDespawnBypass(UUID uuid) {
        boolean newState = !hasBlockDespawnBypass(uuid);
        setBlockDespawnBypass(uuid, newState);
        return newState;
    }

    // ===== BLOCK DESTRUCTION BYPASS =====

    /**
     * Sprawdza czy gracz ma włączony bypass na niszczenie bloków.
     */
    public boolean hasBlockDestructionBypass(UUID uuid) {
        return blockDestructionBypass.getOrDefault(uuid, false);
    }

    /**
     * Ustawia bypass na niszczenie bloków dla gracza.
     */
    public void setBlockDestructionBypass(UUID uuid, boolean enabled) {
        if (enabled) {
            blockDestructionBypass.put(uuid, true);
        } else {
            blockDestructionBypass.remove(uuid);
        }
    }

    /**
     * Przełącza bypass niszczenia bloków dla gracza.
     * @return nowy stan bypass
     */
    public boolean toggleBlockDestructionBypass(UUID uuid) {
        boolean newState = !hasBlockDestructionBypass(uuid);
        setBlockDestructionBypass(uuid, newState);
        return newState;
    }

    // ===== COMMON =====

    /**
     * Usuwa gracza z wszystkich bypassów (przy wyjściu z serwera).
     */
    public void removePlayer(UUID uuid) {
        disallowedBlocksBypass.remove(uuid);
        blockDespawnBypass.remove(uuid);
        blockDestructionBypass.remove(uuid);
    }

    /**
     * Czyści wszystkie bypassy.
     */
    public void clearAll() {
        disallowedBlocksBypass.clear();
        blockDespawnBypass.clear();
        blockDestructionBypass.clear();
    }
}
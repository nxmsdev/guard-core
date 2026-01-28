package dev.nxms.guardcore.managers;

import dev.nxms.guardcore.GuardCore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Zarządza bypassami dla graczy.
 */
public class BypassManager {

    private GuardCore plugin;
    private final Map<UUID, Boolean> disallowedBlocksBypass = new HashMap<>();
    private final Map<UUID, Boolean> blockDespawnBypass = new HashMap<>();
    private final Map<UUID, Boolean> blockDestructionBypass = new HashMap<>();
    private final Map<UUID, Boolean> waterFlowBypass = new HashMap<>();
    private final Map<UUID, Boolean> lavaFlowBypass = new HashMap<>();

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

    // ===== WATER FLOW BYPASS =====

    /**
     * Sprawdza czy gracz ma włączony bypass na rozlewanie wody.
     */
    public boolean hasWaterFlowBypass(UUID uuid) {
        return waterFlowBypass.getOrDefault(uuid, false);
    }

    /**
     * Ustawia bypass na rozlewanie wody dla gracza.
     */
    public void setWaterFlowBypass(UUID uuid, boolean enabled) {
        if (enabled) {
            waterFlowBypass.put(uuid, true);
        } else {
            waterFlowBypass.remove(uuid);
        }
    }

    /**
     * Przełącza bypass rozlewania wody dla gracza.
     * @return nowy stan bypass
     */
    public boolean toggleWaterFlowBypass(UUID uuid) {
        boolean newState = !hasWaterFlowBypass(uuid);
        setWaterFlowBypass(uuid, newState);
        return newState;
    }

    // ===== LAVA FLOW BYPASS =====

    /**
     * Sprawdza czy gracz ma włączony bypass na rozlewanie lawy.
     */
    public boolean hasLavaFlowBypass(UUID uuid) {
        return lavaFlowBypass.getOrDefault(uuid, false);
    }

    /**
     * Ustawia bypass na rozlewanie lawy dla gracza.
     */
    public void setLavaFlowBypass(UUID uuid, boolean enabled) {
        if (enabled) {
            lavaFlowBypass.put(uuid, true);
        } else {
            lavaFlowBypass.remove(uuid);
        }
    }

    /**
     * Przełącza bypass rozlewania lawy dla gracza.
     * @return nowy stan bypass
     */
    public boolean toggleLavaFlowBypass(UUID uuid) {
        boolean newState = !hasLavaFlowBypass(uuid);
        setLavaFlowBypass(uuid, newState);
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
        waterFlowBypass.remove(uuid);
        lavaFlowBypass.remove(uuid);
    }

    /**
     * Czyści wszystkie bypassy.
     */
    public void clearAll() {
        disallowedBlocksBypass.clear();
        blockDespawnBypass.clear();
        blockDestructionBypass.clear();
        waterFlowBypass.clear();
        lavaFlowBypass.clear();

        plugin.getLogger().info("All bypasses ahs been cleared.");
    }
}
package me.twister915.core.player;

import lombok.Data;
import me.twister915.core.Core;

import java.util.ArrayList;
import java.util.List;

@Data
public final class CPlayerManagerSaveTask implements Runnable {
    private final CPlayerManager manager;

    @Override
    public void run() {
        manager.getPermissionsManager().reloadPermissions();
        List<CPlayer> failedToSave = new ArrayList<>();
        int savedPlayers = 0;
        synchronized (manager.getOnlinePlayers()) {
            for (CPlayer cPlayer : manager.getOnlinePlayers()) {
                try {
                    cPlayer.saveIntoDatabase();
                    savedPlayers++;
                } catch (DatabaseConnectException e) {
                    failedToSave.add(cPlayer);
                    e.printStackTrace();
                }
            }
        }
        for (CPlayer cPlayer : failedToSave) {
            Core.logInfo("Failed to save " + cPlayer.toString());
        }
        Core.logInfo("Saved " + savedPlayers + " players!");
    }
}
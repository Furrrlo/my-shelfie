package it.polimi.ingsw;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.Lock;

public class LockHacks {

    private LockHacks() {
    }

    public static RuntimeException dumpLockOwnerStackInException(Lock lock) {
        var owner = getLockOwner(lock);
        if (owner == null)
            return new RuntimeException("Couldn't figure out who was holding the lock " + lock);

        var lockEx = new RuntimeException("Lobby lock was held by " + owner);
        lockEx.setStackTrace(owner.getStackTrace());
        return lockEx;
    }

    public static @Nullable Thread getLockOwner(Lock lock) {
        var ownerName = getLockOwnerName(lock);
        if (ownerName == null)
            return null;

        return Thread.getAllStackTraces().keySet()
                .stream()
                .filter(th -> th.getName().equals(ownerName))
                .findFirst()
                .orElse(null);
    }

    public static @Nullable String getLockOwnerName(Lock lock) {
        var ownerName = lock.toString();
        var startIdx = ownerName.lastIndexOf("Locked by thread ");
        if (startIdx == -1)
            return null;

        ownerName = ownerName.substring(startIdx + "Locked by thread ".length());
        var endIdx = ownerName.lastIndexOf("]");
        if (endIdx == -1)
            return null;

        return ownerName.substring(0, endIdx);
    }
}

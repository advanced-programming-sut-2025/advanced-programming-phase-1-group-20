package org.example.client.radio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RadioSharedStore {
    public static final class RadioTrackEntry {
        public final String name;
        public final String path;
        public final String fromPlayer;
        public final long addedAt;

        public RadioTrackEntry(String name, String path, String fromPlayer, long addedAt) {
            this.name = name;
            this.path = path;
            this.fromPlayer = fromPlayer;
            this.addedAt = addedAt;
        }
    }

    private static final List<RadioTrackEntry> tracks = new ArrayList<>();

    private RadioSharedStore() {}

    public static synchronized void addTrackIfAbsent(String name, String path, String fromPlayer) {
        for (RadioTrackEntry e : tracks) {
            if (e.path != null && e.path.equals(path)) {
                return; // Already present
            }
        }
        RadioTrackEntry entry = new RadioTrackEntry(name, path, fromPlayer, System.currentTimeMillis());
        tracks.add(entry);
        System.out.println("**CLIENT RADIO STORE** added name=" + name + " path=" + path + " from=" + fromPlayer);
    }

    public static synchronized List<RadioTrackEntry> getAllTracks() {
        return Collections.unmodifiableList(new ArrayList<>(tracks));
    }
}



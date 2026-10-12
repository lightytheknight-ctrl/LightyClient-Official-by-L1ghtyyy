package pl.lighty.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Liczy "pop" totemow graczy (serwer nie wysyla pelnego ekwipunku innych graczy). */
public final class TotemTracker {
    private TotemTracker() {}

    private static final Map<UUID, Integer> POPS = new HashMap<>();

    public static void pop(UUID id) { POPS.merge(id, 1, Integer::sum); }
    public static int get(UUID id) { return POPS.getOrDefault(id, 0); }
    public static void clear() { POPS.clear(); }
}

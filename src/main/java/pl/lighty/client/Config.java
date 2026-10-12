package pl.lighty.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Zapis ustawien do .minecraft/config/lighty_client.json */
public final class Config {
    private Config() {}

    public static int theme = 0;
    public static boolean nametagArmor = true;
    public static boolean nametagPops = true;
    public static double hitboxGrow = 0.0;
    public static int xrayRange = 16;
    public static final Set<String> xrayOres = new LinkedHashSet<>(List.of("diamond", "ancient_debris"));
    public static final List<String> xrayCustom = new ArrayList<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final class Data {
        int theme = 0;
        boolean nametagArmor = true;
        boolean nametagPops = true;
        double hitboxGrow = 0.0;
        int xrayRange = 16;
        List<String> xrayOres = new ArrayList<>(List.of("diamond", "ancient_debris"));
        List<String> xrayCustom = new ArrayList<>();
        Map<String, Boolean> enabled = new LinkedHashMap<>();
        Map<String, Integer> keys = new LinkedHashMap<>();
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("lighty_client.json");
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) {
            save();
            return;
        }
        try (Reader r = Files.newBufferedReader(f)) {
            Data d = GSON.fromJson(r, Data.class);
            if (d == null) return;
            theme = Math.floorMod(d.theme, Theme.COUNT);
            nametagArmor = d.nametagArmor;
            nametagPops = d.nametagPops;
            hitboxGrow = d.hitboxGrow;
            xrayRange = Math.max(4, Math.min(48, d.xrayRange));
            xrayOres.clear();
            if (d.xrayOres != null) xrayOres.addAll(d.xrayOres);
            xrayCustom.clear();
            if (d.xrayCustom != null) xrayCustom.addAll(d.xrayCustom);
            for (Modules.M m : Modules.ALL) {
                if (d.enabled != null && d.enabled.containsKey(m.id)) m.enabled = d.enabled.get(m.id);
                if (d.keys != null && d.keys.containsKey(m.id)) m.key = d.keys.get(m.id);
            }
        } catch (Exception e) {
            System.err.println("[Lighty Client] Nie udalo sie wczytac configu: " + e);
        }
    }

    public static void save() {
        Data d = new Data();
        d.theme = theme;
        d.nametagArmor = nametagArmor;
        d.nametagPops = nametagPops;
        d.hitboxGrow = hitboxGrow;
        d.xrayRange = xrayRange;
        d.xrayOres = new ArrayList<>(xrayOres);
        d.xrayCustom = new ArrayList<>(xrayCustom);
        for (Modules.M m : Modules.ALL) {
            d.enabled.put(m.id, m.enabled);
            d.keys.put(m.id, m.key);
        }
        try {
            Path f = file();
            Files.createDirectories(f.getParent());
            try (Writer w = Files.newBufferedWriter(f)) {
                GSON.toJson(d, w);
            }
        } catch (Exception e) {
            System.err.println("[Lighty Client] Nie udalo sie zapisac configu: " + e);
        }
    }
}

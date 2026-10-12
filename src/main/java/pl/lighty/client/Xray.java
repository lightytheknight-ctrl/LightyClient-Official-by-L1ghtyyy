package pl.lighty.client;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/** Skanuje okolice i zapamietuje wybrane bloki (rysuje je Esp przez sciany). */
public final class Xray {
    private Xray() {}

    public record Hit(BlockPos pos, int color) {}

    /** klucz -> {nazwa po polsku, kolor ARGB, id blokow...} */
    public static final Map<String, String[]> PRESETS = new LinkedHashMap<>();
    public static final Map<String, Integer> COLORS = new HashMap<>();

    static {
        add("diamond", "Diament", 0xFF55FFFF, "minecraft:diamond_ore", "minecraft:deepslate_diamond_ore");
        add("ancient_debris", "Starożytne szczątki", 0xFFC8783C, "minecraft:ancient_debris");
        add("emerald", "Szmaragd", 0xFF55FF55, "minecraft:emerald_ore", "minecraft:deepslate_emerald_ore");
        add("gold", "Złoto", 0xFFFFD700, "minecraft:gold_ore", "minecraft:deepslate_gold_ore", "minecraft:nether_gold_ore");
        add("iron", "Żelazo", 0xFFD8AF93, "minecraft:iron_ore", "minecraft:deepslate_iron_ore");
        add("redstone", "Redstone", 0xFFFF3333, "minecraft:redstone_ore", "minecraft:deepslate_redstone_ore");
        add("lapis", "Lapis", 0xFF3355FF, "minecraft:lapis_ore", "minecraft:deepslate_lapis_ore");
        add("coal", "Węgiel", 0xFF888888, "minecraft:coal_ore", "minecraft:deepslate_coal_ore");
        add("copper", "Miedź", 0xFFE07A3F, "minecraft:copper_ore", "minecraft:deepslate_copper_ore");
    }

    private static void add(String key, String label, int color, String... ids) {
        String[] arr = new String[ids.length + 1];
        arr[0] = label;
        System.arraycopy(ids, 0, arr, 1, ids.length);
        PRESETS.put(key, arr);
        COLORS.put(key, color);
    }

    public static volatile List<Hit> hits = List.of();

    public static void scan(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;

        Map<Block, Integer> targets = new HashMap<>();
        for (String key : Config.xrayOres) {
            String[] p = PRESETS.get(key);
            if (p == null) continue;
            int color = COLORS.getOrDefault(key, 0xFFFFFFFF);
            for (int i = 1; i < p.length; i++) {
                Identifier id = Identifier.tryParse(p[i]);
                if (id == null) continue;
                Block b = Registries.BLOCK.get(id);
                if (b != Blocks.AIR) targets.put(b, color);
            }
        }
        for (String s : Config.xrayCustom) {
            Identifier id = Identifier.tryParse(s);
            if (id == null) continue;
            Block b = Registries.BLOCK.get(id);
            if (b != Blocks.AIR) targets.put(b, 0xFFFFFFFF);
        }
        if (targets.isEmpty()) {
            hits = List.of();
            return;
        }

        BlockPos c = mc.player.getBlockPos();
        int r = Config.xrayRange;
        List<Hit> out = new ArrayList<>();
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    m.set(c.getX() + dx, c.getY() + dy, c.getZ() + dz);
                    Integer col = targets.get(mc.world.getBlockState(m).getBlock());
                    if (col != null) {
                        out.add(new Hit(m.toImmutable(), col));
                        if (out.size() >= 400) {
                            hits = out;
                            return;
                        }
                    }
                }
            }
        }
        hits = out;
    }
}

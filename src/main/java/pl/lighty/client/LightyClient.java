package pl.lighty.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class LightyClient implements ClientModInitializer {
    public static final String NAME = "Lighty Client";
    public static final String AUTHOR = "L1ghtyyy";

    /** Klawisz menu (stały). Bindy modulow ustawiasz w menu. */
    public static final int KEY_GUI = GLFW.GLFW_KEY_RIGHT_SHIFT;

    /** Modul, ktoremu wlasnie ustawiany jest klawisz (null = nic). */
    public static Modules.M binding = null;

    private static boolean prevGui;
    private static final boolean[] prevKey = new boolean[16];
    private static int scanTimer = 0;

    @Override
    public void onInitializeClient() {
        Config.load();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> TotemTracker.clear());
        ClientTickEvents.END_CLIENT_TICK.register(LightyClient::tick);

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.of("lighty_client", "esp"),
                Esp::render);
    }

    private static void tick(MinecraftClient mc) {
        boolean gui = down(mc, KEY_GUI);

        if (binding != null) {
            if (down(mc, GLFW.GLFW_KEY_ESCAPE)) {
                binding = null;
            } else if (down(mc, GLFW.GLFW_KEY_BACKSPACE)) {
                binding.key = -1;
                binding = null;
                Config.save();
            } else {
                for (int k = 32; k <= 348; k++) {
                    if (k == KEY_GUI) continue;
                    if (down(mc, k)) {
                        binding.key = k;
                        binding = null;
                        Config.save();
                        break;
                    }
                }
            }
            prevGui = gui;
            return;
        }

        if (mc.player != null && mc.currentScreen == null && gui && !prevGui) {
            mc.setScreen(new LightyScreen());
        }
        prevGui = gui;

        for (int i = 0; i < Modules.ALL.size(); i++) {
            Modules.M m = Modules.ALL.get(i);
            boolean d = m.key > 0 && down(mc, m.key);
            if (d && !prevKey[i] && mc.player != null && mc.currentScreen == null) {
                m.enabled = !m.enabled;
                Config.save();
            }
            prevKey[i] = d;
        }

        if (Modules.XRAY.enabled && mc.world != null && mc.player != null) {
            if (++scanTimer >= 10) {
                scanTimer = 0;
                Xray.scan(mc);
            }
        }
    }

    private static boolean down(MinecraftClient mc, int key) {
        return GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }
}

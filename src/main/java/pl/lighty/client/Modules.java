package pl.lighty.client;

import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class Modules {
    private Modules() {}

    public static final class M {
        public final String id;
        public final String name;
        public boolean enabled;
        public int key;

        M(String id, String name, boolean enabled, int key) {
            this.id = id;
            this.name = name;
            this.enabled = enabled;
            this.key = key;
        }
    }

    public static final M TRACERS = new M("tracers", "Tracery", true, GLFW.GLFW_KEY_R);
    public static final M NAMETAGS = new M("nametags", "Nametagi", true, GLFW.GLFW_KEY_N);
    public static final M HITBOXES = new M("hitboxes", "Hitboxy", false, GLFW.GLFW_KEY_H);
    public static final M ENLARGE = new M("hitboxplus", "Większy hitbox", false, GLFW.GLFW_KEY_J);
    public static final M XRAY = new M("xray", "Xray", false, GLFW.GLFW_KEY_X);
    public static final M NOPUSH = new M("nopush", "NoPush", false, GLFW.GLFW_KEY_P);

    public static final List<M> ALL = List.of(TRACERS, NAMETAGS, HITBOXES, ENLARGE, XRAY, NOPUSH);
}

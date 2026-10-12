package pl.lighty.client;

import org.lwjgl.glfw.GLFW;

public final class KeyNames {
    private KeyNames() {}

    public static String name(int k) {
        if (k <= 0) return "brak";
        switch (k) {
            case GLFW.GLFW_KEY_RIGHT_SHIFT: return "PShift";
            case GLFW.GLFW_KEY_LEFT_SHIFT: return "LShift";
            case GLFW.GLFW_KEY_LEFT_CONTROL: return "LCtrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL: return "PCtrl";
            case GLFW.GLFW_KEY_LEFT_ALT: return "LAlt";
            case GLFW.GLFW_KEY_RIGHT_ALT: return "PAlt";
            case GLFW.GLFW_KEY_SPACE: return "Spacja";
            case GLFW.GLFW_KEY_TAB: return "Tab";
            case GLFW.GLFW_KEY_ENTER: return "Enter";
            case GLFW.GLFW_KEY_UP: return "Gora";
            case GLFW.GLFW_KEY_DOWN: return "Dol";
            case GLFW.GLFW_KEY_LEFT: return "Lewo";
            case GLFW.GLFW_KEY_RIGHT: return "Prawo";
            default:
        }
        if (k >= GLFW.GLFW_KEY_F1 && k <= GLFW.GLFW_KEY_F12) return "F" + (k - GLFW.GLFW_KEY_F1 + 1);
        String n = GLFW.glfwGetKeyName(k, 0);
        if (n != null && !n.isEmpty()) return n.toUpperCase();
        return "K" + k;
    }
}

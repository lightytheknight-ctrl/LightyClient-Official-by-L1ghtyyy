package pl.lighty.client;

import net.minecraft.client.gui.DrawContext;

/** 5 motywow kolorystycznych (ARGB). */
public final class Theme {
    private Theme() {}

    public static final String[] NAMES = {"Fiolet", "Niebieski", "Zielony", "Czerwony", "Pomarańcz"};
    public static final int COUNT = NAMES.length;

    private static final int[] PRIMARY = {0xFFB44CFF, 0xFF4C8CFF, 0xFF4CD964, 0xFFFF4C4C, 0xFFFF9A3C};
    private static final int[] LIGHT   = {0xFFD9A0FF, 0xFFA0C4FF, 0xFFA0F0B0, 0xFFFFA0A0, 0xFFFFC890};
    private static final int[] DARK    = {0xFF3A1670, 0xFF16327A, 0xFF14612A, 0xFF701616, 0xFF7A4010};

    public static int primary() { return PRIMARY[Config.theme]; }
    public static int light() { return LIGHT[Config.theme]; }
    public static int dark() { return DARK[Config.theme]; }

    public static void panel(DrawContext c, int x1, int y1, int x2, int y2) {
        c.fill(x1, y1, x2, y2, 0xE0101018);
        c.fill(x1, y1, x2, y1 + 28, dark());
        int p = primary();
        c.fill(x1, y1, x2, y1 + 1, p);
        c.fill(x1, y2 - 1, x2, y2, p);
        c.fill(x1, y1, x1 + 1, y2, p);
        c.fill(x2 - 1, y1, x2, y2, p);
    }
}

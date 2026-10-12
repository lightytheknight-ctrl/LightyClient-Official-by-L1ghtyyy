package pl.lighty.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.Map;

/** Wybor blokow podswietlanych przez Xray. */
public class XrayScreen extends Screen {
    private static final int PW = 260;
    private static final int ROW = 22;
    private static final int[] RANGES = {8, 12, 16, 24, 32};

    private final Screen parent;
    private int px1, py1, px2, py2;

    public XrayScreen(Screen parent) {
        super(Text.literal("Xray - bloki"));
        this.parent = parent;
    }

    private static Text label(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "WŁ" : "WYŁ"))
                .styled(s -> s.withColor(on ? Theme.light() & 0xFFFFFF : 0xAAAAAA));
    }

    @Override
    protected void init() {
        int presetRows = (Xray.PRESETS.size() + 1) / 2;
        int ph = 28 + 8 + presetRows * ROW + ROW + ROW + 8;
        px1 = this.width / 2 - PW / 2;
        px2 = px1 + PW;
        py1 = Math.max(4, this.height / 2 - ph / 2);
        py2 = py1 + ph;

        int left = px1 + 10;
        int y = py1 + 36;
        int i = 0;
        for (Map.Entry<String, String[]> e : Xray.PRESETS.entrySet()) {
            String key = e.getKey();
            String name = e.getValue()[0];
            int x = left + (i % 2) * 123;
            this.addDrawableChild(ButtonWidget.builder(label(name, Config.xrayOres.contains(key)), b -> {
                if (!Config.xrayOres.remove(key)) Config.xrayOres.add(key);
                Config.save();
                b.setMessage(label(name, Config.xrayOres.contains(key)));
            }).dimensions(x, y, 117, 20).build());
            if (i % 2 == 1) y += ROW;
            i++;
        }
        if (i % 2 == 1) y += ROW;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Zasięg: " + Config.xrayRange + " bloków"), b -> {
            int idx = 0;
            for (int k = 0; k < RANGES.length; k++) if (RANGES[k] == Config.xrayRange) idx = k;
            Config.xrayRange = RANGES[(idx + 1) % RANGES.length];
            Config.save();
            b.setMessage(Text.literal("Zasięg: " + Config.xrayRange + " bloków"));
        }).dimensions(left, y, 240, 20).build());
        y += ROW;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Wróć"), b -> this.client.setScreen(parent))
                .dimensions(left, y, 240, 20).build());
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public void renderBackground(DrawContext c, int mouseX, int mouseY, float delta) {
        super.renderBackground(c, mouseX, mouseY, delta);
        Theme.panel(c, px1, py1, px2, py2);
    }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        super.render(c, mouseX, mouseY, delta);
        c.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Xray - wybór bloków"), this.width / 2, py1 + 5, Theme.light());
        c.drawCenteredTextWithShadow(this.textRenderer, Text.literal("własne id: plik config/lighty_client.json"),
                this.width / 2, py1 + 16, 0xFFAAAAAA);
    }
}

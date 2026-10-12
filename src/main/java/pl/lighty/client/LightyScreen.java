package pl.lighty.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Menu moda - prawy Shift. */
public class LightyScreen extends Screen {
    private static final int PW = 260;
    private static final int ROW = 22;
    private static final double[] GROW = {0.0, 0.1, 0.2, 0.3, 0.5};

    private int px1, py1, px2, py2;
    private final List<Modules.M> bindOrder = new ArrayList<>();
    private final List<ButtonWidget> bindButtons = new ArrayList<>();

    public LightyScreen() {
        super(Text.literal(LightyClient.NAME));
    }

    private static Text onOff(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "WŁ" : "WYŁ"))
                .styled(s -> s.withColor(on ? Theme.light() & 0xFFFFFF : 0xAAAAAA));
    }

    private static Text bindText(Modules.M m) {
        if (LightyClient.binding == m) return Text.literal("Naciśnij klawisz...");
        return Text.literal("Bind: " + KeyNames.name(m.key));
    }

    @Override
    protected void init() {
        bindOrder.clear();
        bindButtons.clear();

        int rows = Modules.ALL.size() + 5;
        int ph = 28 + 8 + rows * ROW + 8;
        px1 = this.width / 2 - PW / 2;
        px2 = px1 + PW;
        py1 = Math.max(4, this.height / 2 - ph / 2);
        py2 = py1 + ph;

        int y = py1 + 36;
        int left = px1 + 10;

        for (Modules.M m : Modules.ALL) {
            this.addDrawableChild(ButtonWidget.builder(onOff(m.name, m.enabled), b -> {
                m.enabled = !m.enabled;
                Config.save();
                b.setMessage(onOff(m.name, m.enabled));
            }).dimensions(left, y, 150, 20).build());

            ButtonWidget bind = ButtonWidget.builder(bindText(m), b -> {
                LightyClient.binding = m;
                b.setMessage(bindText(m));
            }).dimensions(left + 156, y, 84, 20).build();
            this.addDrawableChild(bind);
            bindOrder.add(m);
            bindButtons.add(bind);
            y += ROW;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Motyw: " + Theme.NAMES[Config.theme]), b -> {
            Config.theme = (Config.theme + 1) % Theme.COUNT;
            Config.save();
            b.setMessage(Text.literal("Motyw: " + Theme.NAMES[Config.theme]));
        }).dimensions(left, y, 240, 20).build());
        y += ROW;

        this.addDrawableChild(ButtonWidget.builder(onOff("Zbroja", Config.nametagArmor), b -> {
            Config.nametagArmor = !Config.nametagArmor;
            Config.save();
            b.setMessage(onOff("Zbroja", Config.nametagArmor));
        }).dimensions(left, y, 117, 20).build());
        this.addDrawableChild(ButtonWidget.builder(onOff("Totemy", Config.nametagPops), b -> {
            Config.nametagPops = !Config.nametagPops;
            Config.save();
            b.setMessage(onOff("Totemy", Config.nametagPops));
        }).dimensions(left + 123, y, 117, 20).build());
        y += ROW;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Hitbox wielkość (+bloki): +" + Config.hitboxGrow), b -> {
            int idx = 0;
            for (int i = 0; i < GROW.length; i++) if (Math.abs(GROW[i] - Config.hitboxGrow) < 1e-6) idx = i;
            Config.hitboxGrow = GROW[(idx + 1) % GROW.length];
            Config.save();
            b.setMessage(Text.literal("Hitbox wielkość (+bloki): +" + Config.hitboxGrow));
        }).dimensions(left, y, 240, 20).build());
        y += ROW;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Xray: wybór bloków..."), b ->
                this.client.setScreen(new XrayScreen(this))
        ).dimensions(left, y, 240, 20).build());
        y += ROW;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Gotowe"), b -> this.close())
                .dimensions(left, y, 240, 20).build());
    }

    @Override
    public void tick() {
        for (int i = 0; i < bindButtons.size(); i++) {
            bindButtons.get(i).setMessage(bindText(bindOrder.get(i)));
        }
    }

    @Override
    public void renderBackground(DrawContext c, int mouseX, int mouseY, float delta) {
        super.renderBackground(c, mouseX, mouseY, delta);
        Theme.panel(c, px1, py1, px2, py2);
    }

    @Override
    public void render(DrawContext c, int mouseX, int mouseY, float delta) {
        super.render(c, mouseX, mouseY, delta);
        c.drawCenteredTextWithShadow(this.textRenderer, Text.literal(LightyClient.NAME), this.width / 2, py1 + 5, Theme.light());
        c.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("autor: " + LightyClient.AUTHOR + "  |  Backspace = usuń bind"),
                this.width / 2, py1 + 16, 0xFFAAAAAA);
    }
}

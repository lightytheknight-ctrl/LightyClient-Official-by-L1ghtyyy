package pl.lighty.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Tracery, nametagi, hitboxy i xray rysowane na ekranie (rzutowanie 3D -> 2D),
 * wiec nie zaleza od zmieniajacego sie renderera swiata.
 */
public final class Esp {
    private Esp() {}

    private static final double NEAR = 0.05;
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST,
            EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND};

    private static final class View {
        Vec3d cam, fwd, right, up;
        double tanHalf, aspect;
        int w, h;

        /** {ekranX, ekranY, glebia}; dla punktow za kamera odwraca kierunek. */
        double[] project(Vec3d pos) {
            Vec3d d = pos.subtract(cam);
            double x = d.dotProduct(right);
            double y = d.dotProduct(up);
            double z = d.dotProduct(fwd);
            double depth = z;
            if (z < NEAR) {
                x = -x;
                y = -y;
                z = NEAR;
            }
            double ndcX = x / (z * tanHalf * aspect);
            double ndcY = y / (z * tanHalf);
            return new double[]{(ndcX * 0.5 + 0.5) * w, (0.5 - ndcY * 0.5) * h, depth};
        }
    }

    public static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;
        ctx.drawTextWithShadow(tr, LightyClient.NAME + " by " + LightyClient.AUTHOR, 4, 4, Theme.primary());
        ctx.drawTextWithShadow(tr, "Prawy Shift - menu", 4, 15, Theme.light());

        if (mc.currentScreen != null) return;
        if (!Modules.TRACERS.enabled && !Modules.NAMETAGS.enabled
                && !Modules.HITBOXES.enabled && !Modules.XRAY.enabled) return;

        float td = tick.getTickProgress(false);

        View v = new View();
        v.w = ctx.getScaledWindowWidth();
        v.h = ctx.getScaledWindowHeight();
        v.cam = mc.player.getCameraPosVec(td);
        double yaw = Math.toRadians(mc.player.getYaw(td));
        double pitch = Math.toRadians(mc.player.getPitch(td));
        v.fwd = new Vec3d(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
        v.right = new Vec3d(-Math.cos(yaw), 0, -Math.sin(yaw));
        v.up = v.right.crossProduct(v.fwd);
        v.tanHalf = Math.tan(Math.toRadians(mc.options.getFov().getValue()) / 2.0);
        v.aspect = (double) v.w / (double) v.h;

        double cx = v.w / 2.0;
        double cy = v.h / 2.0;

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator()) continue;

            Vec3d base = p.getLerpedPos(td);
            double dist = base.distanceTo(v.cam);

            if (Modules.TRACERS.enabled) {
                double[] s = v.project(base.add(0, p.getHeight() / 2.0, 0));
                double[] e = clamp(cx, cy, s[0], s[1], v.w, v.h, 6);
                line(ctx, cx, cy, e[0], e[1], Theme.primary());
            }

            if (Modules.HITBOXES.enabled) {
                Box b = p.getBoundingBox().offset(base.subtract(p.getPos())).expand(Config.hitboxGrow);
                drawBox(ctx, v, b, Theme.primary());
            }

            if (Modules.NAMETAGS.enabled) {
                nametag(ctx, tr, v, p, base, dist);
            }
        }

        if (Modules.XRAY.enabled) {
            for (Xray.Hit h : Xray.hits) {
                drawBox(ctx, v, new Box(h.pos()), h.color());
            }
        }
    }

    private static void nametag(DrawContext ctx, TextRenderer tr, View v, PlayerEntity p, Vec3d base, double dist) {
        double[] s = v.project(base.add(0, p.getHeight() + 0.35, 0));
        if (s[2] <= NEAR || s[0] < 0 || s[0] > v.w || s[1] < 0 || s[1] > v.h) return;

        int hp = Math.round(p.getHealth() + p.getAbsorptionAmount());
        StringBuilder sb = new StringBuilder(p.getName().getString())
                .append("  ").append(hp).append(" HP  ").append(Math.round(dist)).append("m");
        if (Config.nametagPops) sb.append("  Pop:").append(TotemTracker.get(p.getUuid()));
        String text = sb.toString();

        int tw = tr.getWidth(text);
        int x = (int) s[0] - tw / 2;
        int y = (int) s[1] - 10;
        ctx.fill(x - 2, y - 2, x + tw + 2, y + 10, 0x90000000);
        ctx.fill(x - 2, y + 10, x + tw + 2, y + 11, Theme.primary());
        ctx.drawTextWithShadow(tr, text, x, y, Theme.light());

        if (Config.nametagArmor) {
            int ix = (int) s[0] - SLOTS.length * 9;
            int iy = y - 20;
            for (EquipmentSlot slot : SLOTS) {
                ItemStack st = p.getEquippedStack(slot);
                if (!st.isEmpty()) {
                    ctx.drawItem(st, ix, iy);
                    ctx.drawStackOverlay(tr, st, ix, iy);
                }
                ix += 18;
            }
        }
    }

    /** Obrys 2D prostokata obejmujacego rzut 8 rogow boxa. */
    private static void drawBox(DrawContext ctx, View v, Box b, int color) {
        double minX = 1e9, minY = 1e9, maxX = -1e9, maxY = -1e9;
        double[] xs = {b.minX, b.maxX};
        double[] ys = {b.minY, b.maxY};
        double[] zs = {b.minZ, b.maxZ};
        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {
                    double[] s = v.project(new Vec3d(x, y, z));
                    if (s[2] < NEAR) return;
                    minX = Math.min(minX, s[0]);
                    minY = Math.min(minY, s[1]);
                    maxX = Math.max(maxX, s[0]);
                    maxY = Math.max(maxY, s[1]);
                }
            }
        }
        if (maxX - minX > 3000 || maxY - minY > 3000) return;
        int x1 = (int) minX, y1 = (int) minY, x2 = (int) maxX, y2 = (int) maxY;
        ctx.fill(x1, y1, x2, y1 + 1, color);
        ctx.fill(x1, y2 - 1, x2, y2, color);
        ctx.fill(x1, y1, x1 + 1, y2, color);
        ctx.fill(x2 - 1, y1, x2, y2, color);
    }

    private static double[] clamp(double cx, double cy, double ex, double ey, int w, int h, int margin) {
        double dx = ex - cx;
        double dy = ey - cy;
        double sx = Math.abs(dx) < 1e-6 ? Double.MAX_VALUE : (w / 2.0 - margin) / Math.abs(dx);
        double sy = Math.abs(dy) < 1e-6 ? Double.MAX_VALUE : (h / 2.0 - margin) / Math.abs(dy);
        double k = Math.min(1.0, Math.min(sx, sy));
        return new double[]{cx + dx * k, cy + dy * k};
    }

    private static void line(DrawContext ctx, double x0, double y0, double x1, double y1, int color) {
        double dx = x1 - x0;
        double dy = y1 - y0;
        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));
        if (steps <= 0) return;
        double sx = dx / steps;
        double sy = dy / steps;
        double x = x0;
        double y = y0;
        for (int i = 0; i <= steps; i++) {
            int px = (int) Math.round(x);
            int py = (int) Math.round(y);
            ctx.fill(px, py, px + 2, py + 2, color);
            x += sx;
            y += sy;
        }
    }
}

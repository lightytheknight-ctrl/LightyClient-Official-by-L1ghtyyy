package pl.lighty.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.lighty.client.Config;
import pl.lighty.client.Modules;

/** NoPush: blokuje odpychanie gracza przez inne byty (gracze, moby). */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true, require = 0)
    private void lighty$noPushEntities(Entity other, CallbackInfo ci) {
        if (!Modules.NOPUSH.enabled) return;
        Object me = MinecraftClient.getInstance().player;
        if (me != null && (me == (Object) this || me == other)) {
            ci.cancel();
        }
    }

    /** Wiekszy hitbox: celownik trafia graczy z lekko wiekszego marginesu. */
    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true, require = 0)
    private void lighty$biggerHitbox(CallbackInfoReturnable<Float> cir) {
        if (!Modules.ENLARGE.enabled) return;
        if ((Object) this instanceof PlayerEntity p && p != MinecraftClient.getInstance().player) {
            cir.setReturnValue((float) Config.hitboxGrow);
        }
    }
}

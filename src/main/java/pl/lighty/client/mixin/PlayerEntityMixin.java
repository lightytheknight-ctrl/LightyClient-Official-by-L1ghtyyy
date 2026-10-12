package pl.lighty.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.lighty.client.Modules;

/** NoPush: woda i lawa nie znosza gracza. */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true, require = 0)
    private void lighty$noPushFluids(CallbackInfoReturnable<Boolean> cir) {
        if (Modules.NOPUSH.enabled && MinecraftClient.getInstance().player == (Object) this) {
            cir.setReturnValue(false);
        }
    }
}

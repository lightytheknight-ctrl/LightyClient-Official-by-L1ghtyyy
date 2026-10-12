package pl.lighty.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.lighty.client.Modules;

/** NoPush: blokuje wypychanie gracza z blokow. */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true, require = 0)
    private void lighty$noPushBlocks(double x, double z, CallbackInfo ci) {
        if (Modules.NOPUSH.enabled) ci.cancel();
    }
}

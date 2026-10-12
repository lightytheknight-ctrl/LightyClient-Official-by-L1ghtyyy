package pl.lighty.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.lighty.client.TotemTracker;

/** Liczy pop-y totemow (status 35). RETURN, zeby nie policzyc dwa razy (watek sieci + glowny). */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityStatus", at = @At("RETURN"), require = 0)
    private void lighty$countPops(EntityStatusS2CPacket packet, CallbackInfo ci) {
        if (packet.getStatus() != 35) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return;
        Entity e = packet.getEntity(mc.world);
        if (e instanceof PlayerEntity p) TotemTracker.pop(p.getUuid());
    }
}

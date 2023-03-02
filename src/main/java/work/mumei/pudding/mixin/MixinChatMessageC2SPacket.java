package work.mumei.pudding.mixin;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ChatMessageC2SPacket.class})
public class MixinChatMessageC2SPacket {
    @Mutable
    @Final
    private String comp_945;

    @Inject(method = {"write"}, at = {@At("HEAD")})
    public void write(PacketByteBuf buf, CallbackInfo ci) {
        if (this.comp_945.startsWith("!")) this.comp_945 = this.comp_945.substring(1);
    }
}

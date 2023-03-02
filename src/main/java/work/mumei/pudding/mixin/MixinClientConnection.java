package work.mumei.pudding.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.mumei.pudding.PuddingChat;

@Mixin({ClientConnection.class})
public abstract class MixinClientConnection {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Shadow
    protected abstract void sendImmediately(Packet<?> paramclass_2596, @Nullable PacketCallbacks paramclass_7648);

    @Inject(method = {"send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V"}, at = {@At("HEAD")}, cancellable = true)
    private void send(Packet<?> p, PacketCallbacks callbacks, CallbackInfo ci) {
        if (p instanceof ChatMessageC2SPacket packet) {
            if (this.mc.player != null) {
                String msg = packet.chatMessage();
                if (!msg.startsWith("!")) {
                    ci.cancel();
                    PuddingChat.INSTANCE.sendMessage(this.mc.getSession().getUsername(), msg);
                }
            }
        }
    }

    @Inject(method = {"sendImmediately"}, at = {@At("HEAD")}, cancellable = true)
    private void sendImmediately(Packet<?> p, PacketCallbacks callbacks, CallbackInfo ci) {
        if (p instanceof ChatMessageC2SPacket packet) {
            if (this.mc.player != null) {
                String msg = packet.chatMessage();
                if (msg.startsWith("!")) {
                    ci.cancel();
                    sendImmediately(
                            new ChatMessageC2SPacket(
                                    msg.substring(1),
                                    packet.timestamp(),
                                    packet.salt(),
                                    packet.signature(),
                                    packet.acknowledgment()
                            ),
                            callbacks
                    );
                }
            }
        }
    }
}
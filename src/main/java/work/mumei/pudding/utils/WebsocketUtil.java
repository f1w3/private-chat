package work.mumei.pudding.utils;

import jakarta.websocket.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import work.mumei.pudding.PuddingChat;

import java.io.IOException;

@ClientEndpoint
public class WebsocketUtil {
    public WebsocketUtil(String websocketurl) {
        WebSocketUrl = websocketurl;
    }

    public static String WebSocketUrl = "";

    @OnOpen
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendText("§7[§5P§7] §r" + MinecraftClient.getInstance().getSession().getUsername() + " is join!");
        } catch (IOException e) {
            MinecraftClient.getInstance().execute(() -> {
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    player.sendMessage(Text.translatable("message.pudding.error.send"));
                }
            });
            PuddingChat.LOGGER.error("Failed to send message to the ws server!", e);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        MinecraftClient.getInstance().execute(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.sendMessage(Text.of(message));
            }
        });
    }

    @OnError
    public void onError(Throwable th) {
        th.printStackTrace();
        PuddingChat.LOGGER.error("An error occurred while connecting to the ws server!", th);
        MinecraftClient.getInstance().execute(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.sendMessage(Text.translatable("message.pudding.error.connect"));
            }
        });
    }

    @OnClose
    public void onClose() {
        MinecraftClient.getInstance().execute(() -> {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.sendMessage(Text.translatable("message.pudding.info.disconnect"));
            }
        });
    }
}
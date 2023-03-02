package work.mumei.pudding;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.mumei.pudding.config.ConfigManager;
import work.mumei.pudding.gui.PuddingGui;
import work.mumei.pudding.utils.WebsocketUtil;

import java.io.IOException;
import java.net.URI;

public class PuddingChat implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("pudding");
	public static final String Version = "1.0.0";
	public static final String ModName = "PuddingChat";
	public static PuddingChat INSTANCE;
	public static String WebSocketUrl = "ws://";
	public static Session session = null;
	private static KeyBinding keyBinding;
	public static ConfigManager Config;
	private PuddingGui ConfigMenu;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading " + ModName + " v" + Version);
		INSTANCE = this;
		Config = new ConfigManager();
		ConfigMenu = new PuddingGui();
		WebSocketUrl = Config.load("websocketurl", "ws://", (url) -> {
			LOGGER.info("Found Url! Connecting...");
			StopConnection();
			StartConnection(url);
		});
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.pudding.openmenu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_J,
				ModName
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				ConfigMenu.OpenGui();
			}
		});
		ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (session == null) {
				MinecraftClient.getInstance().execute(() -> {
					ClientPlayerEntity player = MinecraftClient.getInstance().player;
					if (player != null) {
						player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
						player.sendMessage(Text.translatable("message.pudding.join.non-connection"));
					}
				});
			} else {
				MinecraftClient.getInstance().execute(() -> {
					ClientPlayerEntity player = MinecraftClient.getInstance().player;
					if (player != null) {
						player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
						player.sendMessage(Text.translatable("message.pudding.join.connecting"));
					}
				});
			}
		});
	}

	public static void StopConnection() {
		if (!(session == null)) {
			try {
				session.close();
			} catch (IOException e) {
				LOGGER.info("ERROR: " + e);
			}
			session = null;
		}
	}

	public static void StartConnection(String WebSocketUrl) throws IllegalThreadStateException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		URI uri = URI.create(WebSocketUrl);
		try {
			session = container.connectToServer(new WebsocketUtil(WebSocketUrl), uri);
		} catch (DeploymentException | IOException e) {
			MinecraftClient.getInstance().execute(() -> {
				ClientPlayerEntity player = MinecraftClient.getInstance().player;
				if (player != null) {
					player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
					player.sendMessage(Text.translatable("message.pudding.error.connect"));
				}
			});
			session = null;
		}
	}

	public void sendMessage(String username, String message) {
		if (session != null) {
			try {
				session.getBasicRemote().sendText("§7[§5P§7] §7" + username + ": §r" + message);
			} catch (IOException e) {
				LOGGER.error("ERROR: ", e);
			}
		} else {
			MinecraftClient.getInstance().execute(() -> {
				ClientPlayerEntity player = MinecraftClient.getInstance().player;
				if (player != null) {
					player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
					player.sendMessage(Text.translatable("message.pudding.error.send"));
				}
			});
		}
	}
}

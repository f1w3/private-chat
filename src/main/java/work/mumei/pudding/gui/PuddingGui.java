package work.mumei.pudding.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import work.mumei.pudding.PuddingChat;


public class PuddingGui {
    private static ConfigBuilder getConfigBuilder() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(MinecraftClient.getInstance().currentScreen)
                .setTitle(Text.of(PuddingChat.ModName));
        ConfigCategory general = builder.getOrCreateCategory(Text.of("Setting"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(
                entryBuilder.startStrField(Text.of("Connect Url"), PuddingChat.WebSocketUrl)
                        .setDefaultValue(PuddingChat.WebSocketUrl)
                        .setTooltip(Text.of("WebSocketUrl"))
                        .setSaveConsumer(newValue -> {
                            PuddingChat.WebSocketUrl = newValue;
                            PuddingChat.Config.save("websocketurl", newValue);
                        }).build()
        );
        builder.setSavingRunnable(() -> {
            PuddingChat.StopConnection();
            PuddingChat.StartConnection(PuddingChat.WebSocketUrl);
        });
        builder.transparentBackground();
        return builder;
    }

    public void OpenGui() {
        Screen screen = getConfigBuilder().build();
        MinecraftClient.getInstance().setScreen(screen);
    }
}

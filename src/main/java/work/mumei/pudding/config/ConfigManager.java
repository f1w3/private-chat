package work.mumei.pudding.config;

import work.mumei.pudding.PuddingChat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class ConfigManager {
    private static final String Folder = "pudding/";

    public ConfigManager() {
        File parent = new File(Folder);
        parent.mkdirs();
    }

    public void save(String Filename, String value) {
        File config = new File(Folder + Filename + ".pudding");
        FileWriter writer;
        try {
            writer = new FileWriter(config);
            writer.write(value);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String load(String Filename, String InitValue, Consumer<String> callback) {
        Path path = Paths.get(Folder + Filename + ".pudding");
        String context = InitValue;
        if (!Files.exists(path)) return context;
        try {
            context = Files.readString(path);
        } catch (IOException e) {
            PuddingChat.LOGGER.info("ERROR: " + e);
        }
        callback.accept(context);
        return context;
    }
}

package pl.pomoku.survivalpomoku.configFiles;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CustomConfig {
    private File file;
    private FileConfiguration configuration;
    private FileConfiguration defaultConfiguration;
    private final String fileName;
    private final Plugin plugin;

    public CustomConfig(Plugin plugin, String fileName) {
        this.fileName = fileName;
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);
        configuration = YamlConfiguration.loadConfiguration(file);
        defaultConfiguration = loadDefaults();
    }

    private FileConfiguration loadDefaults() {
        try {
            Reader defConfigStream = new InputStreamReader(
                    Objects.requireNonNull(plugin.getResource(fileName)),
                    StandardCharsets.UTF_8
            );
            return YamlConfiguration.loadConfiguration(defConfigStream);
        } catch (NullPointerException e) {
            return new YamlConfiguration();
        }
    }
    public FileConfiguration get() {
        return configuration;
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveDefaultConfig() {
        if (!file.exists()) {
            configuration.setDefaults(defaultConfiguration);
            configuration.options().copyDefaults(true);
            save();
        }
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }
}

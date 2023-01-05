package cz.revivalo.dailyrewards.files;

import com.tchristofferson.configupdater.ConfigUpdater;
import cz.revivalo.dailyrewards.DailyRewards;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.parser.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class YAMLFile {
    private final File yamlFile;
    public YAMLFile(final String filePath, final File folder){
        yamlFile = new File(folder, filePath);
        FileConfiguration configuration;
        try {
            configuration = YamlConfiguration.loadConfiguration(yamlFile);
        } catch (ParserException exception) {
            Bukkit.getLogger().info("Format exception in "  + yamlFile.getName() + " file.");
            return;
        }
        try {
            configuration.save(yamlFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        configuration.options().copyDefaults(true);

        try {
            ConfigUpdater.update(DailyRewards.getPlugin(DailyRewards.class), filePath, yamlFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfiguration(){
        return YamlConfiguration.loadConfiguration(yamlFile);
    }

    public File getFile() {
        return yamlFile;
    }
}

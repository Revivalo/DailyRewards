package eu.athelion.dailyrewards.configuration;

import com.tchristofferson.configupdater.ConfigUpdater;
import eu.athelion.dailyrewards.DailyRewardsPlugin;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;

@Getter
public class YamlFile {

	private final File file;
	private final UpdateMethod updateMethod;
	private final String filePath;
	private final YamlConfiguration configuration;

	public YamlFile(final String filePath, final File folder, final UpdateMethod updateMethod) {
		file = new File(folder, filePath);
		this.updateMethod = updateMethod;
		this.filePath = filePath;

		boolean update = true;
		switch (this.updateMethod){
			case NEVER:
				update = false;
				break;
			case ON_LOAD:
				update = !file.exists();
				break;
		}

		Validate.notNull(file, "File cannot be null");

		configuration = new YamlConfiguration();

		try {
			configuration.load(file);
		} catch (FileNotFoundException ex) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
		} catch (InvalidConfigurationException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
			return;
		}

		try {
			configuration.save(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		configuration.options().copyDefaults(true);

		if (update) {
			try {
				ConfigUpdater.update(DailyRewardsPlugin.get(), filePath, file, Collections.emptyList());
				reload();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void reload() {
		try {
			configuration.load(file);
		} catch (InvalidConfigurationException | IOException ex) {
			ex.printStackTrace();
		}
	}

	public enum UpdateMethod {
		EVERYTIME, ON_LOAD, NEVER
	}
}
package dev.revivalo.dailyrewards.updatechecker;

import dev.revivalo.dailyrewards.DailyRewardsPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {
	private final int RESOURCE_ID;

	public UpdateChecker(int RESOURCE_ID) {
		this.RESOURCE_ID = RESOURCE_ID;
	}

	public void getVersion(final Consumer<String> consumer) {
		final String link = String.format("https://api.spigotmc.org/legacy/update.php?resource=%d", this.RESOURCE_ID);
		Bukkit.getScheduler().runTaskAsynchronously(
				DailyRewardsPlugin.get(),
				() -> {
					try (final InputStream inputStream = new URL(link).openStream();
						 final Scanner scanner = new Scanner(inputStream)) {

						if (!scanner.hasNext()) return;
						consumer.accept(scanner.next());

					} catch (IOException exception) {
						DailyRewardsPlugin.get()
								.getLogger()
								.info(String.format("Can't look for updates: %s", exception.getMessage()));
					}
				});
	}
}

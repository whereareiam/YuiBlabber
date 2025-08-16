package me.whereareiam.yuiblabber.adapter.config.provider;

import jakarta.annotation.PostConstruct;
import me.whereareiam.yui.api.input.Registry;
import me.whereareiam.yui.api.output.Reloadable;
import me.whereareiam.yui.api.output.config.ConfigurationLoader;
import me.whereareiam.yuiblabber.api.model.config.BlabberChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class BlabberChannelConfigProvider implements Reloadable {
	private final Path channelsPath;
	private final ConfigurationLoader configurationLoader;

	private List<BlabberChannel> blabberChannels = List.of();

	@Autowired
	public BlabberChannelConfigProvider(
			@Qualifier("channelsPath") Path channelsPath,
			ConfigurationLoader configurationLoader,
			Registry<Reloadable> registry
	) {
		this.channelsPath = channelsPath;
		this.configurationLoader = configurationLoader;
		registry.register(this);
	}

	@PostConstruct
	public void init() {
		load();
	}

	public List<BlabberChannel> getAll() {
		if (blabberChannels == null || blabberChannels.isEmpty())
			load();

		return blabberChannels;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		List<BlabberChannel> loaded = new ArrayList<>();

		if (Files.exists(channelsPath) && Files.isDirectory(channelsPath)) {
			try (Stream<Path> paths = Files.list(channelsPath)) {
				paths.filter(Files::isRegularFile)
						.forEach(file -> {
							String fileName = file.getFileName().toString();
							int lastDot = fileName.lastIndexOf('.');
							if (lastDot <= 0) return;

							String nameWithoutExt = fileName.substring(0, lastDot);
							if (nameWithoutExt.isBlank()) return;

							try {
								BlabberChannel cfg = configurationLoader.load(file.getParent().resolve(nameWithoutExt), BlabberChannel.class);
								if (cfg != null && cfg.isEnabled()) loaded.add(cfg);
							} catch (RuntimeException ex) {
								// Skip unreadable/mismatched files
							}
						});
			} catch (IOException ignored) {
				// If listing fails, we'll rely on default below
			}
		}

		if (loaded.isEmpty()) {
			BlabberChannel cfg = configurationLoader.load(channelsPath.resolve("default"), BlabberChannel.class);
			if (cfg != null && cfg.isEnabled()) loaded.add(cfg);
		}

		// De-duplicate by id, keep first occurrence
		Map<String, BlabberChannel> uniqueById = new LinkedHashMap<>();
		for (BlabberChannel cfg : loaded) {
			if (cfg.getId() == null) continue;
			uniqueById.putIfAbsent(cfg.getId(), cfg);
		}

		blabberChannels = List.copyOf(uniqueById.values());
	}
}



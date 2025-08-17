package me.whereareiam.yuiblabber.adapter.config.provider;

import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yui.api.input.Registry;
import me.whereareiam.yui.api.output.Reloadable;
import me.whereareiam.yui.api.output.config.ConfigurationLoader;
import me.whereareiam.yui.api.type.ConfigurationType;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannel;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
public class BlabberChannelsProvider implements Reloadable {
	private final Path dataPath;
	private final ConfigurationLoader configurationLoader;
	private final ConfigurationType configurationType;
	private List<BlabberChannel> channels;

	@Autowired
	public BlabberChannelsProvider(
			@Qualifier("channelsPath") Path channelsPath,
			ConfigurationLoader configurationLoader,
			ConfigurationType configurationType,
			Registry<Reloadable> registry
	) {
		this.dataPath = channelsPath;
		this.configurationLoader = configurationLoader;
		this.configurationType = configurationType;

		registry.register(this);
	}

	public List<BlabberChannel> get() {
		if (channels != null) return channels;

		loadChannels();

		return channels;
	}

	@Override
	public void reload() {
		loadChannels();
	}

	private void loadChannels() {
		channels = new ArrayList<>();
		try (Stream<Path> paths = Files.list(dataPath)) {
			paths.filter(path -> path.getFileName().toString().endsWith(configurationType.getExtension())).forEach(path -> {
				String fileName = path.getFileName().toString().replace(configurationType.getExtension(), "");

				if (Files.isDirectory(path) || fileName.isEmpty()) return;

				channels.addAll(addChannelsFromConfig(path.getParent().resolve(fileName)));
			});
		} catch (IOException e) {
			log.error("Failed to load channels configurations", e);
			channels = Collections.emptyList();
			return;
		}

		if (channels.isEmpty()) channels.addAll(addChannelsFromConfig(dataPath.resolve("default")));

		channels.removeIf(channel -> channels.stream().anyMatch(c -> c != channel && c.getId().equals(channel.getId())));
	}

	private List<BlabberChannel> addChannelsFromConfig(Path path) {
		BlabberChannels blabberChannels = configurationLoader.load(path, BlabberChannels.class);
		return blabberChannels.getChannels().stream()
				.filter(BlabberChannel::isEnabled)
				.toList();
	}
}
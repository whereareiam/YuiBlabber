package me.whereareiam.yuiblabber.adapter.config.provider;

import jakarta.annotation.PostConstruct;
import me.whereareiam.yui.api.input.Registry;
import me.whereareiam.yui.api.output.Reloadable;
import me.whereareiam.yui.api.output.config.ConfigurationLoader;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class BlabberSettingsProvider implements Reloadable {
	private final Path pluginPath;
	private final ConfigurationLoader configLoader;

	private BlabberSettings settings;

	@Autowired
	public BlabberSettingsProvider(
			@Qualifier("pluginPath") Path pluginPath,
			ConfigurationLoader configLoader,
			Registry<Reloadable> registry
	) {
		this.pluginPath = pluginPath;
		this.configLoader = configLoader;

		registry.register(this);
	}

	@PostConstruct
	public void init() {
		load();
	}

	public BlabberSettings get() {
		if (settings == null)
			load();

		return settings;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		settings = configLoader.load(pluginPath.resolve("settings"), BlabberSettings.class);
	}
}
package me.whereareiam.myplugin.adapter.config.provider;

import jakarta.annotation.PostConstruct;
import me.whereareiam.myplugin.api.model.config.PluginSettings;
import me.whereareiam.yui.api.input.Registry;
import me.whereareiam.yui.api.output.Reloadable;
import me.whereareiam.yui.api.output.config.ConfigurationLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PluginSettingsProvider implements Reloadable {
	private final Path pluginPath;
	private final ConfigurationLoader configLoader;

	private PluginSettings settings;

	@Autowired
	public PluginSettingsProvider(@Qualifier("pluginPath") Path pluginPath,
	                              ConfigurationLoader configLoader,
	                              Registry<Reloadable> registry) {
		this.pluginPath = pluginPath;
		this.configLoader = configLoader;

		registry.register(this);
	}

	@PostConstruct
	public void init() {
		load();
	}

	public PluginSettings get() {
		if (settings == null) {
			load();
		}
		return settings;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		settings = configLoader.load(pluginPath.resolve("settings"), PluginSettings.class);
	}
}
package me.whereareiam.myplugin.adapter.config.template;

import me.whereareiam.myplugin.api.model.config.PluginSettings;
import me.whereareiam.yui.api.output.config.DefaultConfig;
import org.springframework.stereotype.Component;

@Component
public class PluginSettingsTemplate implements DefaultConfig<PluginSettings> {
	@Override
	public PluginSettings getDefault() {
		PluginSettings settings = new PluginSettings();

		// Default values
		settings.setTest(true);

		return settings;
	}
}

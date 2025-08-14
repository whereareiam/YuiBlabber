package me.whereareiam.myplugin.adapter.config;

import me.whereareiam.myplugin.adapter.config.provider.PluginSettingsProvider;
import me.whereareiam.myplugin.api.model.config.PluginMessages;
import me.whereareiam.myplugin.api.model.config.PluginSettings;
import me.whereareiam.yui.api.output.config.ConfigurationManager;
import me.whereareiam.yui.api.output.config.DefaultConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ConfigConfiguration {
	@Bean
	public PluginSettings settings(PluginSettingsProvider pluginSettingsProvider) {
		return pluginSettingsProvider.get();
	}

	@Bean
	@Qualifier("pluginLanguagesPath")
	public Path languagesPath(@Qualifier("pluginPath") Path pluginPath) {
		Path languagesPath = pluginPath.resolve("languages");

		if (!languagesPath.toFile().exists()) {
			boolean created = languagesPath.toFile().mkdirs();
			if (!created) throw new RuntimeException("Failed to create plugin languages directory");
		}

		return languagesPath;
	}

	@Autowired
	public void setTemplates(ApplicationContext ctx, ConfigurationManager configManager) {
		configManager.addTemplate(PluginSettings.class, ctx.getBean("pluginSettingsTemplate", DefaultConfig.class));
		configManager.addTemplate(PluginMessages.class, ctx.getBean("pluginMessagesTemplate", DefaultConfig.class));
	}
}

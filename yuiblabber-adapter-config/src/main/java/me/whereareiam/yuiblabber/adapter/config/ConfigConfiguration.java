package me.whereareiam.yuiblabber.adapter.config;

import me.whereareiam.yui.api.output.config.ConfigurationManager;
import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiblabber.adapter.config.provider.BlabberChannelConfigProvider;
import me.whereareiam.yuiblabber.adapter.config.provider.BlabberSettingsProvider;
import me.whereareiam.yuiblabber.api.model.config.BlabberChannel;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@Configuration
public class ConfigConfiguration {
	@Bean
	public BlabberSettings settings(BlabberSettingsProvider yuiBlabberSettingsProvider) {
		return yuiBlabberSettingsProvider.get();
	}

	@Bean
	public List<BlabberChannel> channels(BlabberChannelConfigProvider blabberChannelConfigProvider) {
		return blabberChannelConfigProvider.getAll();
	}

	@Bean
	@Qualifier("channelsPath")
	public Path languagesPath(@Qualifier("pluginPath") Path pluginPath) {
		Path channelsPath = pluginPath.resolve("channels");

		if (!channelsPath.toFile().exists()) {
			boolean created = channelsPath.toFile().mkdirs();
			if (!created) throw new RuntimeException("Failed to create plugin channels directory");
		}

		return channelsPath;
	}

	@Autowired
	public void setTemplates(ApplicationContext ctx, ConfigurationManager configManager) {
		configManager.addTemplate(BlabberSettings.class, ctx.getBean("blabberSettingsTemplate", DefaultConfig.class));
		configManager.addTemplate(BlabberChannel.class, ctx.getBean("blabberChannelTemplate", DefaultConfig.class));
	}
}

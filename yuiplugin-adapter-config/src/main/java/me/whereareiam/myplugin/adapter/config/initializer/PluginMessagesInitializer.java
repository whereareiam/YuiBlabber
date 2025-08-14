package me.whereareiam.myplugin.adapter.config.initializer;

import me.whereareiam.myplugin.api.model.config.PluginMessages;
import me.whereareiam.yui.api.output.config.ConfigurationLoader;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class PluginMessagesInitializer {
	@Autowired
	public PluginMessagesInitializer(@Qualifier("pluginLanguagesPath") Path pluginLanguagesPath, ConfigurationLoader configLoader) {
		configLoader.load(pluginLanguagesPath.resolve(DiscordLocale.ENGLISH_US.getLocale()), PluginMessages.class);
	}
}

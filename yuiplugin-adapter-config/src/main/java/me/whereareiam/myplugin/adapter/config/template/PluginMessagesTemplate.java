package me.whereareiam.myplugin.adapter.config.template;

import me.whereareiam.myplugin.api.model.config.PluginMessages;
import me.whereareiam.yui.api.output.config.DefaultConfig;
import org.springframework.stereotype.Component;

@Component
public class PluginMessagesTemplate implements DefaultConfig<PluginMessages> {
	@Override
	public PluginMessages getDefault() {
		PluginMessages messages = new PluginMessages();

		// Default values
		messages.setSimpleMessage("This is a simple message");

		return messages;
	}
}

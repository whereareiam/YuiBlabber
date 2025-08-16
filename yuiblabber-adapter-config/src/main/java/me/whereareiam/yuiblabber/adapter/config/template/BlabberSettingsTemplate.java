package me.whereareiam.yuiblabber.adapter.config.template;

import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import me.whereareiam.yuisynapse.api.model.Connection;
import me.whereareiam.yuisynapse.api.type.ProviderType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BlabberSettingsTemplate implements DefaultConfig<BlabberSettings> {
	@Override
	public BlabberSettings getDefault() {
		BlabberSettings settings = new BlabberSettings();

		// Default values
		settings.setConnections(List.of(
				Connection.builder()
						.id("example")
						.enabled(false)
						.configuration(Connection.Configuration.builder()
								.provider(ProviderType.OPENROUTER)
								.model("openai/gpt-oss-20b:free")
								.build())
						.behavior(Connection.Behavior.builder()
								.persona(Map.of(
										"PERSONA", List.of(
												"You are a helpful assistant.",
												"You are knowledgeable about various topics.",
												"You provide concise and accurate information."
										)))
								.temperature(0.7)
								.limitations(Connection.Behavior.Limitations.builder()
										.maxTokens(1024)
										.build())
								.build())
						.build()
		));

		return settings;
	}
}

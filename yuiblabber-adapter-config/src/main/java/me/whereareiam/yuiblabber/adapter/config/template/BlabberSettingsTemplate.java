package me.whereareiam.yuiblabber.adapter.config.template;

import me.whereareiam.yui.api.output.config.DefaultConfig;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import me.whereareiam.yuisynapse.api.model.Connection;
import me.whereareiam.yuisynapse.api.model.config.tool.HistoryToolConfig;
import me.whereareiam.yuisynapse.api.model.config.tool.InterestToolConfig;
import me.whereareiam.yuisynapse.api.model.config.tool.LanguageEnforcerToolConfig;
import me.whereareiam.yuisynapse.api.model.config.tool.UserInfoToolConfig;
import me.whereareiam.yuisynapse.api.type.ProviderType;
import me.whereareiam.yuisynapse.api.type.ToolPhase;
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
								.toolchain(List.of(
										Connection.Configuration.ToolInstanceConfig.builder()
												.name("interest")
												.phase(ToolPhase.PRE_PROCESSING)
												.order(10)
												.enabled(true)
												.config(buildInterestConfig())
												.build(),
										Connection.Configuration.ToolInstanceConfig.builder()
												.name("language-enforcer")
												.phase(ToolPhase.PRE_PROCESSING)
												.order(20)
												.enabled(true)
												.config(buildLanguageConfig())
												.build(),
										Connection.Configuration.ToolInstanceConfig.builder()
												.name("user-info")
												.phase(ToolPhase.CONTEXT_PROVIDER)
												.order(30)
												.enabled(true)
												.config(buildUserInfoConfig())
												.build(),
										Connection.Configuration.ToolInstanceConfig.builder()
												.name("history")
												.phase(ToolPhase.CONTEXT_PROVIDER)
												.order(40)
												.enabled(true)
												.config(buildHistoryConfig())
												.build()
								))
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

	private static InterestToolConfig buildInterestConfig() {
		InterestToolConfig config = new InterestToolConfig();
		config.setThreshold(0.5);
		config.setPassIfMentioned(true);
		config.setMentions(List.of("@Yui"));
		config.setPrefixes(List.of("yui ", "y:"));
		config.setPassIfContinuation(true);
		config.setContinuationAllowMin(0.2);
		config.setKeywords(Map.of(
				"en", List.of("help", "plugin", "error", "yui"),
				"ru", List.of("бот", "Юи", "Юяяя")
		));
		config.setContinuationWeight(0.8);
		config.setContinuationHalfLifeSeconds(90);

		return config;
	}

	private static LanguageEnforcerToolConfig buildLanguageConfig() {
		LanguageEnforcerToolConfig c = new LanguageEnforcerToolConfig();
		c.setMode(LanguageEnforcerToolConfig.EnforcementMode.STRICT);

		return c;
	}

	private static UserInfoToolConfig buildUserInfoConfig() {
		UserInfoToolConfig c = new UserInfoToolConfig();
		c.setIncludePrimaryLanguage(true);
		c.setIncludeAdditionalLanguages(true);
		c.setIncludeDiscordTag(true);
		c.setIncludeDisplayName(true);

		return c;
	}

	private static HistoryToolConfig buildHistoryConfig() {
		HistoryToolConfig c = new HistoryToolConfig();
		c.setPerUser(true);
		c.setPerChannel(true);
		c.setRetentionCount(20);
		c.setTtlSeconds(86400L);

		return c;
	}
}

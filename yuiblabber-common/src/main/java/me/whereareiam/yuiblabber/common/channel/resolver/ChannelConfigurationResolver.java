package me.whereareiam.yuiblabber.common.channel.resolver;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChannelConfigurationResolver {
	public Optional<BlabberChannel> findByDiscordChannelId(List<BlabberChannel> channels, String discordChannelId) {
		if (channels == null || channels.isEmpty())
			return Optional.empty();

		return channels.stream()
				.filter(BlabberChannel::isEnabled)
				.filter(cfg -> cfg.getChannelIds() != null && cfg.getChannelIds().contains(discordChannelId))
				.findFirst();
	}
}



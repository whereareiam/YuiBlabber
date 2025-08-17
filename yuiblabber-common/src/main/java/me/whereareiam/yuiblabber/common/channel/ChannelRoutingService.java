package me.whereareiam.yuiblabber.common.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yuiblabber.api.model.config.channel.BlabberChannel;
import me.whereareiam.yuiblabber.common.channel.factory.DiscordMessageFactory;
import me.whereareiam.yuiblabber.common.channel.resolver.ChannelConfigurationResolver;
import me.whereareiam.yuiblabber.common.channel.resolver.ConnectionResolver;
import me.whereareiam.yuiblabber.common.connection.ConnectionSelector;
import me.whereareiam.yuisynapse.api.model.Connection;
import me.whereareiam.yuisynapse.api.model.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelRoutingService {
	private final List<BlabberChannel> channels;

	private final DiscordMessageFactory discordMessageFactory;
	private final StreamingResponseService streamingResponseService;
	private final ChannelConfigurationResolver channelConfigurationResolver;

	private final ConnectionSelector connectionSelector;
	private final ConnectionResolver connectionResolver;

	public void handleMessage(MessageReceivedEvent event) {
		String channelId = event.getChannel().getId();
		log.debug("Received message in channel {} from user {}", channelId, event.getAuthor().getId());

		Optional<BlabberChannel> matched = channelConfigurationResolver.findByDiscordChannelId(channels, channelId);
		if (matched.isEmpty()) {
			log.debug("No channel configuration matched for channel {}. Ignoring.", channelId);
			return;
		}

		BlabberChannel cfg = matched.get();
		log.debug("Matched channel configuration id={}", cfg.getId());
		String connectionId = connectionSelector.selectConnectionId(cfg);
		if (connectionId == null) {
			log.warn("No connection selected for channel config id={}", cfg.getId());
			return;
		}

		Optional<Connection> connectionOpt = connectionResolver.resolveEnabled(connectionId);
		if (connectionOpt.isEmpty()) {
			log.warn("Connection {} not found or disabled for channel config id={}", connectionId, cfg.getId());
			return;
		}

		Connection connection = connectionOpt.get();
		Message userMessage = discordMessageFactory.fromEvent(event, connection.getId());
		streamingResponseService.streamAndReply(event, connection, userMessage);
	}
}



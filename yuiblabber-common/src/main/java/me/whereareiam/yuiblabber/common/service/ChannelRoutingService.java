package me.whereareiam.yuiblabber.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yuiblabber.api.model.config.BlabberChannel;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import me.whereareiam.yuisynapse.api.input.SynapseService;
import me.whereareiam.yuisynapse.api.model.Connection;
import me.whereareiam.yuisynapse.api.model.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ChannelRoutingService {
	private final SynapseService synapseService;
	private final BlabberSettings blabberSettings;
	private final List<BlabberChannel> channels;

	private final Map<String, Integer> roundRobinIndex = new ConcurrentHashMap<>();

	public void handleMessage(MessageReceivedEvent event) {
		String channelId = event.getChannel().getId();
		log.debug("Received message in channel {} from user {}", channelId, event.getAuthor().getId());

		Optional<BlabberChannel> matched = channels.stream()
				.filter(BlabberChannel::isEnabled)
				.filter(cfg -> cfg.getChannelIds() != null && cfg.getChannelIds().contains(channelId))
				.findFirst();

		if (matched.isEmpty()) {
			log.debug("No channel configuration matched for channel {}. Ignoring.", channelId);
			return;
		}

		BlabberChannel cfg = matched.get();
		log.debug("Matched channel configuration id={}", cfg.getId());
		String connectionId = selectConnectionId(cfg);
		if (connectionId == null) {
			log.warn("No connection selected for channel config id={}", cfg.getId());
			return;
		}

		Connection connection = findConnection(connectionId);
		if (connection == null || !connection.isEnabled()) {
			log.warn("Connection {} not found or disabled for channel config id={}", connectionId, cfg.getId());
			return;
		}

		log.debug("Starting typing indicator while awaiting AI response for channel {}", channelId);
		Disposable typing = Flux.interval(Duration.ZERO, Duration.ofSeconds(7))
				.subscribe(n -> {
					log.trace("typing tick {} for channel {}", n, channelId);
					event.getChannel().sendTyping().queue(
							null,
							err -> log.warn("Failed to send typing indicator to channel {}", channelId, err)
					);
				});

		Message userMessage = Message.builder()
				.id(event.getMessageId())
				.role(Message.Role.USER)
				.content(event.getMessage().getContentDisplay())
				.timestamp(Instant.now())
				.metadata(Map.of(
						"userId", event.getAuthor().getId(),
						"channelId", channelId,
						"connectionId", connection.getId()
				))
				.build();

		log.debug("Dispatching message to Synapse connection {}", connection.getId());
		Publisher<Message> responsePub = synapseService.send(connection, userMessage);
		StringBuilder contentBuffer = new StringBuilder();
		Flux.from(responsePub)
				.doOnNext(resp -> {
					String chunk = resp.getContent();
					if (chunk != null) contentBuffer.append(chunk);
				})
				.doOnError(err -> log.error("Failed to obtain AI response for connection {}", connection.getId(), err))
				.doOnComplete(() -> {
					String content = contentBuffer.toString();
					log.debug("Received AI response for connection {} ({} chars)", connection.getId(), content.isEmpty() ? 0 : content.length());
					event.getMessage().reply(content).queue(
							replyMsg -> log.debug("Replied to message {} in channel {} with reply {}", event.getMessageId(), channelId, replyMsg.getId()),
							err -> log.error("Failed to send reply in channel {}", channelId, err)
					);
				})
				.doFinally(sig -> {
					log.trace("typing disposed for channel {} due to {}", channelId, sig);
					if (!typing.isDisposed()) typing.dispose();
				})
				.subscribe();
	}

	private String selectConnectionId(BlabberChannel cfg) {
		List<String> ids = cfg.getConnections();
		if (ids == null || ids.isEmpty())
			return null;

		BlabberChannel.BalancingStrategy strategy = Optional.ofNullable(cfg.getBalancing())
				.orElse(BlabberChannel.BalancingStrategy.RANDOM);

		if (strategy == BlabberChannel.BalancingStrategy.ROUND_ROBIN) {
			Integer idx = roundRobinIndex.get(cfg.getId());
			if (idx == null) idx = 0;
			int current = idx;
			int next = (current + 1) % ids.size();
			roundRobinIndex.put(cfg.getId(), next);

			return ids.get(current);
		} else {
			return ids.get(ThreadLocalRandom.current().nextInt(ids.size()));
		}
	}

	private Connection findConnection(String connectionId) {
		if (blabberSettings.getConnections() == null)
			return null;

		return blabberSettings.getConnections().stream()
				.filter(c -> connectionId.equals(c.getId()))
				.findFirst()
				.orElse(null);
	}
}



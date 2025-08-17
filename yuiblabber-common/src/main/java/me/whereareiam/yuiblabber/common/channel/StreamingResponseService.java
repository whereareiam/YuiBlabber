package me.whereareiam.yuiblabber.common.channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yuisynapse.api.input.SynapseService;
import me.whereareiam.yuisynapse.api.model.Connection;
import me.whereareiam.yuisynapse.api.model.Message;
import me.whereareiam.yuisynapse.api.model.Options;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamingResponseService {
	private final SynapseService synapseService;
	private final ScheduledExecutorService typingScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r, "typing-indicator");
		t.setDaemon(true);
		return t;
	});

	public void streamAndReply(MessageReceivedEvent event, Connection connection, Message userMessage) {
		String channelId = event.getChannel().getId();
		log.debug("Dispatching message to Synapse connection {}", connection.getId());

		Publisher<Message> responsePub = synapseService.send(connection, userMessage, Options.builder().stream(true).build());
		StringBuilder contentBuffer = new StringBuilder();
		AtomicReference<ScheduledFuture<?>> typingFutureRef = new AtomicReference<>();

		Runnable stopTyping = () -> {
			ScheduledFuture<?> f = typingFutureRef.getAndSet(null);
			if (f != null) {
				f.cancel(true);
				log.debug("Stopped typing indicator for channel {}", channelId);
			}
		};

		Flux.from(responsePub)
				.publishOn(Schedulers.boundedElastic())
				.doOnNext(resp -> {
					if (typingFutureRef.get() == null) {
						log.debug("Starting typing indicator while streaming AI response for channel {}", channelId);
						Runnable typingTask = () -> event.getChannel().sendTyping().queue(
								null,
								err -> log.warn("Failed to send typing indicator to channel {}", channelId, err)
						);
						ScheduledFuture<?> future = typingScheduler.scheduleAtFixedRate(typingTask, 0, 7, TimeUnit.SECONDS);
						if (!typingFutureRef.compareAndSet(null, future))
							future.cancel(true);
					}
					String chunk = resp.getContent();
					if (chunk != null) contentBuffer.append(chunk);
				})
				.doOnError(err -> log.error("Failed to obtain AI response for connection {}", connection.getId(), err))
				.doOnComplete(() -> {
					String content = contentBuffer.toString();
					log.debug("Received AI response for connection {} ({} chars)", connection.getId(), content.length());
					if (!content.isBlank()) {
						event.getMessage().reply(content).queue(
								replyMsg -> log.debug("Replied to message {} in channel {} with reply {}", event.getMessageId(), channelId, replyMsg.getId()),
								err -> log.error("Failed to send reply in channel {}", channelId, err)
						);
					} else {
						log.debug("No AI content to reply with for connection {} (empty stream)", connection.getId());
					}
				})
				.doFinally(_ -> stopTyping.run())
				.subscribe();
	}
}

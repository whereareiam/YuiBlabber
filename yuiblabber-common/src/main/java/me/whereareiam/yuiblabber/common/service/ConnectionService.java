package me.whereareiam.yuiblabber.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import me.whereareiam.yuisynapse.api.input.SynapseService;
import me.whereareiam.yuisynapse.api.model.Connection;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionService {
	private final SynapseService synapseService;
	private final BlabberSettings blabberSettings;

	public void warmup() {
		List<Connection> connections = blabberSettings.getConnections();
		if (connections == null || connections.isEmpty()) {
			log.info("No connections defined in Blabber settings to warm up");
			return;
		}

		connections.stream()
				.filter(Connection::isEnabled)
				.forEach(conn -> synapseService.create(conn)
						.thenAccept(created -> log.info("Prepared Synapse connection: {} (model={})", created.getId(), created.getConfiguration() != null ? created.getConfiguration().getModel() : "unknown"))
						.exceptionally(ex -> {
							log.error("Failed to prepare Synapse connection {}", conn.getId(), ex);
							return null;
						}));
	}

	public void shutdown() {
		List<Connection> connections = blabberSettings.getConnections();
		if (connections == null || connections.isEmpty()) return;

		connections.stream()
				.filter(Connection::isEnabled)
				.forEach(conn -> synapseService.close(conn)
						.thenRun(() -> log.info("Closed Synapse connection: {}", conn.getId()))
						.exceptionally(ex -> {
							log.warn("Failed to close Synapse connection {}", conn.getId(), ex);
							return null;
						}));
	}
}



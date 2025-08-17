package me.whereareiam.yuiblabber.common.channel.resolver;

import lombok.RequiredArgsConstructor;
import me.whereareiam.yuiblabber.api.model.config.BlabberSettings;
import me.whereareiam.yuisynapse.api.model.Connection;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConnectionResolver {
	private final BlabberSettings blabberSettings;

	public Optional<Connection> resolveEnabled(String connectionId) {
		if (blabberSettings.getConnections() == null)
			return Optional.empty();

		return blabberSettings.getConnections().stream()
				.filter(c -> connectionId.equals(c.getId()))
				.filter(Connection::isEnabled)
				.findFirst();
	}
}



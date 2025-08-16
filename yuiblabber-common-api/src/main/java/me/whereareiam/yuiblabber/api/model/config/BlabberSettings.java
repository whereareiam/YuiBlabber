package me.whereareiam.yuiblabber.api.model.config;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.yuisynapse.api.model.Connection;

import java.util.List;

@Setter
@Getter
public class BlabberSettings {
	private List<Connection> connections;
}

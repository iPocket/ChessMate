package me.protoflicker.chessmate.util;

import lombok.Getter;

import java.util.Objects;

public final class Pair<F, S> {

	@Getter
	private final F key;

	@Getter
	private final S value;

	public Pair(F key, S value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Pair<?, ?> p)) {
			return false;
		}

		return Objects.equals(p.key, key) && Objects.equals(p.value, value);
	}

	@Override
	public int hashCode() {
		return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
	}
}
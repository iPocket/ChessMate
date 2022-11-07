package me.protoflicker.chessmate.data.record.enums;

import lombok.Getter;

public enum Result {
	LOSS("Loss", 0),
	WIN("Win", 1),
	ONGOING("Ongoing", 2);

	@Getter
	private final String name;

	@Getter
	private final int code;

	Result(String name, int code){
		this.name = name;
		this.code = code;
	}
}

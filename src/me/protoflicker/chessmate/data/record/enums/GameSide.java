package me.protoflicker.chessmate.data.record.enums;

import lombok.Getter;

public enum GameSide {
	WHITE("White", 0),
	BLACK("Black", 1);

	@Getter
	private final String name;

	@Getter
	private final int code;

	GameSide(String name, int code){
		this.name = name;
		this.code = code;
	}
}

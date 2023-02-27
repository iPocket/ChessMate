package me.protoflicker.chessmate.protocol.packet.game.invitation;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;

import java.io.Serializable;

public class GameInvitation implements Serializable {

	@Getter
	private final byte[] inviterId;

	@Getter
	private final byte[] inviteeId;

	@Getter
	private final String startingBoard;

	@Getter
	private final int timeConstraint;

	@Getter
	private final int timeIncrement;

	@Getter
	private final GameSide inviterSide;

	public GameInvitation(byte[] inviterId, byte[] inviteeId, String startingBoard, int timeConstraint, int timeIncrement,
						  GameSide inviterSide){

		this.inviterId = inviterId;
		this.inviteeId = inviteeId;
		this.startingBoard = startingBoard;
		this.timeConstraint = timeConstraint;
		this.timeIncrement = timeIncrement;
		this.inviterSide = inviterSide;
	}
}

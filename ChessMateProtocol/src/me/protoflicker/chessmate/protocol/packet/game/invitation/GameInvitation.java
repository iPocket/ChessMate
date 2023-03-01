package me.protoflicker.chessmate.protocol.packet.game.invitation;

import lombok.Getter;
import lombok.Setter;
import me.protoflicker.chessmate.protocol.chess.enums.GameSide;
import me.protoflicker.chessmate.protocol.chess.enums.SimpleGameInfo;

import java.io.Serializable;

public final class GameInvitation implements Serializable {

	@Getter
	@Setter
	private byte[] invitationId;

	@Getter
	private final GameSide inviterSide;

	@Getter
	private final SimpleGameInfo info;

	public GameInvitation(byte[] invitationId, GameSide inviterSide, SimpleGameInfo info){
		this.invitationId = invitationId;
		this.inviterSide = inviterSide;
		this.info = info;
	}

	public byte[] getInviterId(){
		return info.getId(inviterSide);
	}

	public byte[] getInviteeId(){
		return info.getId(inviterSide.getOpposite());
	}
}

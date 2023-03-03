package me.protoflicker.chessmate.protocol.packet.game.invitation.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;

public class GameInviteAcceptSuccessfulPacket extends Packet implements ServerPacket {

	@Getter
	private final byte[] invitationId;

	@Getter
	private final byte[] gameId;

	public GameInviteAcceptSuccessfulPacket(byte[] invitationId, byte[] gameId){
		this.invitationId = invitationId;
		this.gameId = gameId;
	}
}

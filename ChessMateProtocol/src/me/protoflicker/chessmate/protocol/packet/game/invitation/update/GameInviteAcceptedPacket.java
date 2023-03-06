package me.protoflicker.chessmate.protocol.packet.game.invitation.update;

import lombok.Getter;
import me.protoflicker.chessmate.protocol.Packet;
import me.protoflicker.chessmate.protocol.packet.ServerPacket;
import me.protoflicker.chessmate.protocol.packet.game.invitation.GameInvitation;

public class GameInviteAcceptedPacket extends Packet implements ServerPacket {

	@Getter
	private final GameInvitation invitation;

	@Getter
	private final byte[] gameId;

	public GameInviteAcceptedPacket(GameInvitation invitation, byte[] gameId){
		this.invitation = invitation;
		this.gameId = gameId;
	}
}
